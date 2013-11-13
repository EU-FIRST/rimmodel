package eu.first.RIM;

import java.util.ArrayList;

import si.JDEXi.Distribution;
import eu.first.RIM.Data;

/**
 * ProdCumulate class. Accumulates Data evaluations in order to produce
 * cumulative statistics.
 * 
 * @author Marko Bohanec (marko.bohanec@ijs.si)
 * @version 1.0
 * @since 2012-10-20
 * 
 */
public class ProdCumulate implements Comparable<ProdCumulate> {

    /**
     * Date obtained from Data. Used only for checking whether data of
     * consecutive Data objects match (because aggregating Data for different
     * dates does not make sense and indicates an error).
     */
    private String date;

    /**
     * List of counterparts whose Data was aggregated into this ProdCumulate.
     */
    private ArrayList<String> counterparts;

    /**
     * List of products whose Data was aggregated into this ProdCumulate.
     */
    protected ArrayList<String> products;

    /**
     * List of clients whose Data was aggregated into this ProdCumulate.
     */
    private ArrayList<String> clients;

    /**
     * Qualitative distribution of qRI1.
     */
    private Distribution qRI1;

    /**
     * Qualitative distribution of qRI1 weighted by RV1p/
     */
    private Distribution wRI1;

    /**
     * Distribution used to calculate cumulative RNp.
     */
    protected double cRNp;

    /**
     * Distribution used to calculate cumulative RVp.
     */
    protected double cRVp;

    /**
     * RNp weight relative to cumulative bank RNp at this run
     */
    private double wRNp = -1.0;

    /**
     * RVp weight relative to cumulative bank RNp at this run
     */
    private double wRVp = -1.0;

    /**
     * "Contribution". Used for sorting.
     */
    public double contrib = 0.0;

    /**
     * Constructor. Only creates empty private objects.
     */
    public ProdCumulate() {
        date = null;
        counterparts = new ArrayList<String>();
        products = new ArrayList<String>();
        clients = new ArrayList<String>();
        qRI1 = new Distribution(5);
        wRI1 = new Distribution(5);
        cRNp = 0.0;
        cRVp = 0.0;
    }

    /**
     * Clears contents of all private objects.
     */
    public void clear() {
        date = null;
        counterparts.clear();
        products.clear();
        clients.clear();
        qRI1.clear();
        wRI1.clear();
        cRNp = 0.0;
        cRVp = 0.0;
    }

    /**
     * Test whether two date strings match, i.e., one or both are null, or they
     * are equal.
     * 
     * @param date1
     * @param date2
     * @return boolean
     */
    private boolean datesMatch(String date1, String date2) {
        return (date1 == null) || (date2 == null) || (date1.equals(date2));
    }

    /**
     * cumulate cRNp
     * 
     * @param RNp
     */
    protected void cumulateRNp(double RNp, boolean newProd) {
        cRNp = RNp;
    }

    /**
     * cumulate cRVp
     * 
     * @param RVp
     */
    protected void cumulateRVp(double RVp, boolean newProd) {
        cRVp = RVp;
    }

    /**
     * Accumulates another Data object. Other product and client strings are
     * added to corresponding ProdCumulate lists, and all distributions are
     * updated.
     * 
     * @param d
     * @throws UnsupportedOperationException
     *             When this.date and d.date do not match.
     */
    public void cumulate(Data d) {
        if (!datesMatch(date, d.date)) {
            throw new UnsupportedOperationException(
                    "ProdCumulate.cumulate(): Dates do not match");
        }
        if (date == null) {
            date = d.date;
        }
        boolean newProduct = false;
        if (!counterparts.contains(d.counterpart)) {
            counterparts.add(d.counterpart);
        }
        if (!products.contains(d.product)) {
            products.add(d.product);
            newProduct = true;
        }
        if (!clients.contains(d.client)) {
            clients.add(d.client);
        }

        qRI1.add(d.qRI1);
        wRI1.add(d.qRI1, d.RV1p);
        cumulateRNp(d.RNp, newProduct);
        cumulateRVp(d.RVp, newProduct);

    }

    /**
     * Get date.
     * 
     * @return String
     */
    public String getDate() {
        return new String(date);
    }

    /**
     * Get counterparts list as String[].
     * 
     * @return List of product names.
     */
    public String[] getCounterparts() {
        return counterparts.toArray(new String[0]);
    }

    /**
     * Get products list as String[].
     * 
     * @return List of product names.
     */
    public String[] getProducts() {
        return products.toArray(new String[0]);
    }

    /**
     * Get clients list as String[].
     * 
     * @return List of client names.
     */
    public String[] getClients() {
        return clients.toArray(new String[0]);
    }

    /**
     * Create a new normalized Distribution from d.
     * 
     * @param d
     *            Distribution.
     * @return New normalized distribution.
     */
    private Distribution normalized(Distribution d) {
        Distribution result = new Distribution(d);
        result.normalizeSum();
        return result;
    }

    /**
     * Return normalized distribution of qRI1.
     * 
     * @return New normalized Distribution.
     */
    public Distribution getqRI1() {
        return normalized(qRI1);
    }

    /**
     * Return normalized distribution of wRI1.
     * 
     * @return New normalized Distribution.
     */
    public Distribution getwRI1() {
        return normalized(wRI1);
    }

    /**
     * Get aggregated cRNp.
     * 
     * @return double
     */
    public double getcRNp() {
        return cRNp;
    }

    /**
     * Get aggregated cRVp.
     * 
     * @return double
     */
    public double getcRVp() {
        return cRVp;
    }

    /**
     * Get numeric RI, unweighted
     * 
     * @return double
     */
    public double getRIu() {
        return normalized(qRI1).avgIndex() + 1;
    }

    /**
     * Get numeric RI, weighted
     * 
     * @return double
     */
    public double getRIw() {
        return normalized(wRI1).avgIndex() + 1;
    }

    /**
     * Get numeric RI as a combination of RIu and RIw. Calculated from distributions.
     * 
     * @param Ww
     *            in [0,1]
     * @return (1-Ww)*RIu + Ww*RIw
     */
    public double getRI(double Ww) {
        return (1 - Ww) * getRIu() + Ww * getRIw();
    }

    /**
     * Get RXp weight as a combination of wRNp and wRVp
     * 
     * @param Wv
     *            in [0,1]
     * @return (1-Wv)*wRNp + Wv*wRVp
     */
    public double getRXp(double Wv) {
        return (1 - Wv) * getwRNp() + Wv * getwRVp();
    }

    public void setContribution(double Wv) {
        contrib = getRXp(Wv);
    }

    /**
     * Get wRNp
     * 
     * @return
     */
    public double getwRNp() {
        return wRNp;
    }

    /**
     * Set wRNp through total wRNp at the bank level.
     * 
     * @param totwRNp
     */
    public void setwRNp(double totwRNp) {
        wRNp = getcRNp() / totwRNp;
    }

    /**
     * Get wRVp
     * 
     * @return
     */
    public double getwRVp() {
        return wRVp;
    }

    /**
     * Set wRVp through total wRVp at the bank level.
     * 
     * @param totwRVp
     */
    public void setwRVp(double totwRVp) {
        wRVp = getcRVp() / totwRVp;
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
        { "Date",   "%s",   "Date" },
        { "qRI1",   "%s",   "Qualitative Client/Product Risk Index" },
        { "wRI1",   "%s",   "Qualitative Client/Product Risk Index weighted by RV1p" },
        { "RNp",    "%.4f", "Cumulative RNp" },
        { "RVp",    "%.4f", "Cumulative RVp" },
        { "wRNp",   "%.2f", "Weight RNp" },
        { "wRVp",   "%.2f", "Weight RVp" },
        { "RIu",    "%.2f", "Numeric RI, unweighted" },
        { "RIw",    "%.2f", "Numeric RI, weighted" },
        { "RI",     "%.2f", "Combination of RIu and RIw" } };
    // @formatter:on

    /**
     * Get a tab-delimited string of all variable names.
     * 
     * @return Tab-delimited string
     */
    public static String tabbedNames() {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < variables.length; i++) {
            if (i > 0) {
                sb.append("\t");
            }
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
            if (i > 0) {
                sb.append("\t");
            }
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
     * Increment distribution by one. Needed because all internal qualitative
     * JDEXi values are zero-based, but externally we want to get them
     * one-based.
     * 
     * @param distr
     * @return Incremented Distribution
     */
    public Distribution incr(Distribution distr) {
        Distribution newdist = new Distribution(distr);
        newdist.increment();
        return newdist;
    }

    /**
     * Get a tab-delimited string of all variable values, incremented by one.
     * 
     * @return Tab-delimited string.
     */
    public String tabbedValues() {
        StringBuffer sb = new StringBuffer();
        int idx = 0;
        sb.append(String.format(varFMT(idx) + "\t", getDate()));
        idx++;
        sb.append(String.format(varFMT(idx) + "\t", incr(getqRI1()).toString()));
        idx++;
        sb.append(String.format(varFMT(idx) + "\t", incr(getwRI1()).toString()));
        idx++;
        sb.append(String.format(varFMT(idx) + "\t", 100 * getcRNp()));
        idx++;
        sb.append(String.format(varFMT(idx) + "\t", 100 * getcRVp()));
        idx++;
        sb.append(String.format(varFMT(idx) + "\t", 100 * getwRNp()));
        idx++;
        sb.append(String.format(varFMT(idx) + "\t", 100 * getwRVp()));
        idx++;
        sb.append(String.format(varFMT(idx) + "\t", getRIu()));
        idx++;
        sb.append(String.format(varFMT(idx) + "\t", getRIw()));
        idx++;
        sb.append(String.format(varFMT(idx) + "\t", getRI(0.6)));
        idx++;
        return sb.toString();
    }

    /**
     * Get a string consisting of "name = value" lines for all data variables,
     * incremented by one.
     * 
     * @return Tab-delimited string
     */
    public String dataValues() {
        StringBuffer sb = new StringBuffer();
        int idx = 0;
        // @formatter:off
        sb.append(String.format("%s = " + varFMT(idx) + "\n", varVAR(idx), getDate()));
        idx++;
        sb.append(String.format("%s = " + varFMT(idx) + "\n", varVAR(idx), incr(getqRI1()).toString()));
        idx++;
        sb.append(String.format("%s = " + varFMT(idx) + "\n", varVAR(idx), incr(getwRI1()).toString()));
        idx++;
        sb.append(String.format("%s = " + varFMT(idx) + "\n", varVAR(idx), 100*getcRNp()));
        idx++;
        sb.append(String.format("%s = " + varFMT(idx) + "\n", varVAR(idx), 100*getcRVp()));
        idx++;
        sb.append(String.format("%s = " + varFMT(idx) + "\n", varVAR(idx), getRIu()));
        idx++;
        sb.append(String.format("%s = " + varFMT(idx) + "\n", varVAR(idx), getRIw()));
        idx++;
        sb.append(String.format("%s = " + varFMT(idx) + "\n", varVAR(idx), getRI(0.6)));
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

    @Override
    public int compareTo(ProdCumulate prod) {
        double diff = contrib - prod.contrib;
        if (diff < 0.0)
            return 1;
        else if (diff > 0.0)
            return -1;
        return 0;
    }

}
