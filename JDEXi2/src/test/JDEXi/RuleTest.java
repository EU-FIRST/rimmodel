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

import si.JDEXi.Rule;

/**
 * @author Andrej Kogov\u0161ek, Du\u0161an Omer\u010Devi\u0107, Marko Bohanec
 * @version 2.0
 */
@SuppressWarnings(value={"all"}) 
public class RuleTest extends TestCase {
    public void testLow() {
        Integer low = new Integer(0);
        Rule function = new Rule(low);
        assertEquals("Invalid get low", low, function.getLow());
    }

    public void testNegativeLow() {
        Integer low = new Integer(-1);

        try {
            Rule function = new Rule(low);
            fail("Low is negative");
        } catch (Exception e) {
        }
    }

    public void testLowEqualHigh() {
        Integer low = new Integer(1);
        Integer high = new Integer(1);
        Rule function = new Rule(low, high);
        assertTrue(function.getExplicitness().booleanValue());
    }

    public void testNegativeHigh() {
        Integer low = new Integer(1);
        Integer high = new Integer(-1);

        try {
            Rule function = new Rule(low, high);
            fail("High is negative");
        } catch (Exception e) {
        }
    }

    public void testHighLowerLow() {
        Integer low = new Integer(1);
        Integer high = new Integer(0);

        try {
            Rule function = new Rule(low, high);
            fail("Low is lower than high");
        } catch (Exception e) {
        }
    }

    public void testExplicitness1() {
        Integer low = new Integer(1);
        Integer high = new Integer(1);
        Rule function = new Rule(low, high);
        assertTrue(function.getExplicitness().booleanValue());
    }

    public void testExplicitness2() {
        Integer low = new Integer(1);
        Integer high = new Integer(1);
        Rule function = new Rule(low, high, new Boolean(true));
        assertTrue(function.getExplicitness().booleanValue());
    }

    public void testExplicitnessHighLowestLow() {
        Integer low = new Integer(1);
        Integer high = new Integer(0);

        try {
            Rule function = new Rule(low, high, new Boolean(true));
            fail("High is smaller than low");
        } catch (Exception e) {
        }
    }

    public void testLowEntered1true() {
        Integer low = new Integer(1);
        Rule function = new Rule(low, new Boolean(true));
        assertTrue(function.getExplicitness().booleanValue());
    }

    public void testLowEntered2false() {
        Integer low = new Integer(1);
        Rule function = new Rule(low, new Boolean(false));
        assertFalse(function.getExplicitness().booleanValue());
    }
}
