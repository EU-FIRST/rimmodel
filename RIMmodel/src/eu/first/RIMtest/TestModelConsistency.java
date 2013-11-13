package eu.first.RIMtest;

import si.JDEXi.Model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Testing the consistency of Delphi-based and java-based evaluations of
 * RIMreverse.dxi model.
 * 
 * All combinations of input parameter values are checked. DEXi evaluations from
 * Delphi are read from an external file and compared with java-based
 * evaluations carried out here using si.JDEXi.* v2.0.
 * 
 * @thanks EU FP7 project FIRST (FP7-ICT-257928): Large scale information
 *         extraction and integration infrastructure for supporting financial
 *         decision making.
 * 
 * @author Marko Bohanec (marko.bohanec@ijs.si)
 * @version 1.0
 * @since 2012-10-18
 */
public final class TestModelConsistency {

	/**
	 * Main method.
	 * 
	 * @param args
	 *            Not used
	 */
	public static void main(final String[] args) {
		try {
			String dxi = Model.loadFile("./RIM1.dxi");
			Model model = new Model(dxi);

			System.out.println("Basic: "
					+ model.attributesToString(model.basic));
			System.out.println("Aggregate: "
					+ model.attributesToString(model.aggregate));
			System.out.println("Linked: "
					+ model.attributesToString(model.linked));
			System.out.println();

			BufferedReader in = new BufferedReader(new FileReader(
					"./RIMreverseAllEvals.dat"));
			String dat;
			int[] inp = new int[model.basic.size()];
			int testCount = 0;
			int errCount = 0;

			while ((dat = in.readLine()) != null) {
				// System.out.println(dat);
				testCount++;
				int val = Integer.parseInt(dat.substring(7, 8));
				for (int i = 0; i < inp.length; i++) {
					String s = dat.substring(i, i + 1);
					inp[i] = Integer.parseInt(s);
				}
				model.clearInputValues();
				model.setInputValues(inp);
				model.evaluate();
				// System.out.println(model.getOutputValue(0).getOrdinal());
				if (val != model.getOutputValue(0).getOrdinal()) {
					System.out.println("Difference at " + dat + " "
							+ model.getOutputValue(0).getOrdinal());
					errCount++;
				}
			}
			in.close();
			System.out.println(String.format("%d tests, %d errors", testCount,
					errCount));

		} catch (IOException e) {
			System.out.println("Can't read file");
			System.out.println("Error: " + e.getMessage());
		} catch (Exception e) {
			System.out.println("Invalid parameters");
			System.out.println("Error: " + e.getMessage());
		}
	}

}
