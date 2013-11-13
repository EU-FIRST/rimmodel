package eu.first.RIM;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * HtmlWriter writes to an output html file. It maintains file name and an
 * internal StringBuffer, which is actually written to external file on close().
 * 
 * @author Marko Bohanec (marko.bohanec@ijs.si)
 * @version 1.0
 * @since 2012-10-22
 * 
 */
public class HtmlWriter {

	/**
	 * External html file name.
	 */
	protected String fileName;

	/**
	 * StringBuffer holding file contents.
	 */
	protected StringBuffer sb;

	/**
	 * Constructor. Prepares file &lt;head&gt; part and opens &lt;body&gt;
	 * 
	 * @param fileName
	 *            String defining html file name.
	 * @param head
	 *            String defining file &lt;title&gt; and &lt;h1&gt;
	 */
	public HtmlWriter(String fileName, String head) {
		this.fileName = fileName;
		sb = new StringBuffer();
		htmlHead(head);
	}	

	/**
	 * Constructor. Prepares file. Used with Html5Writer.
	 * 
	 * @param fileName String defining html file name.
	 */
	public HtmlWriter(String fileName) {
		this.fileName = fileName;
		sb = new StringBuffer();
	}
	
	/**
	 * Writes String literally.
	 * 
	 * @param str
	 */
	public void write(String str) {
		sb.append(str);
	}

	/**
	 * Embeds string in opening and closing tag.
	 * 
	 * @param tag
	 * @param str
	 * @return &lt;tag&gt;str&lt;/tag&gt;
	 */
	public static String embed(String tag, String str) {
		return "<" + tag + ">" + str + "</" + tag + ">";
	}

	/**
	 * Creates html &lt;a&gt; tag with reference.
	 * 
	 * @param ref
	 * @param str
	 * @return &lt;a href="ref"&gt;str&lt;/a&gt;
	 */
	public static String href(String ref, String str) {
		String fileName = new File(ref).getName();
		return "<a href=\"" + fileName + "\">" + str + "</a>";
	}

	/**
	 * Start writing main data table.
	 */
	public void beginTable() {
		// write("<table border=\"1\">\n");
		write("<table>\n");
	}
	
	/**
	 * Start writing main data table.
	 */
	public void beginTable(String id) {
		// write("<table border=\"1\">\n");
		write("<table id=\""+id+"\">\n");
	}

	/**
	 * Stop writing main data table.
	 */
	public void endTable() {
		write("</table>\n");
	}

	/**
	 * Write one table header cell with contents "str" and class "cls".
	 */
	public void writeHdCell(String str, String cls) {

		if (cls != "") {
			cls = "class=\"" + cls + "\" ";
		}
		sb.append("<th " + cls + "align=\"right\">");
		sb.append(str);
		sb.append("</th>");
	}

	/**
	 * Write one table cell with contents "str" and class "cls".
	 */
	public void writeCell(String str, String cls) {

		if (cls != "") {
			cls = "class=\"" + cls + "\" ";
		}
		sb.append("<td " + cls + "align=\"right\">");
		sb.append(str);
		sb.append("</td>");
	}

	/**
	 * Writes a table heading row from a tab-delimited string of column
	 * captions.
	 * 
	 * @param str
	 *            Tab-delimited string.
	 */
	public void writeHeadRow(String str) {
		String[] cols = str.split("\t");
		sb.append("<tr>");
		for (int i = 0; i < cols.length; i++) {
			writeHdCell(cols[i], "");
		}
		sb.append("</tr>\n");
	}

	/**
	 * Writes a table row from tab-delimited string of cells.
	 * 
	 * @param str
	 *            Tab-delimited string.
	 */
	public void writeRow(String str) {
		String[] cols = str.split("\t");
		sb.append("<tr>");
		for (int i = 0; i < cols.length; i++) {
			writeCell(cols[i], "");
		}
		sb.append("</tr>\n");
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
	}

	/**
	 * Writes html head.
	 * 
	 * @param head
	 */
	private void htmlHead(String head) {
		// TODO HTML5
		sb.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" ");
		sb.append("\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n");
		sb.append("<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\" lang=\"en\">\n");
		sb.append("<head>\n");
		sb.append(embed("title", head) + "\n");
		sb.append("<meta name=\"author\" content=\"eu.first.RIM software\"/>\n");
		// TODO <link rel="StyleSheet" href="rim.css" type="text/css"/>
		sb.append("</head>\n");
		sb.append("<body>\n");
		sb.append(embed("h1", head) + "\n");
	}

	

	/**
	 * Writes html tail.
	 */
	private void htmlTail() {
		sb.append("<hr/>\n");
		Date date = Calendar.getInstance().getTime();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		sb.append(sdf.format(date) + " � EU FIRST RIM\n");
		sb.append("</body>\n");
	}

	/**
	 * Writes string contents to an external file.
	 * 
	 * @param str
	 * @param fileName
	 * @throws IOException
	 */
	protected void stringToFile(String str, String fileName) throws IOException {
		File newTextFile = new File(fileName);
        System.out.println("Writing "+fileName);
		FileWriter fw = new FileWriter(newTextFile);
		fw.write(str);
		fw.close();		
	}

}
