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

import si.JDEXi.Model;
import si.JDEXi.Value;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


/**
 * @author Andrej Kogov\u0161ek, Du\u0161an Omer\u010Devi\u0107, Marko Bohanec
 * @version 2.0
 */
@SuppressWarnings(value={"deprecation"})  
public class PerformanceTest {
    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Invalid input arguments!");
            System.out.println("1. Argument is xml model");
            System.out.println("2. Argument is attribute name");
            System.out.println(
                "3. Argument is data file that contains variables");
            System.exit(0);
        }

        String xmlFile = args[0];
        String attrName = args[1];
        String datFile = args[2];

        long startTime = System.currentTimeMillis();
        System.out.println("Starting performance test...");
        System.out.println("==========================");
        System.out.println("Model xml file: " + xmlFile);
        System.out.println("Attribute name: " + attrName);
        System.out.println("Variables file: " + datFile);
        System.out.println("==========================");

        try {
            Model model = loadXMLInModel(xmlFile);
            BufferedReader in = new BufferedReader(new FileReader(datFile));
            String line;

            int i = 0;

            while ((line = in.readLine()) != null) {
                i++;
                System.out.println(line);

                String[] strArray = line.split("\\|");
                Value value = model.evaluate(attrName, strArray[0].toString());

                if (value.getName().equals(strArray[1].split("=")[1])) {
                    System.out.println(i + ") Valid value: " + value.getName());
                } else {
                    System.out.println(i + ") Wrong value: " + line);
                }
            }

            in.close();
        } catch (IOException e) {
            System.out.println("Error: Can't read file");
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

        System.out.println("Time: " + (System.currentTimeMillis() - startTime));
    }

    private static Model loadXMLInModel(String xmlFile)
        throws IOException {
        StringBuffer sb = new StringBuffer();

        BufferedReader in = new BufferedReader(new FileReader(xmlFile));
        String xml;

        while ((xml = in.readLine()) != null) {
            sb.append(xml);
        }

        in.close();

        Model model = new Model(sb.toString());

        return model;
    }
}
