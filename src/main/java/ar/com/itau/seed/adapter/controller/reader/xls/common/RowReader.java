package ar.com.itau.seed.adapter.controller.reader.xls.common;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

public interface RowReader {

    int getLastColumnReadIdx();

    void skip();

    String readString();

    Long readLong();

    Optional<Long> readOptionalLong();

    Integer readInt();

    Optional<Integer> readOptionalInt();

    BigDecimal readBigDecimal();

    LocalDate readYYYYMMDD();

    Optional<LocalDate> readOptionalYYYYMMDD();

    Optional<LocalDate> readOptionalYYYYMM();

    LocalDate readYYYYMM();

}
