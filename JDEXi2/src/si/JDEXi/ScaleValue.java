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

import java.io.IOException;
import java.io.OutputStream;

/**
 * ScaleValue class. Represents a single (non-distributed) scale value. Contains
 * elements:
 * <ul>
 * <li>String name: value name</li>
 * <li>String description: value description</li>
 * <li>String group: typically "BAD", "GOOD", or ""</li>
 * </ul>
 * 
 * 
 * @author Andrej Kogov\u0161ek, Du\u0161an Omer\u010Devi\u0107, Marko Bohanec
 * @version 2.0
 */
public class ScaleValue {
	// constants
	public static final String NAME = "NAME";
	public static final String DESCRIPTION = "DESCRIPTION";
	public static final String GROUP = "GROUP";
	public static final String SCALEVALUE = "SCALEVALUE";
	private String name;
	private String description;
	private String group;

	/**
	 * Constructor by parsing Element parameter.
	 * 
	 * @param element
	 *            XML object
	 */
	public ScaleValue(final Element element) {
		if (!element.hasChildNodes()) {
			throw new IllegalArgumentException(
					"ScaleValue: Element doesn't have child nodes");
		}

		NodeList nodes = element.getChildNodes();

		for (int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);

			if (node.getNodeName().equals(NAME)) {
				if (getName() != null) {
					throw new IllegalArgumentException(
							"ScaleValue: Name already exists");
				}

				setName(node.getFirstChild().getNodeValue());
			} else if (node.getNodeName().equals(DESCRIPTION)) {
				if (getDescription() != null) {
					throw new IllegalArgumentException(
							"ScaleValue: Description already exists");
				}

				setDescription(node.getFirstChild().getNodeValue());
			} else if (node.getNodeName().equals(GROUP)) {
				if (getGroup() != null) {
					throw new IllegalArgumentException(
							"ScaleValue: Group already exists");
				}

				setGroup(node.getFirstChild().getNodeValue());
			}
		}

		if (getName() == null) {
			throw new IllegalArgumentException("ScaleValue: Name is undefined");
		}
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
	 * Get value description.
	 * 
	 * @return description
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * Get value group.
	 * 
	 * @return group
	 */
	public String getGroup() {
		return this.group;
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
	 * Set value description.
	 * 
	 * @param aDescription
	 *            String object parameter
	 */
	protected void setDescription(final String aDescription) {
		this.description = aDescription;
	}

	/**
	 * Set value group.
	 * 
	 * @param aGroup
	 *            String object parameter
	 */
	protected void setGroup(final String aGroup) {
		this.group = aGroup;
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
		sb.append("ScaleValue: \n");
		sb.append("Name: ");
		sb.append(getName() + "\n");
		sb.append("Description: ");
		sb.append(getDescription() + "\n");
		sb.append("Group: ");
		sb.append(getGroup() + "\n");

		stream.write(sb.toString().getBytes());
	}
}
