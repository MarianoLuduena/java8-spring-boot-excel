package ar.com.itau.seed.adapter.controller.reader.xls;

import ar.com.itau.seed.adapter.controller.reader.xls.account.AccountPayrollSheetReader;
import ar.com.itau.seed.domain.Employee;
import ar.com.itau.seed.config.util.either.Either;
import ar.com.itau.seed.domain.error.EntityError;
import lombok.SneakyThrows;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.stream.Stream;

@Deprecated
public class AccountXlsReader implements Closeable {

    private static final int PAYROLL_SHEET_NUMBER = 3;

    private final Workbook workbook;
    private final AccountPayrollSheetReader accountPayrollSheetReader;

    public AccountXlsReader(
            final InputStream is,
            final AccountPayrollSheetReader accountPayrollSheetReader
    ) throws IOException {
        this.workbook = new HSSFWorkbook(is, false); // Set to false to preserve memory
        this.accountPayrollSheetReader = accountPayrollSheetReader;
    }

    public Stream<Either<EntityError, Employee>> read() {
        final Sheet sheet = workbook.getSheetAt(PAYROLL_SHEET_NUMBER);
        return accountPayrollSheetReader.read(sheet);
    }

    @SneakyThrows
    public void close() {
        workbook.close();
    }

}
