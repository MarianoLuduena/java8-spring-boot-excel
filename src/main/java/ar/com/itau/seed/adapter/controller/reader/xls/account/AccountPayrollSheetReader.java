package ar.com.itau.seed.adapter.controller.reader.xls.account;

import ar.com.itau.seed.domain.Employee;
import ar.com.itau.seed.config.util.either.Either;
import ar.com.itau.seed.domain.error.EntityError;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.Iterator;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class AccountPayrollSheetReader {

    private static final String NAME_COLUMN_HEADER = "NOMBRE"; // 3rd column ("C")
    private static final int NAME_COLUMN_IDX = 2; // 3rd column ("C")

    private final AccountPayrollRowReader accountPayrollRowReader;

    public AccountPayrollSheetReader(final AccountPayrollRowReader accountPayrollRowReader) {
        this.accountPayrollRowReader = accountPayrollRowReader;
    }

    public Stream<Either<EntityError, Employee>> read(final Sheet sheet) {
        final Iterator<Row> rowsIterator = sheet.iterator();
        boolean shouldLoop = rowsIterator.hasNext();
        while (shouldLoop) {
            final Row row = rowsIterator.next();
            if (NAME_COLUMN_HEADER.equalsIgnoreCase(row.getCell(NAME_COLUMN_IDX).getStringCellValue())) {
                break;
            }
            shouldLoop = rowsIterator.hasNext();
        }
        return StreamSupport.stream(
                        Spliterators.spliteratorUnknownSize(
                                rowsIterator,
                                Spliterator.IMMUTABLE + Spliterator.ORDERED
                        ),
                        false
                )
                .filter(this::rowIsNotEmpty)
                .map(this::readRow);
    }

    private Either<EntityError, Employee> readRow(final Row row) {
        return accountPayrollRowReader.read(row);
    }

    private boolean rowIsNotEmpty(final Row row) {
        final Cell cell = row.getCell(NAME_COLUMN_IDX);
        return Objects.nonNull(cell) && !CellType.BLANK.equals(cell.getCellType());
    }

}
