package eu.first.RIM;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * BankCumulate class. Accumulates Data evaluations in order to produce
 * cumulative statistics at a bank level.
 * 
 * @author Marko Bohanec (marko.bohanec@ijs.si)
 * @version 1.0
 * @since 2012-11-06
 * 
 */
public class BankCumulate {

    /**
     * Date obtained from Data. Used only for checking whether data of
     * consecutive Data objects match (because aggregating Data for different
     * dates does not make sense and indicates an error).
     */
    private String date;

    /**
     * List of ProdCumulate that were cumulated into this BankCumulate.
     */
    private ArrayList<ProdCumulate> products;

    /**
     * List of CtptCumulate that were cumulated into this BankCumulate.
     */
    private ArrayList<CtptCumulate> counterparts;

    /**
     * Cumulative RNp for all products
     */
    protected double cRNp;

    /**
     * Cumulative RVp for all products
     */
    protected double cRVp;

    /**
     * Constructor. Only creates an empty object.
     */
    public BankCumulate() {
        clear();
    }

    /**
     * Clears contents of all private objects.
     */
    public void clear() {
        date = null;
        products = new ArrayList<ProdCumulate>();
        counterparts = new ArrayList<CtptCumulate>();
        cRNp = 0.0;
        cRVp = 0.0;
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
     * Accumulates ProdCumulate object.
     * 
     * @param p
     * @throws UnsupportedOperationException
     *             When this.date and d.date do not match.
     */
    public void cumulate(ProdCumulate p, CtptCumulate c) {
        if (!datesMatch(date, p.getDate())) {
            throw new UnsupportedOperationException(
                    "BankCumulate.cumulate(): Dates do not match");
        }
        if (date == null) {
            date = p.getDate();
        }
        if (!counterparts.contains(c)) {
            counterparts.add(c);
        }
        if (!products.contains(p)) {
            products.add(p);
        }
    }

    public void calcWeights() {

        cRNp = 0.0;
        cRVp = 0.0;

        for (int i = 0; i < products.size(); i++) {
            ProdCumulate p = products.get(i);
            cRNp += p.getcRNp();
            cRVp += p.getcRVp();
        }

        for (int i = 0; i < products.size(); i++) {
            ProdCumulate p = products.get(i);
            p.setwRNp(cRNp);
            p.setwRVp(cRVp);
        }

        cRNp = 0.0;
        cRVp = 0.0;

        for (int i = 0; i < counterparts.size(); i++) {
            CtptCumulate c = counterparts.get(i);
            cRNp += c.getcRNp();
            cRVp += c.getcRVp();
        }

        for (int i = 0; i < counterparts.size(); i++) {
            CtptCumulate c = counterparts.get(i);
            c.setwRNp(cRNp);
            c.setwRVp(cRVp);

        }
    }

    /**
     * 
     * @param Wv
     * @param counterpart
     * @return List of counterpart's products sorted by contribution(Wv).
     *         Returns all products if counterpart=="".
     */
    public ProdCumulate[] sortedProducts(double Wv, String counterpart) {
        int cnt;
        if (counterpart == "") {
            cnt = products.size();

        } else {
            cnt = 0;
            for (int i = 0; i < products.size(); i++) {
                ProdCumulate p = products.get(i);
                String ctpt = p.getCounterparts()[0];
                if (ctpt.equals(counterpart))
                    cnt++;
            }
        }
        ProdCumulate[] result = new ProdCumulate[cnt];
        int j = 0;
        for (int i = 0; i < products.size(); i++) {
            ProdCumulate p = products.get(i);
            String ctpt = p.getCounterparts()[0];
            if (counterpart == "" || counterpart.equals(ctpt)) {
                result[j++] = p;
            }
        }
        for (int i = 0; i < result.length; i++) {
            result[i].setContribution(Wv);
        }
        Arrays.sort(result);
        return result;
    }

    /**
     * 
     * @param Wv
     * @return array of counterparts sorted by contribution(Wv).
     */
    public CtptCumulate[] sortedCounterparts(double Wv) {
        CtptCumulate[] result = counterparts
                .toArray(new CtptCumulate[counterparts.size()]);
        for (int i = 0; i < result.length; i++) {
            result[i].setContribution(Wv);
        }
        Arrays.sort(result);
        return result;
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
        { "cRNp",   "%.4f", "Cumulative RNp" },
        { "cRVp",   "%.4f", "Cumulative RVp" },
        { "RIuN",   "%.2f", "Numeric RI" },
        { "RIuV",   "%.2f", "Numeric RI" },
        { "RIwN",   "%.2f", "Numeric RI" },
        { "RIwV",   "%.2f", "Numeric RI" },
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
     * Obtain RI from products.
     * 
     * @param Ww
     * @param Wv
     * @return
     */
    public double getRI(double Ww, double Wv) {

        double sumW = 0.0;
        double weiRI = 0.0;
        for (int i = 0; i < products.size(); i++) {
            ProdCumulate p = products.get(i);
            double w = p.getRXp(Wv);
            weiRI += w * p.getRI(Ww);
            sumW += w;
        }
        if (sumW == 0.0) {
            return 0.0;
        } else {
            return weiRI / sumW;
        }
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
        sb.append(String.format(varFMT(idx) + "\t", 100 * cRNp));
        idx++;
        sb.append(String.format(varFMT(idx) + "\t", 100 * cRVp));
        idx++;
        sb.append(String.format(varFMT(idx) + "\t", getRI(0.0, 0.0)));
        idx++;
        sb.append(String.format(varFMT(idx) + "\t", getRI(0.0, 1.0)));
        idx++;
        sb.append(String.format(varFMT(idx) + "\t", getRI(1.0, 0.0)));
        idx++;
        sb.append(String.format(varFMT(idx) + "\t", getRI(1.0, 1.0)));
        idx++;
        sb.append(String.format(varFMT(idx) + "\t", getRI(0.6, 0.6)));
        idx++;
        return sb.toString();
    }

}
