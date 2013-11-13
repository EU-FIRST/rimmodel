package eu.first.RIM;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import eu.first.RIM.Data;

/**
 * DataSource extension that reads Data objects from a delimited text file.
 * 
 * @author Marko Bohanec (marko.bohanec@ijs.si)
 * @version 1.0
 * @since 2012-10-20
 * 
 */
public class DelimitedFileDataSource extends DataSource {

    private BufferedReader file;
    private String fileName;
    private String delimiter;
    private String error;

    /**
     * Constructor from file name and data item delimiter. File must have a
     * strictly defined header: first line: "RIM Data", second line:
     * tab-delimited string exactly matching Data.tabbedInputNames().
     * 
     * @param fileName
     * @param delimiter
     * @throws IOException 
     *             When file header does not correspond to requirements.
     */
    public DelimitedFileDataSource(String fileName, String delimiter) throws IOException
            {

        this.fileName = fileName;
        file = new BufferedReader(new FileReader(fileName));
        this.delimiter = delimiter;
        testFileHead();
    }

    /**
     * Constructor assuming default delimiter "\t".
     * 
     * @param fileName
     * @throws IOException 
     */
    public DelimitedFileDataSource(String fileName) throws IOException {
        this(fileName, "\t");
    }

    /**
     * Tests file head.
     * 
     * @throws IOException
     *             When header is inappropriate.
     */
    private void testFileHead() throws IOException {

        String line = file.readLine();
        if (line == null || !line.equals("RIM Data")) {
            throw new IOException(
                    String.format(
                            "DelimitedFileSource: File %s header is \"%s\", should have been \"%s\"",
                            fileName, line, "RIM Data"));
        }
        line = file.readLine();
        String dataNames = Data.tabbedInputNames();
        if (line == null || !line.equals(dataNames)) {
            throw new IOException(
                    String.format(
                            "DelimitedFileSource: Line 2 of file %s should be:\n\"%s\"",
                            fileName, dataNames));
        }
    }

    /**
     * Tests whether there is another line available for getNext().
     * @throws IOException 
     */
    public boolean hasNext() throws IOException {

        boolean ready = file.ready();
        if (!ready) {
            file.close();
        }
        return ready;
    }

    /**
     * Reads another line from input file and converts it into Data object. In
     * case of conversion error, it does not throw IOException, but rather returns
     * null and stores error message, which can be later retrieved by
     * getLastError().
     * @throws IOException
     */
    public Data getNext() throws IOException {

        String line = file.readLine();
        String[] element = line.split(delimiter);
        error = null;
        try {
            int idx = 0;
            String counterpart = element[idx++];
            String product = element[idx++];
            String client = element[idx++];
            String date = parseDate(element[idx++]);
            double Slp = parseDouble(element[idx++]);
            double Ssp = parseDouble(element[idx++]);
            long N = parseLong(element[idx++]);
            double TA = parseDouble(element[idx++]);
            int SRI = parseInt(element[idx++]);
            long Np = parseLong(element[idx++]);
            long Vp = parseLong(element[idx++]);
            int RP = parseInt(element[idx++]);
            long V1 = parseLong(element[idx++]);
            long Vc = parseLong(element[idx++]);
            double PP = parseDouble(element[idx++]);
            double BP = parseDouble(element[idx++]);
            double dB = parseDouble(element[idx++]);
            double P = parseDouble(element[idx++]);

            return new Data(counterpart, product, client, date, Slp, Ssp, N,
                    TA, SRI, Np, Vp, RP, V1, Vc, PP, BP, dB, P);

        } catch (Exception e) {
            error = e.getMessage();
        }
        return null;
    }

    /**
     * After getNext() has returned null because of data conversion error,
     * getLastError() returns the error message string.
     */
    public String getLastError() {
        return error;
    }

    /**
     * Parse file date string into internal date string. Internal representation
     * should be sortable, that is, the format YYYY-MM-DD or similar is strongly
     * suggested.
     * 
     * @param date
     * @return Internal date string.
     */
    private String parseDate(String date) {
        return date;
    }

    /**
     * Parse file string into double.
     * 
     * @param dbl
     * @return
     */
    private double parseDouble(String dbl) {
        return Double.parseDouble(dbl.replaceAll("[.]", "")
                .replaceAll(",", "."));
    }

    /**
     * Parse file string into long.
     * 
     * @param dbl
     * @return
     */
    private long parseLong(String lng) {
        return Long.parseLong(lng.replaceAll("[.,]", ""));
    }

    /**
     * Parse file string into int.
     * 
     * @param dbl
     * @return
     */
    private int parseInt(String intg) {
        return Integer.parseInt(intg.replaceAll("[.,]", ""));
    }

}
