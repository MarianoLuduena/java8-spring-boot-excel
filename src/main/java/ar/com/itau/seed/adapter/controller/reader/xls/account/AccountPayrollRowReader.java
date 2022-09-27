package ar.com.itau.seed.adapter.controller.reader.xls.account;

import ar.com.itau.seed.adapter.controller.mapper.ControllerMapper;
import ar.com.itau.seed.adapter.controller.reader.xls.common.DefaultRowReader;
import ar.com.itau.seed.adapter.controller.reader.xls.common.RowReader;
import ar.com.itau.seed.domain.Address;
import ar.com.itau.seed.domain.Employee;
import ar.com.itau.seed.config.util.either.Either;
import ar.com.itau.seed.config.util.either.Left;
import ar.com.itau.seed.config.util.either.Right;
import ar.com.itau.seed.domain.error.EntityError;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;

import java.util.Collections;

public class AccountPayrollRowReader {

    private static final int FIRST_DATA_COLUMN_IDX = 2; // 3rd column ("C")

    private final ControllerMapper mapper;

    /**
     * COLUMN - HEADER
     * C      - Nombre
     * D      - Apellido
     * E      - Sexo
     * F      - Fecha Nac.
     * G      - País Nac.
     * H      - Prov. Nac.
     * I      - Nacionalid.
     * J      - Reside?
     * K      - Tipo Doc.
     * L      - Nro. Doc.
     * M      - Emitido
     * N      - Tipo CUI
     * O      - Nro. CUI
     * P      - Calle
     * Q      - Nro.
     * R      - Piso
     * S      - Depto.
     * T      - Código Pos.
     * U      - Prefijo Tel.
     * V      - Teléfono
     * W      - Fecha Ingreso
     * X      - Ingreso Neto
     * Y      - Sucursal
     * Z      - Fecha Residencia
     * AA     - Celular
     * AB     - Email
     * AC     - Tipo Contrat.
     * AD     - Residencia Fiscal Extranjera
     *
     * @param mapper used for converting the values from the file to values of the domain layer
     */
    public AccountPayrollRowReader(final ControllerMapper mapper) {
        this.mapper = mapper;
    }

    public Either<EntityError, Employee> read(final Row row) {
        final RowReader rowReader = new DefaultRowReader(row);
        final String rowNumber = String.valueOf(row.getRowNum() + 1);
        try {
            for (int i = 0; i < FIRST_DATA_COLUMN_IDX; i++) {
                // Skip unused columns
                rowReader.skip();
            }
            final Employee employee =
                    Employee.builder()
                            .referenceId(rowNumber)
                            .name(rowReader.readString())
                            .surname(rowReader.readString())
                            .sex(rowReader.readString())
                            .birthDate(rowReader.readYYYYMMDD())
                            .countryOfBirth(rowReader.readString())
                            .provinceOfBirth(StringUtils.substring(rowReader.readString(), 0, 2))
                            .nationality(rowReader.readString())
                            .isResident(rowReader.readInt() == 1)
                            .documentType(mapper.toDocumentType(rowReader.readString()))
                            .documentNumber(rowReader.readInt())
                            .documentIssuedBy(rowReader.readString())
                            .uidType(mapper.toUIDType(rowReader.readString()))
                            .uid(rowReader.readLong())
                            .address(
                                    Address.builder()
                                            .street(rowReader.readString())
                                            .number(rowReader.readOptionalLong().orElse(0L).toString())
                                            .floor(rowReader.readString())
                                            .apartment(rowReader.readString())
                                            .zipCode(rowReader.readLong().toString())
                                            .landlinePrefix(rowReader.readOptionalInt().orElse(null))
                                            .landlineNumber(rowReader.readLong())
                                            .build()
                            )
                            .entryDate(rowReader.readYYYYMM())
                            .netIncome(rowReader.readBigDecimal())
                            .officeBranch(StringUtils.substring(rowReader.readString(), 0, 4))
                            .residenceDate(rowReader.readOptionalYYYYMM().orElse(null))
                            .mobilePhoneNumber(rowReader.readLong())
                            .email(rowReader.readString())
                            .contractType(rowReader.readString())
                            .hasForeignTaxResidence(mapper.toBoolean(rowReader.readString()))
                            .build();

            return Right.of(employee);
        } catch (Exception ex) {
            final String cellId = row.getCell(rowReader.getLastColumnReadIdx()).getAddress().formatAsString();
            return Left.of(
                    new EntityError(
                            rowNumber,
                            Collections.singletonList(EntityError.Error.parsing(cellId, ex.getMessage()))
                    )
            );
        }
    }

}
