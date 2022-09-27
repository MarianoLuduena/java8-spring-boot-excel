package ar.com.itau.seed.adapter.controller.mapper;

import org.apache.poi.hssf.eventusermodel.EventWorkbookBuilder;
import org.apache.poi.hssf.eventusermodel.FormatTrackingHSSFListener;
import org.apache.poi.hssf.eventusermodel.HSSFEventFactory;
import org.apache.poi.hssf.eventusermodel.HSSFListener;
import org.apache.poi.hssf.eventusermodel.HSSFRequest;
import org.apache.poi.hssf.eventusermodel.MissingRecordAwareHSSFListener;
import org.apache.poi.hssf.eventusermodel.dummyrecord.LastCellOfRowDummyRecord;
import org.apache.poi.hssf.eventusermodel.dummyrecord.MissingCellDummyRecord;
import org.apache.poi.hssf.model.HSSFFormulaParser;
import org.apache.poi.hssf.record.BOFRecord;
import org.apache.poi.hssf.record.BlankRecord;
import org.apache.poi.hssf.record.BoolErrRecord;
import org.apache.poi.hssf.record.BoundSheetRecord;
import org.apache.poi.hssf.record.FormulaRecord;
import org.apache.poi.hssf.record.LabelRecord;
import org.apache.poi.hssf.record.LabelSSTRecord;
import org.apache.poi.hssf.record.NoteRecord;
import org.apache.poi.hssf.record.NumberRecord;
import org.apache.poi.hssf.record.RKRecord;
import org.apache.poi.hssf.record.SSTRecord;
import org.apache.poi.hssf.record.StringRecord;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Reference: https://poi.apache.org/components/spreadsheet/how-to.html#event_api
 */
@SuppressWarnings({"java:S106", "java:S4823"})
public class AccountsPayrollXlsSheet2Txt implements HSSFListener, Closeable {

    private static final char DELIMITER = '\t';
    private static final int LINE_BUFFER_CAPACITY = 350;

    private final int minColumns;
    private final POIFSFileSystem fs;
    private final PrintStream output;

    private int lastRowNumber;
    private int lastColumnNumber;

    /**
     * Should we output the formula, or the value it has?
     */
    private final boolean outputFormulaValues = false;

    /**
     * For parsing Formulas
     */
    private EventWorkbookBuilder.SheetRecordCollectingListener workbookBuildingListener;
    private HSSFWorkbook stubWorkbook;

    // Records we pick up as we process
    private SSTRecord sstRecord;
    private FormatTrackingHSSFListener formatListener;

    /**
     * So we known which sheet we're on
     */
    private int sheetIndex = -1;
    private BoundSheetRecord[] orderedBSRs;
    private final List<BoundSheetRecord> boundSheetRecords = new ArrayList<>();

    /**
     * My code here!
     * So we can handle when to start and stop parsing the sheet we are interested in
     */
    private boolean isPayrollSheet = false;
    private StringBuilder stringBuilder = new StringBuilder(LINE_BUFFER_CAPACITY);

    // For handling formulas with string results
    private int nextRow;
    private int nextColumn;
    private boolean outputNextStringRecord;

    /**
     * Creates a new XLS -&gt; CSV converter
     *
     * @param fs         The POIFSFileSystem to process
     * @param output     The PrintStream to output the CSV to
     * @param minColumns The minimum number of columns to output, or -1 for no minimum
     */
    public AccountsPayrollXlsSheet2Txt(POIFSFileSystem fs, PrintStream output, int minColumns) {
        this.fs = fs;
        this.output = output;
        this.minColumns = minColumns;
    }

    public AccountsPayrollXlsSheet2Txt(File file, PrintStream output, int minColumns) throws IOException {
        this(
                new POIFSFileSystem(file, true),
                output,
                minColumns
        );
    }

    /**
     * Initiates the processing of the XLS file to CSV
     *
     * @throws IOException if the workbook contained errors
     */
    public void process() throws IOException {
        MissingRecordAwareHSSFListener listener = new MissingRecordAwareHSSFListener(this);
        formatListener = new FormatTrackingHSSFListener(listener);

        HSSFEventFactory factory = new HSSFEventFactory();
        HSSFRequest request = new HSSFRequest();

        if (outputFormulaValues) {
            request.addListenerForAllRecords(formatListener);
        } else {
            workbookBuildingListener = new EventWorkbookBuilder.SheetRecordCollectingListener(formatListener);
            request.addListenerForAllRecords(workbookBuildingListener);
        }

        factory.processWorkbookEvents(request, fs);
    }

    /**
     * Main HSSFListener method, processes events, and outputs the
     * CSV as the file is processed.
     */
    @Override
    public void processRecord(org.apache.poi.hssf.record.Record record) {
        int thisRow = -1;
        int thisColumn = -1;
        String thisStr = null;

        switch (record.getSid()) {
            case BoundSheetRecord.sid:
                boundSheetRecords.add((BoundSheetRecord) record);
                break;
            case BOFRecord.sid:
                BOFRecord br = (BOFRecord) record;
                if (br.getType() == BOFRecord.TYPE_WORKSHEET) {
                    // Create sub workbook if required
                    if (workbookBuildingListener != null && stubWorkbook == null) {
                        stubWorkbook = workbookBuildingListener.getStubHSSFWorkbook();
                    }

                    // Output the worksheet name
                    // Works by ordering the BSRs by the location of
                    //  their BOFRecords, and then knowing that we
                    //  process BOFRecords in byte offset order
                    sheetIndex++;
                    if (orderedBSRs == null) {
                        orderedBSRs = BoundSheetRecord.orderByBofPosition(boundSheetRecords);
                    }

                    // Only process
                    isPayrollSheet = orderedBSRs[sheetIndex].getSheetname().startsWith("Nomina");
                }
                break;

            case SSTRecord.sid:
                sstRecord = (SSTRecord) record;
                break;

            case BlankRecord.sid:
                if (!isPayrollSheet) return;
                BlankRecord brec = (BlankRecord) record;

                thisRow = brec.getRow();
                thisColumn = brec.getColumn();
                thisStr = "";
                break;
            case BoolErrRecord.sid:
                if (!isPayrollSheet) return;
                BoolErrRecord berec = (BoolErrRecord) record;

                thisRow = berec.getRow();
                thisColumn = berec.getColumn();
                thisStr = "";
                break;

            case FormulaRecord.sid:
                if (!isPayrollSheet) return;
                FormulaRecord frec = (FormulaRecord) record;

                thisRow = frec.getRow();
                thisColumn = frec.getColumn();

                if (outputFormulaValues) {
                    if (Double.isNaN(frec.getValue())) {
                        // Formula result is a string
                        // This is stored in the next record
                        outputNextStringRecord = true;
                        nextRow = frec.getRow();
                        nextColumn = frec.getColumn();
                    } else {
                        thisStr = formatListener.formatNumberDateCell(frec);
                    }
                } else {
                    thisStr = '"' +
                            HSSFFormulaParser.toFormulaString(stubWorkbook, frec.getParsedExpression()) + '"';
                }
                break;
            case StringRecord.sid:
                if (outputNextStringRecord) {
                    // String for formula
                    StringRecord srec = (StringRecord) record;
                    thisStr = srec.getString();
                    thisRow = nextRow;
                    thisColumn = nextColumn;
                    outputNextStringRecord = false;
                }
                break;

            case LabelRecord.sid:
                if (!isPayrollSheet) return;
                LabelRecord lrec = (LabelRecord) record;

                thisRow = lrec.getRow();
                thisColumn = lrec.getColumn();
                thisStr = lrec.getValue();
                break;
            case LabelSSTRecord.sid:
                if (!isPayrollSheet) return;
                LabelSSTRecord lsrec = (LabelSSTRecord) record;

                thisRow = lsrec.getRow();
                thisColumn = lsrec.getColumn();
                if (sstRecord == null) {
                    thisStr = '"' + "(No SST Record, can't identify string)" + '"';
                } else {
                    thisStr = sstRecord.getString(lsrec.getSSTIndex()).toString();
                }
                break;
            case NoteRecord.sid:
                if (!isPayrollSheet) return;
                NoteRecord nrec = (NoteRecord) record;

                thisRow = nrec.getRow();
                thisColumn = nrec.getColumn();
                // TODO: Find object to match nrec.getShapeId()
                thisStr = '"' + "(TODO)" + '"';
                break;
            case NumberRecord.sid:
                if (!isPayrollSheet) return;
                NumberRecord numrec = (NumberRecord) record;

                thisRow = numrec.getRow();
                thisColumn = numrec.getColumn();

                // Format
                thisStr = formatListener.formatNumberDateCell(numrec);
                break;
            case RKRecord.sid:
                if (!isPayrollSheet) return;
                RKRecord rkrec = (RKRecord) record;

                thisRow = rkrec.getRow();
                thisColumn = rkrec.getColumn();
                thisStr = '"' + "(TODO)" + '"';
                break;
            default:
                break;
        }

        // Handle new row
        if (thisRow != -1 && thisRow != lastRowNumber) {
            lastColumnNumber = -1;
        }

        // Handle missing column
        if (record instanceof MissingCellDummyRecord) {
            MissingCellDummyRecord mc = (MissingCellDummyRecord) record;
            thisRow = mc.getRow();
            thisColumn = mc.getColumn();
            thisStr = "";
        }

        // If we got something to print out, do so
        if (thisStr != null) {
            if (thisColumn > 0) {
                stringBuilder.append(DELIMITER);
            }
            stringBuilder.append(thisStr);
        }

        // Update column and row count
        if (thisRow > -1)
            lastRowNumber = thisRow;
        if (thisColumn > -1)
            lastColumnNumber = thisColumn;

        // Handle end of row
        if (record instanceof LastCellOfRowDummyRecord) {
            // Print out any missing commas if needed
            if (minColumns > 0) {
                // Columns are 0 based
                if (lastColumnNumber == -1) {
                    lastColumnNumber = 0;
                }
                for (int i = lastColumnNumber; i < (minColumns); i++) {
                    stringBuilder.append(DELIMITER);
                }
            }

            // We're onto a new row
            lastColumnNumber = -1;

            // End the row
            // Generated line from row, skipping the first column
            final String lineToPrint = stringBuilder.toString();

            // Reset the buffer
            stringBuilder = new StringBuilder(LINE_BUFFER_CAPACITY);

            // Skip line if not a valid payroll
            if (lineOnlyContainsDelimiters(lineToPrint)) {
                return;
            }

            final String rowWithRowNumber = String.valueOf(lastRowNumber + 1) + DELIMITER + lineToPrint;
            output.println(rowWithRowNumber);
        }
    }

    private boolean lineOnlyContainsDelimiters(final String line) {
        for (int i = 0; i < line.length(); i++) {
            if (!(line.charAt(i) == DELIMITER)) return false;
        }
        return true;
    }

    @Override
    public void close() throws IOException {
        this.fs.close();
    }

}
