package eu.first.RIM;

import java.util.SortedMap;
import java.util.TreeMap;

/**
 * CtptCumulate class. Accumulates Data evaluations in order to produce
 * cumulative statistics of counterparts at a bank level.
 * 
 * @author Marko Bohanec (marko.bohanec@ijs.si)
 * @version 1.0
 * @since 2013-1-18
 * 
 */
public class CtptCumulate extends ProdCumulate {

    // Counterpart sentiment
    public double S;

    private SortedMap<String, ProdCumulate> prods;

    /**
     * Constructor. Only creates empty private objects.
     */
    public CtptCumulate() {
        super();
        prods = new TreeMap<String, ProdCumulate>();
    }

    public void cumulate(Data d) {
        super.cumulate(d);
        S = d.S;
    }

    @Override
    protected void cumulateRNp(double RNp, boolean newProd) {
        if (newProd) {
            cRNp += RNp;
        }
    }

    /**
     * cumulate cRVp
     * 
     * @param RVp
     */
    @Override
    protected void cumulateRVp(double RVp, boolean newProd) {
        if (newProd) {
            cRVp += RVp;
        }
    }

    public void addProduct(ProdCumulate p) {
        String key = p.getProducts()[0];
        ProdCumulate cumul = prods.get(key);
        if (cumul == null) {
            prods.put(key, p);
        }
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
        for (SortedMap.Entry<String, ProdCumulate> entry : prods.entrySet()) {
            ProdCumulate p = entry.getValue();
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

}
