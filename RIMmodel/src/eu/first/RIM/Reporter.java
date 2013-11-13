package eu.first.RIM;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.SortedMap;
import java.util.TreeMap;

import eu.first.RIM.Data;
import eu.first.RIM.ProdCumulate;
import eu.first.RIM.BankCumulate;

/**
 * Reporter class. Has three functions: (1) storing results of all
 * product/client data (input and output data as stored in a Data object), (2)
 * accumulating statistic at a counterpart, product, client and bank level and
 * (3) making external reports.
 * 
 * @author Marko Bohanec (marko.bohanec@ijs.si)
 * @version 1.0
 * @since 2012-10-22
 * 
 */
public class Reporter {

    public int dupErrors=0;

    /**
     * Product/client time series (sorted by date in string format).
     */
    protected SortedMap<String, Data> prod_client;

    /**
     * CtptCumulate time series (sorted by date in string format).
     */
    protected SortedMap<String, CtptCumulate> counterparts;

    /**
     * ProdCumulate time series (sorted by date in string format).
     */
    protected SortedMap<String, ProdCumulate> products;

    /**
     * BankCumulate time series (sorted by date in string format).
     */
    protected SortedMap<String, BankCumulate> bank;

    /**
     * Folder for reports.
     */
    protected String rptDirName = "";

    /**
     * Constructor. Prepares private holders for data and statistics.
     */
    public Reporter() {
        prod_client = new TreeMap<String, Data>();
        products = new TreeMap<String, ProdCumulate>();
        counterparts = new TreeMap<String, CtptCumulate>();
        bank = new TreeMap<String, BankCumulate>();
    }

    /**
     * Generates a default SortedMap key from String arguments.
     * 
     * @param strings
     * @return Tab-delimited string of arguments.
     */
    protected String key(String... strings) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < strings.length; i++) {
            if (i > 0)
                sb.append("\t");
            sb.append(strings[i]);
        }
        return sb.toString();
    }

    /**
     * Add Data object to corresponding SortedMap.
     * 
     * @param data
     * @param map
     */
    protected void reportData(Data data, SortedMap<String, Data> map) {
        String key = key(data.counterpart, data.product, data.client, data.date);
        Data olddata = map.get(key);
        if (olddata == null) {

// IMPORTANT: USE ONLY ONE OF THE FOLOWING map.put LINES

// the following line is required to generate product_client html reports
 map.put(key, data);

// the following line is used just for bookkeeping of processed product_client items
// no data is stored for product_client html reports
// using this variant of map.put uses significantly less heap space
//            map.put(key, null); // just bookkeeping, no data is saved for product_client html reports

        } else {
//           System.out
//                    .println("RIM.Reporter.reportData: Duplicate data item, ignored: "
//                            + key);
            dupErrors++;
        }
    }

    /**
     * CtptCumulate Data into SortedMap products.
     * 
     * @param data
     */
    private CtptCumulate cumulateCounterpart(Data data, ProdCumulate p) {
        String key = key(data.counterpart, data.date);
        CtptCumulate cumul = counterparts.get(key);
        if (cumul == null) {
            cumul = new CtptCumulate();
            counterparts.put(key, cumul);
        }
        cumul.cumulate(data);
        cumul.addProduct(p);
        return cumul;
    }

    /**
     * ProdCumulate Data into SortedMap products.
     * 
     * @param data
     */
    private ProdCumulate cumulateProduct(Data data) {
        String key = key(data.product, data.date);
        ProdCumulate cumul = products.get(key);
        if (cumul == null) {
            cumul = new ProdCumulate();
            products.put(key, cumul);
        }
        cumul.cumulate(data);
        return cumul;
    }

    /**
     * ProdCumulate Data into SortedMap bank.
     * 
     * @param data
     */
    private BankCumulate cumulateBank(ProdCumulate prod, CtptCumulate ctpt) {
        String key = key(prod.getDate());
        BankCumulate cumul = bank.get(key);
        if (cumul == null) {
            cumul = new BankCumulate();
            bank.put(key, cumul);
        }
        cumul.cumulate(prod, ctpt);
        return cumul;
    }

    /**
     * Perform all operations of saving and cumulating a single Data object.
     * 
     * @param data
     */
    public void cumulate(Data data) {
        reportData(data, prod_client);
        ProdCumulate p = cumulateProduct(data);
        CtptCumulate c = cumulateCounterpart(data,p);
        cumulateBank(p, c);
    }

    /**
     * Perform post-cumulate operations.
     * 
     * @param data
     */
    public void postCumulate() {
        for (SortedMap.Entry<String, BankCumulate> entry : bank.entrySet()) {
            BankCumulate b = entry.getValue();
            b.calcWeights();
        }
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
        File dir = new File(rptDirName);
        if (!dir.isDirectory()) {
            throw new IOException("Directory " + rptDirName + " does not exist");
        }
        String[] info = dir.list();
        for (int i = 0; i < info.length; i++) {
            File n = new File(rptDirName + File.separator + info[i]);
            if (!n.isFile()) {
                continue;
            }
            if (!info[i].endsWith(".html")) {
                continue;
            }
            System.out.println("Removing " + n.getPath());
            if (!n.delete())
                throw new IOException("Couldn't remove " + n.getPath());
        }
    }

    /**
     * Used to make a file name from other names. All non-alphanumeric
     * characters are replaced by an underline.
     * 
     * @param s
     * @return
     */
    private String underline(String s) {
        return s.replaceAll("[^A-Za-z0-9]", "_");
    }

    /**
     * Returns the name of main Index html file.
     * 
     * @return
     */
    protected String indexFileName() {
        return rptDirName + "/RIM.html";
    }

    /**
     * Returns the name of a Bank Data html file.
     * 
     * @return
     */
    protected String bankFileName() {
        return rptDirName + "/RIM_Bank.html";
    }

    /**
     * Returns the name of a Counterpart Data html file.
     * 
     * @return
     */
    protected String cFileName(String counterpart) {
        return rptDirName + "/RIM_C_" + underline(counterpart) + ".html";
    }

    /**
     * Returns the name of a Product Data html file.
     * 
     * @return
     */
    protected String pFileName(String product) {
        return rptDirName + "/RIM_P_" + underline(product) + ".html";
    }

    /**
     * Returns the name of a Product/Client Data html file.
     * 
     * @return
     */
    protected String pcFileName(String counterpart, String product,
            String client) {
        return rptDirName + "/RIM_PC_" + underline(product) + "_"
                + underline(client) + ".html";
    }

    /**
     * Make Bank Data report.
     * 
     * @throws IOException
     */
    protected void reportBank() throws IOException {
        HtmlWriter html = new HtmlWriter(bankFileName(), "Cumulative Bank Data");
        html.beginTable();
        html.writeHeadRow(BankCumulate.tabbedNames());
        for (SortedMap.Entry<String, BankCumulate> entry : bank.entrySet()) {
            html.writeRow(entry.getValue().tabbedValues());
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
                        "Cumulative Counterpart Data");
                html.write("<h2>Counterpart: " + counterpart + "</h2>\n");
                html.beginTable();
                ;
                html.writeHeadRow(CtptCumulate.tabbedNames());
            }
            html.writeRow(entry.getValue().tabbedValues());
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
                        "Cumulative Product Data");
                html.write("<h2>Product: " + product + "</h2>\n");
                html.beginTable();
                ;
                html.writeHeadRow(ProdCumulate.tabbedNames());
            }
            html.writeRow(entry.getValue().tabbedValues());
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
            if (!product.equals(key[0]) || !client.equals(key[1])) {
                if (html != null) {
                    html.endTable();
                    html.close();
                }
                counterpart = key[0];
                product = key[1];
                client = key[2];
                html = new HtmlWriter(pcFileName(counterpart, product, client),
                        "Product/Client Data");
                html.write("<h2>Counterpart: " + counterpart + "</h2>\n");
                html.write("<h2>Product: " + product + "</h2>\n");
                html.write("<h2>Client: " + client + "</h2>\n");
                html.beginTable();
                html.writeHeadRow(Data.tabbedNames());
            }
            html.writeRow(entry.getValue().tabbedValues());
        }
        if (html != null) {
            html.endTable();
            html.close();
        }
    }

    /**
     * Make main Index report.
     * 
     * @throws IOException
     */
    private void reportIndex() throws IOException {
        HtmlWriter index = new HtmlWriter(indexFileName(), "RIM Report");
        index.write(HtmlWriter.embed("p",
                HtmlWriter.href(bankFileName(), "Bank"))
                + "\n");
        index.write("<ul>\n");
        String counterpart = "";
        String product = "";
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
                index.write("<ul>\n");
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
                        HtmlWriter.href(pFileName(product), product))
                        + "\n");
                index.write("<ul>\n");
                client = key[2];
                index.write(HtmlWriter.embed("li", HtmlWriter.href(
                        pcFileName(counterpart, product, client), client))
                        + "\n");
            } else if (!client.equals(key[2])) { // new client
                client = key[2];
                index.write(HtmlWriter.embed("li", HtmlWriter.href(
                        pcFileName(counterpart, product, client), client))
                        + "\n");
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

        index.close();
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
        prepareDir();
        reportBank();
        reportProductsClients();
        reportProducts();
        reportCounterparts();
        reportIndex();
        System.out.println("Invoking browser");
        //java.awt.Desktop.getDesktop().browse(
          //      new URI("file:///" + indexFileName()));
    }

    protected String DiscretizeProductRI(double prodRI) {
        String result = "";
        if (prodRI <= 1.3)
            result = "low";
        else if (prodRI < 1.6)
            result = "med-low";
        else if (prodRI < 1.9)
            result = "med";
        else if (prodRI < 2.2)
            result = "high";
        else
            result = "v-high";
        return result;
    }

    protected String DiscretizeCumulRI(double cumulRI) {
        String result = "";
        if (cumulRI <= 1.2)
            result = "low";
        else if (cumulRI < 1.3)
            result = "med-low";
        else if (cumulRI < 1.4)
            result = "med";
        else if (cumulRI < 1.6)
            result = "high";
        else
            result = "v-high";
        return result;
    }

}
