////////////////////////////////////////////////////////////////////////////////
//JDEXi2:	Implements evaluation of decision alternatives based on
//			qualitative multi-attribute models produced by DEXi software
//			(http://kt.ijs.si/MarkoBohanec/dexi.html)
//
//			Authors: Marko Bohanec, Dusan Omercevic, Andrej Kogovsek
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
//Version 2.0 2012-11-12 changes and new features:
//		Port to java 1.7:
//		- Using templates
//		- Class Vector --> ArrayList
//		Model extensions (for compatibility with advanced/new DEXi features):
//		- Link attributes
//		Extensions of decision alternative evaluations:
//		- New methods for setting and getting model variables:
//			. Typical new evaluation sequence:
//	        	model.clearInputValues()
//				model.setInputValue(s)...
//				model.evaluate()
//				model.getOutputValue(s)...
//		- New set of robust evaluate() methods that:
//			. Handle incomplete models (missing scales, functions)
//			. Take into account under-specified functions (low/high intervals)
//			. Carry out three types of distribution-based evaluations:
//				Model.Evaluation.SET: set-based (the same as DEXi)
//				Model.Evaluation.PROB: probabilistic (not supported by DEXi)
//				Model.Evaluation.FUZZY: fuzzy (not supported by DEXi)
//		Improved documentation.
////////////////////////////////////////////////////////////////////////////////

package si.JDEXi;

import org.w3c.dom.*;

import org.xml.sax.InputSource;

import si.JDEXi.Attribute;
import si.JDEXi.Value;
import si.JDEXi.VariableList;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;

import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * <p>
 * Implements evaluation of decision alternatives based on qualitative
 * multi-attribute models produced by <a
 * href="http://kt.ijs.si/MarkoBohanec/dexi.html">DEXi</a> software.
 * 
 * <p>
 * JDEXi2 supports:
 * <ul>
 * <li>parsing and reading of DEXi models from .dxi files (XML format)
 * [constructor Model()]</li>
 * <li>clearing and setting model input values [methods setInputValue(s)...]</li>
 * <li>carrying out the evaluation [methods evaluate()...]</li>
 * <li>obtaining evaluation results [methods getOutputValue(s)...]</li>
 * </ul>
 * 
 * <p>
 * JDEXi2 does not support any modification of models; DEXi software should be
 * used for that purpose.
 * </p>
 * 
 * <p>Authors: Marko Bohanec, Dušan Omerèeviæ, Andrej Kogovšek</p>
 * 
 * <p>
 * JDEXi2 changes and new features:
 * <ul>
 * <li>Port to java 1.7:</li>
 * <ul>
 * <li>Using templates</li>
 * <li>Class Vector --> ArrayList</li>
 * </ul>
 * <li>Model extensions (for compatibility with advanced/new DEXi features):</li>
 * <ul>
 * <li>Link attributes</li>
 * </ul>
 * <li>Extensions of decision alternative evaluations:</li>
 * <ul>
 * <li>New methods for setting and getting model variables:</li>
 * <ul>
 * <li>Typical new evaluation sequence:</li>
 * <ul>
 * <li>model.clearInputValues()</li>
 * <li>model.setInputValue(s)...</li>
 * <li>model.evaluate()</li>
 * <li>model.getOutputValue(s)...</li>
 * </ul>
 * </ul>
 * <li>New set of robust evaluate() methods that:</li>
 * <ul>
 * <li>Handle incomplete models (missing scales, functions)</li>
 * <li>Take into account under-specified functions (low/high intervals)</li>
 * <li>Carry out three types of distribution-based evaluations:</li>
 * <ul>
 * <li>Model.Evaluation.SET: set-based (the same as DEXi)</li>
 * <li>Model.Evaluation.PROB: probabilistic (not supported by DEXi)</li>
 * <li>Model.Evaluation.FUZZY: fuzzy (not supported by DEXi)</li>
 * </ul>
 * </ul> </ul>
 * <li>Improved documentation.</li> </ul>
 * 
 * @author Andrej Kogov\u0161ek, Du\u0161an Omer\u010Devi\u0107, Marko Bohanec
 * @version 2.0
 * @since 2012-11-12
 */
public class Model {

    /**
     * List of all root attributes.
     */
    private ArrayList<Attribute> attributes;

    /**
     * DEXi advanced setting: Link equal attributes.
     */
    private Boolean linking = new Boolean(false);

    /**
     * List of all all basic (input) attributes.
     */
    public ArrayList<Attribute> basic = null;

    /**
     * List of all aggregate (output) attributes.
     */
    public ArrayList<Attribute> aggregate = null;

    /**
     * List of all linked attributes.
     */
    public ArrayList<Attribute> linked = null;

    /**
     * Model constructor.
     * 
     * @param xml
     *            String, containing DEXi model in standard DEXi XML format.
     */
    public Model(final String xml) {
        parseXML(xml);
        ArrayList<Attribute> all = getAllAttributes();
        if (linking)
            linkAttributes(all);
        basic = new ArrayList<Attribute>();
        aggregate = new ArrayList<Attribute>();
        linked = new ArrayList<Attribute>();
        for (int i = 0; i < all.size(); i++) {
            Attribute att = all.get(i);
            if (att.getLink() != null)
                linked.add(att);
            else if (att.getInputs() == 0)
                basic.add(att);
            else
                aggregate.add(att);
        }
    }

    /**
     * Evaluation types, which specify hot to evaluate value distributions.
     * 
     * @value SET: set-based evaluation (this is standard and only DEXi
     *        behavior).
     * @value PROB: probabilistic evaluation (extension to DEXi).
     * @value FUZZY: fuzzy evaluation (extension to DEXi).
     * 
     */
    public enum Evaluation {
        SET, PROB, FUZZY
    };

    /**
     * attributestoString.
     * 
     * @param aList
     *            List of attributes.
     * @return ";"-separated string of attribute names.
     */
    public String attributesToString(ArrayList<Attribute> aList) {
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < aList.size(); i++) {
            if (i > 0)
                sb.append(";");
            sb.append(aList.get(i).getName());
        }
        return sb.toString();
    }

    /**
     * linkAttributes method links attributes after loading the model.
     * 
     * @param aList
     *            List of attributes.
     */
    private void linkAttributes(ArrayList<Attribute> aList) {
        for (int i = 0; i < aList.size(); i++) {
            aList.get(i).linkAttribute(aList);
        }
    }

    /**
     * Collects all model attributes.
     * 
     * @return A list of all attributes.
     */
    public ArrayList<Attribute> getAllAttributes() {
        ArrayList<Attribute> allAttributes = new ArrayList<Attribute>();
        for (int i = 0; i < attributes.size(); i++) {
            attributes.get(i).addAttributes(allAttributes);
        }

        return allAttributes;
    }

    /**
     * Clears the values of all attributes in the list.
     * 
     * @param aList
     *            List of attributes.
     */
    protected void clearAttributeValues(ArrayList<Attribute> aList) {
        for (int i = 0; i < aList.size(); i++) {
            Attribute att = aList.get(i);
            att.clearValues();
        }
    }

    /**
     * Clear the values of all input attributes.
     */
    public void clearInputValues() {
        clearAttributeValues(basic);
    }

    /**
     * Clear the values of all output attributes.
     */
    public void clearOutputValues() {
        clearAttributeValues(aggregate);
        clearAttributeValues(linked);
    }

    /**
     * Set the values of input attributes.
     * 
     * @param variables
     *            String in the form: "name=value;...". Unspecified names remain
     *            undefined.
     */
    public void setInputValuesByNames(final String variables) {

        VariableList variableList = new VariableList(variables);

        for (int i = 0; i < variableList.size(); i++) {
            Variable var = variableList.getVariable(i);
            Attribute att = findAttribute(var.getName(), basic);
            if (att == null) {
                throw new IllegalArgumentException(
                        "Model.setInputValues: Unknown attribute name: "
                                + var.getName());
            }
            String val = var.getValue();
            att.setValue(val);
        }
    }

    /**
     * Set the values of input attributes, in the order of basic attributes.
     * 
     * @param values
     *            String[] containing attribute values (as strings). Array
     *            length must exactly match the number of basic attributes.
     */
    public void setInputValues(final String[] values) {

        if (values.length != basic.size()) {
            throw new IllegalArgumentException(String.format(
                    "Model.setInputValues: Argument count is %d, should be %d",
                    values.length, basic.size()));
        }

        for (int i = 0; i < values.length; i++) {
            Attribute att = basic.get(i);
            att.setValue(values[i]);
        }
    }

    /**
     * Set the values of input attributes, in the order of basic attributes.
     * 
     * @param values
     *            String[] containing attribute values (as strings). Array
     *            length must exactly match the number of basic attributes.
     *            Individual strings can have one of the forms: (1) literal
     *            string, indicating attribute name, (2) numeric string,
     *            indicating one-based ordinal number, or (3) numeric string
     *            starting with "0", indicating zero-based ordinal.
     */
    public void setInputValuesSmart(final String[] values) {

        if (values.length != basic.size()) {
            throw new IllegalArgumentException(String.format(
                    "Model.setInputValues: Argument count is %d, should be %d",
                    values.length, basic.size()));
        }
        for (int i = 0; i < values.length; i++) {
            Attribute att = basic.get(i);
            att.setValueSmart(values[i]);
        }
    }

    /**
     * Set the values of input attributes, in the order of basic attributes.
     * 
     * @param values
     *            int[] containing ordinal values of attributes. Array length
     *            must exactly match the number of basic attributes.
     */
    public void setInputValues(final int[] values) {

        if (values.length != basic.size()) {
            throw new IllegalArgumentException(String.format(
                    "Model.setInputValues: Argument count is %d, should be %d",
                    values.length, basic.size()));
        }
        for (int i = 0; i < values.length; i++) {
            Attribute att = basic.get(i);
            att.setValue(values[i]);
        }
    }

    /**
     * Set input value of an individual attribute.
     * 
     * @param name
     *            Attribute name.
     * @param value
     *            Ordinal value.
     */
    public void setInputValue(final String name, final int value) {

        Attribute att = findAttribute(name, basic);
        if (att == null) {
            throw new IllegalArgumentException(
                    "Model.setInputValue: Unknown attribute name: " + name);
        }
        att.setValue(value);
    }

    /**
     * Set input value of an individual attribute.
     * 
     * @param name
     *            Attribute name.
     * @param value
     *            String value.
     */
    public void setInputValue(final String name, final String value) {

        Attribute att = findAttribute(name, basic);
        if (att == null) {
            throw new IllegalArgumentException(
                    "Model.setInputValue: Unknown attribute name: " + name);
        }
        att.setValue(value);
    }

    /**
     * Set input value of an individual attribute.
     * 
     * @param index
     *            Attribute index in the list of basic attribute.
     * @param value
     *            Ordinal value.
     */
    public void setInputValue(final int index, final int value) {

        Attribute att = basic.get(index);
        att.setValue(value);
    }

    /**
     * Set input value of an individual attribute.
     * 
     * @param index
     *            Attribute index in the list of basic attribute.
     * @param value
     *            String value.
     */
    public void setInputValue(final int index, final String value) {

        Attribute att = basic.get(index);
        att.setValue(value);
    }

    /**
     * Set input value distribution of an individual attribute.
     * 
     * @param name
     *            Attribute name.
     * @param distr
     *            Distribution of values. Should contain a number between 0.0
     *            and 1.0 for each value that can be assigned to the attribute.
     *            The size of distr must much the number of values that can be
     *            assigned to the attribute..
     */
    public void setInputValue(final String name, Distribution distr) {

        Attribute att = findAttribute(name, basic);
        if (att == null) {
            throw new IllegalArgumentException(
                    "Model.setInputValue: Unknown attribute name: " + name);
        }
        att.setDistr(distr);
    }

    /**
     * Set input value distribution of an individual attribute.
     * 
     * @param name
     *            Attribute name.
     * @param distr
     *            double[] containing a distribution of values, that is numbers
     *            between 0.0 and 1.0, of length equal to attribute scale size.
     */
    public void setInputValue(final String name, final double[] distr) {

        Attribute att = findAttribute(name, basic);
        if (att == null) {
            throw new IllegalArgumentException(
                    "Model.setInputValue: Unknown attribute name: " + name);
        }
        att.setDistr(distr);
    }

    /**
     * Set input value distribution of an individual attribute.
     * 
     * @param index
     *            Attribute index in the list of basic attributes.
     * @param distr
     *            Distribution of values. Should contain a number between 0.0
     *            and 1.0 for each value that can be assigned to the attribute.
     *            The size of distr must much the number of values that can be
     *            assigned to the attribute..
     */
    public void setInputValue(final int index, Distribution distr) {

        Attribute att = basic.get(index);
        att.setDistr(distr);
    }

    /**
     * Set input value distribution of an individual attribute.
     * 
     * @param index
     *            Attribute index in the list of basic attributes.
     * @param distr
     *            double[] containing a distribution of values, that is numbers
     *            between 0.0 and 1.0, of length equal to attribute scale size.
     */
    public void setInputValue(final int index, final double[] distr) {

        Attribute att = basic.get(index);
        att.setDistr(distr);
    }

    /**
     * Set input distribution value of an individual attribute.
     * 
     * All methods of the type setInputValue(att,value,fact) accumulate, that
     * is, they do not clear the corresponding input distribution before setting
     * each value/fact pair. A full distribution can thus be set by consecutive
     * calls for different values.
     * 
     * @param name
     *            Attribute name.
     * @param value
     *            Ordinal value.
     * @param fact
     *            Probability or possibility (fuzzy membership) of value.
     *            Typically, fact is a number between 0.0 and 1.0.
     */
    public void setInputValue(final String name, final int value,
            final double fact) {

        Attribute att = findAttribute(name, basic);
        if (att == null) {
            throw new IllegalArgumentException(
                    "Model.setInputValue: Unknown attribute name: " + name);
        }
        att.setDistrValue(value, fact);
    }

    /**
     * Set input distribution value of an individual attribute.
     * 
     * @param name
     *            Attribute name.
     * @param value
     *            String value.
     * @param fact
     *            Probability or possibility (fuzzy membership) of value.
     */
    public void setInputValue(final String name, final String value,
            final double fact) {

        Attribute att = findAttribute(name, basic);
        if (att == null) {
            throw new IllegalArgumentException(
                    "Model.setInputValue: Unknown attribute name: " + name);
        }
        att.setDistrValue(value, fact);
    }

    /**
     * Set input distribution value of an individual attribute.
     * 
     * @param index
     *            Attribute index in the list of basic attributes.
     * @param value
     *            Ordinal value.
     * @param fact
     *            Probability or possibility (fuzzy membership) of value.
     */
    public void setInputValue(final int index, final int value,
            final double fact) {

        Attribute att = basic.get(index);
        att.setDistrValue(value, fact);
    }

    /**
     * Set input distribution value of an individual attribute.
     * 
     * @param index
     *            Attribute index in the list of basic attributes.
     * @param value
     *            String value.
     * @param fact
     *            Probability or possibility (fuzzy membership) of value.
     */
    public void setInputValue(final int index, final String value,
            final double fact) {

        Attribute att = basic.get(index);
        att.setDistrValue(value, fact);
    }

    /**
     * Get a ";"-separated string of all output value assignments. Each
     * individual assignment is presented in the "name=value" form, where "name"
     * is the name of output attribute and "value" is a shortest representation
     * of a single or distributed value.
     */
    public String getOutputValues() {

        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < aggregate.size(); i++) {
            if (i > 0) {
                sb.append(";");
            }
            Attribute att = aggregate.get(i);
            Value val = att.getValue();
            Distribution distr = att.getDistr();
            sb.append(att.getName() + "=");
            if (val != null) {
                sb.append(val.getName());
            } else if (distr != null) {
                sb.append(att.distrString());
            } else {
                sb.append("<null>");
            }
        }
        return sb.toString();
    }

    /**
     * Get the value of the index-th basic (input) attribute.
     * 
     * @param index
     *            Index of the attribute in the depth-first list of basic
     *            attributes
     * @return Single (not distributed) value of the attribute, if it exists.
     */
    public Value getInputValue(final int index) {
        Attribute att = basic.get(index);
        return att.getValue();
    }

    /**
     * Get the value of some aggregate (input) attribute.
     * 
     * @param name
     *            Name of the input attribute.
     * @return Single (not distributed) value of the attribute, if it exists.
     */
    public Value getInputValue(final String name) {
        Attribute att = findAttribute(name, basic);
        if (att == null) {
            throw new IllegalArgumentException(
                    "Model.getInputValue: Unknown attribute name: " + name);
        }
        return att.getValue();
    }

    /**
     * Get the value of the index-th aggregate (output) attribute. This method
     * returns the current value and should be typically used after evaluation.
     * 
     * @param index
     *            Index of the attribute in the depth-first list of aggregate
     *            attributes. index=0 corresponds to the first root attribute.
     * @return Single (not distributed) value of the attribute, if it exists.
     *         Use getOutputDistr() method for obtaining full value
     *         distribution.
     */
    public Value getOutputValue(final int index) {
        Attribute att = aggregate.get(index);
        return att.getValue();
    }

    /**
     * Get the value of some aggregate (output) attribute. This method returns
     * the current value and should be typically used after evaluation.
     * 
     * @param name
     *            Name of the output attribute.
     * @return Single (not distributed) value of the attribute, if it exists.
     *         Use getOutputDistr() method for obtaining full value
     *         distribution.
     */
    public Value getOutputValue(final String name) {
        Attribute att = findAttribute(name, aggregate);
        if (att == null) {
            throw new IllegalArgumentException(
                    "Model.getOutputValue: Unknown attribute name: " + name);
        }
        return att.getValue();
    }

    /**
     * Get the value distribution of the index-th aggregate (output) attribute.
     * This method returns the current distribution and should be used after
     * evaluation that generates distributions.
     * 
     * @param index
     *            Index of the attribute in the depth-first list of aggregate
     *            attributes. index=0 corresponds to the first root attribute.
     * @return Distribution of values.
     * 
     */
    public Distribution getOutputDistr(final int index) {
        Attribute att = aggregate.get(index);
        return att.getDistr();
    }

    /**
     * Get the value distribution of some aggregate (output) attribute. This
     * method returns the current distribution and should be used after
     * evaluation that generates distributions.
     * 
     * @param name
     *            Attribute name.
     * @return Distribution of values.
     */
    public Distribution getOutputDistr(final String name) {
        Attribute att = findAttribute(name, aggregate);
        if (att == null) {
            throw new IllegalArgumentException(
                    "Model.getOutputValue: Unknown attribute name: " + name);
        }
        return att.getDistr();
    }

    /**
     * Get value distribution of index-th output attribute.
     * 
     * @param index
     *            Output attribute index.
     * @return String representing the distribution.
     */
    public String getOutputDistrString(final int index) {
        Attribute att = aggregate.get(index);
        return att.distrString();
    }

    /**
     * Get value distribution of some output attribute.
     * 
     * @param name
     *            Output attribute name.
     * @return String representing the distribution.
     */
    public String getOutputDistrString(final String name) {
        Attribute att = findAttribute(name, aggregate);
        if (att == null) {
            throw new IllegalArgumentException(
                    "Model.getOutputValue: Unknown attribute name: " + name);
        }
        return att.distrString();
    }

    /**
     * Evaluate method (left from JDEXi version 1.0, deprecated). This method is
     * called alone and does not require an evaluation sequence as introduced in
     * JDEXi2 (that is: clear, setInputs, evaluate, getOutputs). However, it can
     * perform only "fast" evaluation on fully-specified non-linked models and
     * requires fully defined input data. Otherwise, it fails with an exception.
     * 
     * @param strAttribute
     *            Name of the attribute whose evaluated value should be
     *            returned.
     * @param variables
     *            ";"-separated list of "name=value" pairs representing input
     *            data.
     * @return Single value of strAttribute.
     * @see #evaluate()
     * @deprecated
     */
    public Value evaluate(final String strAttribute, final String variables) {
        if (strAttribute == null) {
            throw new IllegalArgumentException(
                    "Model.evaluate: Missing attribute name");
        }

        VariableList variableList = new VariableList(variables);
        Attribute attribute = findAttribute(strAttribute);

        if (attribute == null) {
            throw new IllegalArgumentException(
                    "Model.evaluate: Unknown attribute name: " + strAttribute);
        }

        Value value = attribute.evaluate(variableList);

        return value;
    }

    /**
     * Fast evaluation method. This method takes into account only single
     * (non-distributed) values of input attributes and generates only single
     * values of output attributes. This method requires that all model
     * components are fully defined and that all input data are specified in a
     * form of single values. This includes a 100% definition of all DEXi
     * utility functions. Otherwise, this method fails with an exception.
     * 
     * <p>
     * After successful evaluation, values of output attributes are defined and
     * can be retrieved by getOutputValue() methods.
     */
    public void evaluate() {

        clearOutputValues();

        for (int i = 0; i < attributes.size(); i++) {
            Attribute att = attributes.get(i);
            att.evaluate();
        }
    }

    /**
     * Full evaluation method. This method should be used whenever model
     * components (scales, functions) are not fully defined or input data is
     * (partly) specified in terms of value distributions rather than single
     * values. This method does not fail when model components and/or input data
     * are incompletely defined (it may generate useless results, though).
     * 
     * <p>
     * Overall, this method operates on value distributions rather than single
     * values. Before evaluation, all single values assigned to input attributes
     * are converted into distributions (when both a single value and
     * distribution have been specified for an individual attribute, only the
     * distribution is used). After evaluation, all output distributions that
     * can be represented by a single value, are converted to corresponding
     * single values. That is, after evaluation, all output distributions are
     * defined and can be retrieved with getOuputDistr() methods. Some
     * attributes may also have assigned single values, which can be retrieved
     * with getOutputValue() methods.
     * 
     * @param evalType
     *            Type of evaluation.
     * @param normalize
     *            Boolean value indicating whether or not distributions are
     *            normalized before (for input attributes) and after (output
     *            attributes) evaluation.
     * 
     *            Normalization depends on evaluation type: SET: all non zero
     *            membership values are converted to 1.0 PROB: the sum of
     *            probabilities is normalized to 1.0 FUZZY: the maximum set
     *            membership is normalized to 1.0
     * @see Model.Evaluation enum type.
     */
    public void evaluate(Evaluation evalType, boolean normalize) {

        for (int i = 0; i < basic.size(); i++) {
            Attribute att = basic.get(i);
            att.prepareInputDistribution(evalType, normalize);
        }
        clearOutputValues();

        for (int i = 0; i < attributes.size(); i++) {
            Attribute att = attributes.get(i);
            att.evaluate(evalType, normalize);
        }

        for (int i = 0; i < aggregate.size(); i++) {
            Attribute att = aggregate.get(i);
            att.prepareOutputDistribution(evalType, normalize);
        }
    }

    /**
     * Find attribute in the model by name.
     * 
     * @param name
     *            Attribute name.
     * @return Attribute
     */
    protected Attribute findAttribute(final String name) {

        return findAttribute(name, attributes);
    }

    /**
     * Find attribute by name in list.
     * 
     * @param name
     *            Attribute name.
     * @param aList
     *            List of attributes.
     * @return Attribute
     */
    protected Attribute findAttribute(final String name,
            ArrayList<Attribute> aList) {

        for (int i = 0; i < aList.size(); i++) {
            Attribute attr = aList.get(i);

            if (attr.getName().equals(name)) {
                return attr;
            }
        }

        return null;
    }

    /**
     * Get model explicitness, that is, whether or not all functions are 100%
     * defined.
     * 
     * @return Boolean object
     */
    public Boolean getExplicitness() {
        boolean isExplicit = true;

        for (int i = 0; i < attributes.size(); i++) {
            Attribute attribute = attributes.get(i);
            isExplicit = isExplicit
                    && attribute.getExplicitness().booleanValue();
        }

        return new Boolean(isExplicit);
    }

    /**
     * Get model completeness, that is, whether or not all functions have a
     * required number of rule.
     * 
     * @return Boolean object
     */
    public Boolean getCompleteness() {
        boolean isComplete = true;

        for (int i = 0; i < attributes.size(); i++) {
            Attribute attribute = attributes.get(i);
            isComplete = isComplete
                    && attribute.getCompleteness().booleanValue();
        }

        return new Boolean(isComplete);
    }

    /**
     * Return a String array of names of attributes in attlist.
     * 
     * @param attlist
     *            ArrayList of attributes.
     * @return String[] of attribute names.
     */
    public String[] listAttributes(ArrayList<Attribute> attlist) {
        String[] list = new String[attlist.size()];
        for (int i = 0; i < attlist.size(); i++) {
            list[i] = attlist.get(i).getName();
        }
        return list;
    }

    /**
     * Return a String array of input attribute names.
     * 
     * @return String[] of input attribute names.
     */
    public String[] inputs() {
        return listAttributes(basic);
    }

    /**
     * Return a String array of output attribute names.
     * 
     * @return String[] of output attribute names.
     */
    public String[] outputs() {
        return listAttributes(aggregate);
    }

    /**
     * Return a tab-delimited string of attribute names.
     * 
     * @param attlist
     *            List of attributes.
     * @return Tab-delimited String.
     */
    public String tabbedAttributes(ArrayList<Attribute> attlist) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < attlist.size(); i++) {
            if (i > 0)
                sb.append("\t");
            sb.append(attlist.get(i).getName());
        }
        return sb.toString();
    }

    /**
     * Return a tab-delimited string of input attribute names.
     * 
     * @return Tab-delimited String.
     */
    public String tabbedInputs() {
        return tabbedAttributes(basic);
    }

    /**
     * Return a tab-delimited string of output attribute names.
     * 
     * @return Tab-delimited String.
     */
    public String tabbedOutputs() {
        return tabbedAttributes(aggregate);
    }

    /**
     * Parse XML.
     * 
     * @param xml
     *            String object, containing DEXi XML representation of a model.
     */
    protected void parseXML(final String xml) {
        Element element = null;

        try {
            DocumentBuilderFactory domFactory = DocumentBuilderFactory
                    .newInstance();
            DocumentBuilder domBuilder = domFactory.newDocumentBuilder();
            element = domBuilder.parse(new InputSource(new StringReader(xml)))
                    .getDocumentElement();
        } catch (Exception e) {
            throw new IllegalArgumentException("Model: Invalid xml data");
        }

        if (!element.getNodeName().equals("DEXi")) {
            throw new IllegalArgumentException("Model: Xml root must be DEXi");
        }

        NodeList nodes = element.getChildNodes();
        attributes = new ArrayList<Attribute>();

        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);

            if (node.getNodeName().equals("ATTRIBUTE")) {
                Attribute attribute = new Attribute((Element) node);
                attributes.add(attribute);
            } else if (node.getNodeName().equals("SETTINGS")
                    && node.hasChildNodes()) {
                NodeList settings = node.getChildNodes();

                for (int j = 0; j < settings.getLength(); j++) {
                    Node setting = settings.item(j);
                    if (setting.getNodeName().equals("LINKING")) {
                        linking = new Boolean(setting.getFirstChild()
                                .getNodeValue().equals("True"));
                    }
                }
            }
        }
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
        stream.write("Model: \n".getBytes());

        for (int i = 0; i < attributes.size(); i++) {
            stream.write(("Attribute[" + i + "]: \n").getBytes());

            Attribute attribute = attributes.get(i);
            attribute.print(stream);
        }
    }

    /**
     * Read content of file and return String object
     * 
     * @param xmlFile
     *            File path
     * @return String object
     * @throws IOException
     *             Throws exception
     */
    public static String loadFile(final String xmlFile) throws IOException {
        StringBuffer sb = new StringBuffer();

        BufferedReader in = new BufferedReader(new FileReader(xmlFile));
        String xml;

        while ((xml = in.readLine()) != null) {
            sb.append(xml);
        }

        in.close();

        return sb.toString();
    }
}
