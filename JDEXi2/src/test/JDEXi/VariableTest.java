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

/**
 * @author Andrej Kogov\u0161ek, Du\u0161an Omer\u010Devi\u0107, Marko Bohanec
 * @version 2.0
 */
@SuppressWarnings(value={"all"}) 
public class VariableTest extends TestCase {
    public void testVariable() {
        String name = "name1";
        String value = "value1";
        String hash = name + "=" + value;

        Variable variable = new Variable(hash);
        assertEquals("Invalid name value", name, variable.getName());
        assertEquals("Invalid value", value, variable.getValue());
    }

    public void testInvalidVariableValue() {
        String name = "name1";
        String value = "";
        String hash = name + "=" + value;

        try {
            Variable variable = new Variable(hash);
            fail("Invalid value");
        } catch (Exception e) {
        }
    }

    public void testInvalidVariableName() {
        String name = "";
        String value = "value1";
        String hash = name + "=" + value;

        try {
            Variable variable = new Variable(hash);
            fail("Invalid name");
        } catch (Exception e) {
        }
    }

    public void testEmptyString() {
        try {
            Variable variable = new Variable("");
            fail("Input is empty");
        } catch (Exception e) {
        }
    }

    public void testMultiple() {
        try {
            Variable variable = new Variable("==");
            fail("Invalid input parameter");
        } catch (Exception e) {
        }
    }
}
