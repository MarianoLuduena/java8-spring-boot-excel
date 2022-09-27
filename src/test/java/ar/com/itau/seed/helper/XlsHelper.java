package ar.com.itau.seed.helper;

import lombok.SneakyThrows;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.function.Function;

public class XlsHelper {

    @SneakyThrows
    public <T> T doWithWorkbook(final String filename, final Function<Workbook, T> block) {
        final File file = new File(filename);
        try (final InputStream is = new BufferedInputStream(new FileInputStream(file))) {
            try (final Workbook workbook = new HSSFWorkbook(is, false)) {
                return block.apply(workbook);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
    }

    @SneakyThrows
    public <T> T create(final String filename, final Function<Workbook, T> block) {
        try (final Workbook workbook = new HSSFWorkbook()) {
            final T result = block.apply(workbook);
            try (final OutputStream os = new BufferedOutputStream(new FileOutputStream(filename))) {
                workbook.write(os);
            }
            return result;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
    }

}
