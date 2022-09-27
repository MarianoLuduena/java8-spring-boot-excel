package ar.com.itau.seed.adapter.controller.mapper;

import ar.com.itau.seed.adapter.controller.reader.csv.CsvReader;
import ar.com.itau.seed.config.ErrorCode;
import ar.com.itau.seed.config.exception.UnknownValueException;
import ar.com.itau.seed.config.util.either.Either;
import ar.com.itau.seed.config.util.either.Left;
import ar.com.itau.seed.config.util.either.Right;
import ar.com.itau.seed.domain.Address;
import ar.com.itau.seed.domain.DocumentType;
import ar.com.itau.seed.domain.Employee;
import ar.com.itau.seed.domain.UIDType;
import ar.com.itau.seed.domain.error.EntityError;
import ar.com.itau.seed.domain.error.ErrorType;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class ControllerMapper {

    private static final String YES_STRING_VALUE = "S";

    private static final Map<String, DocumentType> DOCUMENT_TYPE_BY_CONTROLLER_VALUE =
            Optional.of(new HashMap<String, DocumentType>())
                    .map(hm -> {
                        hm.put("LE", DocumentType.LE);
                        hm.put("1", DocumentType.LE);
                        hm.put("LC", DocumentType.LC);
                        hm.put("2", DocumentType.LC);
                        hm.put("DNI", DocumentType.DNI);
                        hm.put("3", DocumentType.DNI);
                        hm.put("CI", DocumentType.CI);
                        hm.put("4", DocumentType.CI);
                        hm.put("PASSPORT", DocumentType.PASSPORT);
                        hm.put("5", DocumentType.PASSPORT);
                        hm.put("CD", DocumentType.CD);
                        hm.put("6", DocumentType.CD);
                        return Collections.unmodifiableMap(hm);
                    })
                    .get();

    private static final Map<String, UIDType> UNIQUE_ID_TYPE_BY_CONTROLLER_VALUE =
            Optional.of(new HashMap<String, UIDType>())
                    .map(hm -> {
                        hm.put("CUIT", UIDType.CUIT);
                        hm.put("1", UIDType.CUIT);
                        hm.put("CUIL", UIDType.CUIL);
                        hm.put("2", UIDType.CUIL);
                        hm.put("CIE", UIDType.CIE);
                        hm.put("3", UIDType.CIE);
                        return Collections.unmodifiableMap(hm);
                    })
                    .get();

    private static final Map<ErrorType, String> CONTROLLER_VALUE_BY_ERROR_TYPE =
            Optional.of(new EnumMap<ErrorType, String>(ErrorType.class))
                    .map(em -> {
                        em.put(ErrorType.PARSING, "PARSING");
                        em.put(ErrorType.VALIDATION, "VALIDATION");
                        em.put(ErrorType.BUSINESS, "BUSINESS");
                        return Collections.unmodifiableMap(em);
                    })
                    .get();

    public ControllerMapper() {
        // Do nothing
    }

    public DocumentType toDocumentType(final String value) {
        return Optional.ofNullable(DOCUMENT_TYPE_BY_CONTROLLER_VALUE.get(value))
                .orElseThrow(() -> new UnknownValueException(ErrorCode.DOCUMENT_TYPE_NOT_FOUND));
    }

    public UIDType toUIDType(final String value) {
        return Optional.ofNullable(UNIQUE_ID_TYPE_BY_CONTROLLER_VALUE.get(value))
                .orElseThrow(() -> new UnknownValueException(ErrorCode.UNIQUE_ID_TYPE_NOT_FOUND));
    }

    public Boolean toBoolean(final String value) {
        return YES_STRING_VALUE.equals(value);
    }

    public String fromErrorType(final ErrorType errorType) {
        return CONTROLLER_VALUE_BY_ERROR_TYPE.get(errorType);
    }

    public Stream<Either<EntityError, Employee>> employeeFromPath(final Path path) throws IOException {
        return Files.lines(path)
                .map(this::employeeFromLine)
                .filter(Optional::isPresent)
                .map(Optional::get);
    }

    private Optional<Either<EntityError, Employee>> employeeFromLine(final String line) {
        final CsvReader csvReader = CsvReader.tsv(line);
        long lineId = -1L;
        try {
            lineId = csvReader.readLong();

            // Data begins from line 4 onwards
            if (lineId < 4L) {
                return Optional.empty();
            }

            csvReader.skip(); // Empty column
            final String number = csvReader.readString().toUpperCase();
            final String name = csvReader.readString().toUpperCase();
            final String surname = csvReader.readString().toUpperCase();

            // No need to keep parsing this line
            if (name.isEmpty() && surname.isEmpty()) {
                return Optional.empty();
            }

            final Employee employee =
                    Employee.builder()
                            .referenceId(Long.toString(lineId))
                            .number(number)
                            .name(name)
                            .surname(surname)
                            .sex(csvReader.readString().toUpperCase())
                            .birthDate(csvReader.readYYYYMMDD())
                            .countryOfBirth(csvReader.readString().toUpperCase())
                            .provinceOfBirth(StringUtils.substring(csvReader.readString().toUpperCase(), 0, 2))
                            .nationality(csvReader.readString().toUpperCase())
                            .isResident(csvReader.readInt() == 1)
                            .documentType(toDocumentType(csvReader.readString().toUpperCase()))
                            .documentNumber(csvReader.readInt())
                            .documentIssuedBy(csvReader.readString().toUpperCase())
                            .uidType(toUIDType(csvReader.readString().toUpperCase()))
                            .uid(csvReader.readLong())
                            .address(
                                    Address.builder()
                                            .street(csvReader.readString().toUpperCase())
                                            .number(csvReader.readOptionalLong().orElse(0L).toString())
                                            .floor(csvReader.readString().toUpperCase())
                                            .apartment(csvReader.readString().toUpperCase())
                                            .zipCode(String.valueOf(csvReader.readLong()))
                                            .landlinePrefix(csvReader.readOptionalInt().orElse(null))
                                            .landlineNumber(csvReader.readLong())
                                            .build()
                            )
                            .entryDate(csvReader.readYYYYMM())
                            .netIncome(csvReader.readBigDecimal())
                            .officeBranch(StringUtils.substring(csvReader.readString().toUpperCase(), 0, 4))
                            .residenceDate(csvReader.readOptionalYYYYMM().orElse(null))
                            .mobilePhoneNumber(csvReader.readLong())
                            .email(csvReader.readString().toUpperCase())
                            .contractType(csvReader.readString().toUpperCase())
                            .hasForeignTaxResidence(toBoolean(csvReader.readString().toUpperCase()))
                            .build();

            return Optional.of(Right.of(employee));
        } catch (Exception ex) {
            // TODO: Decouple fieldId definition (we should not know it comes from an Excel sheet!)
            final String fieldId = toExcelColumnName(csvReader.getReadIndex()) + lineId;
            return Optional.of(
                    Left.of(
                            new EntityError(
                                    Long.toString(lineId),
                                    Collections.singletonList(EntityError.Error.parsing(fieldId, ex.getMessage()))
                            )
                    )
            );
        }
    }

    private String toExcelColumnName(int columnNumber) {
        final StringBuilder columnName = new StringBuilder();

        while (columnNumber > 0) {
            final int modulo = (columnNumber - 1) % 26;
            columnName.insert(0, 'A' + modulo);
            columnNumber = (columnNumber - modulo) / 26;
        }

        return columnName.toString();
    }

}
