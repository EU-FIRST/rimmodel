package eu.first.RIM;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Html5Writer extends HtmlWriter {
	
	static String menu="";
	public Html5Writer(boolean isClient, String fileName, String reportName)
	{
		super(fileName);
		html5Head(isClient, reportName);
		
	}
	
	public Html5Writer(String fileName, boolean empty)
	{
		super(fileName);
	}

	
	/**
	 * Writes html head.
	 * 
	 * @param head
	 */
	private void html5Head(boolean isClient, String reportName) {
		sb.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n");
		sb.append("<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\" lang=\"en\" dir=\"ltr\">\n");
		sb.append("<head>\n");
		sb.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />\n");
		sb.append("<title>Reputational Index Model Report</title>\n");
		sb.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />\n");
		sb.append("<meta name=\"keywords\" content=\"FIRST, project, EU, FP7, finance, semantics, ontology, sentiment, pipeline, large-scale, scaling, decision-making, reputational, market, surveillance, brokerage, retail, banking, blogs, unstructured\" />\n");
		sb.append("<meta name=\"copyright\" content=\"FIRST Consortium\" />\n");
		sb.append("<meta name=\"description\" content=\"FIRST develops and provides a large scale information extraction and integration infrastructure which will assist in various ways during the process of financial decision making. This area is extraordinarily faced with the challenges of extremely large, dynamic, and heterogeneous sources of information. The daily work and the business success of all decision makers in this industry depend on the availability of highly trustable, easily acquirable information. FIRST: Opening up new before-the-fact information for earlier and better treatment of evolving conditions in advanced financial decision making\" />\n");
		sb.append("<meta name=\"abstract\" content=\"The EU-funded FP7 FIRST project provides a large-scale information extraction, information integration and decision making infrastructure for information management in the financial domain\" />\n");

		sb.append("<link rel=\"shortcut icon\" href=\"./favicon.ico\" type=\"image/x-icon\" />\n");
		sb.append("<link type=\"text/css\" rel=\"stylesheet\" href=\"./css"+(isClient?"_client":"")+".css\" />\n");
		sb.append("</head>\n");
		sb.append("<body>\n");
		sb.append("<script type=\"text/javascript\"\n" + 
				"		src=\"http://code.jquery.com/jquery-1.8.2.js\"></script>\n" + 
				"	<script type=\"text/javascript\" src=\"./select.js\"></script>\n");				
		sb.append("<div id=\"page\">\n" + 
				"		<div id=\"header\">\n" + 
				"			<div id=\"header-inner\">\n" + 
				"				<div id=\"logo\">\n" + 
				"					 <img src=\"./logo.png\" alt=\"Home\" height=\"108px\" />\n" + 
				"				</div>\n" + 
				"				<div id=\"heading\">Reputational Index Model Report</div>\n" + 
				
				"				<div id=\"fp7-logo\">\n" + 
				"					<a href=\"http://cordis.europa.eu/fp7/home_en.html\"\n" + 
				"						title=\"European Commission\"> <img src=\"./fp7_logo.png\"\n" + 
				"						alt=\"FP7 logo\" title=\"fp7logo\" />\n" + 
				"					</a>\n" + 
				"				</div>\n" + 
				"				<div id=\"heading-inner\">Report for: "+reportName+"</div>\n" + 
				"			</div>\n" + 
				"		</div>\n" + 
				"\n" + 
				"		<div id=\"content-outer\">\n" + 
				"           <div id=\"menu\">\n" +menu+						"				</div>\n" + 
				"			<div id=\"content\">\n");		
		sb.append("<div id=\"selectall\"><a href=\"#\" onclick=\"selectAllSimple();\">Select All</a></div>\n");
	}
	
	/**
	 * Write one table header cell with contents "str", class "cls" and id "id".
	 */
	public void writeHdCell(String str, String cls, String id) {

		if (cls != "") {
			cls = "class=\"" + cls + "\" ";
		}
		if(id!="")
		{
			id="id=\""+id+"\" ";
		}
		sb.append("<th " + cls +id+ ">");
		sb.append(str+" ");
		sb.append("</th>\n");
	}
	
	/**
	 * Write one table cell with contents "str", class "cls" and id "id".
	 */
	public void writeCell(String str, String cls, String id) {

		if (cls != "") {
			cls = "class=\"" + cls + "\" ";
		}
		if(id!="")
		{
			id="id=\""+id+"\" ";
		}
		sb.append("<td " + cls +id +">");
		sb.append(str+" ");
		sb.append("</td>\n");
	}
	
	/**
	 * Close HtmlWriter. Adds html file tail and writes the whole file contents
	 * to external file.
	 * 
	 * @throws IOException
	 */
	public void close() throws IOException {
		htmlTail();
		stringToFile(sb.toString(), fileName);		
		//System.out.println("Written " + fileName);
	}
	/**
	 * Writes html tail.
	 */	
	private void htmlTail() {
		Date date = Calendar.getInstance().getTime();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		sb.append("</div>\n" + 
		"		</div>\n" + 
		"		<div id=\"footer\">"+(sdf.format(date))+" &copy; EU FIRST RIM</div>\n" + 
		"	</div>\n" + 
		"</body>\n" + 
		"</html>");
		
	}
	
	/**
	 * Close HtmlWriter. Writes the whole file contents
	 * to external file.
	 * 
	 * @throws IOException
	 */
	public void writeToFile() throws IOException {		
		stringToFile(sb.toString(), fileName);		
		System.out.println("Written " + fileName);
	}
	
	
	
}
