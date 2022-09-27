package ar.com.itau.seed.adapter.rest;

import ar.com.itau.seed.adapter.rest.model.AccountsBatchRestModel;
import ar.com.itau.seed.application.port.out.UploadAccountsPayroll;
import ar.com.itau.seed.domain.AccountsBatch;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
public class PaymentsRestAdapter implements UploadAccountsPayroll {

    public void upload(final AccountsBatch accountsBatch) {
        log.info("Uploading batch {}", accountsBatch);
        final AccountsBatchRestModel model = AccountsBatchRestModel.from(accountsBatch);
        if (accountsBatch.employeeCount() < 200) {
            log.info("Base64 encoded file with fixed-width lines: {}", model);
        }
    }

}
