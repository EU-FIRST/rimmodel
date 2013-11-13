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
////////////////////////////////////////////////////////////////////////////////
//Version 2.0 2012-11-12:
//		Distribution class added
////////////////////////////////////////////////////////////////////////////////

package si.JDEXi;

import java.util.Arrays;
import java.lang.Math;

/**
 * Distribution class.
 * 
 * <p>
 * Distribution consists of:
 * <ul>
 * <li>double[] distribution</li>
 * <li>double cumulative: holds some cumulative value, interpretation-dependent</li>
 * </ul>
 * 
 * @author Marko Bohanec
 * @version 2.0
 */
public class Distribution {

    private double[] distribution = null;
    private double cumulative = 0.0;

    /**
     * Constructs Distribution of given size.
     * 
     * @param aSize
     *            Initial length of distribution.
     */
    public Distribution(final int aSize) {
        distribution = new double[aSize];
        clear();
    }

    /**
     * Constructs Distribution from aCum/aDistr.
     * 
     * @param aCum
     *            Initial cumulative value.
     * @param aDistr
     *            Initial distribution.
     */
    public Distribution(final double aCum, final double[] aDistr) {
        cumulative = aCum;
        distribution = aDistr;
    }

    /**
     * Constructs Distribution from another Distribution.
     * 
     * @param aDistr
     *            Other Distribution.
     */
    public Distribution(Distribution aDistr) {
        distribution = Arrays.copyOf(aDistr.distribution,
                aDistr.distribution.length);
        cumulative = aDistr.cumulative;
    }

    /**
     * Get distribution size. Cumulative is not counted.
     * 
     * @return Length of distribution.
     */
    public int size() {
        return distribution.length;
    }

    /**
     * Get cumulative value
     * 
     * @return double
     */
    public double getCumulative() {
        return cumulative;
    }

    /**
     * Set cumulative.
     * 
     * @param aCumul
     *            New cumulative vaue.
     */
    public void setCumulative(double aCumul) {
        cumulative = aCumul;
    }

    /**
     * Get distribution value at index.
     * 
     * @param index
     * @return value
     */
    public double getValue(final int index) {
        return distribution[index];
    }

    /**
     * Set distribution value at index to val.
     * 
     * @param index
     * @param val
     */
    public void setValue(final int index, final double val) {
        distribution[index] = val;
    }

    /**
     * Clear distribution. All distribution values and cumulative are set to
     * 0.0.
     */
    public void clear() {
        for (int i = 0; i < distribution.length; i++) {
            distribution[i] = 0.0;
        }
        cumulative = 0.0;
    }

    /**
     * Fully populate distribution. All distribution values and cumulative are
     * set to 1.0.
     */
    public void full() {
        for (int i = 0; i < distribution.length; i++) {
            distribution[i] = 1.0;
        }
        cumulative = 1.0;
    }

    /**
     * Create a uniform distribution with respect to the number of values.
     * Cumulative is set to 1.0.
     */
    public void uniform() {
        cumulative = 1.0;
        for (int i = 0; i < distribution.length; i++) {
            distribution[i] = 1.0 / size();
        }
    }

    /**
     * Get a single value represented by distribution.
     * 
     * @return If distribution holds a single element with value not equal to
     *         0.0, returns the index of that element, otherwise returns -1.
     */
    public int getSingle() {
        int count = getCount();
        if (count == 1) {
            for (int i = 0; i < distribution.length; i++) {
                if (distribution[i] != 0.0) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * Set the distribution to single value. The element at index val and
     * cumulative are set to 1.0, other elements are cleared.
     * 
     * @param val
     */
    public void setSingle(int val) {
        clear();
        distribution[val] = 1.0;
        cumulative = 1.0;
    }

    /**
     * Count the number of non-zero elements.
     * 
     * @return Count
     */
    public int getCount() {
        int cnt = 0;
        for (int i = 0; i < distribution.length; i++) {
            if (distribution[i] != 0.0) {
                cnt++;
            }
        }
        return cnt;
    }

    /**
     * Represent distribution with a set of values.
     * 
     * @return int[] containing indices of non-zero elements.
     */
    public int[] getSet() {
        int count = getCount();
        if (count == 0) {
            return null;
        } else {
            int[] set = new int[count];
            int x = 0;
            for (int i = 0; i < distribution.length; i++) {
                if (distribution[i] != 0.0) {
                    set[x++] = i;
                }
            }
            return set;
        }
    }

    /**
     * Convert a set into distribution.
     * 
     * @param set
     *            int[] containing indices of distribution elements whose values
     *            are set to 1.o. Cumulative is also set to 1.0.
     */
    public void setSet(int[] set) {
        clear();
        cumulative = 1.0;
        for (int i = 0; i < set.length; i++) {
            distribution[i] = 1.0;
        }
    }

    /**
     * Calculate the sum of distribution values.
     * 
     * @return double Sum.
     */
    public double sum() {
        double s = 0.0;
        for (int i = 0; i < distribution.length; i++) {
            s += distribution[i];
        }
        return s;
    }

    /**
     * Calculate the maximum of distribution values.
     * 
     * @return double Maximum.
     */
    public double max() {
        double m = 0.0;
        for (int i = 0; i < distribution.length; i++) {
            m = Math.max(m, distribution[i]);
        }
        return m;
    }

    /**
     * Calculate the average of distribution values.
     * 
     * @return double Average.
     */
    public double average() {
        return sum() / size();
    }

    /**
     * Calculate the average index of distribution values. Warning: This is not
     * equal to average().
     * 
     * @return double Average index.
     */
    public double avgIndex() {
        double a = 0.0;
        for (int i = 0; i < distribution.length; i++) {
            a += i * distribution[i];
        }
        return a;
    }

    /**
     * Calculate weighted sum.
     * 
     * @return cumulative * sum().
     */
    public double sumMul() {
        return cumulative * sum();
    }

    /**
     * Calculate sum() divided by cumulative. Returns 0.0 if cumulative==0.0.
     * 
     * @return sum() / cumulative.
     */
    public double sumDiv() {
        if (cumulative == 0.0)
            return 0.0;
        else
            return sum() / cumulative;
    }

    public void add(Distribution aDistr) {
        cumulative += aDistr.cumulative;
        for (int i = 0; i < distribution.length; i++) {
            distribution[i] += aDistr.distribution[i];
        }
    }

    /**
     * Increment value at index by val and cumulative by cum.
     * 
     * @param index
     *            Element index.
     * @param val
     *            Value increment.
     * @param cum
     *            Cumulative increment.
     */
    public void add(final int index, final double val, final double cum) {
        cumulative += cum;
        distribution[index] += val;
    }

    /**
     * Increment value at index by val. Cumulative is increased by 1.0.
     * 
     * @param index
     *            Element index.
     * @param val
     *            Value increment.
     */
    public void add(final int index, final double val) {
        add(index, val, 1.0);
    }

    /**
     * Increment value at index by 1.0. Cumulative is increased by 1.0.
     * 
     * @param index
     *            Element index.
     */
    public void add(final int index) {
        add(index, 1.0, 1.0);
    }

    /**
     * Divide all elements and cumulative by by.
     * 
     * @param by
     */
    public void divBy(final double by) {
        cumulative /= by;
        for (int i = 0; i < distribution.length; i++) {
            distribution[i] /= by;
        }
    }

    /**
     * Multiply all elements and cumulative by by.
     * 
     * @param by
     */
    public void mulBy(final double by) {
        cumulative *= by;
        for (int i = 0; i < distribution.length; i++) {
            distribution[i] *= by;
        }
    }

    /**
     * Normalize distribution by dividing all elements by n and setting
     * cumulative to 1.0. Warning: n==0.0 throws an exception.
     * 
     * @param n
     *            Divisor.
     */
    public void normalize(final double n) {
        divBy(n);
        cumulative = 1.0;
    }

    /**
     * Normalize distribution by dividing all elements by cumulative.
     */
    public void normalize() {
        normalize(cumulative);
    }

    /**
     * Normalize distribution so that sum()==1.0.
     */
    public void normalizeSum() {
        normalize(sum());
    }

    /**
     * Normalize distribution so that max()==1.0.
     */
    public void normalizeMax() {
        normalize(max());
    }

    /**
     * Convert distribution to a set by setting all non-zero elements and
     * cumulative to 1.0.
     */
    public void normalizeSet() {
        cumulative = 1.0;
        for (int i = 0; i < distribution.length; i++) {
            if (distribution[i] != 0.0) {
                distribution[i] = 1.0;
            }
        }
    }

    /**
     * Increment distribution size by one. Insert new 0.0 element at
     * distribution[0].
     */
    public void increment() {
        double[] newdistr = new double[distribution.length + 1];
        System.arraycopy(distribution, 0, newdistr, 1, distribution.length);
        newdistr[0] = 0.0;
        distribution = newdistr;
    }
        
    /**
     * Represent a double with a short string format.
     * 
     * @param dbl
     * @return
     */
    private String fmtDouble(final double dbl) {
        String s = String.format("%.2f", dbl).replaceAll(",", ".");
        if (!s.contains("."))
            return s;
        while (s.endsWith("0")) {
            s = s.substring(0, s.length() - 1);
        }
        if (s.endsWith(".")) {
            s = s.substring(0, s.length() - 1);
        }
        return s;
    }

    /**
     * Represent distribution with a short string.
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        boolean empty = true;
        if (cumulative != 1.0)
            sb.append("[" + fmtDouble(cumulative) + "]");
        for (int i = 0; i < distribution.length; i++) {
            if (distribution[i] != 0.0) {
                if (!empty)
                    sb.append(";");
                empty = false;
                sb.append(Integer.toString(i));
                if (distribution[i] != 1.0) {
                    sb.append("/" + fmtDouble(distribution[i]));
                }
            }
        }
        return sb.toString();
    }

}
