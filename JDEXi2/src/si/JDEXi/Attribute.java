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

import si.JDEXi.Rule;
import si.JDEXi.Scale;
import si.JDEXi.Value;
import si.JDEXi.Variable;
import si.JDEXi.VariableList;
import si.JDEXi.Distribution;
import si.JDEXi.Model;

import java.io.IOException;
import java.io.OutputStream;

import java.util.*;

/**
 * Attribute class.
 * 
 * <p>
 * Implements DEXi attribute, which is defined by:
 * <ul>
 * <li>String name: attribute name</li>
 * <li>String description: a textual description</li>
 * <li>Scale scale: list of values that can be assigned to the attribute</li>
 * <li>ArrayList<Rule> function: utility function</li>
 * <li>ArrayList<Attribute> attributes: array of the attribute's immediate
 * descendants in attribute hierarchy</li>
 * <li>Attribute link: linked attribute (considered "logically" the same as this
 * attribute)</li>
 * </ul>
 * 
 * @author Andrej Kogov\u0161ek, Du\u0161an Omer\u010Devi\u0107, Marko Bohanec
 * @version 2.0
 */
public class Attribute {
    private ArrayList<Attribute> attributes;
    private String description;
    private Boolean completeness;
    private Boolean explicitness;
    private String name;
    private ArrayList<Rule> function = null;
    private Value value;
    private Distribution distr;
    private Scale scale = null;
    private Attribute link = null;

    /**
     * Constructor with element parameter.
     * 
     * @param element
     *            XML object parameter.
     */
    public Attribute(final Element element) {
        NodeList nodes = element.getChildNodes();
        attributes = new ArrayList<Attribute>();

        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);

            if (node.getNodeName().equals("NAME")) {
                if (getName() != null) {
                    throw new IllegalArgumentException(
                            "Attribute: Name already exists");
                }
                setName(node.getFirstChild().getNodeValue());
            } else if (node.getNodeName().equals("DESCRIPTION")) {
                if (getDescription() != null) {
                    throw new IllegalArgumentException(
                            "Attribute: Description already exists");
                }
                setDescription(node.getFirstChild().getNodeValue());
            } else if (node.getNodeName().equals("SCALE")) {
                if (scale != null) {
                    throw new IllegalArgumentException(
                            "Attribute: Scale already exists");
                }
                scale = new Scale((Element) node);
            } else if (node.getNodeName().equals("FUNCTION")) {
                if (function != null) {
                    throw new IllegalArgumentException(
                            "Attribute: Function already exists");
                }
                parseFunction(node);
            } else if (node.getNodeName().equals("ATTRIBUTE")) {
                Attribute attribute = new Attribute((Element) node);
                attributes.add(attribute);
            }
        }
        if (getName() == null) {
            throw new IllegalArgumentException("Attribute: Name is undefined");
        }

        checkExplicitness();
        checkCompleteness();
    }

    /**
     * Parse function from XML.
     * 
     * @param node
     *            Node object parameter
     */
    private void parseFunction(final Node node) {
        NodeList nodes = node.getChildNodes();
        String low = null;
        String high = null;
        String entered = null;
        function = new ArrayList<Rule>();

        for (int x = 0; x < nodes.getLength(); x++) {
            Node funNode = nodes.item(x);

            if (funNode.getNodeName().equals("LOW")) {
                low = funNode.getFirstChild().getNodeValue();
            } else if (funNode.getNodeName().equals("HIGH")) {
                high = funNode.getFirstChild().getNodeValue();
            } else if (funNode.getNodeName().equals("ENTERED")) {
                entered = funNode.getFirstChild().getNodeValue();
            }
        }
        if (high == null) {
            high = low;
        }

        Integer[] iLow = parseFunction(low);
        Integer[] iHigh = parseFunction(high);
        Boolean[] bEntered = null;
        if (entered != null) {
            bEntered = parseEntered(entered);
        }

        if ((iHigh.length != iLow.length)
                || ((bEntered != null) && (bEntered.length != iLow.length))) {
            throw new IllegalArgumentException(
                    "Attribute: high.length != low.length");
        }

        for (int l = 0; l < iLow.length; l++) {
            Rule rule = new Rule(iLow[l], iHigh[l], new Boolean(
                    (bEntered == null) || bEntered[l].booleanValue()));
            function.add(rule);
        }
    }

    /**
     * Parse function value vector from String.
     * 
     * @param value
     *            String object parameter
     * @return Array of Integer objects
     */
    private static Integer[] parseFunction(final String value) {
        Integer[] integer = null;
        char[] chars = value.toCharArray();
        integer = new Integer[chars.length];

        for (int i = 0; i < chars.length; i++) {
            integer[i] = new Integer(new String(chars, i, 1));
        }

        return integer;
    }

    /**
     * Parse entered vector from String.
     * 
     * @param value
     *            String object parameter
     * @return Array of Boolean objects
     */
    private static Boolean[] parseEntered(final String value) {
        Boolean[] entered = null;
        char[] chars = value.toCharArray();
        entered = new Boolean[chars.length];

        for (int i = 0; i < chars.length; i++) {
            String strEntered = new String(chars, i, 1);

            if (strEntered.equals("+")) {
                entered[i] = new Boolean(true);
            } else {
                entered[i] = new Boolean(false);
            }
        }
        return entered;
    }

    /**
     * Check completeness of subtree of attributes.
     */
    protected void checkCompleteness() {
        boolean isComplet = true;
        int product = 1;

        for (int i = 0; i < attributes.size(); i++) {
            Attribute att = attributes.get(i);

            if (att.getScaleSize().intValue() != 0) {
                product = product * att.getScaleSize().intValue();
            }

            isComplet = isComplet && att.getCompleteness().booleanValue();
        }

        isComplet = isComplet && (this.getScaleSize().intValue() > 0);
        isComplet = isComplet
                && ((function == null) || (product == function.size()));
        setCompleteness(new Boolean(isComplet));
    }

    /**
     * Check explicitness of subtree of attributes.
     * 
     */
    protected void checkExplicitness() {
        boolean isExplicit = true;

        for (int a = 0; a < attributes.size(); a++) {
            Attribute att = attributes.get(a);
            isExplicit = isExplicit && att.getExplicitness().booleanValue();
        }

        if (function != null) {
            for (int f = 0; f < function.size(); f++) {
                Rule rule = function.get(f);
                isExplicit = isExplicit
                        && rule.getExplicitness().booleanValue();
            }
        }
        setExplicitness(new Boolean(isExplicit));
    }

    /**
     * Add attribute subtree to a list of attributes.
     * 
     * @param aList
     *            ArrayList<Attribute>
     */
    public void addAttributes(ArrayList<Attribute> aList) {
        aList.add(this);
        for (int i = 0; i < attributes.size(); i++) {
            attributes.get(i).addAttributes(aList);
        }
    }

    /**
     * Tells whether this attribute affects another attribute.
     * 
     * @param att
     *            Another attribute
     * @return
     */
    public boolean affects(Attribute att) {
        return att.depends(this);
    }

    /**
     * Tells whether this attribute depends on another attribute.
     * 
     * @param att
     *            Another attribute
     * @return
     */
    public boolean depends(Attribute att) {
        for (int i = 0; i < attributes.size(); i++) {
            Attribute a = attributes.get(i);
            if (att == a)
                return true;
            if (a.depends(att))
                return true;
        }
        return false;
    }

    /**
     * Find an attribute that is a candidate for linking with attribute named
     * aName.
     * 
     * @param aName
     *            Attribute name.
     * @param aList
     *            List of attributes.
     * @return Attribute Candidate for linking or null.
     */
    private Attribute findLinkAttribute(final String aName,
            ArrayList<Attribute> aList) {

        Attribute agg = null;
        Attribute bas = null;
        int aggCount = 0;

        for (int i = 0; i < aList.size(); i++) {
            Attribute att = aList.get(i);

            if (att.getName().equals(aName)) {
                if (att.link != null) {
                    // not a candidate any more
                } else if (att.attributes.size() == 0) {
                    bas = att;
                } else {
                    agg = att;
                    aggCount++;
                }
            }
        }

        if (agg != null && aggCount == 1)
            return agg;
        else
            return bas;
    }

    /**
     * Try to link this.attribute and all its descendants with candidate
     * attributes in a list.
     * 
     * @param aList
     *            List of attributes.
     */
    public void linkAttribute(ArrayList<Attribute> aList) {

        link = null;
        if (attributes.size() == 0) {
            Attribute lnk = findLinkAttribute(getName(), aList);
            if (lnk == this) {
                lnk = null;
            } else if (lnk != null) {
                if (affects(lnk)) {
                    lnk = null;
                }
                else if (scale == null) {
                    if (lnk.scale == null) {
                        // ok
                    } else
                        lnk = null;
                } else if (scale.getSize() == lnk.scale.getSize()) {
                    lnk = null;
                }
            }
            link = lnk;
        }
        for (int i = 0; i < attributes.size(); i++) {
            attributes.get(i).linkAttribute(aList);
        }
    }

    /**
     * Calculate function value, depending on values of descendant attributes,
     * and assign the calculated value to the value of this.attribute. Only
     * works with single values, not value distributions.
     * 
     */
    protected void calculateValue() {
        int index = 0;
        int factor = 1;

        if (attributes.size() > 0) {
            for (int i = attributes.size() - 1; i >= 0; i--) {
                int subOrdinal = (attributes.get(i)).getValue().getOrdinal()
                        .intValue();
                int subSize = (attributes.get(i)).getScaleSize().intValue();
                index = index + (factor * subOrdinal);
                factor = factor * subSize;
            }

            Rule rule = function.get(index);
            setValue(scale.findValue(rule.getLow()));
        }
    }

    /**
     * Evaluate attribute and its descendants (left from version 1.0,
     * deprecated).
     * 
     * @param variableList
     *            VariableList object parameter
     * @return Value object
     * @deprecated
     */
    public Value evaluate(final VariableList variableList) {
        if (!getExplicitness().booleanValue()) {
            throw new IllegalStateException("Attribute: Explicitness is false");
        }

        if (!getCompleteness().booleanValue()) {
            throw new IllegalStateException("Attribute: Completeness is false");
        }

        for (int i = 0; i < attributes.size(); i++) {
            Attribute attribute = attributes.get(i);
            attribute.evaluate(variableList);
        }

        if (attributes.size() > 0) {
            calculateValue();
        } else {
            Variable variable = variableList.findVariable(getName());

            if (variable == null) {
                throw new IllegalArgumentException(
                        "Attribute: Missing variable");
            }

            Value scaleValue = scale.findValue(variable.getValue());
            setValue(scaleValue);
        }

        return getValue();
    }

    /**
     * Fast evaluation of this.attribute and its descendants. Works only with
     * single values, not value distributions.
     */
    public void evaluate() {

        if (value != null)
            return;

        if (!getExplicitness().booleanValue()) {
            throw new IllegalStateException("Attribute: Explicitness is false");
        }

        if (!getCompleteness().booleanValue()) {
            throw new IllegalStateException("Attribute: Completeness is false");
        }

        for (int i = 0; i < attributes.size(); i++) {
            Attribute attribute = attributes.get(i);
            attribute.evaluate();
        }

        if (attributes.size() > 0) {
            calculateValue();
        } else if (link != null) {
            link.evaluate();
            value = new Value(link.value);
        }
    }

    /**
     * Full evaluation of this.attribute and its descendants. Works with value
     * distributions.
     * 
     * @param evalType
     *            Evaluation type.
     * @param normalize
     *            If true, normalize results after ealuation.
     */
    public void evaluate(Model.Evaluation evalType, boolean normalize) {

        if (distr != null)
            return;

        if (attributes.size() > 0) {
            if (scale == null || function == null) {
                return;
            } else {
                for (int i = 0; i < attributes.size(); i++) {
                    Attribute attribute = attributes.get(i);
                    attribute.evaluate(evalType, normalize);
                }
                calculateDistr(evalType);
            }
        } else if (link != null) {
            link.evaluate(evalType, normalize);
            distr = new Distribution(link.distr);
        }
    }

    /**
     * Normalize value distribution, currently assigned to this.attribute.
     * 
     * @param evalType
     */
    private void normalizeDistr(Model.Evaluation evalType) {

        switch (evalType) {
        case SET:
            distr.normalizeSet();
            break;
        case PROB:
            distr.normalizeSum();
            break;
        case FUZZY:
            distr.normalizeMax();
            break;
        }
    }

    /**
     * Prepare input value distribution of this.attribute (before evaluation).
     * If a single value is defined, it is converted to distribution. If there
     * is no single value or scale is missing, a full value set is assigned to
     * distribution. If normalize==true, the distribution is finally normalized.
     * 
     * @param evalType
     * @param normalize
     */
    public void prepareInputDistribution(Model.Evaluation evalType,
            boolean normalize) {

        if (scale == null) {
            clearValues();
            return;
        }

        if (distr == null) {
            distr = new Distribution(scale.getSize());
            if (value == null) {
                distr.full();
            } else {
                distr.setSingle(value.getOrdinal().intValue());
            }
        }
        if (normalize) {
            normalizeDistr(evalType);
        }
    }

    /**
     * Process output value distribution of this.attribute (after evaluation).
     * First, distribution is normalized (when normalize==true) and then
     * converted to a single value, if possible.
     * 
     * @param evalType
     * @param normalize
     */
    public void prepareOutputDistribution(Model.Evaluation evalType,
            boolean normalize) {

        value = null;

        if (distr == null) {
            return;
        }

        if (normalize) {
            normalizeDistr(evalType);
        }

        int ord = distr.getSingle();

        if (ord >= 0) {
            Value scaleValue = scale.findValue(ord);
            setValue(scaleValue);
        }
    }

    /**
     * Get attribute name.
     * 
     * @return String Attribute name.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Get description.
     * 
     * @return String Attribute description.
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Get explicitness.
     * 
     * @return Boolean object
     */
    public Boolean getExplicitness() {
        return this.explicitness;
    }

    /**
     * Get completeness.
     * 
     * @return Boolean object
     */
    public Boolean getCompleteness() {
        return this.completeness;
    }

    /**
     * Set completeness value
     * 
     * @param aCompleteness
     *            Boolean object parameter
     */
    protected void setCompleteness(final Boolean aCompleteness) {
        this.completeness = aCompleteness;
    }

    /**
     * Get scale size.
     * 
     * @return Integer Scale size.
     */
    public Integer getScaleSize() {
        if (scale == null) {
            return new Integer(0);
        } else {
            return this.scale.getSize();
        }
    }

    /**
     * Get single attribute value.
     * 
     * @return Value object. Null if undefined.
     */
    public Value getValue() {
        return this.value;
    }

    /**
     * Format a double number into a short form used to represent distributions.
     * 
     * @param dbl
     * @return String
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
     * Represent this.attribute's value distribution in string form.
     * 
     * @return String
     */
    protected String distrString() {
        StringBuffer sb = new StringBuffer();
        boolean empty = true;
        if (distr.getCumulative() != 1.0)
            sb.append("[" + fmtDouble(distr.getCumulative()) + "]");
        for (int i = 0; i < distr.size(); i++) {
            double fact = distr.getValue(i);
            if (fact != 0.0) {
                if (!empty)
                    sb.append(",");
                empty = false;
                sb.append(scale.findValue(i).getName());
                if (fact != 1.0) {
                    sb.append("/" + fmtDouble(fact));
                }
            }
        }
        return sb.toString();
    }

    /**
     * Get value distribution.
     * 
     * @return Distribution
     */
    public Distribution getDistr() {
        return this.distr;
    }

    /**
     * Get linked attribute.
     * 
     * @return Attribute object
     */
    public Attribute getLink() {
        return this.link;
    }

    /**
     * Set linked attribute.
     * 
     * @param aAtt
     *            Attribute.
     */
    protected void setLink(final Attribute aAtt) {
        this.link = aAtt;
    }

    /**
     * Number of this.attribute's immediate descendants.
     * 
     * @return count of input attributes
     */
    public int getInputs() {
        return this.attributes.size();
    }

    /**
     * Calculate function value with respect to args[].
     * 
     * @param args
     *            Function arguments - ordinal numbers.
     * @return Rule corresponding to args[].
     */
    protected Rule functionValue(int[] args) {
        int index = 0;
        int factor = 1;

        for (int i = args.length - 1; i >= 0; i--) {
            int subOrdinal = args[i];
            int subSize = (attributes.get(i)).getScaleSize().intValue();
            index = index + (factor * subOrdinal);
            factor = factor * subSize;
        }
        return function.get(index);
    }

    /**
     * Calculate distribution value that corresponds to distribution values
     * assigned to this.attribute's descendants' distributions.
     * 
     * @param args
     *            Function arguments - ordinal numbers.
     * @param evalType
     * @return Calculated value.
     */
    protected double distributionValue(int[] args, Model.Evaluation evalType) {
        double value = 1.0;

        if (evalType != Model.Evaluation.SET) {
            for (int i = 0; i < args.length; i++) {
                Attribute att = attributes.get(i);
                double val = att.distr.getValue(args[i]);
                switch (evalType) {
                case SET:
                    break;
                case PROB:
                    value *= val;
                    break;
                case FUZZY:
                    value = Math.min(value, val);
                    break;
                }
            }
        }
        return value;
    }

    /**
     * Calculate this.attribute's value distribution with respect to value
     * distributions of its descendants in the model. This is the core full
     * evaluation method for an individual attribute.
     * 
     * @param evalType
     */
    protected void calculateDistr(Model.Evaluation evalType) {

        for (int i = 0; i < attributes.size(); i++) {
            Attribute att = attributes.get(i);
            if (att.distr == null)
                return;
            if (att.distr.getCount() == 0)
                return;
        }

        int[][] params = new int[attributes.size()][];
        for (int i = 0; i < attributes.size(); i++) {
            Attribute att = attributes.get(i);
            params[i] = att.distr.getSet();
        }

        int[] paridx = new int[attributes.size()];
        int[] args = new int[attributes.size()];

        distr = new Distribution(scale.getSize());

        int carry = 0;

        while (carry == 0) {

            for (int i = 0; i < args.length; i++) {
                args[i] = params[i][paridx[i]];
            }

            double factor = distributionValue(args, evalType);
            Rule rule = functionValue(args);
            int low = rule.getLow().intValue();
            int high = rule.getHigh().intValue();
            if (evalType == Model.Evaluation.PROB && low < high) {
                factor /= high - low + 1;
            }

            switch (evalType) {
            case SET:
                for (int i = low; i <= high; i++) {
                    distr.setValue(i, 1.0);
                }
                break;
            case PROB:
                for (int i = low; i <= high; i++) {
                    distr.setValue(i, distr.getValue(i) + factor);
                }
                break;
            case FUZZY:
                for (int i = low; i <= high; i++) {
                    distr.setValue(i, Math.max(distr.getValue(i), factor));
                }
                break;
            }
            carry = 1;

            for (int i = paridx.length - 1; i >= 0; i--) {
                paridx[i] += carry;
                if (paridx[i] < params[i].length) {
                    carry = 0;
                    break;
                } else {
                    paridx[i] = 0;
                }
            }

            distr.setCumulative(1.0);
        }
    }

    /**
     * Set attribute description value.
     * 
     * @param aDescription
     *            String object parameter.
     */
    protected void setDescription(final String aDescription) {
        this.description = aDescription;
    }

    /**
     * Set explicitness value.
     * 
     * @param aExplicitness
     *            Boolean object parameter.
     */
    protected void setExplicitness(final Boolean aExplicitness) {
        this.explicitness = aExplicitness;
    }

    /**
     * Set attribute name value.
     * 
     * @param aName
     *            String object parameter.
     */
    protected void setName(final String aName) {
        this.name = aName;
    }

    /**
     * Clear single value.
     */
    protected void clearValue() {
        this.value = null;
    }

    /**
     * Set single value to aValue object.
     * 
     * @param aValue
     *            Value object parameter
     */
    protected void setValue(final Value aValue) {
        this.value = aValue;
    }

    /**
     * Set single value by string.
     * 
     * @param aValue
     *            String
     */
    protected void setValue(final String aValue) {
        Value scaleValue = scale.findValue(aValue);
        if (scaleValue == null) {
            throw new IllegalArgumentException(
                    "Attribute: Unknown value name: " + aValue);
        }
        setValue(scaleValue);
    }

    /**
     * Set single value by string smartly.
     * 
     * @param aValue
     *            String can have one of the forms: (1) literal string,
     *            indicating attribute name, (2) numeric string, indicating a
     *            one-based ordinal number, or (3) numeric string starting with
     *            "0", indicating a zero-based ordinal number.
     */
    protected void setValueSmart(final String aValue) {
        boolean isNum = true;
        int oValue = 0;
        try {
            oValue = Integer.parseInt(aValue);
        } catch (NumberFormatException nfe) {
            isNum = false;
        }
        if (isNum) {
            if (aValue.startsWith("0")) {
                setValue(oValue);
            } else {
                setValue(oValue - 1);
            }
        } else {
            Value scaleValue = scale.findValue(aValue);
            if (scaleValue == null) {
                throw new IllegalArgumentException(
                        "Attribute: Unknown value name: " + aValue);
            }
            setValue(scaleValue);
        }
    }

    /**
     * Set single value by ordinal number.
     * 
     * @param aValue
     *            int
     */
    protected void setValue(final int aValue) {
        Value scaleValue = scale.findValue(aValue);
        if (scaleValue == null) {
            throw new IllegalArgumentException(
                    "Attribute: Unknown value ordinal: "
                            + Integer.toString(aValue));
        }
        setValue(scaleValue);
    }

    /**
     * Clear value distribution.
     */
    protected void clearDistr() {
        this.distr = null;
    }

    /**
     * Clear both values: single and distribution.
     */
    protected void clearValues() {
        this.value = null;
        this.distr = null;
    }

    /**
     * Set value distribution.
     * 
     * @param distribution
     *            Distribution object.
     */
    protected void setDistr(final Distribution distribution) {
        if (scale == null) {
            throw new IllegalArgumentException(
                    "Attribute.setDistr: Scale is null");
        }
        if (distribution.size() != scale.getSize()) {
            throw new IllegalArgumentException(
                    "Attribute.setDistr: Distribution size does not match scale size");
        }

        distr = new Distribution(distribution);
        value = null;
    }

    /**
     * Set value distribution.
     * 
     * @param distribution
     *            double[]
     */
    protected void setDistr(final double[] distribution) {
        if (scale == null) {
            throw new IllegalArgumentException(
                    "Attribute.setDistr: Scale is null");
        }
        if (distribution.length != scale.getSize()) {
            throw new IllegalArgumentException(
                    "Attribute.setDistr: Distribution size does not match scale size");
        }

        distr = new Distribution(1.0, distribution);
        value = null;
    }

    /**
     * Set value distribution to aValue/aVal.
     * 
     * @param aValue
     *            Attribute value by string.
     * @param aVal
     *            Value factor.
     */
    protected void setDistrValue(final String aValue, final double aVal) {
        if (scale == null) {
            throw new IllegalArgumentException(
                    "Attribute.setDistrValue: Scale is null");
        }

        Value scaleValue = scale.findValue(aValue);

        if (scaleValue == null) {
            throw new IllegalArgumentException(
                    "Attribute.setDistrValue: Unknown value name: " + aValue);
        }
        if (distr == null) {
            distr = new Distribution(scale.getSize());
            distr.setCumulative(1.0);
        }
        value = null;

        distr.setValue(scaleValue.getOrdinal().intValue(), aVal);
    }

    /**
     * Set value distribution to aValue/1.0.
     * 
     * @param aValue
     *            Attribute value by string.
     */
    protected void setDistrValue(final String aValue) {
        setDistrValue(aValue, 1.0);
    }

    /**
     * Set value distribution to aValue/aVal.
     * 
     * @param aValue
     *            Ordinal value.
     * @param aVal
     *            Value factor.
     */
    protected void setDistrValue(final int aValue, final double aVal) {
        if (scale == null) {
            throw new IllegalArgumentException(
                    "Attribute.setDistrValue: Scale is null");
        }

        if (distr == null) {
            distr = new Distribution(scale.getSize());
            distr.setCumulative(1.0);
        }
        value = null;

        distr.setValue(aValue, aVal);
    }

    /**
     * Set value distribution to aValue/1.0.
     * 
     * @param aValue
     *            Ordinal value.
     */
    protected void setDistrValue(final int aValue) {
        setDistrValue(aValue, 1.0);
    }

    /**
     * Print object values to OutputStream.
     * 
     * @param stream
     *            OutputStream object parameter.
     * @throws IOException
     *             Throws exception if stream cannot write.
     */
    public void print(final OutputStream stream) throws IOException {
        StringBuffer sb = new StringBuffer();
        sb.append("Attribute: \n");
        sb.append("Name: ");
        sb.append(getName() + "\n");
        sb.append("Description: ");
        sb.append(getDescription() + "\n");
        sb.append("Completeness: ");
        sb.append(getCompleteness() + "\n");
        sb.append("Explicitness: ");
        sb.append(getExplicitness() + "\n");
        sb.append("Value: ");
        stream.write(sb.toString().getBytes());

        if (value != null) {
            value.print(stream);
        }

        for (int i = 0; i < attributes.size(); i++) {
            stream.write(("SubAttributes[" + i + "]: \n").getBytes());

            Attribute attribute = attributes.get(i);
            attribute.print(stream);
        }

        if (function != null) {
            for (int f = 0; f < function.size(); f++) {
                stream.write(("Rules[" + f + "] \n").getBytes());

                Rule rule = function.get(f);
                rule.print(stream);
            }
        }

        scale.print(stream);
    }
}
