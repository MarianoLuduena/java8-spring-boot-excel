package ar.com.itau.seed.adapter.db.model;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class EmployeeDbModel {

    private String referenceId;
    private String number;
    private String name;
    private String surname;
    private String sex;
    private LocalDate birthDate;
    private String countryOfBirth;
    private String provinceOfBirth;
    private String nationality;
    private Boolean isResident;
    private String documentType;
    private Integer documentNumber;
    private String documentIssuedBy;
    private String uidType;
    private Long uid;
    private AddressDbModel address;
    private LocalDate entryDate;
    private BigDecimal netIncome;
    private String officeBranch;
    private LocalDate residenceDate;
    private Long mobilePhoneNumber;
    private String email;
    private String contractType;
    private Boolean hasForeignTaxResidence;

}
