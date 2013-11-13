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

import java.io.IOException;
import java.io.OutputStream;

/**
 * Value class. Represents a single (non-distributed) value that can be assigned
 * to an attribute. Contains elements:
 * <ul>
 * <li>String name: value name</li>
 * <li>Integer ordinal: corresponding ordinal value</li>
 * </ul>
 * 
 * @author Andrej Kogov\u0161ek, Du\u0161an Omer\u010Devi\u0107, Marko Bohanec
 * @version 2.0
 */
public class Value {
	private String name;
	private Integer ordinal;

	/**
	 * Constructor with name and ordinal parameters.
	 * 
	 * @param aName
	 *            String object parameter
	 * @param aOrdinal
	 *            Integer object parameter
	 */
	public Value(final String aName, final Integer aOrdinal) {
		setName(aName);
		setOrdinal(aOrdinal);
	}

	/**
	 * Constructor from another Value.
	 * 
	 * @param value
	 *            Other Value object.
	 */
	public Value(Value value) {
		setName(value.getName());
		setOrdinal(value.getOrdinal());
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
	 * @return ordinal
	 */
	public Integer getOrdinal() {
		return this.ordinal;
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
	 * @param aOrdinal
	 *            Integer object parameter
	 */
	protected void setOrdinal(final Integer aOrdinal) {
		this.ordinal = aOrdinal;
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
		StringBuffer sb = new StringBuffer();
		sb.append("Value: \n");
		sb.append("Name: ");
		sb.append(getName() + "\n");
		sb.append("Ordinal: ");
		sb.append(getOrdinal() + "\n");

		stream.write(sb.toString().getBytes());
	}
}
