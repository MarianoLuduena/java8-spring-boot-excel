package ar.com.itau.seed.adapter.rest.mapper;

import ar.com.itau.seed.config.ErrorCode;
import ar.com.itau.seed.config.exception.NotFoundException;
import ar.com.itau.seed.domain.AccountsBatch;
import ar.com.itau.seed.domain.Address;
import ar.com.itau.seed.domain.DocumentType;
import ar.com.itau.seed.domain.Employee;
import ar.com.itau.seed.domain.UIDType;
import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class FileMapper {

    private static final String HEADER_RECORD_TYPE = "H";
    private static final String PAYROLL_RECORD_TYPE = "NA";
    private static final String TRAILER_RECORD_TYPE = "T";
    private static final char NUMBER_PAD_CHAR = '0';
    private static final char STRING_PAD_CHAR = ' ';
    private static final DateTimeFormatter BASIC_ISO_FORMATTER = DateTimeFormatter.BASIC_ISO_DATE;
    private static final DateTimeFormatter YEAR_MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyyMM");
    private static final String NUM_BOOLEAN_YES = "1";
    private static final String NUM_BOOLEAN_NO = "0";
    private static final String STR_BOOLEAN_YES = "S";
    private static final String STR_BOOLEAN_NO = "N";
    private static final int BASE64_LINE_SIZE_IN_BYTES = 320 * 4 / 3; // Number of characters times 4/3

    private static final Map<DocumentType, String> CODE_BY_DOCUMENT_TYPE;
    private static final Map<UIDType, String> CODE_BY_UID_TYPE;

    static {
        final Map<DocumentType, String> codeByDocType = new EnumMap<>(DocumentType.class);
        codeByDocType.put(DocumentType.LE, "1");
        codeByDocType.put(DocumentType.LC, "2");
        codeByDocType.put(DocumentType.DNI, "3");
        codeByDocType.put(DocumentType.CI, "4");
        codeByDocType.put(DocumentType.PASSPORT, "5");
        codeByDocType.put(DocumentType.CD, "6");
        CODE_BY_DOCUMENT_TYPE = Collections.unmodifiableMap(codeByDocType);

        final Map<UIDType, String> codeByUidType = new EnumMap<>(UIDType.class);
        codeByUidType.put(UIDType.CUIT, "1");
        codeByUidType.put(UIDType.CUIL, "2");
        codeByUidType.put(UIDType.CIE, "3");
        CODE_BY_UID_TYPE = Collections.unmodifiableMap(codeByUidType);
    }

    public FileMapper() {
        // Do nothing
    }

    public String toBase64(final AccountsBatch accountsBatch) {
        final int size =
                (accountsBatch.employeeCount() + 2) * BASE64_LINE_SIZE_IN_BYTES; // Number of records + header + trailer
        final ByteArrayOutputStream os = new ByteArrayOutputStream(size);
        try (final PrintWriter pw = new PrintWriter(os)) {
            pw.println(header());
            accountsBatch.getEmployees().forEach(employee -> pw.println(toFixedWith(employee)));
            pw.println(trailer());
            pw.flush();
            return Base64.getEncoder().encodeToString(os.toByteArray());
        }
    }

    private String header() {
        return HEADER_RECORD_TYPE;
    }

    private String trailer() {
        return TRAILER_RECORD_TYPE;
    }

    private String toFixedWith(final Employee employee) {
        final Address address = employee.getAddress();
        return String.join(
                "",
                PAYROLL_RECORD_TYPE,
                rightPadStr(employee.getName(), 20),
                rightPadStr(employee.getSurname(), 20),
                employee.getSex(),
                employee.getBirthDate().format(BASIC_ISO_FORMATTER),
                employee.getCountryOfBirth(),
                leftPadNum(employee.getProvinceOfBirth(), 2),
                employee.getNationality(),
                toNumericBoolean(employee.getIsResident()),
                fromDocumentType(employee.getDocumentType()),
                leftPadNum(String.valueOf(employee.getDocumentNumber()), 8),
                rightPadStr(employee.getDocumentIssuedBy(), 20),
                fromUidType(employee.getUidType()),
                leftPadNum(String.valueOf(employee.getUid()), 11),
                rightPadStr(address.getStreet(), 30),
                leftPadNum(address.getNumber(), 5),
                rightPadStr(StringUtils.defaultString(address.getFloor()), 3),
                rightPadStr(StringUtils.defaultString(address.getApartment()), 3),
                leftPadNum(address.getZipCode(), 4),
                leftPadNum(String.valueOf(address.getLandlinePrefix()), 7),
                leftPadNum(String.valueOf(address.getLandlineNumber()), 10),
                employee.getEntryDate().format(YEAR_MONTH_FORMATTER),
                formatDecimal(employee.getNetIncome()),
                leftPadNum(employee.getOfficeBranch(), 4),
                leftPadNum(Optional.ofNullable(employee.getResidenceDate())
                        .map(date -> date.format(YEAR_MONTH_FORMATTER)).orElse(""), 6),
                leftPadNum(String.valueOf(employee.getMobilePhoneNumber()), 10),
                rightPadStr(employee.getEmail(), 100),
                rightPadStr(employee.getContractType(), 10),
                toStringBoolean(employee.getHasForeignTaxResidence())
        );
    }

    private String toNumericBoolean(final Boolean bool) {
        return Objects.nonNull(bool) && bool ? NUM_BOOLEAN_YES : NUM_BOOLEAN_NO;
    }

    private String toStringBoolean(final Boolean bool) {
        return Objects.nonNull(bool) && bool ? STR_BOOLEAN_YES : STR_BOOLEAN_NO;
    }

    private String fromDocumentType(final DocumentType documentType) {
        return Optional.ofNullable(CODE_BY_DOCUMENT_TYPE.get(documentType))
                .orElseThrow(() -> new NotFoundException(ErrorCode.INTERNAL_ERROR));
    }

    private String fromUidType(final UIDType uidType) {
        return Optional.ofNullable(CODE_BY_UID_TYPE.get(uidType))
                .orElseThrow(() -> new NotFoundException(ErrorCode.INTERNAL_ERROR));
    }

    private String formatDecimal(final BigDecimal number) {
        final int fraction =
                number.remainder(BigDecimal.ONE)
                        .movePointRight(number.scale())
                        .abs()
                        .stripTrailingZeros()
                        .intValue();

        return StringUtils.leftPad(String.valueOf(number.longValueExact()), 15, NUMBER_PAD_CHAR)
                + StringUtils.rightPad(String.valueOf(fraction), 2, NUMBER_PAD_CHAR);
    }

    private String leftPadNum(final String str, int size) {
        return StringUtils.leftPad(StringUtils.defaultString(str), size, NUMBER_PAD_CHAR);
    }

    private String rightPadStr(final String str, int size) {
        return StringUtils.rightPad(StringUtils.defaultString(str), size, STRING_PAD_CHAR);
    }

}
