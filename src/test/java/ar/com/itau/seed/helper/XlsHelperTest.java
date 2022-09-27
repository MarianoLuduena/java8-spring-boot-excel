package ar.com.itau.seed.helper;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

class XlsHelperTest {

    private static final XlsHelper HELPER = new XlsHelper();
    private static final String FILE = "src/test/resources/accounts/sample10.xls";

    @Test
    @Disabled
    void generateTestFile() {
        HELPER.create(FILE, workbook -> {
            workbook.createSheet("Instrucciones");
            workbook.createSheet("Carta Nro. 1");
            workbook.createSheet("DatosGenerales");
            final Sheet payroll = workbook.createSheet("Nomina Nro. 1");
            workbook.createSheet("Referencias");

            final List<String> headers = Arrays.asList("Nombre", "Apellido", "Sexo", "Fecha Nac.", "País Nac.", "Prov. Nac.", "Nacionalid.",
                    "Reside?", "Tipo Doc.", "Nro. Doc.", "Emitido", "Tipo CUI", "Nro. CUI", "Calle", "Nro.", "Piso",
                    "Depto.", "Código Pos.", "Prefijo Tel.", "Teléfono", "Fecha Ingreso", "Ingreso Neto", "Sucursal",
                    "Fecha Residencia", "Celular", "Email", "Tipo Contrat.", "Residencia Fiscal Extranjera");

            final Row headerRow = payroll.createRow(2);
            for (int idx = 0; idx < headers.size(); idx++) {
                final String header = headers.get(idx);
                headerRow.createCell(idx + 2, CellType.STRING).setCellValue(header);
            }

            for (int idx = 3; idx < 1003; idx++) {
                final Row row = payroll.createRow(idx);

                row.createCell(2, CellType.STRING).setCellValue("T" + idx);
                row.createCell(3, CellType.STRING).setCellValue("TERMINATOR");
                row.createCell(4, CellType.STRING).setCellValue("M");
                row.createCell(5, CellType.NUMERIC).setCellValue(19850116D);
                row.createCell(6, CellType.STRING).setCellValue("ARG");
                row.createCell(7, CellType.STRING).setCellValue("02 - BUENOS AIRES");
                row.createCell(8, CellType.STRING).setCellValue("ARG");
                row.createCell(9, CellType.STRING).setCellValue("1");
                row.createCell(10, CellType.STRING).setCellValue("3");
                row.createCell(11, CellType.NUMERIC).setCellValue(idx);
                row.createCell(12, CellType.STRING).setCellValue("RNP");
                row.createCell(13, CellType.STRING).setCellValue("2");
                row.createCell(14, CellType.NUMERIC).setCellValue(Double.parseDouble("30" + idx + "2"));
                row.createCell(15, CellType.STRING).setCellValue("JOAQUÍN V GONZÁLEZ");
                row.createCell(16, CellType.STRING).setCellValue("1212");
                row.createCell(17, CellType.BLANK);
                row.createCell(18, CellType.BLANK);
                row.createCell(19, CellType.STRING).setCellValue("1407");
                row.createCell(20, CellType.NUMERIC).setCellValue(11D);
                row.createCell(21, CellType.NUMERIC).setCellValue(55775379D);
                row.createCell(22, CellType.STRING).setCellValue("202208");
                row.createCell(23, CellType.NUMERIC).setCellValue(50000D);
                row.createCell(24, CellType.STRING).setCellValue("0061 - CENTRO");
                row.createCell(25, CellType.BLANK);
                row.createCell(26, CellType.NUMERIC).setCellValue(1155775379D);
                row.createCell(27, CellType.STRING).setCellValue("T" + idx + "@fake.com.ar");
                row.createCell(28, CellType.STRING).setCellValue("EFECTIVO");
                row.createCell(29, CellType.STRING).setCellValue("N");
            }
            return null;
        });
        Assertions.assertThat(true).isTrue();
    }

    @Test
    @Disabled
    void read() {
        final String rowDefinition = HELPER.doWithWorkbook("/private/tmp/test.xls", workbook -> {
            final Sheet payroll = workbook.getSheetAt(3);
            final Row firstRow = payroll.getRow(3);
            final StringBuilder sb = new StringBuilder();
            for (int column = 2; column < 30; column++) {
                final Cell cell = firstRow.getCell(column);
                sb.append(StringUtils.rightPad(String.valueOf(cell.getColumnIndex()), 2)).append(": ");
                switch (cell.getCellType()) {
                    case STRING:
                        sb.append(cell.getStringCellValue());
                        break;
                    case NUMERIC:
                        sb.append(cell.getNumericCellValue());
                        break;
                    default:
                        break;
                }
                sb.append(" (").append(cell.getCellType().name()).append(")\n");
            }
            return sb.toString();
        });

        System.out.println(rowDefinition);
        Assertions.assertThat(rowDefinition).isNotBlank();
    }

}
