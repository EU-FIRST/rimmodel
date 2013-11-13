////////////////////////////////////////////////////////////////////////////////
//JDEXi2:   Implements evaluation of decision alternatives based on
//          qualitative multi-attribute models produced by DEXi software
//          (http://kt.ijs.si/MarkoBohanec/dexi.html)
//
//          Authors: Marko Bohanec, Dusan Omercevic, Andrej Kogovsek
//          (http://kt.ijs.si/MarkoBohanec/jdexi.html)
//
//JDEXi2 library is free software; you can redistribute it and/or
//modify it under the terms of the GNU Lesser General Public
//License as published by the Free Software Foundation; either
//version 2.1 of the License, or (at your option) any later version.
//
//This library is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
//Lesser General Public License for more details.
//
//You should have received a copy of the GNU Lesser General Public
//License along with this library; if not, write to the Free Software
//Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
////////////////////////////////////////////////////////////////////////////////


package si.JDEXi;

import java.io.IOException;

import si.JDEXi.Model;

/**
 * Evaluate class. Illustrates a typical use of JDEXi2.
 * 
 * @author Marko Bohanec
 * @version 2.0
 */
public final class Eval {

	/**
	 * Main class
	 * 
	 * @param args
	 *            Input parameters
	 */
	public static void main(String[] args) {
		if (args.length != 2) {
			System.out.println("JDEX2.Eval: Invalid input arguments");
			System.out.println("Usage: DEXi_file_name variables");
			System.out.println("variables should contain a \";\"-separated list of name=value pairs");
			System.exit(0);
		}

		String modelFile = args[0];
		String variables = args[1];
		
		System.out.println("JDEXi.Evaluate " + modelFile + " " + variables);

		try {
			Model model = new Model(Model.loadFile(modelFile));
			System.out.println("\nAttributes:");
			System.out.println("  Basic: " + model.attributesToString(model.basic));
			System.out.println("  Aggregate: "
					+ model.attributesToString(model.aggregate));
			System.out.println("  Linked: " + model.attributesToString(model.linked));

			System.out.println("\nDEXi evaluation results:");
			model.clearInputValues();
			model.setInputValuesByNames(variables);
			model.evaluate(Model.Evaluation.SET, true);

			System.out.println("Output values: " + model.getOutputValues());

		} catch (IOException e) {
			System.out.println("IO Exception: " + e.getMessage());
		} catch (Exception e) {
			System.out.println("Exception: " + e.getMessage());
		}
	}

}

