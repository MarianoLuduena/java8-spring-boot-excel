package ar.com.itau.seed.adapter.controller.reader.csv;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Optional;

public class CsvReader {

    private static final char COMMA = ',';
    private static final char DOT = '.';
    private static final String BASIC_ISO_DATE_FIRST_DAY_OF_MONTH = "01";

    public static CsvReader tsv(final String str) {
        return new CsvReader(str, '\t');
    }

    private final Iterator<String> parts;
    private int readIndex;
    private String lastStringRead;

    public CsvReader(final String str, final char delimiter) {
        this.parts = Arrays.stream(str.split(String.valueOf(delimiter))).iterator();
        this.readIndex = 0;
        this.lastStringRead = "";
    }

    public int getReadIndex() {
        return readIndex;
    }

    public void skip() {
        doReadString();
    }

    public String readString() {
        return doReadString();
    }

    private String doReadString() {
        if (parts.hasNext()) {
            readIndex += 1;
            lastStringRead = parts.next();
            return lastStringRead.trim();
        }
        return "";
    }

    private Optional<String> toOptional(final String str) {
        return Optional.ofNullable(str).filter(s -> !s.isEmpty());
    }

    public long readLong() {
        return doReadLong();
    }

    private long doReadLong() {
        return Long.parseLong(doReadString());
    }

    public Optional<Long> readOptionalLong() {
        return doReadOptionalLong();
    }

    private Optional<Long> doReadOptionalLong() {
        return toOptional(doReadString()).map(Long::parseLong);
    }

    public int readInt() {
        return Long.valueOf(doReadLong()).intValue();
    }

    public Optional<Integer> readOptionalInt() {
        return doReadOptionalLong().map(Long::intValue);
    }

    public BigDecimal readBigDecimal() {
        return new BigDecimal(doReadString().replace(COMMA, DOT));
    }

    public LocalDate readYYYYMMDD() {
        return LocalDate.parse(String.valueOf(doReadLong()), DateTimeFormatter.BASIC_ISO_DATE);
    }

    public Optional<LocalDate> readOptionalYYYYMM() {
        return doReadOptionalLong()
                .map(number ->
                        LocalDate.parse(number + BASIC_ISO_DATE_FIRST_DAY_OF_MONTH, DateTimeFormatter.BASIC_ISO_DATE));
    }

    public LocalDate readYYYYMM() {
        return LocalDate.parse(doReadLong() + BASIC_ISO_DATE_FIRST_DAY_OF_MONTH, DateTimeFormatter.BASIC_ISO_DATE);
    }

}
