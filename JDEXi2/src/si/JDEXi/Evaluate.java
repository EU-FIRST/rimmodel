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

import si.JDEXi.Evaluate;
import si.JDEXi.Model;

/**
 * Evaluate class. Left for compatibility with JDEXi version 1.0, deprecated.
 * @author Andrej Kogov\u0161ek, Du\u0161an Omer\u010Devi\u0107
 * @version 1.0
 * @deprecated
 */
public final class Evaluate {
    
    /**
     * Constructor
     * @param modelFile String object parameter
     * @param attrName String attrName parameter
     * @param variables String variables
     */
    private Evaluate(final String modelFile, final String attrName,
        final String variables) {
        System.out.println("DEXi evaluate");
        System.out.println("==========================");
        System.out.println("Model xml file: " + modelFile);
        System.out.println("Attribute name: " + attrName);
        System.out.println("Variables: " + variables);
        System.out.println("==========================");

        try {
            Model model = new Model(Model.loadFile(modelFile));
        	System.out.println("\nAttributes:");    
        	System.out.println("  Basic: "+model.attributesToString(model.basic));    
        	System.out.println("  Aggregate: "+model.attributesToString(model.aggregate));    
        	System.out.println("  Linked: "+model.attributesToString(model.linked));    

            model.evaluate(attrName,variables);

            System.out.println("Value of " + attrName + " is " + model.getOutputValue(attrName).getName());
            System.out.println("Output values: " + model.getOutputValues());
            
        } catch (IOException e) {
            System.out.println("Can't read file");
            System.out.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Invalid parameters");
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Main class
     * @param args Input parameters
     */
    public static void main(final String[] args) {
        if (args.length != 3) {
            System.out.println("Invalid input arguments!");
            System.out.println("1. Argument is xml model");
            System.out.println("2. Argument is attribute name");
            System.out.println("3. Argument is variables");
            System.exit(0);
        }

        new Evaluate(args[0], args[1], args[2]);
    }
}
