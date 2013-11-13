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

import si.JDEXi.Model;
import si.JDEXi.Value;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.net.URL;


/**
 * @author Andrej Kogov\u0161ek, Du\u0161an Omer\u010Devi\u0107, Marko Bohanec
 * @version 2.0
 */
@SuppressWarnings(value={"all"})  
public class ModelTest extends TestCase {
    public void testOKXmlModel() {
        Model model = loadXMLInModel("Car2.xml");
        String strAttribute = "CAR";
        String variables = "BUY.PRICE=low;MAINT.PRICE=low;#PERS=more;#DOORS=more;LUGGAGE=big;SAFETY=high";
        Value value = model.evaluate(strAttribute, variables);
        assertEquals("Invalid value", "exc", value.getName());
    }

    public void testInvalidXmlModel() {
        String xmlFile = "CarModelInvalid.xml";
        String strAttribute = "CAR";
        String variables = "PRICE=medium;MAINT.PRICE=low";

        try {
            Model model = loadXMLInModel(xmlFile);
            fail("Invalid XML model");
            model.evaluate(strAttribute, variables);
        } catch (Exception e) {
        }
    }

    public void testInvalidVariable() {
        try {
            Model model = loadXMLInModel("Car2.xml");
            String strAttribute = "CAR";
            String variables = "PRICE=invalid;MAINT.INVALID=low";
            model.evaluate(strAttribute, variables);
            fail("Invalid variables");
        } catch (Exception e) {
        }
    }

    public void testGetExplicit() {
        Model model = loadXMLInModel("Car2.xml");
        String strAttribute = "CAR";
        String variables = "BUY.PRICE=low;MAINT.PRICE=low;#PERS=more;#DOORS=more;LUGGAGE=big;SAFETY=small";
        Value value = model.evaluate(strAttribute, variables);
        assertTrue(model.getExplicitness().booleanValue());
    }

    public void testGetComplete() {
        Model model = loadXMLInModel("Car2.xml");
        String strAttribute = "CAR";
        String variables = "PRICE=medium;MAINT.PRICE=low";
        variables = "BUY.PRICE=low;MAINT.PRICE=low;#PERS=more;#DOORS=more;LUGGAGE=big;SAFETY=high";
        variables = "BUY.PRICE=high;MAINT.PRICE=high;#PERS=to_2;#DOORS=more;LUGGAGE=small;SAFETY=medium";
        model.evaluate(strAttribute, variables);
        assertTrue(model.getCompleteness().booleanValue());
    }

    public void testNULLAttributeName() {
        Model model = loadXMLInModel("Car2.xml");
        String strAttribute = null;
        String variables = "BUY.PRICE=low;MAINT.PRICE=low;#PERS=more;#DOORS=more;LUGGAGE=big;SAFETY=small";

        try {
            Value value = model.evaluate(strAttribute, variables);
            fail("Invalid attribute name");
        } catch (Exception e) {
        }
    }

    public void testInvalidAttributeName() {
        Model model = loadXMLInModel("Car2.xml");
        String strAttribute = "INVALIDNAME";
        String variables = "BUY.PRICE=low;MAINT.PRICE=low;#PERS=more;#DOORS=more;LUGGAGE=big;SAFETY=small";

        try {
            Value value = model.evaluate(strAttribute, variables);
            fail("Invalid attribute name");
        } catch (Exception e) {
            assertEquals("Unknown attribute name",
                "Model: Unknown attribute name", e.getMessage());
        }
    }

    private Model loadXMLInModel(String xmlFile) {
        StringBuffer sb = new StringBuffer();

        try {
            URL url = ModelTest.class.getResource(xmlFile);
            BufferedReader in = new BufferedReader(new InputStreamReader(
                        url.openStream()));

            String xml;

            while ((xml = in.readLine()) != null) {
                sb.append(xml);
            }

            in.close();
        } catch (IOException e) {
        }

        Model model = new Model(sb.toString());

        return model;
    }
}
