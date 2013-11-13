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

import java.util.ArrayList;

import si.JDEXi.Variable;

/**
 * VariableList class. Contains a list of Variable objects.
 * 
 * @author Andrej Kogov\u0161ek, Du\u0161an Omer\u010Devi\u0107, Marko Bohanec
 * @version 2.0
 */
public class VariableList {
	private ArrayList<Variable> variables;

	/**
	 * Constructor with list parameter.
	 * 
	 * @param list
	 *            String object parameter
	 */
	public VariableList(final String list) {
		String[] hash = null;
		variables = new ArrayList<Variable>();

		if (list != null) {
			hash = list.split(";");

			for (int i = 0; i < hash.length; i++) {
				if ((i > 0) && (findVariable(hash[i].split("=")[0]) != null)) {
					throw new IllegalArgumentException(
							"VariableList: Variable with this name already exists");
				}

				Variable variable = new Variable(hash[i]);
				variables.add(variable);
			}
		}
	}

	/**
	 * Find variable by name.
	 * 
	 * @param name
	 *            String object parameter
	 * @return Variable; return empty variable object if not found
	 */
	public Variable findVariable(final String name) {
		Variable variable = null;

		for (int i = 0; i < variables.size(); i++) {
			Variable var = variables.get(i);

			if (var.getName().equals(name)) {
				variable = var;
			}
		}

		return variable;
	}

	/**
	 * Get list size.
	 * 
	 * @return size of VariableList
	 */
	public int size() {
		return variables.size();
	}

	/**
	 * Get Variable at index.
	 * 
	 * @param index
	 *            Index of variable
	 * @return Variable
	 */
	public Variable getVariable(int index) {
		return variables.get(index);
	}

}
