////////////////////////////////////////////////////////////////////////////////
//JDEXi2:	Implements evaluation of decision alternatives based on
//			qualitative multi-attribute models produced by DEXi software
//			http://kt.ijs.si/MarkoBohanec/dex.html)
//
//			Copyright (C) 2012 Marko Bohanec, Jožef Stefan Institute (www.ijs.si)
//			Authors: dr. Marko Bohanec, Dusan Omercevic, Andrej Kogovsek
//			(http://kt.ijs.si/MarkoBohanec/jdexi2.html)
//
//JDEXi2 is based on JDEXi library:
//
//			Copyright (C) 2004 Asobi d.o.o. (www.asobi.si)
//			Authors: dr. Marko Bohanec, Dusan Omercevic, Andrej Kogovsek
//			(http://kt.ijs.si/MarkoBohanec/jdexi.html)
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
//Version 2.0 2012-10-30:
//		General changes:
//			port to java 1.7
//			using templates
//			class Vector --> ArrayList
//			
////////////////////////////////////////////////////////////////////////////////

package test.JDEXi;

import junit.framework.TestCase;

import si.JDEXi.Variable;
import si.JDEXi.VariableList;

/**
 * @author Andrej Kogov\u0161ek, Du\u0161an Omer\u010Devi\u0107, Marko Bohanec
 * @version 2.0
 */
@SuppressWarnings(value={"all"}) 
public class VariableListTest extends TestCase {
    public void testVariableList() {
        // Names
        String name1 = "name1";
        String name2 = "name2";
        String name3 = "name3";

        // Values
        String value1 = "value1";
        String value2 = "value2";
        String value3 = "value3";

        // Hashes
        String hash1 = name1 + "=" + value1;
        String hash2 = name2 + "=" + value2;
        String hash3 = name3 + "=" + value3;
        String list = hash1 + ";" + hash2 + ";" + hash3;

        VariableList variableList = new VariableList(list);
        Variable variable = variableList.findVariable(name1);
        assertEquals("Invalid name", name1, variable.getName());
        assertEquals("Invalid value", value1, variable.getValue());

        variable = variableList.findVariable(name2);
        assertEquals("Invalid name", name2, variable.getName());
        assertEquals("Invalid value", value2, variable.getValue());

        variable = variableList.findVariable(name3);
        assertEquals("Invalid name", name3, variable.getName());
        assertEquals("Invalid value", value3, variable.getValue());
    }

    public void testInvalidVariableList() {
        //  Names
        String name1 = "name1";
        String name2 = "name2";

        // Values
        String value1 = "value1";
        String value2 = "";

        // Hashes
        String hash1 = name1 + "=" + value1;
        String hash2 = name2 + "=" + value2;
        String list = hash1 + ";" + hash2 + ";";

        try {
            VariableList variableList = new VariableList(list);
            fail("Invalid value");
        } catch (Exception e) {
        }
    }

    // testiraj 2x isti name
    public void testVariableName() {
        // Names
        String name1 = "name";
        String name2 = "name";

        // Values
        String value1 = "value1";
        String value2 = "value2";

        // Hashes
        String hash1 = name1 + "=" + value1;
        String hash2 = name2 + "=" + value2;
        String list = hash1 + ";" + hash2 + ";";

        try {
            VariableList variableList = new VariableList(list);
            fail("Invalid value");
        } catch (Exception e) {
            e.getMessage();
        }
    }
}
