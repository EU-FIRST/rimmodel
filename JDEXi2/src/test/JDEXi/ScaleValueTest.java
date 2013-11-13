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

import si.JDEXi.ScaleValue;

/**
 * @author Andrej Kogov\u0161ek, Du\u0161an Omer\u010Devi\u0107, Marko Bohanec
 * @version 2.0
 */
@SuppressWarnings(value={"all"}) 
public class ScaleValueTest extends TestCase {
    public void testScaleValue() {
        String name = "unacc";
        String description = "unacceptable car";
        String group = "BAD";
        String xmlFile = "bin/test/DEXi/ScaleValue.xml";

        StringBuffer sb = new StringBuffer();
        sb.append("<?xml version=\"1.0\"?>");
        sb.append("<SCALEVALUE>");
        sb.append("<NAME>unacc</NAME>");
        sb.append("<DESCRIPTION>unacceptable car</DESCRIPTION>");
        sb.append("<GROUP>BAD</GROUP>");
        sb.append("</SCALEVALUE>");

        Element element = Utils.parseXml(sb.toString(), false);

        ScaleValue scaleValue = new ScaleValue(element);
        assertEquals("Invalid name value", name, scaleValue.getName());
        assertEquals("Invalid description value", description,
            scaleValue.getDescription());
        assertEquals("Invalid group value", group, scaleValue.getGroup());
    }

    public void testScaleValueTag() {
        StringBuffer sb = new StringBuffer();
        sb.append("<?xml version=\"1.0\"?>");

        //sb.append("<SCALEVALUE>"); Invalid xml!
        sb.append("<NAME>unacc</NAME>");
        sb.append("<DESCRIPTION>unacceptable car</DESCRIPTION>");
        sb.append("<GROUP>BAD</GROUP>");
        sb.append("</SCALEVALUE>");

        try {
            Element element = Utils.parseXml(sb.toString(), false);
            ScaleValue scaleValue = new ScaleValue(element);
            fail("Xml dont have SCALEVALUE tag");
        } catch (Exception e) {
        }
    }

    public void testXMLName2() {
        StringBuffer sb = new StringBuffer();
        sb.append("<?xml version=\"1.0\"?>");
        sb.append("<SCALEVALUE>");
        sb.append("<NAME>unacc</NAME>");
        sb.append("<NAME>unacc</NAME>");
        sb.append("<DESCRIPTION>unacceptable car</DESCRIPTION>");
        sb.append("<GROUP>BAD</GROUP>");
        sb.append("</SCALEVALUE>");

        try {
            Element element = Utils.parseXml(sb.toString(), false);
            ScaleValue scaleValue = new ScaleValue(element);
            fail("Xml has 2 name elements");
        } catch (Exception e) {
        }
    }

    public void testXMLName1() {
        StringBuffer sb = new StringBuffer();
        sb.append("<?xml version=\"1.0\"?>");
        sb.append("<SCALEVALUE>");
        sb.append("<NAME>unacc</NAME>");
        sb.append("<DESCRIPTION>unacceptable car</DESCRIPTION>");
        sb.append("<GROUP>BAD</GROUP>");
        sb.append("</SCALEVALUE>");

        Element element = Utils.parseXml(sb.toString(), false);
        ScaleValue scaleValue = new ScaleValue(element);
        assertEquals("Invalid get value", "unacc", scaleValue.getName());
    }

    public void testXMLName0() {
        StringBuffer sb = new StringBuffer();
        sb.append("<?xml version=\"1.0\"?>");
        sb.append("<SCALEVALUE>");
        sb.append("<DESCRIPTION>unacceptable car</DESCRIPTION>");
        sb.append("<GROUP>BAD</GROUP>");
        sb.append("</SCALEVALUE>");

        try {
            Element element = Utils.parseXml(sb.toString(), false);
            ScaleValue scaleValue = new ScaleValue(element);
            fail("Xml has 0 name elements");
        } catch (Exception e) {
        }
    }

    public void testXMLDescription2() {
        StringBuffer sb = new StringBuffer();
        sb.append("<?xml version=\"1.0\"?>");
        sb.append("<SCALEVALUE>");
        sb.append("<NAME>unacc</NAME>");
        sb.append("<DESCRIPTION>unacceptable car</DESCRIPTION>");
        sb.append("<DESCRIPTION>unacceptable car</DESCRIPTION>");
        sb.append("<GROUP>BAD</GROUP>");
        sb.append("</SCALEVALUE>");

        try {
            Element element = Utils.parseXml(sb.toString(), false);
            ScaleValue scaleValue = new ScaleValue(element);
            fail("Xml has 2 description elements");
        } catch (Exception e) {
        }
    }

    public void testXMLDescription1() {
        StringBuffer sb = new StringBuffer();
        sb.append("<?xml version=\"1.0\"?>");
        sb.append("<SCALEVALUE>");
        sb.append("<NAME>unacc</NAME>");
        sb.append("<DESCRIPTION>unacceptable car</DESCRIPTION>");
        sb.append("<GROUP>BAD</GROUP>");
        sb.append("</SCALEVALUE>");

        Element element = Utils.parseXml(sb.toString(), false);
        ScaleValue scaleValue = new ScaleValue(element);
        assertNotNull(scaleValue.getDescription());
    }

    public void testXMLDescription0() {
        StringBuffer sb = new StringBuffer();
        sb.append("<?xml version=\"1.0\"?>");
        sb.append("<SCALEVALUE>");
        sb.append("<NAME>unacc</NAME>");
        sb.append("<GROUP>BAD</GROUP>");
        sb.append("</SCALEVALUE>");

        Element element = Utils.parseXml(sb.toString(), false);
        ScaleValue scaleValue = new ScaleValue(element);

        if (scaleValue.getDescription() != null) {
            fail("Xml has 0 description element");
        }
    }

    public void testXMLGroup2() {
        StringBuffer sb = new StringBuffer();
        sb.append("<?xml version=\"1.0\"?>");
        sb.append("<SCALEVALUE>");
        sb.append("<NAME>unacc</NAME>");
        sb.append("<DESCRIPTION>unacceptable car</DESCRIPTION>");
        sb.append("<GROUP>BAD</GROUP>");
        sb.append("<GROUP>BAD</GROUP>");
        sb.append("</SCALEVALUE>");

        try {
            Element element = Utils.parseXml(sb.toString(), false);
            ScaleValue scaleValue = new ScaleValue(element);
            fail("Xml has 2 group elements");
        } catch (Exception e) {
        }
    }

    public void testXMLGroup1() {
        StringBuffer sb = new StringBuffer();
        sb.append("<?xml version=\"1.0\"?>");
        sb.append("<SCALEVALUE>");
        sb.append("<NAME>unacc</NAME>");
        sb.append("<DESCRIPTION>unacceptable car</DESCRIPTION>");
        sb.append("<GROUP>BAD</GROUP>");
        sb.append("</SCALEVALUE>");

        Element element = Utils.parseXml(sb.toString(), false);
        ScaleValue scaleValue = new ScaleValue(element);

        if (scaleValue.getGroup() == null) {
            fail("Xml has 1 group elements");
        }
    }

    public void testXMLGroup0() {
        StringBuffer sb = new StringBuffer();
        sb.append("<?xml version=\"1.0\"?>");
        sb.append("<SCALEVALUE>");
        sb.append("<NAME>unacc</NAME>");
        sb.append("<DESCRIPTION>unacceptable car</DESCRIPTION>");
        sb.append("</SCALEVALUE>");

        Element element = Utils.parseXml(sb.toString(), false);
        ScaleValue scaleValue = new ScaleValue(element);

        if (scaleValue.getGroup() != null) {
            fail("Xml has 0 group elements");
        }
    }
}
