package ar.com.itau.seed.adapter.controller;

import ar.com.itau.seed.adapter.controller.mapper.AccountsPayrollXlsSheet2Txt;
import ar.com.itau.seed.adapter.controller.mapper.ControllerMapper;
import ar.com.itau.seed.adapter.controller.model.AccountsFileUploadControllerModel;
import ar.com.itau.seed.application.port.in.CreateAccounts;
import ar.com.itau.seed.domain.AccountsBatch;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Objects;

@RestController
@RequestMapping("/accounts")
@Slf4j
@Validated
public class AccountControllerAdapter {

    private static final int COLUMNS_TO_READ = 29;
    private static final String TXT_EXTENSION = "txt";
    private static final String XLS_EXTENSION = "xls";

    private final CreateAccounts createAccounts;
    private final ControllerMapper mapper = new ControllerMapper();

    public AccountControllerAdapter(final CreateAccounts createAccounts) {
        this.createAccounts = createAccounts;
    }

    @PostMapping
    public AccountsFileUploadControllerModel upload(
            @RequestParam("file") final MultipartFile file
    ) throws IOException {
        final long startTime = System.currentTimeMillis();
        log.info(
                "Content-Type {} | Name {} | Original filename {}",
                file.getContentType(),
                file.getName(),
                file.getOriginalFilename()
        );

        File txtFile = null;
        try {
            File xlsFile = createTmpFile(file.getName(), XLS_EXTENSION);
            txtFile = createTmpFile(file.getOriginalFilename(), TXT_EXTENSION);

            file.transferTo(xlsFile);
            PrintStream printStream = null;
            AccountsPayrollXlsSheet2Txt xlsSheet2Txt = null;
            try {
                printStream = new PrintStream(new BufferedOutputStream(new FileOutputStream(txtFile)));
                xlsSheet2Txt = new AccountsPayrollXlsSheet2Txt(xlsFile, printStream, COLUMNS_TO_READ);
                xlsSheet2Txt.process();
                printStream.flush();
            } finally {
                close(xlsSheet2Txt);
                close(printStream);
                delete(xlsFile);
            }

            final AccountsBatch accountsBatch = createAccounts.execute(
                    new CreateAccounts.Cmd(
                            mapper.employeeFromPath(txtFile.toPath()),
                            txtFile.getName()
                    )
            );

            return AccountsFileUploadControllerModel.from(accountsBatch);
        } catch (Exception ex) {
            log.error("Error processing input file {}", file.getOriginalFilename(), ex);
            throw ex;
        } finally {
            delete(txtFile);
            log.info("Total processing time: {} ms", System.currentTimeMillis() - startTime);
        }
    }

    private File createTmpFile(final String originalFilename, final String extension) throws IOException {
        return File.createTempFile(
                "account-payroll-" + originalFilename + "-",
                "." + extension
        );
    }

    private void close(final Closeable closeable) {
        try {
            if (Objects.nonNull(closeable)) {
                closeable.close();
            }
        } catch (Exception e) {
            log.error("Error closing resource {}", closeable, e);
        }
    }

    private void delete(final File file) {
        if (Objects.nonNull(file)) {
            final boolean tmpFileDeletionResult = file.delete();
            log.info("Result of deleting file {}: {}", file.getAbsolutePath(), tmpFileDeletionResult);
        }
    }

}
