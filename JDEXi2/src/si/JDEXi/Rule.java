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
 * Rule class. Represents a single rule from a DEXi utility function. Rule
 * consists of two ordinal values, low and high, which represent the lower and
 * upper bound of the rule value interval. Rule is also entered (i.e.,
 * explicitly entered by a DEXi user) or not (calculated by DEXi), and is
 * explicit (low==high) or not.
 * 
 * @author Andrej Kogov\u0161ek, Du\u0161an Omer\u010Devi\u0107, Marko Bohanec
 * @version 2.0
 */
public class Rule {
	private Integer low;
	private Integer high;
	private Boolean entered;
	private Boolean explicit;

	/**
	 * Constructor with low parameter. High value is set to low and the rule is
	 * entered.
	 * 
	 * @param aLow
	 *            Integer object parameter
	 */
	public Rule(final Integer aLow) {
		this(aLow, aLow, new Boolean(true));
	}

	/**
	 * Constructor with low, high parameters. Entered is implicitly true.
	 * 
	 * @param aLow
	 *            Integer object parameter
	 * @param aHigh
	 *            Integer object parameter
	 */
	public Rule(final Integer aLow, final Integer aHigh) {
		this(aLow, aHigh, new Boolean(true));
	}

	/**
	 * Constructor with low, high, entered parameters.
	 * 
	 * @param aLow
	 *            Integer object parameter
	 * @param aHigh
	 *            Integer object parameter
	 * @param aEntered
	 *            Boolean object parameter
	 */
	public Rule(final Integer aLow, final Integer aHigh, final Boolean aEntered) {
		int intLow = aLow.intValue();
		int intHigh = aHigh.intValue();

		if (intLow < 0) {
			throw new IllegalArgumentException(
					"Rules: Low is negative ordinal value");
		}

		if (intHigh < 0) {
			throw new IllegalArgumentException(
					"Rules: High is negative ordinal value");
		}

		if (intLow > intHigh) {
			throw new IllegalArgumentException("Rules: Low > High");
		}

		if ((intLow == intHigh) && aEntered.booleanValue()) {
			setExplicitness(new Boolean(true));
		} else {
			setExplicitness(new Boolean(false));
		}

		setLow(aLow);
		setHigh(aHigh);
		setEntered(aEntered);
	}

	/**
	 * Constructor with low and entered parameters. High is implicitly made equal to low.
	 * 
	 * @param aLow
	 *            Integer object parameter
	 * @param aEntered
	 *            Boolean object parameter
	 */
	public Rule(final Integer aLow, final Boolean aEntered) {
		this(aLow, aLow, aEntered);
	}

	/**
	 * Get low value.
	 * 
	 * @return low
	 */
	public Integer getLow() {
		return this.low;
	}

	/**
	 * Get high value.
	 * 
	 * @return high
	 */
	public Integer getHigh() {
		return this.high;
	}

	/**
	 * Get entered value.
	 * 
	 * @return entered
	 */
	public Boolean getEntered() {
		return this.entered;
	}

	/**
	 * Get explicit value.
	 * 
	 * @return explicit
	 */
	public Boolean getExplicitness() {
		return this.explicit;
	}

	/**
	 * Set low value.
	 * 
	 * @param aLow
	 *            Integer object parameter
	 */
	protected void setLow(final Integer aLow) {
		this.low = aLow;
	}

	/**
	 * Set high value.
	 * 
	 * @param aHigh
	 *            Integer object parameter
	 */
	protected void setHigh(final Integer aHigh) {
		this.high = aHigh;
	}

	/**
	 * Set entered value.
	 * 
	 * @param aEntered
	 *            Boolean object parameter
	 */
	protected void setEntered(final Boolean aEntered) {
		this.entered = aEntered;
	}

	/**
	 * Set explicitness value.
	 * 
	 * @param aExplicit
	 *            Boolean object parameter
	 */
	protected void setExplicitness(final Boolean aExplicit) {
		this.explicit = aExplicit;
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
		sb.append("Rules: \n");
		sb.append("Low: ");
		sb.append(getLow() + "\n");
		sb.append("High: ");
		sb.append(getHigh() + "\n");
		sb.append("Entered: ");
		sb.append(getEntered() + "\n");
		sb.append("Explicit: ");
		sb.append(getExplicitness() + "\n");

		stream.write(sb.toString().getBytes());
	}
}
