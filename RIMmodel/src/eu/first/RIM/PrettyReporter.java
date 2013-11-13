package eu.first.RIM;

import java.io.IOException;
import java.util.SortedMap;

import si.JDEXi.Distribution;

public class PrettyReporter extends Reporter {

    private static double Ww = 0.6;
    private static double Wv = 0.6;
    private static int SrtTabLen = 5;

    public PrettyReporter() {
        super();
    }

    /**
     * Make Bank Data report.
     * 
     * @throws IOException
     */
    protected void reportBank() throws IOException {
        HtmlWriter html = new HtmlWriter(bankFileName(),
                "Cumulative Bank Reputational Risk Assessment");
        html.write("<h2>Ww = " + String.format("%.0f %%", 100 * Ww)
                + ": combining " + String.format("%.0f ", 100 * Ww)
                + "weighted by RV1p + "
                + String.format("%.0f ", 100 * (1 - Ww)) + "unweighted"
                + "</h2>\n");
        html.write("<h2>Wv = " + String.format("%.0f %%", 100 * Wv)
                + ": combining " + String.format("%.0f ", 100 * Wv) + "RVp + "
                + String.format("%.0f ", 100 * (1 - Wv)) + "RNp" + "</h2>\n");

        html.beginTable();
        html.write("<tr>\n");
        html.writeHdCell("Date", "HdDate");
        html.writeHdCell("RI", "HdOut");
        html.writeHdCell("Level", "HdOut");
        /*
         * html.writeHdCell("lowRI", "HdSup"); html.writeHdCell("highRI",
         * "HdSup"); html.writeHdCell("RIuN", "HdDbg"); html.writeHdCell("RIuV",
         * "HdDbg"); html.writeHdCell("RIwN", "HdDbg"); html.writeHdCell("RIwV",
         * "HdDbg");
         */
        html.writeHdCell("RNp%", "HdDbg");
        html.writeHdCell("RVp%", "HdDbg");
        if (SrtTabLen > 0) {
            html.writeHdCell("&nbsp;", "HdSkip");
            html.writeHdCell("Counterpart", "HdOut");
            html.writeHdCell("RI", "HdOut");
            html.writeHdCell("Contr%", "HdOut");
            html.writeHdCell("&nbsp;", "HdSkip");
            html.writeHdCell("Product", "HdOut");
            html.writeHdCell("RI", "HdOut");
            html.writeHdCell("Contr%", "HdOut");
        }
        html.write("</tr>\n");
        for (SortedMap.Entry<String, BankCumulate> entry : bank.entrySet()) {
            BankCumulate c = entry.getValue();
            html.write("<tr>\n");
            html.writeCell(c.getDate(), "Date");
            double cumRI = c.getRI(Ww, Wv);
            html.writeCell(String.format("%.2f", cumRI), "Out");
            html.writeCell(DiscretizeCumulRI(cumRI), "Out");
            /*
             * double RI00 = c.getRI(0.0, 0.0); double RI10 = c.getRI(1.0, 0.0);
             * double RI01 = c.getRI(0.0, 1.0); double RI11 = c.getRI(1.0, 1.0);
             * html.writeCell( String.format("%.2f", Math.min(Math.min(RI00,
             * RI10), Math.min(RI01, RI11))), "Sup"); html.writeCell(
             * String.format("%.2f", Math.max(Math.max(RI00, RI10),
             * Math.max(RI01, RI11))), "Sup");
             * html.writeCell(String.format("%.2f", RI00), "Dbg");
             * html.writeCell(String.format("%.2f", RI10), "Dbg");
             * html.writeCell(String.format("%.2f", RI01), "Dbg");
             * html.writeCell(String.format("%.2f", RI11), "Dbg");
             */
            html.writeCell(String.format("%.4f", 100 * c.cRNp), "Dbg");
            html.writeCell(String.format("%.4f", 100 * c.cRVp), "Dbg");
            if (SrtTabLen == 0) {
                html.write("</tr>\n");
            } else {
                CtptCumulate[] sortedCtpts = c.sortedCounterparts(Wv);
                ProdCumulate[] sortedProds = c.sortedProducts(Wv, "");
                int lines = Math.min(SrtTabLen,
                        Math.max(sortedCtpts.length, sortedProds.length));
                for (int i = 0; i < lines; i++) {
                    if (i > 0) {
                        html.write("<tr>\n");
                        for (int j = 0; j < 5; j++) {
                            html.writeCell("&nbsp;", "Out");
                        }
                    }
                    html.writeCell("&nbsp;", "Out");
                    if (i < sortedCtpts.length) {
                        CtptCumulate ctpt = sortedCtpts[i];
                        html.writeCell(ctpt.getCounterparts()[0], "Out");
                        html.writeCell(String.format("%.2f", ctpt.getRI(Ww)),
                                "Out");
                        html.writeCell(String.format("%.2f", 100*ctpt.contrib),
                                "Out");
                    } else {
                        html.writeCell("&nbsp;", "Out");
                        html.writeCell("&nbsp;", "Out");
                        html.writeCell("&nbsp;", "Out");
                    }
                    html.writeCell("&nbsp;", "Out");
                    if (i < sortedProds.length) {
                        ProdCumulate prod = sortedProds[i];
                        html.writeCell(prod.getProducts()[0], "Out");
                        html.writeCell(String.format("%.2f", prod.getRI(Ww)),
                                "Out");
                        html.writeCell(String.format("%.2f", 100*prod.contrib),
                                "Out");
                    } else {
                        html.writeCell("&nbsp;", "Out");
                        html.writeCell("&nbsp;", "Out");
                        html.writeCell("&nbsp;", "Out");
                    }
                    html.write("</tr>\n");
                }
                html.write("</tr>\n");
            }
        }
        html.endTable();
        html.close();
    }

    /**
     * Make Counterpart Data reports.
     * 
     * @throws IOException
     */
    protected void reportCounterparts() throws IOException {
        HtmlWriter html = null;
        String counterpart = "";
        for (SortedMap.Entry<String, CtptCumulate> entry : counterparts
                .entrySet()) {
            String[] key = entry.getKey().split("\t");
            if (!counterpart.equals(key[0])) { // new counterpart
                if (html != null) {
                    html.endTable();
                    html.close();
                }
                counterpart = key[0];
                html = new HtmlWriter(cFileName(counterpart),
                        "Cumulative Counterpart Risk Assessment");
                html.write("<h2>Counterpart: " + counterpart + "</h2>\n");
                html.write("<h2>Ww = " + String.format("%.0f %%", 100 * Ww)
                        + ": combining " + String.format("%.0f ", 100 * Ww)
                        + "weighted by RV1p + "
                        + String.format("%.0f ", 100 * (1 - Ww)) + "unweighted"
                        + "</h2>\n");
                html.beginTable();
                html.write("<tr>\n");
                html.writeHdCell("Date", "HdDate");
                html.writeHdCell("RI", "HdOut");
                html.writeHdCell("Level", "HdOut");
                html.writeHdCell("Sent", "HdSup");
                html.writeHdCell("Contr%", "HdSup");
                html.writeHdCell("RNp%", "HdSup");
                html.writeHdCell("RVp%", "HdSup");
                if (SrtTabLen > 0) {
                    html.writeHdCell("&nbsp;", "HdSkip");
                    html.writeHdCell("Product", "HdOut");
                    html.writeHdCell("RI", "HdOut");
                    html.writeHdCell("Contr%", "HdOut");
                }
                html.write("</tr>\n");
            }
            CtptCumulate c = entry.getValue();
            html.write("<tr>\n");
            html.writeCell(c.getDate(), "Date");
            html.writeCell(String.format("%.2f", c.getRI(Ww)), "Out");
            html.writeCell(DiscretizeProductRI(c.getRI(Ww)), "Out");
            html.writeCell(String.format("%.2f", c.S), "Out");
            html.writeCell(String.format("%.2f", 100 * c.getRXp(Wv)), "Out");
            html.writeCell(String.format("%.4f", 100 * c.getcRNp()), "Sup");
            html.writeCell(String.format("%.4f", 100 * c.getcRVp()), "Sup");
            BankCumulate bc = bank.get(key(c.getDate()));
            if (SrtTabLen == 0 || bc == null) {
                html.write("</tr>\n");
            } else {
                ProdCumulate[] sortedProds = bc.sortedProducts(Wv, counterpart);
                int lines = Math.min(SrtTabLen, sortedProds.length);
                for (int i = 0; i < lines; i++) {
                    if (i > 0) {
                        html.write("<tr>\n");
                        for (int j = 0; j < 7; j++) {
                            html.writeCell("&nbsp;", "Out");
                        }
                    }
                    html.writeCell("&nbsp;", "Out");
                    ProdCumulate prod = sortedProds[i];
                    html.writeCell(prod.getProducts()[0], "Out");
                    html.writeCell(String.format("%.2f", prod.getRI(Ww)), "Out");
                    html.writeCell(String.format("%.2f", 100*prod.contrib), "Out");
                    html.write("</tr>\n");
                }
                html.write("</tr>\n");
            }
        }
        if (html != null) {
            html.endTable();
            html.close();
        }
    }

    /**
     * Make Product Data reports.
     * 
     * @throws IOException
     */
    protected void reportProducts() throws IOException {
        HtmlWriter html = null;
        String product = "";
        for (SortedMap.Entry<String, ProdCumulate> entry : products.entrySet()) {
            String[] key = entry.getKey().split("\t");
            if (!product.equals(key[0])) { // new product
                if (html != null) {
                    html.endTable();
                    html.close();
                }
                product = key[0];
                html = new HtmlWriter(pFileName(product),
                        "Cumulative Product Risk Assessment");
                html.write("<h2>Product: " + product + "</h2>\n");
                html.write("<h2>Ww = " + String.format("%.0f %%", 100 * Ww)
                        + ": combining " + String.format("%.0f ", 100 * Ww)
                        + "weighted by RV1p + "
                        + String.format("%.0f ", 100 * (1 - Ww)) + "unweighted"
                        + "</h2>\n");
                html.beginTable();
                html.write("<tr>\n");
                html.writeHdCell("Date", "HdDate");
                html.writeHdCell("RI", "HdOut");
                html.writeHdCell("Level", "HdOut");
                html.writeHdCell("Contr%", "HdOut");
                html.writeHdCell("RNp%", "HdSup");
                html.writeHdCell("RVp%", "HdSup");
                html.writeHdCell("&nbsp;", "HdSkip");
                for (int c = 1; c <= 5; c++) {
                    html.writeHdCell("&nbsp;&nbsp;&nbsp;" + c
                            + "&nbsp;&nbsp;&nbsp;", "HdSup");
                }
                html.write("</tr>\n");
            }
            ProdCumulate c = entry.getValue();
            html.write("<tr>\n");
            html.writeCell(c.getDate(), "Date");
            html.writeCell(String.format("%.2f", c.getRI(Ww)), "Out");
            html.writeCell(DiscretizeProductRI(c.getRI(Ww)), "Out");
            html.writeCell(String.format("%.2f", 100 * c.getRXp(Wv)), "Out");
            html.writeCell(String.format("%.4f", 100 * c.getcRNp()), "Sup");
            html.writeCell(String.format("%.4f", 100 * c.getcRVp()), "Sup");
            html.writeCell("&nbsp;", "Skip");
            Distribution wRI1 = c.getwRI1();
            double w;
            for (int i = 0; i < 5; i++) {
                if (i < wRI1.size()) {
                    w = wRI1.getValue(i);
                } else
                    w = 0.0;
                // !! NEJC, teh pet stolpcev naj bo enako �irokih, vsebina
                // najbolje, da je centrirana
                html.writeCell(String.format("%.0f", 100 * w), "Sup");
            }
            html.write("</tr>\n");
        }
        if (html != null) {
            html.endTable();
            html.close();
        }
    }

    /**
     * Make Product/Client Data reports.
     * 
     * @throws IOException
     */
    protected void reportProductsClients() throws IOException {
        HtmlWriter html = null;
        String counterpart = "";
        String product = "";
        String client = "";
        for (SortedMap.Entry<String, Data> entry : prod_client.entrySet()) {
            String[] key = entry.getKey().split("\t");
            if (!counterpart.equals(key[0]) || !product.equals(key[1])
                    || !client.equals(key[2])) {
                if (html != null) {
                    html.endTable();
                    html.close();
                }
                counterpart = key[0];
                product = key[1];
                client = key[2];
                html = new HtmlWriter(pcFileName(counterpart, product, client),
                        "Product/Client Risk Assessment");
                html.write("<h2>Product: " + product + "</h2>\n");
                html.write("<h2>Client: " + client + "</h2>\n");
                html.beginTable();
                html.write("<tr>\n");
                html.writeHdCell("Counterpart", "HdId");
                html.writeHdCell("Product", "HdId");
                html.writeHdCell("Client", "HdId");
                html.writeHdCell("Date", "HdDate");
                html.writeHdCell("&nbsp;", "HdSkip");
                // QUALITATIVE AGGREGATES
                html.writeHdCell("qRI", "HdQualMainOut");
                html.writeHdCell("qPM", "HdQualAggOut");
                html.writeHdCell("&nbsp;", "HdSkip");
                // SENTIMENT
                html.writeHdCell("qS", "HdQualSentOut");
                html.writeHdCell("S", "HdSentOut");
                html.writeHdCell("Slp", "HdSentInp");
                html.writeHdCell("Ssp", "HdSentInp");
                html.writeHdCell("&nbsp;", "HdSkip");
                // PERFORMANCE
                html.writeHdCell("qP", "HdQualPerfOut");
                html.writeHdCell("P", "HdPerfInp");
                html.writeHdCell("PP", "HdPerfInp");
                html.writeHdCell("BP", "HdPerfInp");
                html.writeHdCell("dB", "HdPerfInp");
                html.writeHdCell("&nbsp;", "HdSkip");
                // MISMATCHING
                html.writeHdCell("qM", "HdQualMismOut");
                html.writeHdCell("dM", "HdMismOut");
                html.writeHdCell("SRI", "HdMismInp");
                html.writeHdCell("RP", "HdMismInp");
                html.writeHdCell("&nbsp;", "HdSkip");
                // CUSTOMER VOLUMES
                html.writeHdCell("qRV1c", "HdQualCVolOut");
                html.writeHdCell("RV1c%", "HdCVolOut");
                html.writeHdCell("RVc%", "HdCVolOut");
                html.writeHdCell("V1", "HdCVolInp");
                html.writeHdCell("Vc", "HdCVolInp");
                html.writeHdCell("&nbsp;", "HdSkip");
                // PRODUCT VOLUMES
                html.writeHdCell("RNp%", "HdPVolOut");
                html.writeHdCell("Np", "HdPVolInp");
                html.writeHdCell("TN", "HdPVolInp");
                html.writeHdCell("RV1p%", "HdPVolOut");
                html.writeHdCell("RVp%", "HdPVolOut");
                html.writeHdCell("Vp", "HdPVolInp");
                html.writeHdCell("TA", "HdPVolInp");
                html.write("</tr>\n");
            }
            html.write("<tr>\n");
            Data d = entry.getValue();
            html.writeCell(d.counterpart, "Id");
            html.writeCell(d.product, "Id");
            html.writeCell(d.client, "Id");
            html.writeCell(d.date, "Date");
            html.writeHdCell("", "Skip");
            html.writeCell(String.format("%d", d.qRI1 + 1), "QualMainOut");
            html.writeCell(String.format("%d", d.qPM + 1), "QualAggOut");
            html.writeHdCell("", "Skip");
            html.writeCell(String.format("%d", d.qS + 1), "QualSentOut");
            html.writeCell(String.format("%.4f", d.S), "SentOut");
            html.writeCell(String.format("%.4f", d.Slp), "SentInp");
            html.writeCell(String.format("%.4f", d.Ssp), "SentInp");
            html.writeHdCell("", "Skip");
            html.writeCell(String.format("%d", d.qP + 1), "QualPerfOut");
            html.writeCell(String.format("%.2f", d.P), "PerfInp");
            html.writeCell(String.format("%.2f", d.PP), "PerfInp");
            html.writeCell(String.format("%.2f", d.BP), "PerfInp");
            html.writeCell(String.format("%.2f", d.dB), "PerfInp");
            html.writeHdCell("", "Skip");
            html.writeCell(String.format("%d", d.qM + 1), "QualMismOut");
            html.writeCell(String.format("%.0f", d.dM), "MismOut");
            html.writeCell(String.format("%d", d.SRI), "MismInp");
            html.writeCell(String.format("%d", d.RP), "MismInp");
            html.writeHdCell("", "Skip");
            html.writeCell(String.format("%d", d.qRV1c + 1), "QualCVolOut");
            html.writeCell(String.format("%.4f", 100 * d.RV1c), "CVolOut");
            html.writeCell(String.format("%.4f", 100 * d.RVc), "CVolOut");
            html.writeCell(String.format("%d", d.V1), "CVolInp");
            html.writeCell(String.format("%d", d.Vc), "CVolInp");
            html.writeHdCell("", "Skip");
            html.writeCell(String.format("%.4f", 100 * d.RNp), "PVolOut");
            html.writeCell(String.format("%d", d.Np), "PVolInp");
            html.writeCell(String.format("%d", d.TN), "PVolInp");
            html.writeCell(String.format("%.4f", 100 * d.RV1p), "PVolOut");
            html.writeCell(String.format("%.4f", 100 * d.RVp), "PVolOut");
            html.writeCell(String.format("%d", d.Vp), "PVolInp");
            html.writeCell(String.format("%.0f", d.TA), "PVolInp");

            html.write("</tr>\n");
        }

        if (html != null) {
            html.endTable();
            html.close();
        }
    }

}
