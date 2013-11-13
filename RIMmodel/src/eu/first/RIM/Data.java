package eu.first.RIM;

/**
 * Data class. Stores data manipulated by Model class. Data corresponds to a
 * single product/customer pair. Data includes both input data, which represent
 * value arguments for a single run of Model, and output results, obtained by
 * Model evaluation.
 * 
 * @author Marko Bohanec (marko.bohanec@ijs.si)
 * @version 1.0
 * @since 2012-10-20
 * 
 */
public class Data {

    // <== INPUT DATA

    // IDs

    /**
     * Counterpart name. Input. ID.
     */
    public String counterpart;

    /**
     * Product name. Input. ID.
     */
    public String product;

    /**
     * Client name. Input. ID.
     */
    public String client;

    /**
     * Date. Input. ID.
     */
    public String date;

    // SENTIMENT

    /**
     * Sentiment polarity, long term. Input. Sentiment data. Affects sentiment.
     */
    public double Slp;

    /**
     * Sentiment polarity, short term. Input. Sentiment data. Affects sentiment.
     */
    public double Ssp;

    // BANK DATA

    /**
     * Total number of customers. Input. Bank data. Affects product volumes.
     */
    public long TN;

    /**
     * Total assets. Input. Bank data. Affects product volumes.
     */
    public double TA;

    // PRODUCT DATA

    /**
     * SRI. Input. Product data. Affects mismatching.
     */
    public int SRI;

    /**
     * Number of clients holding product P. Input. Product data. Affects product
     * volumes.
     */
    public long Np;

    /**
     * Volumes of product P. Input. Product data. Affects product volumes.
     */
    public long Vp;

    // CUSTOMER DATA

    /**
     * Risk profile. Input. Customer data. Affects mismatching.
     */
    public int RP;

    /**
     * Volume of products per customer. Input. Customer data. Affects customer
     * volumes.
     */
    public long V1;

    /**
     * Total customer assets. Input. Customer data. Affects customer volumes.
     */
    public long Vc;

    /**
     * Product performance. Input. Customer data. Affects performance. Already
     * taken into account in this.P.
     */
    public double PP;

    /**
     * Benchmark performance. Input. Customer data. Affects performance. Already
     * taken into account in this.P.
     */
    public double BP;

    /**
     * Delta Benchmark. Input. Customer data. Affects performance. Already taken
     * into account in this.P.
     */
    public double dB;

    /**
     * Performance. Input. Customer data. Affects performance.
     */
    public double P;

    // ==> OUTPUT DATA

    // SENTIMENT

    /**
     * Sentiment polarity. Output. Sentiment.
     */
    public double S;

    /**
     * Qualitative sentiment. Output. Sentiment.
     */
    public int qS;

    // PERFORMANCE

    /**
     * Qualitative performance. Output. Performance.
     */
    public int qP;

    // MISMATCHING

    /**
     * Delta mismatching. Output. Mismatching.
     */
    public double dM;

    /**
     * Qualitative mismatching. Output. Mismatching.
     */
    public int qM;

    // CUSTOMER VOLUMES

    /**
     * Relative product/customer volumes in customer. Output. Customer volumes.
     */
    public double RV1c;

    /**
     * Qualitative customer volumes. Output. Customer volumes.
     */
    public int qRV1c;

    /**
     * Relative customer volumes. Output. Product volumes.
     */
    public double RVc;

    // PRODUCT VOLUMES

    /**
     * Relative product/customer volumes in product. Output. Product volumes.
     */
    public double RV1p;

    /**
     * Relative number of products. Output. Product volumes.
     */
    public double RNp;

    /**
     * Relative product volumes. Output. Product volumes.
     */
    public double RVp;
    
    // QUALITATIVE AGGREGATES

    /**
     * Qualitative PM. Output. Qualitative aggregate.
     */
    public int qPM;

    /**
     * Qualitative RIcp. Output. Qualitative aggregate.
     */
    public int qRI1;

    /**
     * Constructor with arguments corresponding to all input variables.
     * 
     * @param counterpart
     * @param product
     * @param client
     * @param date
     * @param Slp
     * @param Ssp
     * @param N
     * @param TA
     * @param SRI
     * @param Np
     * @param Vp
     * @param RP
     * @param Vc
     * @param Vc
     * @param PP
     * @param BP
     * @param dB
     * @param P
     */
    public Data(String counterpart, String product, String client, String date,
            double Slp, double Ssp, long TN, double TA, int SRI, long Np,
            long Vp, int RP, long V1, long Vc, double PP, double BP, double dB,
            double P) {
        this.counterpart = counterpart;
        this.product = product;
        this.client = client;
        this.date = date;
        this.Slp = Slp;
        this.Ssp = Ssp;
        this.TN = TN;
        this.TA = TA;
        this.SRI = SRI;
        this.Np = Np;
        this.Vp = Vp;
        this.RP = RP;
        this.V1 = V1;
        this.Vc = Vc;
        this.PP = PP;
        this.BP = BP;
        this.dB = dB;
        this.P = P;
    }

    /**
     * Index to variables[][] for obtaining variable names.
     */
    private static final int VAR = 0;

    /**
     * Index to variables[][] for obtaining variable formats.
     */
    private static final int FMT = 1;

    /**
     * Index to variables[][] for obtaining variable descriptions.
     */
    private static final int DSC = 2;

    /**
     * String[][3] array that holds a triplet of strings { name, format,
     * description } for each variable.
     */
    private static final String[][] variables = {
    // @formatter:off
        { "Counterpart", "%s",      "Counterpart" },
        { "Product",     "%s",      "Product" },
        { "Client",      "%s",      "Client" },
        { "Date",        "%s",      "Date" },
        { "Slp",         "%.4f",    "Sentiment polarity, long term" },
        { "Ssp",         "%.4f",    "Sentiment polarity, short term" },
        { "TN",          "%d",      "Total number of customers" },
        { "TA",          "%.0f",    "Total assets" },
        { "SRI",         "%d",      "SRI" },
        { "Np",          "%d",      "Number of products" },
        { "Vp",          "%d",      "Volume of products" },
        { "RP",          "%d",      "Risk profile" },
        { "V1",          "%d",      "Volume of products per customer" },
        { "Vc",          "%d",      "Total customer assets" },
        { "PP",          "%.2f",    "Product performance" },
        { "BP",          "%.2f",    "Benchmark performance" },
        { "dB",          "%.2f",    "Delta Benchmark" },
        { "P",           "%.2f",    "Performance" },
        { "S",           "%.4f",    "Sentiment polarity" },
        { "qS",          "%d",      "Qualitative sentiment" },
        { "qP",          "%d",      "Qualitative performance" },
        { "dM",          "%.0f",    "Delta mismatching" },
        { "qM",          "%d",      "Qualitative mismatching" },
        { "RV1c",        "%.4f",    "Relative product/customer volumes in customer" },
        { "qRV1c",       "%d",      "Qualitative customer volumes" },
        { "RVc",         "%.4f",    "Relative customer volumes" },
        { "RV1p",        "%.4f",    "Relative product/customer volumes in product" },
        { "RNp",         "%.4f",    "Relative number of products" },
        { "RVp",         "%.4f",    "Relative product volumes" },
        { "qPM",         "%d",      "Qualitative PM" },
        { "qRI1",        "%d",      "Qualitative RI1" } };
    // @formatter:on

    /**
     * Get a tab-delimited string of input variable names.
     * 
     * @return Tab-delimited string
     */
    public static String tabbedInputNames() {
        StringBuffer sb = new StringBuffer();
        boolean endOfInput = false;
        for (int i = 0; i < variables.length && !endOfInput; i++) {
            if (i > 0)
                sb.append("\t");
            String name = variables[i][VAR];
            sb.append(name);
            endOfInput = name.equals("P");
        }
        return sb.toString();
    }

    /**
     * Get a tab-delimited string of all variable names.
     * 
     * @return Tab-delimited string
     */
    public static String tabbedNames() {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < variables.length; i++) {
            if (i > 0)
                sb.append("\t");
            sb.append(variables[i][VAR]);
        }
        return sb.toString();
    }

    /**
     * Get a tab-delimited string of all variable descriptions.
     * 
     * @return Tab-delimited string
     */
    public static String tabbedDescriptions() {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < variables.length; i++) {
            if (i > 0)
                sb.append("\t");
            sb.append(variables[i][DSC]);
        }
        return sb.toString();
    }

    /**
     * A private shortcut for variables[index][FMT].
     * 
     * @return String
     */
    private String varFMT(int index) {
        return variables[index][FMT];
    }

    /**
     * A private shortcut for variables[index][VAR].
     * 
     * @return String
     */
    private String varVAR(int index) {
        return variables[index][VAR];
    }

    /**
     * Get a tab-delimited string of all variable values. Qualitative variable
     * values are incremented by one, that is, from zero-based to one-based
     * values.
     * 
     * @return Tab-delimited string
     */
    public String tabbedValues() {
        StringBuffer sb = new StringBuffer();
        int idx = 0;
        sb.append(String.format(varFMT(idx) + "\t", counterpart));
        idx++;
        sb.append(String.format(varFMT(idx) + "\t", product));
        idx++;
        sb.append(String.format(varFMT(idx) + "\t", client));
        idx++;
        sb.append(String.format(varFMT(idx) + "\t", date));
        idx++;
        sb.append(String.format(varFMT(idx) + "\t", Slp));
        idx++;
        sb.append(String.format(varFMT(idx) + "\t", Ssp));
        idx++;
        sb.append(String.format(varFMT(idx) + "\t", TN));
        idx++;
        sb.append(String.format(varFMT(idx) + "\t", TA));
        idx++;
        sb.append(String.format(varFMT(idx) + "\t", SRI));
        idx++;
        sb.append(String.format(varFMT(idx) + "\t", Np));
        idx++;
        sb.append(String.format(varFMT(idx) + "\t", Vp));
        idx++;
        sb.append(String.format(varFMT(idx) + "\t", RP));
        idx++;
        sb.append(String.format(varFMT(idx) + "\t", V1));
        idx++;
        sb.append(String.format(varFMT(idx) + "\t", Vc));
        idx++;
        sb.append(String.format(varFMT(idx) + "\t", PP));
        idx++;
        sb.append(String.format(varFMT(idx) + "\t", BP));
        idx++;
        sb.append(String.format(varFMT(idx) + "\t", dB));
        idx++;
        sb.append(String.format(varFMT(idx) + "\t", P));
        idx++;
        sb.append(String.format(varFMT(idx) + "\t", S));
        idx++;
        sb.append(String.format(varFMT(idx) + "\t", qS + 1));
        idx++;
        sb.append(String.format(varFMT(idx) + "\t", qP + 1));
        idx++;
        sb.append(String.format(varFMT(idx) + "\t", dM));
        idx++;
        sb.append(String.format(varFMT(idx) + "\t", qM + 1));
        idx++;
        sb.append(String.format(varFMT(idx) + "\t", 100 * RV1c));
        idx++;
        sb.append(String.format(varFMT(idx) + "\t", qRV1c + 1));
        idx++;
        sb.append(String.format(varFMT(idx) + "\t", 100 * RVc));
        idx++;
        sb.append(String.format(varFMT(idx) + "\t", 100 * RV1p));
        idx++;
        sb.append(String.format(varFMT(idx) + "\t", 100 * RNp));
        idx++;
        sb.append(String.format(varFMT(idx) + "\t", 100 * RVp));
        idx++;
        sb.append(String.format(varFMT(idx) + "\t", qPM + 1));
        idx++;
        sb.append(String.format(varFMT(idx) + "\t", qRI1 + 1));
        idx++;
        return sb.toString();
    }

    /**
     * Get a string consisting of "name = value" lines for all data variables.
     * Qualitative values are transformed from zero-based to one-base.
     * 
     * @return Tab-delimited string
     */
    public String dataValues() {
        StringBuffer sb = new StringBuffer();
        int idx = 0;
        // @formatter:off
		sb.append(String.format("%s = " + varFMT(idx) + "\n", varVAR(idx), counterpart));
		idx++;
		sb.append(String.format("%s = " + varFMT(idx) + "\n", varVAR(idx), product));
		idx++;
		sb.append(String.format("%s = " + varFMT(idx) + "\n", varVAR(idx), client));
		idx++;
		sb.append(String.format("%s = " + varFMT(idx) + "\n", varVAR(idx), date));
		idx++;
		sb.append(String.format("%s = " + varFMT(idx) + "\n", varVAR(idx), Slp));
		idx++;
		sb.append(String.format("%s = " + varFMT(idx) + "\n", varVAR(idx), Ssp));
		idx++;
		sb.append(String.format("%s = " + varFMT(idx) + "\n", varVAR(idx), TN));
		idx++;
		sb.append(String.format("%s = " + varFMT(idx) + "\n", varVAR(idx), TA));
		idx++;
		sb.append(String.format("%s = " + varFMT(idx) + "\n", varVAR(idx), SRI));
		idx++;
		sb.append(String.format("%s = " + varFMT(idx) + "\n", varVAR(idx), Np));
		idx++;
		sb.append(String.format("%s = " + varFMT(idx) + "\n", varVAR(idx), Vp));
		idx++;
		sb.append(String.format("%s = " + varFMT(idx) + "\n", varVAR(idx), RP));
		idx++;
		sb.append(String.format("%s = " + varFMT(idx) + "\n", varVAR(idx), V1));
		idx++;
		sb.append(String.format("%s = " + varFMT(idx) + "\n", varVAR(idx), Vc));
		idx++;
		sb.append(String.format("%s = " + varFMT(idx) + "\n", varVAR(idx), PP));
		idx++;
		sb.append(String.format("%s = " + varFMT(idx) + "\n", varVAR(idx), BP));
		idx++;
		sb.append(String.format("%s = " + varFMT(idx) + "\n", varVAR(idx), dB));
		idx++;
		sb.append(String.format("%s = " + varFMT(idx) + "\n", varVAR(idx), P));
		idx++;
		sb.append(String.format("%s = " + varFMT(idx) + "\n", varVAR(idx), S));
		idx++;
		sb.append(String.format("%s = " + varFMT(idx) + "\n", varVAR(idx), qS+1));
		idx++;
		sb.append(String.format("%s = " + varFMT(idx) + "\n", varVAR(idx), qP+1));
		idx++;
		sb.append(String.format("%s = " + varFMT(idx) + "\n", varVAR(idx), dM));
		idx++;
		sb.append(String.format("%s = " + varFMT(idx) + "\n", varVAR(idx), qM+1));
		idx++;
		sb.append(String.format("%s = " + varFMT(idx) + "\n", varVAR(idx), 100*RV1c));
		idx++;
		sb.append(String.format("%s = " + varFMT(idx) + "\n", varVAR(idx), qRV1c+1));
		idx++;
        sb.append(String.format("%s = " + varFMT(idx) + "\n", varVAR(idx), 100*RVc));
        idx++;
        sb.append(String.format("%s = " + varFMT(idx) + "\n", varVAR(idx), 100*RV1p));
        idx++;
        sb.append(String.format("%s = " + varFMT(idx) + "\n", varVAR(idx), 100*RNp));
        idx++;
        sb.append(String.format("%s = " + varFMT(idx) + "\n", varVAR(idx), 100*RVp));
        idx++;
		sb.append(String.format("%s = " + varFMT(idx) + "\n", varVAR(idx), qPM+1));
		idx++;
		sb.append(String.format("%s = " + varFMT(idx) + "\n", varVAR(idx), qRI1+1));
		idx++;
		// @formatter:on
        return sb.toString();
    }

    /**
     * Get index of variable "name".
     * 
     * @param name
     *            Variable name.
     * 
     * @return Variable index or -1 if not found.
     */
    public int indexOf(String name) {
        for (int i = 0; i < variables.length; i++) {
            if (name.equals(variables[i][VAR])) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Get format specifier for variable "name".
     * 
     * @param name
     *            Variable name.
     * @return Format or "%g" for undefined variable.
     */
    public String formatOf(String name) {
        int idx = indexOf(name);
        if (idx < 0) {
            return "%g";
        } else {
            return variables[idx][FMT];
        }
    }

}
