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

import org.w3c.dom.*;

import si.JDEXi.ScaleValue;
import si.JDEXi.Value;

import java.io.IOException;
import java.io.OutputStream;

import java.util.*;

/**
 * Scale class. Scale contains a list of scaleValues.
 * 
 * @author Andrej Kogov\u0161ek, Du\u0161an Omer\u010Devi\u0107, Marko Bohanec
 * @version 2.0
 */
public class Scale {
	private ArrayList<ScaleValue> scaleValues;

	/**
	 * Constructor by parsing Element parameter.
	 * 
	 * @param element
	 *            XML object
	 */
	public Scale(final Element element) {
		NodeList nodes = element.getChildNodes();
		scaleValues = new ArrayList<ScaleValue>();

		for (int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);

			if (node.getNodeName().equals("SCALEVALUE") && node.hasChildNodes()) {
				ScaleValue scaleValue = new ScaleValue((Element) node);
				scaleValues.add(scaleValue);
			}
		}

	}

	/**
	 * Find string scale value.
	 * 
	 * @param strValue
	 *            Value name.
	 * @return Object value if value found, otherwise null.
	 */
	public Value findValue(final String strValue) {
		Value value = null;

		for (int i = 0; i < scaleValues.size(); i++) {
			ScaleValue scaleValue = scaleValues.get(i);

			if (scaleValue.getName().equals(strValue)) {
				value = new Value(scaleValue.getName(), new Integer(i));
			}
		}

		return value;
	}

	/**
	 * Find ordinal scale value.
	 * 
	 * @param ordinal
	 *            Ordinal value.
	 * @return Object value if value found, otherwise null.
	 */
	public Value findValue(final Integer ordinal) {
		Value value = null;

		if (ordinal.intValue() < scaleValues.size()) {
			ScaleValue scaleValue = scaleValues.get(ordinal.intValue());
			value = new Value(scaleValue.getName(), ordinal);
		}

		return value;
	}

	/**
	 * Get scale size (number of scaleValues). 
	 * @return Integer
	 */
	public Integer getSize() {
		return this.scaleValues.size();
	}

	/**
	 * Print object values to OutputStream.
	 * 
	 * @param stream
	 *            OutputStream object parameter
	 * @throws IOException
	 *             Throws exception if stream cannot write
	 */
	public void print(final OutputStream stream) throws IOException {
		stream.write(("Scale: \n" + "Size: \n" + getSize() + "\n").getBytes());

		for (int i = 0; i < scaleValues.size(); i++) {
			stream.write(("scaleValue[" + i + "]: \n").getBytes());

			ScaleValue scaleValue = scaleValues.get(i);
			scaleValue.print(stream);
		}
	}

}
