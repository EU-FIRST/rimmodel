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

/**
 * Variable class. Represents a named variable with assigned String value.
 * 
 * @author Andrej Kogov\u0161ek, Du\u0161an Omer\u010Devi\u0107, Marko Bohanec
 * @version 2.0
 */
public class Variable {
	private String name;
	private String value;

	/**
	 * Constructor with String (name=value) parameter.
	 * 
	 * @param hash
	 *            Input string in format like "name=value"
	 */
	public Variable(final String hash) {
		String[] str = hash.split("=");

		if (str.length != 2) {
			throw new IllegalArgumentException("Variable: Invalid value");
		}

		if ((str[0] == null) || str[0].equals("")) {
			throw new IllegalArgumentException("Variable: Name is null");
		}

		if ((str[1] == null) || str[1].equals("")) {
			throw new IllegalArgumentException("Variable: Value is null");
		}

		setName(str[0]);
		setValue(str[1]);
	}

	/**
	 * Get value name.
	 * 
	 * @return name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Get ordinal value.
	 * 
	 * @return value
	 */
	public String getValue() {
		return this.value;
	}

	/**
	 * Set value name.
	 * 
	 * @param aName
	 *            String object parameter
	 */
	protected void setName(final String aName) {
		this.name = aName;
	}

	/**
	 * Set ordinal value.
	 * 
	 * @param aValue
	 *            String object parameter
	 */
	protected void setValue(final String aValue) {
		this.value = aValue;
	}
}
