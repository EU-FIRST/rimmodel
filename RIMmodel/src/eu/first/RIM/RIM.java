package eu.first.RIM;

import java.io.File;


/**
 * RIM class.
 * 
 * <p>
 * RIM stands for Reputational Index Model, developed in the framework of EU
 * project FIRST. RIM assesses reputational risk of financial products, which
 * are produced by some bank counterparts and are owned by some bank clients.
 * RIM is a multi-attribute model consisting of qualitative (symbolic) and
 * quantitative (numeric) variables. The qualitative part of RIM has been
 * developed with DEXi software (http://kt.ijs.si/MarkoBohanec/dexi.html). RIM
 * is fully documented in FIRST deliverable D6.2, 2012.
 * </p>
 * 
 * <p>
 * RIM class performs a calculation of Reputational Index based on data read
 * from one or more input files. Results are written to html files.
 * </p>
 * 
 * <p>
 * Usage:<br/>
 * <code>java -jar RIM.jar input_file [input_file...] output_folder</code></br>
 * where:<br/>
 * <code>input_file</code>: tab-delimited text file containing data for
 * product/client pairs.</br> <code>output_folder</code>: folder for generated
 * html reports.
 * </p>
 * 
 * <p>
 * Input files are read by eu.first.DataSource. Files are tab-delimited; each
 * line should contain complete data for a single product/client pair (see
 * eu.first.Data for detailed description of required data). The format of input
 * files must satisfy the requirements specified in
 * eu.first.DelimitedFileDataSource. Data corresponding to different products,
 * clients and dates can appear in any order, as they are collected and sorted
 * later when making reports.
 * </p>
 * 
 * <p>
 * After reading data each product/client pair data from input files, RIM
 * evaluates this data using eu.first.ModelRIM. Obtained results are saved and
 * gradually summarized using the eu.first.Reporter class.
 * </p>
 * 
 * <p>
 * Next, RIM makes reports from accumulated data, using eu.first.Reporter and
 * eu.first.HtmlWriter. Reports are written in the form of html files to the
 * output folder. The folder must exist. All previously existing html files on
 * that folder are deleted. Reports consist of: (1) a main index file, (2)
 * cumulative reports for each product, (3) cumulative reports for each client,
 * and (4) detailed reports for all product/client pairs. All reports contain
 * time series, sorted by date.
 * </p>
 * 
 * <p>
 * Finally, if everything has succeeded, RIM invokes the default Web browser
 * showing the main index report file.
 * </p>
 * 
 * <p>
 * The development of eu.first.RIM.* software was financially supported by EU
 * FP7 project FIRST (FP7-ICT-257928) <i>Large scale information extraction and
 * integration infrastructure for supporting financial decision making</i>.
 * </p>
 * 
 * @author Software: Marko Bohanec (marko.bohanec@ijs.si)
 * @author Model: Giorgio Aprile (MPS), Marko Bohanec (JSI), Maria Costante
 *         (MPS), Morena Foti (MPS), Nejc Trdin (JSI), Martin �nidar�i� (JSI)
 * @version 1.0
 * @since 2012-10-22
 */
public class RIM {

    /**
     * Usage: java -jar RIM.jar input_file [input_file...] output_folder
     * 
     * @param args
     * @throws Exception 
     */
    public static void main(String[] args) throws Exception {

        if (args.length < 2) {
            System.out
                    .println("Usage: java -jar RIM.jar input_file [input_file...] output_folder");
            System.exit(1);
        }
        for (int i = 0; i < args.length - 1; i++) {
            File file = new File(args[i]);
            if (!file.exists()) {
                System.out.println("Input file does not exist: " + args[i]);
                System.exit(2);
            }
        }
        String rptDir = args[args.length - 1].replaceAll("\\\\", "/");
        File dir = new File(rptDir);
        if (!dir.isDirectory()) {
            System.out.println("Output folder does not exist: " + rptDir);
            System.exit(3);
        }

        try {
            int dataCount = 0;
            int dataErrorsCount = 0;
            long start = System.currentTimeMillis();
            ModelRIM rim = new ModelRIM();            
            PrettyHTML5Reporter rpt = new PrettyHTML5Reporter();
            //Reporter rpt = new PrettyReporter();
            int cnt = 0;
            for (int i = 0; i < args.length - 1; i++) {
                System.out.println("Processing file " + args[i]);
                DataSource source = new DelimitedFileDataSource(args[i]);
                while (source.hasNext()) {
                    Data data = source.getNext();
                    cnt++;
                    if (data == null) {
                        System.out.println(String.format(
                                "Error reading data item in line %d: %s", cnt,
                                source.getLastError()));
                        dataErrorsCount++;
                    } else {
                        rim.evaluateProductClient(data);
                        rpt.cumulate(data);                        
                        dataCount++;
                    }
                    if ((cnt % 1000) == 0) {
                      System.out.print(".");
                    }
                }
            }
            System.out.println("\nData items processed: " + cnt);
            
            
            rpt.postCumulate();
            
            rpt.report(rptDir);

            System.out.println("Total number of data items: " + dataCount);
            if (dataErrorsCount > 0) {
                System.out.println("Input data warnings: " + dataErrorsCount);
            }
            System.out.println("Running time: " + (System.currentTimeMillis()-start)+" ms");
            

        } catch (Exception e) {        	
            System.out.println("RIM terminated with error: " + e.getMessage() + "\n" + e);
            throw e;
        }
    }

}
