package ar.com.itau.seed.adapter.db.mapper;

import ar.com.itau.seed.adapter.db.model.AccountsBatchDbModel;
import ar.com.itau.seed.adapter.db.model.AddressDbModel;
import ar.com.itau.seed.adapter.db.model.EmployeeDbModel;
import ar.com.itau.seed.config.ErrorCode;
import ar.com.itau.seed.config.exception.NotFoundException;
import ar.com.itau.seed.domain.AccountsBatch;
import ar.com.itau.seed.domain.DocumentType;
import ar.com.itau.seed.domain.Employee;
import ar.com.itau.seed.domain.UIDType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.smile.databind.SmileMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Slf4j
public class DbMapper {

    private static final String LE_DOCUMENT_TYPE = "LE";
    private static final String LC_DOCUMENT_TYPE = "LC";
    private static final String DNI_DOCUMENT_TYPE = "DNI";
    private static final String CI_DOCUMENT_TYPE = "CI";
    private static final String PASSPORT_DOCUMENT_TYPE = "PASSPORT";
    private static final String CD_DOCUMENT_TYPE = "CD";

    private static final String CUIT_UID_TYPE = "CUIT";
    private static final String CUIL_UID_TYPE = "CUIL";
    private static final String CIE_UID_TYPE = "CIE";

    private static LocalDateTime now() {
        return ZonedDateTime.now().withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime();
    }

    private static final Map<DocumentType, String> DB_MODEL_BY_DOCUMENT_TYPE =
            Optional.of(new EnumMap<DocumentType, String>(DocumentType.class))
                    .map(em -> {
                        em.put(DocumentType.LE, LE_DOCUMENT_TYPE);
                        em.put(DocumentType.LC, LC_DOCUMENT_TYPE);
                        em.put(DocumentType.DNI, DNI_DOCUMENT_TYPE);
                        em.put(DocumentType.CI, CI_DOCUMENT_TYPE);
                        em.put(DocumentType.PASSPORT, PASSPORT_DOCUMENT_TYPE);
                        em.put(DocumentType.CD, CD_DOCUMENT_TYPE);
                        return Collections.unmodifiableMap(em);
                    })
                    .get();

    private static final Map<String, DocumentType> DOCUMENT_TYPE_BY_DB_MODEL =
            Optional.of(new HashMap<String, DocumentType>())
                    .map(m -> {
                        m.put(LE_DOCUMENT_TYPE, DocumentType.LE);
                        m.put(LC_DOCUMENT_TYPE, DocumentType.LC);
                        m.put(DNI_DOCUMENT_TYPE, DocumentType.DNI);
                        m.put(CI_DOCUMENT_TYPE, DocumentType.CI);
                        m.put(PASSPORT_DOCUMENT_TYPE, DocumentType.PASSPORT);
                        m.put(CD_DOCUMENT_TYPE, DocumentType.CD);
                        return Collections.unmodifiableMap(m);
                    })
                    .get();

    private static final Map<UIDType, String> DB_MODEL_BY_UID_TYPE =
            Optional.of(new EnumMap<UIDType, String>(UIDType.class))
                    .map(em -> {
                        em.put(UIDType.CUIT, CUIT_UID_TYPE);
                        em.put(UIDType.CUIL, CUIL_UID_TYPE);
                        em.put(UIDType.CIE, CIE_UID_TYPE);
                        return Collections.unmodifiableMap(em);
                    })
                    .get();

    private static final Map<String, UIDType> UID_TYPE_BY_DB_MODEL =
            Optional.of(new HashMap<String, UIDType>())
                    .map(m -> {
                        m.put(CUIT_UID_TYPE, UIDType.CUIT);
                        m.put(CUIL_UID_TYPE, UIDType.CUIL);
                        m.put(CIE_UID_TYPE, UIDType.CIE);
                        return Collections.unmodifiableMap(m);
                    })
                    .get();

    private final ObjectMapper objectMapper =
            SmileMapper.builder()
                    .addModule(new JavaTimeModule())
                    .build();

    private String documentTypeToDbModel(final DocumentType documentType) {
        return DB_MODEL_BY_DOCUMENT_TYPE.get(documentType);
    }

    private DocumentType fromDbModelToDocumentType(final String documentType) {
        return Optional.ofNullable(DOCUMENT_TYPE_BY_DB_MODEL.get(documentType))
                .orElseThrow(() -> new NotFoundException(ErrorCode.INTERNAL_ERROR));
    }

    private String uidTypeToDbModel(final UIDType uidType) {
        return DB_MODEL_BY_UID_TYPE.get(uidType);
    }

    private UIDType fromDbModelToUidType(final String uidType) {
        return Optional.ofNullable(UID_TYPE_BY_DB_MODEL.get(uidType))
                .orElseThrow(() -> new NotFoundException(ErrorCode.INTERNAL_ERROR));
    }

    @SneakyThrows
    public AccountsBatchDbModel fromAccountsCreationBatch(final AccountsBatch cmd) {
        final LocalDateTime currentTimestamp = now();

        final AccountsBatchDbModel model = new AccountsBatchDbModel();
        model.setSourceName(cmd.getSourceName());

        final long startTime = System.currentTimeMillis();
        model.setContent(
                objectMapper.writeValueAsBytes(
                        cmd.getEmployees().stream()
                                .map(this::fromEmployee)
                                .collect(Collectors.toList())
                )
        );
        log.info("Serializing employees took {} ms", System.currentTimeMillis() - startTime);

        model.setCreatedAt(currentTimestamp);
        model.setUpdatedAt(currentTimestamp);
        return model;
    }

    private EmployeeDbModel fromEmployee(final Employee domain) {
        final EmployeeDbModel model = new EmployeeDbModel();
        model.setReferenceId(domain.getReferenceId());
        model.setNumber(domain.getNumber());
        model.setName(domain.getName());
        model.setSurname(domain.getSurname());
        model.setSex(domain.getSex());
        model.setBirthDate(domain.getBirthDate());
        model.setCountryOfBirth(domain.getCountryOfBirth());
        model.setProvinceOfBirth(domain.getProvinceOfBirth());
        model.setNationality(domain.getNationality());
        model.setIsResident(domain.getIsResident());
        model.setDocumentType(documentTypeToDbModel(domain.getDocumentType()));
        model.setDocumentNumber(domain.getDocumentNumber());
        model.setDocumentIssuedBy(domain.getDocumentIssuedBy());
        model.setUidType(uidTypeToDbModel(domain.getUidType()));
        model.setUid(domain.getUid());
        model.setAddress(AddressDbModel.from(domain.getAddress()));
        model.setEntryDate(domain.getEntryDate());
        model.setNetIncome(domain.getNetIncome());
        model.setOfficeBranch(domain.getOfficeBranch());
        model.setResidenceDate(domain.getResidenceDate());
        model.setMobilePhoneNumber(domain.getMobilePhoneNumber());
        model.setEmail(domain.getEmail());
        model.setContractType(domain.getContractType());
        model.setHasForeignTaxResidence(domain.getHasForeignTaxResidence());
        return model;
    }

    private Employee toEmployee(EmployeeDbModel model) {
        return Employee.builder()
                .referenceId(model.getReferenceId())
                .number(model.getNumber())
                .name(model.getName())
                .surname(model.getSurname())
                .sex(model.getSex())
                .birthDate(model.getBirthDate())
                .countryOfBirth(model.getCountryOfBirth())
                .provinceOfBirth(model.getProvinceOfBirth())
                .nationality(model.getNationality())
                .isResident(model.getIsResident())
                .documentType(fromDbModelToDocumentType(model.getDocumentType()))
                .documentNumber(model.getDocumentNumber())
                .documentIssuedBy(model.getDocumentIssuedBy())
                .uidType(fromDbModelToUidType(model.getUidType()))
                .uid(model.getUid())
                .address(model.getAddress().toDomain())
                .entryDate(model.getEntryDate())
                .netIncome(model.getNetIncome())
                .officeBranch(model.getOfficeBranch())
                .residenceDate(model.getResidenceDate())
                .mobilePhoneNumber(model.getMobilePhoneNumber())
                .email(model.getEmail())
                .contractType(model.getContractType())
                .hasForeignTaxResidence(model.getHasForeignTaxResidence())
                .build();
    }

}
