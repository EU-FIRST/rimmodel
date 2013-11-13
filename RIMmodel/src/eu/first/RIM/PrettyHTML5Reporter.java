package eu.first.RIM;

import java.io.File;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.net.URISyntaxException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.SortedMap;

import si.JDEXi.Distribution;

public class PrettyHTML5Reporter extends Reporter {

    private static double Ww = 0.6;
    private static double Wv = 0.6;
    private static final int PRODUCT = 5;
    private static final int COUNTERPART = 5;
    private static final int BANK = 5;

    public PrettyHTML5Reporter() throws IOException {
        super();
    }

    /**
     * Returns the name of a Bank Data html file.
     * 
     * @return
     */
    protected String bankFileName() {
        return rptDirName + "/index.html";
    }

    /**
     * Make Bank Data report.
     * 
     * @throws IOException
     */
    protected void reportBank() throws IOException {
        Html5Writer html = new Html5Writer(false, bankFileName(), "Bank");

        html.beginTable("demoTbl");
        html.write("<thead>\n");
        html.write("<tr>\n");
        html.writeHdCell("Date", "tableHead", "HdDate");
        html.writeHdCell("RI", "tableHead", "HdOut");
        html.writeHdCell("Level", "tableHead", "HdSup");
        html.writeHdCell("RNp%", "tableHead", "HdDbg");
        html.writeHdCell("RVp%", "tableHead", "HdDbg");

        if (BANK > 0) {
            html.writeHdCell("", "empty", "");
            html.writeHdCell("Counterpart", "tableHead", "HdOut");
            html.writeHdCell("RI", "tableHead", "HdOut");
            html.writeHdCell("Contr%", "tableHead", "HdOut");
            html.writeHdCell("", "empty", "");
            html.writeHdCell("Product", "tableHead", "HdOut");
            html.writeHdCell("RI", "tableHead", "HdOut");
            html.writeHdCell("Contr%", "tableHead", "HdOut");
        }
        html.write("</tr>\n");
        html.write("</thead>\n");
        String cls = "first";
        for (SortedMap.Entry<String, BankCumulate> entry : bank.entrySet()) {
            BankCumulate c = entry.getValue();
            html.write("<tr>\n");
            html.writeCell(c.getDate(), cls, "Date");
            double cumRI = c.getRI(Ww, Wv);
            html.writeCell(String.format("%.2f", cumRI), cls, "Out");
            html.writeCell(DiscretizeCumulRI(cumRI), cls, "Out");
            html.writeCell(String.format("%.4f", 100 * c.cRNp), cls, "Dbg");
            html.writeCell(String.format("%.4f", 100 * c.cRVp), cls, "Dbg");
            if (BANK == 0) {
                if (cls.equals("first")) {
                    cls = "second";
                } else
                    cls = "first";
                html.write("</tr>\n");
            } else {
                CtptCumulate[] sortedCtpts = c.sortedCounterparts(Wv);
                ProdCumulate[] sortedProds = c.sortedProducts(Wv, "");
                int lines = Math.min(BANK,
                        Math.max(sortedCtpts.length, sortedProds.length));
                for (int i = 0; i < lines; i++) {
                    if (i > 0) {
                        html.write("<tr>\n");
                        for (int j = 0; j < 5; j++) {
                            html.writeCell("", "empty", "");
                        }
                    }
                    html.writeCell("", "empty", "");
                    if (i < sortedCtpts.length) {
                        CtptCumulate ctpt = sortedCtpts[i];
                        html.writeCell(ctpt.getCounterparts()[0], cls, "Out");
                        html.writeCell(String.format("%.2f", ctpt.getRI(Ww)),
                                cls, "Out");
                        html.writeCell(
                                String.format("%.2f", 100 * ctpt.contrib), cls,
                                "Out");
                    } else {
                        html.writeCell("", "empty", "");
                        html.writeCell("", "empty", "");
                        html.writeCell("", "empty", "");
                    }
                    html.writeCell("", "empty", "");
                    if (i < sortedProds.length) {
                        ProdCumulate prod = sortedProds[i];
                        html.writeCell(prod.getProducts()[0], cls, "Out");
                        html.writeCell(String.format("%.2f", prod.getRI(Ww)),
                                cls, "Out");
                        html.writeCell(
                                String.format("%.2f", 100 * prod.contrib), cls,
                                "Out");
                    } else {
                        html.writeCell("", "empty", "");
                        html.writeCell("", "empty", "");
                        html.writeCell("", "empty", "");
                    }
                    html.write("</tr>\n");
                    if (cls.equals("first")) {
                        cls = "second";
                    } else
                        cls = "first";
                }
                // html.write("</tr>\n");
            }

        }
        html.endTable();
        html.write("<h2>Ww = " + String.format("%.0f %%", 100 * Ww)
                + ": combining " + String.format("%.0f ", 100 * Ww)
                + "weighted by RV1p + "
                + String.format("%.0f ", 100 * (1 - Ww)) + "unweighted"
                + "</h2>\n");
        html.write("<h2>Wv = " + String.format("%.0f %%", 100 * Wv)
                + ": combining " + String.format("%.0f ", 100 * Wv) + "RVp + "
                + String.format("%.0f ", 100 * (1 - Wv)) + "RNp" + "</h2>\n");
        html.close();
    }

    /**
     * Make Product Data reports.
     * 
     * @throws IOException
     */
    protected void reportProducts() throws IOException {
        Html5Writer html = null;
        String product = "";
        String cls = "first";
        for (SortedMap.Entry<String, ProdCumulate> entry : products.entrySet()) {
            String[] key = entry.getKey().split("\t");
            if (!product.equals(key[0])) { // new product
                if (html != null) {
                    html.endTable();

                    html.write("<h2>Ww = " + String.format("%.0f %%", 100 * Ww)
                            + ": combining " + String.format("%.0f ", 100 * Ww)
                            + "weighted by RV1p + "
                            + String.format("%.0f ", 100 * (1 - Ww))
                            + "unweighted" + "</h2>\n");
                    html.close();
                }
                cls = "first";
                product = key[0];
                html = new Html5Writer(false, pFileName(product), product);

                html.beginTable("demoTbl");
                html.write("<thead>\n");
                html.write("<tr>\n");

                html.writeHdCell("Date", "tableHead", "HdDate");
                html.writeHdCell("RI", "tableHead", "HdOut");
                html.writeHdCell("Level", "tableHead", "HdOut");
                html.writeHdCell("Contr%", "tableHead", "HdOut");
                html.writeHdCell("RNp%", "tableHead", "HdSup");
                html.writeHdCell("RVp%", "tableHead", "HdSup");
                html.writeHdCell("", "empty", "");
                for (int i = 0; i < 5; i++)
                    html.writeHdCell((i + 1) + "", "tableHead", "HdClient");

                html.write("</tr>\n");
                html.write("</thead>\n");
            }
            ProdCumulate c = entry.getValue();
            html.write("<tr>\n");
            html.writeCell(c.getDate(), cls, "Date");
            double cumRI = c.getRI(Ww);
            html.writeCell(String.format("%.2f", cumRI), cls, "Out");
            html.writeCell(DiscretizeProductRI(cumRI), cls, "Out");
            html.writeCell(String.format("%.2f", 100 * c.getRXp(Wv)), cls,
                    "Out");
            html.writeCell(String.format("%.4f", 100 * c.getcRNp()), cls, "Sup");
            html.writeCell(String.format("%.4f", 100 * c.getcRVp()), cls, "Sup");
            Distribution wRI1 = c.getwRI1();
            double w;
            html.writeHdCell("", "empty", "");
            for (int i = 0; i < 5; i++) {
                if (i < wRI1.size()) {
                    w = wRI1.getValue(i);
                } else
                    w = 0.0;
                html.writeCell(String.format("%.0f", 100 * w), cls, "Sup");
            }
            html.write("</tr>\n");
            if (cls.equals("first")) {
                cls = "second";
            } else
                cls = "first";
        }
        if (html != null) {
            html.endTable();

            html.write("<h2>Ww = " + String.format("%.0f %%", 100 * Ww)
                    + ": combining " + String.format("%.0f ", 100 * Ww)
                    + "weighted by RV1p + "
                    + String.format("%.0f ", 100 * (1 - Ww)) + "unweighted"
                    + "</h2>\n");
            html.close();
        }
    }

    /**
     * Write one table header cell with contents "str", class "cls" and id "id".
     */
    private String hdCell(String str, String cls, String id) {

        String tmp = "";
        if (cls != "") {
            cls = "class=\"" + cls + "\" ";
        }
        if (id != "") {
            id = "id=\"" + id + "\" ";
        }
        tmp += "<th " + cls + id + ">";
        tmp += str + " ";
        tmp += "</th>\n";
        return tmp;
    }

    /**
     * Write one table header cell with contents "str", class "cls" and id "id".
     */
    private String cell(String str, String cls, String id) {

        String tmp = "";
        if (cls != "") {
            cls = "class=\"" + cls + "\" ";
        }
        if (id != "") {
            id = "id=\"" + id + "\" ";
        }
        tmp += "<td " + cls + id + ">";
        tmp += str + " ";
        tmp += "</td>\n";
        return tmp;
    }

    /**
     * Make Product/Client Data reports.
     * 
     * @throws IOException
     */
    protected void reportProductsClients() throws IOException {
        // DONE
        Html5Writer html = null;
        String counterpart = "";
        String product = "";
        String client = "";
        String cls = "";
        StringBuffer fixed = new StringBuffer();
        ArrayList<Entry<String, Data>> entries = new ArrayList<Entry<String, Data>>();

        for (Iterator<Entry<String, Data>> it = prod_client.entrySet()
                .iterator(); it.hasNext();)
            entries.add(it.next());
        Entry<String, Data> entry, tmp;
        for (int i = 0; i < entries.size(); i++) {
            entry = entries.get(i);

            String[] key = entry.getKey().split("\t");
            if (!counterpart.equals(key[0]) || !product.equals(key[1])
                    || !client.equals(key[2])) {
                if (html != null) {
                    fixed.append("</table>\n");
                    html.write(fixed.toString());
                    html.write("</div>\n");
                    html.close();
                }
                fixed = new StringBuffer();
                cls = "first";
                counterpart = key[0];
                product = key[1];
                client = key[2];
                html = new Html5Writer(true, pcFileName(counterpart, product,
                        client), product + " with " + client);

                html.write("<div id=\"tables\">\n");

                fixed.append("<table id=\"demoTbl\">\n");

                fixed.append("<thead>\n");

                fixed.append("<tr>\n");

                fixed.append(hdCell("Counterpart", "tableHead fixed",
                        "HdCounterpart"));
                fixed.append(hdCell("Product", "tableHead fixed", "HdProduct"));
                fixed.append(hdCell("Client", "tableHead fixed", "HdClient"));
                fixed.append(hdCell("Date", "tableHead rightCell fixed",
                        "HdDate"));
                fixed.append(hdCell("", "empty", ""));

                // QUALITATIVE AGGREGATES
                fixed.append(hdCell("qRI", "tableHead qual", "HdQualMainOut"));
                fixed.append(hdCell("qPM", "tableHead rightCell qual",
                        "HdQualAggOut"));
                fixed.append(hdCell("", "empty", ""));
                // SENTIMENT
                fixed.append(hdCell("qS", "tableHead sent", "HdQualSentOut"));
                fixed.append(hdCell("S", "tableHead sent", "HdSentOut"));
                fixed.append(hdCell("Slp", "tableHead sent", "HdSentInp"));
                fixed.append(hdCell("Ssp", "tableHead rightCell sent",
                        "HdSentInp"));
                fixed.append(hdCell("", "empty", ""));
                // PERFORMANCE
                fixed.append(hdCell("qP", "tableHead perf", "HdQualPerfOut"));
                fixed.append(hdCell("P", "tableHead perf", "HdPerfInp"));
                fixed.append(hdCell("PP", "tableHead perf", "HdPerfInp"));
                fixed.append(hdCell("BP", "tableHead perf", "HdPerfInp"));
                fixed.append(hdCell("dB", "tableHead rightCell perf",
                        "HdPerfInp"));
                fixed.append(hdCell("", "empty", ""));
                // MISMATCHING
                fixed.append(hdCell("qM", "tableHead mismatch", "HdQualMismOut"));
                fixed.append(hdCell("dM", "tableHead mismatch", "HdMismOut"));
                fixed.append(hdCell("SRI", "tableHead mismatch", "HdMismInp"));
                fixed.append(hdCell("RP", "tableHead rightCell mismatch",
                        "HdMismInp"));
                fixed.append(hdCell("", "empty", ""));
                // CUSTOMER VOLUMES
                fixed.append(hdCell("qRV1c", "tableHead cust", "HdQualCVolOut"));
                fixed.append(hdCell("RV1c%", "tableHead cust", "HdCVolOut"));
                fixed.append(hdCell("RVc%", "tableHead cust", "HdCVolOut"));
                fixed.append(hdCell("V1", "tableHead cust", "HdCVolInp"));
                fixed.append(hdCell("Vc", "tableHead rightCell cust",
                        "HdCVolInp"));
                fixed.append(hdCell("", "empty", ""));
                // PRODUCT VOLUMES
                fixed.append(hdCell("RNp%", "tableHead prod", "HdPVolOut"));
                fixed.append(hdCell("Np", "tableHead prod", "HdPVolInp"));
                fixed.append(hdCell("TN", "tableHead prod", "HdPVolInp"));
                fixed.append(hdCell("RV1p%", "tableHead prod", "HdPVolOut"));
                fixed.append(hdCell("RVp%", "tableHead prod", "HdPVolOut"));
                fixed.append(hdCell("Vp", "tableHead prod", "HdPVolInp"));
                fixed.append(hdCell("TA", "tableHead rightCell prod",
                        "HdPVolInp"));

                fixed.append("</tr>\n");

                fixed.append("</thead>\n");
            }
            boolean last = false;
            if (i + 1 < entries.size()) {
                tmp = entries.get(i + 1);
                String[] keyt = tmp.getKey().split("\t");
                if (!counterpart.equals(keyt[0]) || !product.equals(keyt[1])
                        || !client.equals(keyt[2])) {

                    last = true;
                }
            }
            fixed.append("<tr>\n");

            Data d = entry.getValue();

            fixed.append(cell(d.counterpart, (last ? "bottomCell " : " ") + cls
                    + "fixed", "Date"));
            fixed.append(cell(d.product, (last ? "bottomCell " : " ") + cls
                    + "fixed", "Date"));
            fixed.append(cell(d.client, (last ? "bottomCell " : " ") + cls
                    + "fixed", "Date"));
            fixed.append(cell(d.date, "rightCell"
                    + (last ? "bottomCell " : " ") + cls + "fixed", "Date"));
            fixed.append(cell("", "empty", ""));

            fixed.append(cell(String.format("%d", d.qRI1 + 1), cls + "qual"
                    + (last ? " bottomCell" : ""), "QualMainOut"));
            fixed.append(cell(String.format("%d", d.qPM + 1), cls
                    + "qual rightCell" + (last ? "bottomCell" : ""),
                    "QualAggOut"));
            fixed.append(cell("", "empty", ""));

            fixed.append(cell(String.format("%d", d.qS + 1), cls + "sent"
                    + (last ? " bottomCell" : ""), "QualSentOut"));
            fixed.append(cell(String.format("%.4f", d.S), cls + "sent"
                    + (last ? " bottomCell" : ""), "SentOut"));
            fixed.append(cell(String.format("%.4f", d.Slp), cls + "sent"
                    + (last ? " bottomCell" : ""), "SentInp"));
            fixed.append(cell(String.format("%.4f", d.Ssp), cls + "sent"
                    + " rightCell" + (last ? "bottomCell" : ""), "SentInp"));
            fixed.append(cell("", "empty", ""));

            fixed.append(cell(String.format("%d", d.qP + 1), cls + "perf"
                    + (last ? " bottomCell" : ""), "QualPerfOut"));
            fixed.append(cell(String.format("%.2f", d.P), cls + "perf"
                    + (last ? " bottomCell" : ""), "PerfInp"));
            fixed.append(cell(String.format("%.2f", d.PP), cls + "perf"
                    + (last ? " bottomCell" : ""), "PerfInp"));
            fixed.append(cell(String.format("%.2f", d.BP), cls + "perf"
                    + (last ? " bottomCell" : ""), "PerfInp"));
            fixed.append(cell(String.format("%.2f", d.dB), cls + "perf"
                    + " rightCell" + (last ? "bottomCell" : ""), "PerfInp"));
            fixed.append(cell("", "empty", ""));

            fixed.append(cell(String.format("%d", d.qM + 1), cls + "mismatch"
                    + (last ? " bottomCell" : ""), "QualMismOut"));
            fixed.append(cell(String.format("%.0f", d.dM), cls + "mismatch"
                    + (last ? " bottomCell" : ""), "MismOut"));
            fixed.append(cell(String.format("%d", d.SRI), cls + "mismatch"
                    + (last ? " bottomCell" : ""), "MismInp"));
            fixed.append(cell(String.format("%d", d.RP), cls + "mismatch"
                    + " rightCell" + (last ? "bottomCell" : ""), "MismInp"));
            fixed.append(cell("", "empty", ""));

            fixed.append(cell(String.format("%d", d.qRV1c + 1), cls + "cust"
                    + (last ? " bottomCell" : ""), "QualCVolOut"));
            fixed.append(cell(String.format("%.4f", 100 * d.RV1c), cls + "cust"
                    + (last ? " bottomCell" : ""), "CVolOut"));
            fixed.append(cell(String.format("%.4f", 100 * d.RVc), cls + "cust"
                    + (last ? " bottomCell" : ""), "CVolOut"));
            fixed.append(cell(String.format("%d", d.V1), cls + "cust"
                    + (last ? " bottomCell" : ""), "CVolInp"));
            fixed.append(cell(String.format("%d", d.Vc), cls + "cust"
                    + " rightCell" + (last ? "bottomCell" : ""), "CVolInp"));
            fixed.append(cell("", "empty", ""));

            fixed.append(cell(String.format("%.4f", 100 * d.RNp), cls + "prod"
                    + (last ? " bottomCell" : ""), "PVolOut"));
            fixed.append(cell(String.format("%d", d.Np), cls + "prod"
                    + (last ? " bottomCell" : ""), "PVolInp"));
            fixed.append(cell(String.format("%d", d.TN), cls + "prod"
                    + (last ? " bottomCell" : ""), "PVolInp"));
            fixed.append(cell(String.format("%.4f", 100 * d.RV1p), cls + "prod"
                    + (last ? " bottomCell" : ""), "PVolOut"));
            fixed.append(cell(String.format("%.4f", 100 * d.RVp), cls + "prod"
                    + (last ? " bottomCell" : ""), "PVolOut"));
            fixed.append(cell(String.format("%d", d.Vp), cls + "prod"
                    + (last ? " bottomCell" : ""), "PVolInp"));
            fixed.append(cell(String.format("%.0f", d.TA), cls + "prod"
                    + " rightCell" + (last ? "bottomCell" : ""), "PVolInp"));

            fixed.append("</tr>\n");

            if (cls.equals("first")) {
                cls = "second";
            } else
                cls = "first";

        }
        if (html != null) {
            fixed.append("</table>\n");
            html.write(fixed.toString());
            html.write("</div>\n");
            html.close();
        }
    }

    protected void createPrerequisites() throws IOException {
        copy(this.getClass().getClassLoader()
                .getResourceAsStream("eu/first/RIM/webRsc/logo.png"),
                new FileOutputStream(rptDirName + "/logo.png"));
        copy(this.getClass().getClassLoader()
                .getResourceAsStream("eu/first/RIM/webRsc/fp7_logo.png"),
                new FileOutputStream(rptDirName + "/fp7_logo.png"));
        copy(this.getClass().getClassLoader()
                .getResourceAsStream("eu/first/RIM/webRsc/css.css"),
                new FileOutputStream(rptDirName + "/css.css"));
        copy(this.getClass().getClassLoader()
                .getResourceAsStream("eu/first/RIM/webRsc/css_client.css"),
                new FileOutputStream(rptDirName + "/css_client.css"));
        copy(this.getClass().getClassLoader()
                .getResourceAsStream("eu/first/RIM/webRsc/favicon.ico"),
                new FileOutputStream(rptDirName + "/favicon.ico"));
        copy(this.getClass().getClassLoader()
                .getResourceAsStream("eu/first/RIM/webRsc/select.js"),
                new FileOutputStream(rptDirName + "/select.js"));
    }

    /**
     * Make all reports. If successful, invoke browser with the main Index file.
     * 
     * @param rptDirName
     *            Name of folder for storing report files.
     * @throws IOException
     * @throws URISyntaxException
     */
    public void report(String rptDirName) throws IOException,
            URISyntaxException {
        this.rptDirName = rptDirName;
        System.out.println("Writing reports to " + rptDirName);
        prepareDir();// DONE
        createPrerequisites();// DONE
        reportMenu();// DONE
        reportBank();// DONE
        reportProductsClients();// DONE
        reportProducts();// DONE
        reportCounterparts();// DONE

        System.out.println("Invoking browser");
        java.awt.Desktop.getDesktop().browse(new File(bankFileName()).toURI());

        System.out.println("Counterparts : " + counterparts.size());
        System.out.println("Products     : " + products.size());
        System.out.println("Prods/Clients: " + prod_client.size());
    }

    /**
     * Prepare external folder for writing reports. The folder must exist. All
     * existing *.html files are deleted from the folder.
     * 
     * @throws IOException
     *             When report folder is not a directory or existing html files
     *             cannot be deleted from it.
     */
    protected void prepareDir() throws IOException {
        super.prepareDir();
        File dir = new File(rptDirName);
        String[] info = dir.list();
        for (int i = 0; i < info.length; i++) {
            File n = new File(rptDirName + File.separator + info[i]);
            if (!n.isFile()) {
                continue;
            }
            if (info[i].endsWith("css.css")
                    || info[i].endsWith("css_client.css")
                    || info[i].endsWith("select.js")
                    || info[i].endsWith("logo.png")
                    || info[i].endsWith("logo_fp7.png")) {
                System.out.println("Removing " + n.getPath());
                if (!n.delete())
                    throw new IOException("Couldn't remove " + n.getPath());
            }

        }
    }

    /**
     * Returns the name of menu html file.
     * 
     * @return
     */
    protected String menuFileName() {
        return rptDirName + "/menu.html";
    }

    /**
     * Make menu report.
     * 
     * @throws IOException
     */
    private void reportMenu() throws IOException {
        Html5Writer index = new Html5Writer(menuFileName(), true);

        index.write(HtmlWriter.href(bankFileName(), "Bank") + "\n");

        String product = "";
        String counterpart = "";
        String client = "";

        for (SortedMap.Entry<String, Data> entry : prod_client.entrySet()) {

            String[] key = entry.getKey().split("\t");

            if (!counterpart.equals(key[0])) {
                if (client != "") {
                    index.write("</ul>\n");
                    client = "";
                }
                if (product != "") {
                    index.write("</ul>\n");
                    product = "";
                }
                if (counterpart != "") {
                    index.write("</ul>\n");
                }
                index.write("<ul style=\"padding-left:1em;\">\n");
                counterpart = key[0];
                index.write(HtmlWriter.embed("li",
                        HtmlWriter.href(cFileName(counterpart), counterpart))
                        + "\n");
            }
            if (!product.equals(key[1])) { // new product/client
                if (client != "") {
                    index.write("</ul>\n");
                    client = "";
                }
                if (product != "") {
                    index.write("</ul>\n");
                    product = "";
                }
                product = key[1];
                index.write("<ul>\n");
                index.write(HtmlWriter.embed("li",
                        HtmlWriter.href(pFileName(product), product)));
                index.write("\n");
                index.write("<ul>\n");
                client = key[2];
                index.write(HtmlWriter.embed("li", HtmlWriter.href(
                        pcFileName(counterpart, product, client), client)));
                index.write("\n");
            } else if (!client.equals(key[2])) { // new client
                client = key[2];
                index.write(HtmlWriter.embed("li", HtmlWriter.href(
                        pcFileName(counterpart, product, client), client)));
                index.write("\n");
            }

        }
        if (client != "") {
            index.write("</ul>\n");
        }
        if (product != "") {
            index.write("</ul>\n");
        }
        if (counterpart != "") {
            index.write("</ul>\n");
        }

        index.menu = index.sb.toString();
    }

    private static void copy(InputStream input, OutputStream output)
            throws IOException {
        byte[] buffer = new byte[4096];
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
        }
    }

    /**
     * Make Counterpart Data reports.
     * 
     * @throws IOException
     */
    protected void reportCounterparts() throws IOException {
        Html5Writer html = null;
        String counterpart = "";
        String cls = "first";
        for (SortedMap.Entry<String, CtptCumulate> entry : counterparts
                .entrySet()) {
            String[] key = entry.getKey().split("\t");
            if (!counterpart.equals(key[0])) { // new counterpart
                cls = "first";
                if (html != null) {
                    html.write("</table>\n");
                    html.write("<h2>Ww = " + String.format("%.0f %%", 100 * Ww)
                            + ": combining " + String.format("%.0f ", 100 * Ww)
                            + "weighted by RV1p + "
                            + String.format("%.0f ", 100 * (1 - Ww))
                            + "unweighted" + "</h2>\n");
                    html.close();
                }
                counterpart = key[0];
                html = new Html5Writer(false, cFileName(counterpart),
                        counterpart);

                html.write("<table id=\"demoTbl\">\n");

                html.write("<thead>\n");

                html.write("<tr>\n");

                html.writeHdCell("Date", "tableHead", "HdDate");
                html.writeHdCell("RI", "tableHead", "HdOut");
                html.writeHdCell("Level", "tableHead", "HdOut");
                html.writeHdCell("Sent", "tableHead", "HdSup");
                html.writeHdCell("Contr%", "tableHead", "HdSup");
                html.writeHdCell("RNp%", "tableHead", "HdSup");
                html.writeHdCell("RVp%", "tableHead", "HdSup");

                if (COUNTERPART > 0) {
                    html.writeHdCell("", "empty", "");
                    html.writeHdCell("Product", "tableHead", "HdOut");
                    html.writeHdCell("RI", "tableHead", "HdOut");
                    html.writeHdCell("Contr%", "tableHead", "HdOut");
                }
                html.write("</tr>\n");
                html.write("</thead>\n");
            }
            CtptCumulate c = entry.getValue();
            html.write("<tr>\n");
            html.writeCell(c.getDate(), cls, "Date");
            // double cumRI = c.getRI(Ww); // RI calculated from distributions
            double cumRI = c.getRI(Ww, Wv); // RI calculated from products
            html.writeCell(String.format("%.2f", cumRI), cls, "Out");
            html.writeCell(DiscretizeProductRI(cumRI), cls, "Out");
            html.writeCell(String.format("%.2f", c.S), cls, "Out");
            html.writeCell(String.format("%.2f", 100 * c.getRXp(Wv)), cls,
                    "Out");
            html.writeCell(String.format("%.4f", 100 * c.getcRNp()), cls, "Sup");
            html.writeCell(String.format("%.4f", 100 * c.getcRVp()), cls, "Sup");
            BankCumulate bc = bank.get(key(c.getDate()));

            if (COUNTERPART == 0 || bc == null) {
                html.write("</tr>\n");
                if (cls.equals("first")) {
                    cls = "second";
                } else
                    cls = "first";
            } else {
                ProdCumulate[] sortedProds = bc.sortedProducts(Wv, counterpart);
                int lines = Math.min(COUNTERPART, sortedProds.length);
                for (int i = 0; i < lines; i++) {
                    if (i > 0) {
                        html.write("<tr>\n");
                        for (int j = 0; j < 7; j++) {
                            html.writeCell("", "empty", "");
                        }
                    }
                    html.writeCell("", "empty", "");
                    ProdCumulate prod = sortedProds[i];
                    html.writeCell(prod.getProducts()[0], cls, "Out");
                    html.writeCell(String.format("%.2f", prod.getRI(Ww)), cls,
                            "Out");
                    html.writeCell(String.format("%.2f", 100 * prod.contrib),
                            cls, "Out");
                    html.write("</tr>\n");
                    if (cls.equals("first")) {
                        cls = "second";
                    } else
                        cls = "first";
                }
                html.write("</tr>\n");
            }

        }
        if (html != null) {
            html.write("</table>\n");
            html.write("<h2>Ww = " + String.format("%.0f %%", 100 * Ww)
                    + ": combining " + String.format("%.0f ", 100 * Ww)
                    + "weighted by RV1p + "
                    + String.format("%.0f ", 100 * (1 - Ww)) + "unweighted"
                    + "</h2>\n");
            html.close();
        }
    }

}
