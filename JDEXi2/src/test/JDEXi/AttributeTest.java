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

import org.w3c.dom.*;

import si.JDEXi.Attribute;
import si.JDEXi.Value;
import si.JDEXi.VariableList;


/**
 * @author Andrej Kogov\u0161ek, Du\u0161an Omer\u010Devi\u0107, Marko Bohanec
 * @version 2.0
 */
@SuppressWarnings(value={"all"}) 
public class AttributeTest extends TestCase {
    String xmlFile = "bin/test/DEXi/CarModel.xml";

    public void testInvalidName() {
        StringBuffer sb = new StringBuffer();
        sb.append("<ATTRIBUTE>");

        //sb.append("<NAME>CAR</NAME>"); Invalid
        sb.append("<DESCRIPTION>Quality of a car</DESCRIPTION>");
        sb.append("<SCALE>");
        sb.append("<SCALEVALUE>");
        sb.append("<NAME>unacc</NAME>");
        sb.append("<DESCRIPTION>unacceptable car</DESCRIPTION>");
        sb.append("<GROUP>BAD</GROUP>");
        sb.append("</SCALEVALUE>");
        sb.append("<SCALEVALUE>");
        sb.append("<NAME>acc</NAME>");
        sb.append(
            "<DESCRIPTION>acceptable, but I won&apos;t like it</DESCRIPTION>");
        sb.append("</SCALEVALUE>");
        sb.append("<SCALEVALUE>");
        sb.append("<NAME>good</NAME>");
        sb.append("<DESCRIPTION>satisfies my needs</DESCRIPTION>");
        sb.append("</SCALEVALUE>");
        sb.append("<SCALEVALUE>");
        sb.append("<NAME>exc</NAME>");
        sb.append("<DESCRIPTION>excellent car</DESCRIPTION>");
        sb.append("<GROUP>GOOD</GROUP>");
        sb.append("</SCALEVALUE>");
        sb.append("</SCALE>");
        sb.append("<OPTION>3</OPTION>");
        sb.append("<OPTION>2</OPTION>");
        sb.append("</ATTRIBUTE>");

        try {
            Element element = Utils.parseXml(sb.toString(), false);
            Attribute attribute = new Attribute(element);
            fail("Invalid name");
        } catch (Exception e) {
        }

        //
    }

    public void testInvalidDescription() {
        StringBuffer sb = new StringBuffer();
        sb.append("<ATTRIBUTE>");
        sb.append("<NAME>CAR</NAME>");
        sb.append("<DESCRIPTION>Quality of a car</DESCRIPTION>");
        sb.append("<DESCRIPTION>Quality of a car</DESCRIPTION>"); // Invalid
        sb.append("<SCALE>");
        sb.append("<SCALEVALUE>");
        sb.append("<NAME>unacc</NAME>");
        sb.append("<DESCRIPTION>unacceptable car</DESCRIPTION>");
        sb.append("<GROUP>BAD</GROUP>");
        sb.append("</SCALEVALUE>");
        sb.append("<SCALEVALUE>");
        sb.append("<NAME>acc</NAME>");
        sb.append(
            "<DESCRIPTION>acceptable, but I won&apos;t like it</DESCRIPTION>");
        sb.append("</SCALEVALUE>");
        sb.append("<SCALEVALUE>");
        sb.append("<NAME>good</NAME>");
        sb.append("<DESCRIPTION>satisfies my needs</DESCRIPTION>");
        sb.append("</SCALEVALUE>");
        sb.append("<SCALEVALUE>");
        sb.append("<NAME>exc</NAME>");
        sb.append("<DESCRIPTION>excellent car</DESCRIPTION>");
        sb.append("<GROUP>GOOD</GROUP>");
        sb.append("</SCALEVALUE>");
        sb.append("</SCALE>");
        sb.append("<OPTION>3</OPTION>");
        sb.append("<OPTION>2</OPTION>");
        sb.append("</ATTRIBUTE>");

        try {
            Element element = Utils.parseXml(sb.toString(), false);
            Attribute attribute = new Attribute(element);
            fail("Invalid description");
        } catch (Exception e) {
        }
    }

    public void testInvalidScale() {
        StringBuffer sb = new StringBuffer();
        sb.append("<ATTRIBUTE>");
        sb.append("<NAME>CAR</NAME>");
        sb.append("<DESCRIPTION>Quality of a car</DESCRIPTION>");
        sb.append("<SCALE>"); // Invalid
        sb.append("<SCALEVALUE>");
        sb.append("<NAME>unacc</NAME>");
        sb.append("<DESCRIPTION>unacceptable car</DESCRIPTION>"); // Invalid
        sb.append("<GROUP>BAD</GROUP>");
        sb.append("</SCALEVALUE>");
        sb.append("</SCALE>");
        sb.append("<SCALE>");
        sb.append("<SCALEVALUE>");
        sb.append("<NAME>unacc</NAME>");
        sb.append("<DESCRIPTION>unacceptable car</DESCRIPTION>"); // Invalid
        sb.append("<GROUP>BAD</GROUP>");
        sb.append("</SCALEVALUE>");
        sb.append("</SCALE>");
        sb.append("<SCALE>");
        sb.append("<SCALEVALUE>");
        sb.append("<NAME>unacc</NAME>");
        sb.append("<DESCRIPTION>unacceptable car</DESCRIPTION>");
        sb.append("<GROUP>BAD</GROUP>");
        sb.append("</SCALEVALUE>");
        sb.append("<SCALEVALUE>");
        sb.append("<NAME>acc</NAME>");
        sb.append(
            "<DESCRIPTION>acceptable, but I won&apos;t like it</DESCRIPTION>");
        sb.append("</SCALEVALUE>");
        sb.append("<SCALEVALUE>");
        sb.append("<NAME>good</NAME>");
        sb.append("<DESCRIPTION>satisfies my needs</DESCRIPTION>");
        sb.append("</SCALEVALUE>");
        sb.append("<SCALEVALUE>");
        sb.append("<NAME>exc</NAME>");
        sb.append("<DESCRIPTION>excellent car</DESCRIPTION>");
        sb.append("<GROUP>GOOD</GROUP>");
        sb.append("</SCALEVALUE>");
        sb.append("</SCALE>");
        sb.append("<OPTION>3</OPTION>");
        sb.append("<OPTION>2</OPTION>");
        sb.append("</ATTRIBUTE>");

        try {
            Element element = Utils.parseXml(sb.toString(), false);
            Attribute attribute = new Attribute(element);
            fail("Invalid scale");
        } catch (Exception e) {
        }
    }

    public void testInvalidFunction() {
        StringBuffer sb = new StringBuffer();
        sb.append("<ATTRIBUTE>");
        sb.append("<NAME>CAR</NAME>");
        sb.append("<DESCRIPTION>Quality of a car</DESCRIPTION>");
        sb.append("<SCALE>");
        sb.append("<SCALEVALUE>");
        sb.append("<NAME>unacc</NAME>");
        sb.append("<DESCRIPTION>unacceptable car</DESCRIPTION>");
        sb.append("<GROUP>BAD</GROUP>");
        sb.append("</SCALEVALUE>");
        sb.append("<SCALEVALUE>");
        sb.append("<NAME>acc</NAME>");
        sb.append(
            "<DESCRIPTION>acceptable, but I won&apos;t like it</DESCRIPTION>");
        sb.append("</SCALEVALUE>");
        sb.append("<SCALEVALUE>");
        sb.append("<NAME>good</NAME>");
        sb.append("<DESCRIPTION>satisfies my needs</DESCRIPTION>");
        sb.append("</SCALEVALUE>");
        sb.append("<SCALEVALUE>");
        sb.append("<NAME>exc</NAME>");
        sb.append("<DESCRIPTION>excellent car</DESCRIPTION>");
        sb.append("<GROUP>GOOD</GROUP>");
        sb.append("</SCALEVALUE>");
        sb.append("</SCALE>");
        sb.append("<FUNCTION>");
        sb.append("<LOW>000002230233</LOW>"); // Invalid low>high
        sb.append("<HIGH>000001230233</HIGH>");
        sb.append("</FUNCTION>");
        sb.append("<OPTION>3</OPTION>");
        sb.append("<OPTION>2</OPTION>");
        sb.append("</ATTRIBUTE>");

        try {
            Element element = Utils.parseXml(sb.toString(), false);
            Attribute attribute = new Attribute(element);
            fail("Invalid function");
        } catch (Exception e) {
        }
    }

    public void testInvalidAttribute() {
        StringBuffer sb = new StringBuffer();
        sb.append("<ATTRIBUTE>");
        sb.append("</ATTRIBUTE>");

        try {
            Element element = Utils.parseXml(sb.toString(), false);
            Attribute attribute = new Attribute(element);
            fail("Invalid attribute");
        } catch (Exception e) {
        }
    }

    public void testOKConstructor0() {
        StringBuffer sb = new StringBuffer();
        sb.append("<ATTRIBUTE>");
        sb.append("<NAME>CAR</NAME>");
        sb.append("</ATTRIBUTE>");

        Element element = Utils.parseXml(sb.toString(), false);
        Attribute attribute = new Attribute(element);
        assertEquals("Invalid name", "CAR", attribute.getName());
    }

    public void testOKConstructor1() {
        StringBuffer sb = new StringBuffer();
        sb.append("<ATTRIBUTE>");
        sb.append("<NAME>CAR</NAME>");
        sb.append("<DESCRIPTION>Quality of a car</DESCRIPTION>");
        sb.append("<SCALE>");
        sb.append("<SCALEVALUE>");
        sb.append("<NAME>unacc</NAME>");
        sb.append("<DESCRIPTION>unacceptable car</DESCRIPTION>");
        sb.append("<GROUP>BAD</GROUP>");
        sb.append("</SCALEVALUE>");
        sb.append("<SCALEVALUE>");
        sb.append("<NAME>acc</NAME>");
        sb.append(
            "<DESCRIPTION>acceptable, but I won&apos;t like it</DESCRIPTION>");
        sb.append("</SCALEVALUE>");
        sb.append("<SCALEVALUE>");
        sb.append("<NAME>good</NAME>");
        sb.append("<DESCRIPTION>satisfies my needs</DESCRIPTION>");
        sb.append("</SCALEVALUE>");
        sb.append("<SCALEVALUE>");
        sb.append("<NAME>exc</NAME>");
        sb.append("<DESCRIPTION>excellent car</DESCRIPTION>");
        sb.append("<GROUP>GOOD</GROUP>");
        sb.append("</SCALEVALUE>");
        sb.append("</SCALE>");
        sb.append("</ATTRIBUTE>");

        Element element = Utils.parseXml(sb.toString(), false);
        Attribute attribute = new Attribute(element);
        assertEquals("Invalid name", "CAR", attribute.getName());
        assertEquals("Invalid description", "Quality of a car",
            attribute.getDescription());
        assertEquals("Invalid scale", new Integer(4), attribute.getScaleSize());
    }

    public void testOKConstructor2() {
        StringBuffer sb = new StringBuffer();

        sb.append("<ATTRIBUTE>");
        sb.append("<NAME>CAR1</NAME>");
        sb.append("<ATTRIBUTE>");
        sb.append("<NAME>CAR2</NAME>");
        sb.append("</ATTRIBUTE>");
        sb.append("</ATTRIBUTE>");

        Element element = Utils.parseXml(sb.toString(), false);
        Attribute attribute = new Attribute(element);
        assertFalse(attribute.getCompleteness().booleanValue());
    }

    public void testEvaluate1() {
        StringBuffer sb = new StringBuffer();
        sb.append("<ATTRIBUTE>");
        sb.append("<NAME>CAR</NAME>");
        sb.append("<DESCRIPTION>Quality of a car</DESCRIPTION>");
        sb.append("<SCALE>");
        sb.append("<SCALEVALUE>");
        sb.append("<NAME>unacc</NAME>");
        sb.append("<DESCRIPTION>unacceptable car</DESCRIPTION>");
        sb.append("<GROUP>BAD</GROUP>");
        sb.append("</SCALEVALUE>");
        sb.append("<SCALEVALUE>");
        sb.append("<NAME>acc</NAME>");
        sb.append(
            "<DESCRIPTION>acceptable, but I won&apos;t like it</DESCRIPTION>");
        sb.append("</SCALEVALUE>");
        sb.append("</SCALE>");
        sb.append("</ATTRIBUTE>");

        Element element = Utils.parseXml(sb.toString(), false);

        Attribute attribute = new Attribute(element);
        VariableList variableList = new VariableList("CAR=unacc;");
        Value value = attribute.evaluate(variableList);

        assertTrue("Invalid completeness",
            attribute.getCompleteness().booleanValue());
        assertTrue("Invalid explicitness",
            attribute.getExplicitness().booleanValue());
    }

    public void testEvaluate2() {
        Element element = Utils.parseXml("bin/test/JDEXi/AttributeTest.xml", true);

        Attribute attribute = new Attribute(element);
        VariableList variableList = new VariableList(
                "BUY.PRICE=low;MAINT.PRICE=low;#PERS=more;#DOORS=more;LUGGAGE=medium;SAFETY=small");
        Value value = attribute.evaluate(variableList);
        assertEquals("Invalid variable value", "unacc", value.getName());
    }

    public void testAtrributeNULL() {
        Element element = Utils.parseXml("bin/test/JDEXi/AttributeTest.xml", true);

        Attribute attribute = new Attribute(element);

        try {
            VariableList variableList = new VariableList(null);
            Value value = attribute.evaluate(variableList);
            fail("variablelist is null");
        } catch (Exception e) {
        }
    }

    public void testNULLVariables() {
        StringBuffer sb = new StringBuffer();
        sb.append("<ATTRIBUTE>");
        sb.append("<NAME>CAR</NAME>");
        sb.append("<DESCRIPTION>Quality of a car</DESCRIPTION>");
        sb.append("<SCALE>");
        sb.append("<SCALEVALUE>");
        sb.append("<NAME>unacc</NAME>");
        sb.append("<DESCRIPTION>unacceptable car</DESCRIPTION>");
        sb.append("<GROUP>BAD</GROUP>");
        sb.append("</SCALEVALUE>");
        sb.append("<SCALEVALUE>");
        sb.append("<NAME>acc</NAME>");
        sb.append(
            "<DESCRIPTION>acceptable, but I won&apos;t like it</DESCRIPTION>");
        sb.append("</SCALEVALUE>");
        sb.append("</SCALE>");
        sb.append("</ATTRIBUTE>");

        Element element = Utils.parseXml(sb.toString(), false);

        try {
            Attribute attribute = new Attribute(element);
            VariableList variableList = new VariableList(null);
            attribute.evaluate(variableList);
            fail("VariableList is NULL");
        } catch (Exception e) {
            assertEquals("Missing variable", "Attribute: Missing variable",
                e.getMessage());
        }
    }

    public void testWithVariables() {
        Element element = Utils.parseXml("bin/test/JDEXi/AttributeTest.xml", true);
        Attribute attribute = new Attribute(element);
        VariableList variableList = new VariableList(
                "BUY.PRICE=low;MAINT.PRICE=low;#PERS=more;#DOORS=more;LUGGAGE=big;SAFETY=high");
        Value value = attribute.evaluate(variableList);
        assertEquals("Invalid variable value", "exc", value.getName());
    }

    public void testExplicitAndCompleteFail() {
        StringBuffer sb = new StringBuffer();
        sb.append("<ATTRIBUTE>");
        sb.append("<NAME>CAR</NAME>");
        sb.append("<DESCRIPTION>Quality of a car</DESCRIPTION>");
        sb.append("<SCALE>");
        sb.append("<SCALEVALUE>");
        sb.append("<NAME>unacc</NAME>");
        sb.append("<DESCRIPTION>unacceptable car</DESCRIPTION>");
        sb.append("<GROUP>BAD</GROUP>");
        sb.append("</SCALEVALUE>");
        sb.append("<SCALEVALUE>");
        sb.append("<NAME>acc</NAME>");
        sb.append(
            "<DESCRIPTION>acceptable, but I won&apos;t like it</DESCRIPTION>");
        sb.append("</SCALEVALUE>");
        sb.append("</SCALE>");
        sb.append("</ATTRIBUTE>");

        Element element = Utils.parseXml(sb.toString(), false);

        Attribute attribute = new Attribute(element);
        assertTrue(attribute.getCompleteness().booleanValue());
        assertTrue(attribute.getExplicitness().booleanValue());
    }

    public void testInvalidVariables() {
        StringBuffer sb = new StringBuffer();
        sb.append("<ATTRIBUTE>");
        sb.append("<NAME>CAR</NAME>");
        sb.append("<DESCRIPTION>Quality of a car</DESCRIPTION>");
        sb.append("<SCALE>");
        sb.append("<SCALEVALUE>");
        sb.append("<NAME>unacc</NAME>");
        sb.append("<DESCRIPTION>unacceptable car</DESCRIPTION>");
        sb.append("<GROUP>BAD</GROUP>");
        sb.append("</SCALEVALUE>");
        sb.append("<SCALEVALUE>");
        sb.append("<NAME>acc</NAME>");
        sb.append(
            "<DESCRIPTION>acceptable, but I won&apos;t like it</DESCRIPTION>");
        sb.append("</SCALEVALUE>");
        sb.append("</SCALE>");
        sb.append("<FUNCTION>");
        sb.append("<LOW>000001230233</LOW>");
        sb.append("</FUNCTION>");
        sb.append("</ATTRIBUTE>");

        try {
            Element element = Utils.parseXml(sb.toString(), false);
            VariableList variableList = new VariableList("");
            Attribute attribute = new Attribute(element);
            attribute.evaluate(variableList);
            fail("Invalid variables");
        } catch (Exception e) {
        }
    }

    public void testGetExplicit() {
        StringBuffer sb = new StringBuffer();
        sb.append("<ATTRIBUTE>");
        sb.append("<NAME>CAR</NAME>");
        sb.append("<DESCRIPTION>Quality of a car</DESCRIPTION>");
        sb.append("<SCALE>");
        sb.append("<SCALEVALUE>");
        sb.append("<NAME>unacc</NAME>");
        sb.append("<DESCRIPTION>unacceptable car</DESCRIPTION>");
        sb.append("<GROUP>BAD</GROUP>");
        sb.append("</SCALEVALUE>");
        sb.append("<SCALEVALUE>");
        sb.append("<NAME>acc</NAME>");
        sb.append(
            "<DESCRIPTION>acceptable, but I won&apos;t like it</DESCRIPTION>");
        sb.append("</SCALEVALUE>");
        sb.append("</SCALE>");
        sb.append("<FUNCTION>");
        sb.append("<LOW>000001230233</LOW>");
        sb.append("</FUNCTION>");
        sb.append("</ATTRIBUTE>");

        Element element = Utils.parseXml(sb.toString(), false);

        Attribute attribute = new Attribute(element);
        assertTrue(attribute.getExplicitness().booleanValue());
    }

    public void testGetDescription() {
        StringBuffer sb = new StringBuffer();
        sb.append("<ATTRIBUTE>");
        sb.append("<NAME>CAR</NAME>");
        sb.append("<DESCRIPTION>Quality of a car</DESCRIPTION>");
        sb.append("<SCALE>");
        sb.append("<SCALEVALUE>");
        sb.append("<NAME>unacc</NAME>");
        sb.append("<DESCRIPTION>unacceptable car</DESCRIPTION>");
        sb.append("<GROUP>BAD</GROUP>");
        sb.append("</SCALEVALUE>");
        sb.append("<SCALEVALUE>");
        sb.append("<NAME>acc</NAME>");
        sb.append(
            "<DESCRIPTION>acceptable, but I won&apos;t like it</DESCRIPTION>");
        sb.append("</SCALEVALUE>");
        sb.append("<SCALEVALUE>");
        sb.append("<NAME>good</NAME>");
        sb.append("<DESCRIPTION>satisfies my needs</DESCRIPTION>");
        sb.append("</SCALEVALUE>");
        sb.append("<SCALEVALUE>");
        sb.append("<NAME>exc</NAME>");
        sb.append("<DESCRIPTION>excellent car</DESCRIPTION>");
        sb.append("<GROUP>GOOD</GROUP>");
        sb.append("</SCALEVALUE>");
        sb.append("</SCALE>");
        sb.append("<FUNCTION>");
        sb.append("<LOW>000001230233</LOW>");
        sb.append("</FUNCTION>");
        sb.append("</ATTRIBUTE>");

        Element element = Utils.parseXml(sb.toString(), false);
        Attribute attribute = new Attribute(element);
        assertEquals("Invalid getSize", "Quality of a car",
            attribute.getDescription());
    }

    public void testGetName() {
        StringBuffer sb = new StringBuffer();
        sb.append("<ATTRIBUTE>");
        sb.append("<NAME>CAR</NAME>");
        sb.append("<DESCRIPTION>Quality of a car</DESCRIPTION>");
        sb.append("<SCALE>");
        sb.append("<SCALEVALUE>");
        sb.append("<NAME>unacc</NAME>");
        sb.append("<DESCRIPTION>unacceptable car</DESCRIPTION>");
        sb.append("<GROUP>BAD</GROUP>");
        sb.append("</SCALEVALUE>");
        sb.append("<SCALEVALUE>");
        sb.append("<NAME>acc</NAME>");
        sb.append(
            "<DESCRIPTION>acceptable, but I won&apos;t like it</DESCRIPTION>");
        sb.append("</SCALEVALUE>");
        sb.append("<SCALEVALUE>");
        sb.append("<NAME>good</NAME>");
        sb.append("<DESCRIPTION>satisfies my needs</DESCRIPTION>");
        sb.append("</SCALEVALUE>");
        sb.append("<SCALEVALUE>");
        sb.append("<NAME>exc</NAME>");
        sb.append("<DESCRIPTION>excellent car</DESCRIPTION>");
        sb.append("<GROUP>GOOD</GROUP>");
        sb.append("</SCALEVALUE>");
        sb.append("</SCALE>");
        sb.append("<FUNCTION>");
        sb.append("<LOW>000001230233</LOW>");
        sb.append("</FUNCTION>");
        sb.append("</ATTRIBUTE>");

        Element element = Utils.parseXml(sb.toString(), false);
        Attribute attribute = new Attribute(element);
        assertEquals("Invalid getName", "CAR", attribute.getName());
    }

    public void testGetSize() {
        StringBuffer sb = new StringBuffer();
        sb.append("<ATTRIBUTE>");
        sb.append("<NAME>CAR</NAME>");
        sb.append("<DESCRIPTION>Quality of a car</DESCRIPTION>");
        sb.append("<SCALE>");
        sb.append("<SCALEVALUE>");
        sb.append("<NAME>unacc</NAME>");
        sb.append("<DESCRIPTION>unacceptable car</DESCRIPTION>");
        sb.append("<GROUP>BAD</GROUP>");
        sb.append("</SCALEVALUE>");
        sb.append("<SCALEVALUE>");
        sb.append("<NAME>acc</NAME>");
        sb.append(
            "<DESCRIPTION>acceptable, but I won&apos;t like it</DESCRIPTION>");
        sb.append("</SCALEVALUE>");
        sb.append("<SCALEVALUE>");
        sb.append("<NAME>good</NAME>");
        sb.append("<DESCRIPTION>satisfies my needs</DESCRIPTION>");
        sb.append("</SCALEVALUE>");
        sb.append("<SCALEVALUE>");
        sb.append("<NAME>exc</NAME>");
        sb.append("<DESCRIPTION>excellent car</DESCRIPTION>");
        sb.append("<GROUP>GOOD</GROUP>");
        sb.append("</SCALEVALUE>");
        sb.append("</SCALE>");
        sb.append("<FUNCTION>");
        sb.append("<LOW>000001230233</LOW>");
        sb.append("</FUNCTION>");
        sb.append("</ATTRIBUTE>");

        Element element = Utils.parseXml(sb.toString(), false);
        Attribute attribute = new Attribute(element);
        assertEquals("Invalid getSize", new Integer(4), attribute.getScaleSize());
    }

    public void testGetValue() {
        StringBuffer sb = new StringBuffer();
        sb.append("<ATTRIBUTE>");
        sb.append("<NAME>CAR</NAME>");
        sb.append("<DESCRIPTION>Quality of a car</DESCRIPTION>");
        sb.append("<SCALE>");
        sb.append("<SCALEVALUE>");
        sb.append("<NAME>unacc</NAME>");
        sb.append("<DESCRIPTION>unacceptable car</DESCRIPTION>");
        sb.append("<GROUP>BAD</GROUP>");
        sb.append("</SCALEVALUE>");
        sb.append("<SCALEVALUE>");
        sb.append("<NAME>acc</NAME>");
        sb.append(
            "<DESCRIPTION>acceptable, but I won&apos;t like it</DESCRIPTION>");
        sb.append("</SCALEVALUE>");
        sb.append("<SCALEVALUE>");
        sb.append("<NAME>good</NAME>");
        sb.append("<DESCRIPTION>satisfies my needs</DESCRIPTION>");
        sb.append("</SCALEVALUE>");
        sb.append("<SCALEVALUE>");
        sb.append("<NAME>exc</NAME>");
        sb.append("<DESCRIPTION>excellent car</DESCRIPTION>");
        sb.append("<GROUP>GOOD</GROUP>");
        sb.append("</SCALEVALUE>");
        sb.append("</SCALE>");
        sb.append("<FUNCTION>");
        sb.append("<LOW>000001230233</LOW>");
        sb.append("</FUNCTION>");
        sb.append("</ATTRIBUTE>");

        Element element = Utils.parseXml(sb.toString(), false);
        Attribute attribute = new Attribute(element);
        Value value = attribute.getValue();
        assertNull(value);
    }
}
