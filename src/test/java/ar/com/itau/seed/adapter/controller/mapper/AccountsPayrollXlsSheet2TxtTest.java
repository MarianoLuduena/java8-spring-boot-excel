package ar.com.itau.seed.adapter.controller.mapper;

import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.util.List;

class AccountsPayrollXlsSheet2TxtTest {

    @Test
    void xls2Txt() throws IOException {
        final int minColumns = 29;
        final String filename = "src/test/resources/accounts/sample10.xls";
        final File target = File.createTempFile("converted", ".txt");

        try (final PrintStream os = new PrintStream(new BufferedOutputStream(new FileOutputStream(target)))) {
            final AccountsPayrollXlsSheet2Txt xls2tsv =
                    new AccountsPayrollXlsSheet2Txt(
                            new POIFSFileSystem(new FileInputStream(filename)),
                            os,
                            minColumns
                    );
            xls2tsv.process();
            os.flush();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        final List<String> actual = Files.readAllLines(target.toPath());
        final boolean targetFileDeleted = target.delete();
        Assertions.assertThat(actual).hasSize(11);
        Assertions.assertThat(targetFileDeleted).isTrue();
    }

}
