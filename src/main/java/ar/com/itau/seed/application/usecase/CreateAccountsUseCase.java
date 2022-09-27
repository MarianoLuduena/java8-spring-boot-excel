package ar.com.itau.seed.application.usecase;

import ar.com.itau.seed.application.port.out.UploadAccountsPayroll;
import ar.com.itau.seed.application.service.ValidationService;
import ar.com.itau.seed.application.port.in.CreateAccounts;
import ar.com.itau.seed.application.port.out.SaveAccountsBatch;
import ar.com.itau.seed.domain.AccountsBatch;
import ar.com.itau.seed.domain.Employee;
import ar.com.itau.seed.domain.error.EntityError;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import java.util.Set;

@Component
@Slf4j
public class CreateAccountsUseCase implements CreateAccounts {

    private final ValidationService validationService;
    private final SaveAccountsBatch saveAccountsBatch;
    private final UploadAccountsPayroll uploadAccountsPayroll;

    public CreateAccountsUseCase(
            final ValidationService validationService,
            final SaveAccountsBatch saveAccountsBatch,
            final UploadAccountsPayroll uploadAccountsPayroll
    ) {
        this.validationService = validationService;
        this.saveAccountsBatch = saveAccountsBatch;
        this.uploadAccountsPayroll = uploadAccountsPayroll;
    }

    @SneakyThrows
    @Override
    public AccountsBatch execute(final CreateAccounts.Cmd cmd) {
        log.info("Processing cmd {}", cmd);
        final AccountsBatch accountsBatch = AccountsBatch.create(cmd.getSourceName());
        final long startTime = System.currentTimeMillis();
        cmd.getMaybeEmployees().forEach(maybeEmployee -> {
            if (maybeEmployee.isLeft()) {
                accountsBatch.addError(maybeEmployee.left());
                return;
            }
            final Employee employee = maybeEmployee.right();
            final Set<ConstraintViolation<Employee>> violations = validationService.findViolations(employee);
            if (!violations.isEmpty()) {
                accountsBatch.addError(
                        new EntityError(employee.getReferenceId(), EntityError.Error.validation(violations))
                );
                return;
            }

            accountsBatch.addEmployee(employee);
        });

        log.info("Accounts batch validated in {} ms", System.currentTimeMillis() - startTime);
        if (accountsBatch.hasErrors()) {
            log.error("There are {} errors in the records {}...",
                    accountsBatch.errorCount(), accountsBatch.getErrors().get(0));
            return accountsBatch;
        }

        log.info("{} employees successfully validated", accountsBatch.employeeCount());
        final AccountsBatch updatedAccountsBatch = accountsBatch.withId(saveAccountsBatch.save(accountsBatch));

        uploadAccountsPayroll.upload(updatedAccountsBatch);

        return updatedAccountsBatch;
    }

}
