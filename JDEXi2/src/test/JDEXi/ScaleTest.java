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

import org.w3c.dom.Element;

import si.JDEXi.Scale;
import si.JDEXi.Value;


/**
 * @author Andrej Kogov\u0161ek, Du\u0161an Omer\u010Devi\u0107, Marko Bohanec
 * @version 2.0
 */
@SuppressWarnings(value={"all"}) 
public class ScaleTest extends TestCase {
    public void testScale() {
        StringBuffer sb = new StringBuffer();
        sb.append("<?xml version=\"1.0\"?>");
        sb.append("<SCALE>");
        sb.append("<SCALEVALUE>");
        sb.append("<NAME>unacc</NAME>");
        sb.append("<NAME>unacc</NAME>"); // error!
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

        try {
            Element element = Utils.parseXml(sb.toString(), false);
            Scale scale = new Scale(element);
            fail("Invalid scale object");
        } catch (Exception e) {
        }
    }

    public void testFindValueString() {
        StringBuffer sb = new StringBuffer();
        sb.append("<?xml version=\"1.0\"?>");
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

        Element element = Utils.parseXml(sb.toString(), false);
        Scale scale = new Scale(element);
        Value value = scale.findValue("acc");
        assertEquals("Invalid value", "acc", value.getName());
    }

    public void testNotFoundValueString() {
        StringBuffer sb = new StringBuffer();
        sb.append("<?xml version=\"1.0\"?>");
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

        Element element = Utils.parseXml(sb.toString(), false);
        Scale scale = new Scale(element);
        Value value = scale.findValue("ex");
        assertNull(value);
    }

    public void testFindValueInteger() {
        StringBuffer sb = new StringBuffer();
        sb.append("<?xml version=\"1.0\"?>");
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

        Element element = Utils.parseXml(sb.toString(), false);
        Scale scale = new Scale(element);
        Value value = scale.findValue(new Integer(1));
        assertEquals("Invalid ordinal value", new Integer(1), value.getOrdinal());
    }

    public void testNotFoundValueInteger() {
        StringBuffer sb = new StringBuffer();
        sb.append("<?xml version=\"1.0\"?>");
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

        Element element = Utils.parseXml(sb.toString(), false);
        Scale scale = new Scale(element);
        Value value = scale.findValue(new Integer(4));
        assertNull(value);
    }
}
