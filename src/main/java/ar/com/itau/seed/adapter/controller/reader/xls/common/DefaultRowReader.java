package ar.com.itau.seed.adapter.controller.reader.xls.common;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class DefaultRowReader implements RowReader {

    private final Row row;
    private int currentIdx = 0;

    /**
     * Utility class to read Excel rows.
     * <p>
     * Starts reading from the very first column (index 0) and keeps advancing one column at a time after each
     * invocation to any of the read* or skip methods.
     * <p>
     * IMPORTANT: This implementation is NOT thead safe.
     *
     * @param row to be parsed
     */
    public DefaultRowReader(final Row row) {
        this.row = row;
    }

    public int getLastColumnReadIdx() {
        return currentIdx - 1;
    }

    public void skip() {
        next();
    }

    public String readString() {
        final Cell cell = cell();
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case BLANK:
                return "";
            case NUMERIC:
                return String.valueOf(cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                throw new UnsupportedOperationException("[" + cell.getAddress().formatAsString()
                        + "] Cell type not supported :" + cell.getCellType());
        }
    }

    public Long readLong() {
        return doReadLong();
    }

    private Long doReadLong() {
        final Cell cell = cell();
        switch (cell.getCellType()) {
            case STRING:
                return Long.parseLong(cell.getStringCellValue());
            case NUMERIC:
                return Double.valueOf(cell.getNumericCellValue()).longValue();
            case BOOLEAN:
                return booleanToLong(cell.getBooleanCellValue());
            default:
                throw new UnsupportedOperationException("[" + cell.getAddress().formatAsString()
                        + "] Cell type not supported :" + cell.getCellType());
        }
    }

    public Optional<Long> readOptionalLong() {
        return doReadOptionalLong();
    }

    private Optional<Long> doReadOptionalLong() {
        final Cell cell = cell();
        switch (cell.getCellType()) {
            case NUMERIC:
                return Optional.of(cell.getNumericCellValue()).map(Double::longValue);
            case STRING:
                return Optional.ofNullable(cell.getStringCellValue())
                        .filter(str -> !str.isEmpty())
                        .map(Long::parseLong);
            case BLANK:
                return Optional.empty();
            case BOOLEAN:
                return Optional.of(cell.getBooleanCellValue()).map(this::booleanToLong);
            default:
                throw new UnsupportedOperationException("[" + cell.getAddress().formatAsString()
                        + "] Cell type not supported :" + cell.getCellType());
        }
    }

    private Long booleanToLong(final Boolean value) {
        return value ? 1L : 0L;
    }

    public Integer readInt() {
        return doReadLong().intValue();
    }

    public Optional<Integer> readOptionalInt() {
        return doReadOptionalLong().map(Long::intValue);
    }

    public BigDecimal readBigDecimal() {
        final Cell cell = cell();
        switch (cell.getCellType()) {
            case STRING:
                return new BigDecimal(cell.getStringCellValue());
            case NUMERIC:
                return BigDecimal.valueOf(cell.getNumericCellValue());
            default:
                throw new UnsupportedOperationException("Cell type not supported");
        }
    }

    public LocalDate readYYYYMMDD() {
        return LocalDate.parse(doReadLong().toString(), DateTimeFormatter.BASIC_ISO_DATE);
    }

    public Optional<LocalDate> readOptionalYYYYMMDD() {
        return doReadOptionalLong().map(number -> LocalDate.parse(number.toString(), DateTimeFormatter.BASIC_ISO_DATE));
    }

    public LocalDate readYYYYMM() {
        return LocalDate.parse(doReadLong() + "01", DateTimeFormatter.BASIC_ISO_DATE);
    }

    public Optional<LocalDate> readOptionalYYYYMM() {
        return doReadOptionalLong().map(number -> LocalDate.parse(number + "01", DateTimeFormatter.BASIC_ISO_DATE));
    }

    private Cell cell() {
        final Cell cell = row.getCell(currentIdx);
        next();
        return cell;
    }

    private void next() {
        currentIdx += 1;
    }

}
