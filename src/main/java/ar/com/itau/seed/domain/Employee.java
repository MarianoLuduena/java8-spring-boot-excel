package ar.com.itau.seed.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Employee {
    @NotBlank private String referenceId;
    @NotNull private String number;
    @NotBlank @Size(min = 1, max = 20) private String name;
    @NotBlank @Size(min = 1, max = 20) private String surname;
    @NotNull @Pattern(regexp = "^([MF])$") private String sex;
    @NotNull @Past private LocalDate birthDate;
    @NotBlank @Size(min = 3, max = 3) private String countryOfBirth;
    @Size(min = 2, max = 2) private String provinceOfBirth;
    @NotBlank @Size(min = 3, max = 3) private String nationality;
    private Boolean isResident;
    @NotNull private DocumentType documentType;
    @NotNull @Positive @Max(99999999L) private Integer documentNumber;
    @Size(max = 20) private String documentIssuedBy;
    @NotNull private UIDType uidType;
    @NotNull @Positive @Max(99999999999L) private Long uid;
    @NotNull @Valid private Address address;
    @PastOrPresent private LocalDate entryDate;
    @NotNull @PositiveOrZero @Digits(integer = 15, fraction = 2) private BigDecimal netIncome;
    @NotBlank @Size(min = 1, max = 4) private String officeBranch;
    @PastOrPresent private LocalDate residenceDate;
    @NotNull @Positive @Max(9999999999L) private Long mobilePhoneNumber;
    @NotBlank @Email @Size(min = 3, max = 100) private String email;
    @NotBlank @Size(min = 1, max = 10) private String contractType;
    private Boolean hasForeignTaxResidence;
}
