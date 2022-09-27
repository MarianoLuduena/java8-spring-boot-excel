package ar.com.itau.seed.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {
    @NotBlank @Size(min = 1, max = 50) private String street;
    @NotBlank @Size(min = 1, max = 5) private String number;
    @Size(max = 3) private String floor;
    @Size(max = 3) private String apartment;
    @NotBlank @Size(min = 1, max = 4) private String zipCode;
    @PositiveOrZero @Max(9999999L) private Integer landlinePrefix;
    @NotNull @PositiveOrZero @Max(9999999999L) private Long landlineNumber;
}
