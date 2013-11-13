package eu.first.RIM;

import si.JDEXi.Model;
import eu.first.RIM.Data;

/**
 * ModelRIM class. RIM stands for Reputational Index Model, developed in the
 * framework of EU project FIRST. RIM assesses reputational risk of financial
 * products, which are produced by some bank counterparts and are owned by some
 * bank clients. RIM is a multi-attribute model consisting of qualitative
 * (symbolic) and quantitative (numeric) variables. The qualitative part of RIM
 * has been developed with DEXi software
 * (http://kt.ijs.si/MarkoBohanec/dexi.html). RIM is fully documented in FIRST
 * deliverable D6.2, 2012.
 * 
 * <p>
 * ModelRIM class provides methods for evaluating reputational risk of different
 * entities: product/client pairs and various product/client lists. It provides
 * a wrapper around the DEXi model, using the package si.JDEXi version 2.0
 * (http://kt.ijs.si/MarkoBohanec/jdexi2.html). The quantitative part of RIM is
 * implemented entirely in this code.
 * 
 * <p>
 * The development of eu.first.RIM.* software was financially supported by EU
 * FP7 project FIRST (FP7-ICT-257928) <i>Large scale information extraction and
 * integration infrastructure for supporting financial decision making</i>.
 * 
 * @author Software: Marko Bohanec (marko.bohanec@ijs.si)
 * @author Model: Giorgio Aprile (MPS), Marko Bohanec (JSI), Maria Costante
 *         (MPS), Morena Foti (MPS), Nejc Trdin (JSI), Martin Žnidaršiè (JSI)
 * @version 1.0
 * @since 2012-10-18
 */
public class ModelRIM {

    protected Model model;
    
    /**
     * Weight of long-term sentiment (vs. short time-sentiment), defined on [0,1].
     */
    public double Wsl = 0.3;

    /**
     * Constructor. Parses the model from XML string, which is returned by
     * rimXML() method.
     */
    public ModelRIM() {
        model = new Model(rimXML());
    }

    protected double FuncS(double Ssp, double Slp) {
        return Wsl * Slp + (1-Wsl) * Ssp;
    }

    protected int DiscretizeS(double S) {
        S = 100 * S;
        if (S <= -30.0)
            return 4; // very-neg
        if (S <= -20.0)
            return 3; // high-neg
        if (S <= -10.0)
            return 2; // med-neg
        if (S <= -1.0)
            return 1; // low-neg
        return 0; // neutral
    }

    protected int DiscretizeP(double P) {
        P = 100 * P;
        if (P <= -100.0)
            return 4; // very-high
        if (P <= -50.0)
            return 3; // high
        if (P <= -25.0)
            return 2; // medium
        if (P <= -10.0)
            return 1; // low
        return 0; // in-line
    }

    protected double FuncdM(int SRI, int RP) {
        return SRI - RP;
    }

    protected int DiscretizeM(double dM) {
        if (dM >= 3.5)
            return 4; // very-high
        if (dM >= 2.5)
            return 3; // high
        if (dM >= 1.5)
            return 2; // medium
        if (dM >= 0.5)
            return 1; // low
        return 0; // in-line
    }

    protected double FuncRV1c(long V1, long Vc) {
        return 1.0 * V1 / Vc;
    }

    protected int DiscretizeRV1c(double RVc) {
        RVc = 100 * RVc;
        if (RVc >= 30.0)
            return 4; // very-high
        if (RVc >= 20.0)
            return 3; // high
        if (RVc >= 10.0)
            return 2; // medium
        if (RVc >= 5.0)
            return 1; // medium-low
        return 0; // low
    }

    protected double FuncRVc(long Vc, double TA) {
        return Vc / TA;
    }

    protected double FuncRV1p(long V1, long Vp) {
        return 1.0 * V1 / Vp;
    }

    protected double FuncRNp(long Np, long TN) {
        return 1.0 * Np / TN;
    }

    protected double FuncRVp(long Vp, double TA) {
        return Vp / TA;
    }

    /**
     * Basic data processing: calculation of numerical quantities and their
     * discretization into qualitative DEXi values. Discrete values are input to
     * subsequent qualitativeEvaluation().
     * 
     * @param data
     */
    protected void basicDataProcessing(Data data) {

        // SENTIMENT
        data.S = FuncS(data.Ssp, data.Slp);
        data.qS = DiscretizeS(data.S);

        // PERFORMANCE
        data.qP = DiscretizeP(data.P);

        // MISMATCHING
        data.dM = FuncdM(data.SRI, data.RP);
        data.qM = DiscretizeM(data.dM);

        // CUSTOMER VOLUMES
        data.RV1c = FuncRV1c(data.V1, data.Vc);
        data.qRV1c = DiscretizeRV1c(data.RV1c);
        data.RVc = FuncRVc(data.Vc, data.TA);

        // PRODUCT VOLUMES
        data.RV1p = FuncRV1c(data.V1, data.Vp);
        data.RNp = FuncRNp(data.Np, data.TN);
        data.RVp = FuncRVp(data.Vp, data.TA);
    }

    /**
     * Perform qualitative evaluation using the DEXi model.
     * 
     * @param data
     */
    protected void qualitativeEvaluation(Data data) {
        model.setInputValue("qS", data.qS);
        model.setInputValue("qP", data.qP);
        model.setInputValue("qM", data.qM);
        model.setInputValue("qRV1c", data.qRV1c);

        model.evaluate();

        data.qRI1 = model.getOutputValue("qRI1").getOrdinal().intValue();
        data.qPM = model.getOutputValue("qPM").getOrdinal().intValue();
    }

    /**
     * Perform evaluation of a single product/client pair.
     * 
     * @param data
     */
    public void evaluateProductClient(Data data) {
        basicDataProcessing(data);
        qualitativeEvaluation(data);
    }

    /**
     * Get RIM DEXi model. The model is hard-coded to avoid hassle with
     * installing and referring to external files.
     * 
     * @return String containing XML representation of RIM.
     */
    private String rimXML() {
        StringBuffer sb = new StringBuffer();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        sb.append("<DEXi>");
        sb.append("  <NAME>RIM1 version 1</NAME>");
        sb.append("  <DESCRIPTION>");
        sb.append("    <LINE>A Multi-Attribute Model for the Assessment of Reputational Risk Index for one product/client pair.</LINE>");
        sb.append("    <LINE/>");
        sb.append("    <LINE>Project: FIRST (http://project-first.eu/)</LINE>");
        sb.append("    <LINE/>");
        sb.append("    <LINE>Version 1, 6.11.2012: Derived from RIM v0.5 reverse by: extracting only the qRIcp subtree, renaming qRIcp to qRI1 and renaming qRVc to qRC1c.</LINE>");
        sb.append("    <LINE/>");
        sb.append("    <LINE>Authors: Giorgio Aprile (MPS), Marko Bohanec (JSI), Maria Costante (MPS), Morena Foti (MPS) , Nejc Trdin (JSI) , Martin Žnidaršiè (JSI)</LINE>");
        sb.append("  </DESCRIPTION>");
        sb.append("  <SETTINGS>");
        sb.append("    <REPORTS>1;2;5</REPORTS>");
        sb.append("    <COMPLEX>False</COMPLEX>");
        sb.append("    <OPTLEVELS>False</OPTLEVELS>");
        sb.append("  </SETTINGS>");
        sb.append("  <ATTRIBUTE>");
        sb.append("    <NAME>qRI1</NAME>");
        sb.append("    <DESCRIPTION>Reputational Index for one customer-product pair</DESCRIPTION>");
        sb.append("    <SCALE>");
        sb.append("      <ORDER>DESC</ORDER>");
        sb.append("      <SCALEVALUE>");
        sb.append("        <NAME>low</NAME>");
        sb.append("        <GROUP>GOOD</GROUP>");
        sb.append("      </SCALEVALUE>");
        sb.append("      <SCALEVALUE>");
        sb.append("        <NAME>medium-low</NAME>");
        sb.append("        <GROUP>GOOD</GROUP>");
        sb.append("      </SCALEVALUE>");
        sb.append("      <SCALEVALUE>");
        sb.append("        <NAME>medium</NAME>");
        sb.append("      </SCALEVALUE>");
        sb.append("      <SCALEVALUE>");
        sb.append("        <NAME>high</NAME>");
        sb.append("        <GROUP>BAD</GROUP>");
        sb.append("      </SCALEVALUE>");
        sb.append("      <SCALEVALUE>");
        sb.append("        <NAME>very-high</NAME>");
        sb.append("        <GROUP>BAD</GROUP>");
        sb.append("      </SCALEVALUE>");
        sb.append("    </SCALE>");
        sb.append("    <FUNCTION>");
        sb.append("      <LOW>00000000111122222333233440000000012122222233333444011221122312223233333344412223122332223333334334441223312233223343333434444</LOW>");
        sb.append("    </FUNCTION>");
        sb.append("    <ATTRIBUTE>");
        sb.append("      <NAME>qS</NAME>");
        sb.append("      <DESCRIPTION>Sentiment</DESCRIPTION>");
        sb.append("      <SCALE>");
        sb.append("        <ORDER>DESC</ORDER>");
        sb.append("        <SCALEVALUE>");
        sb.append("          <NAME>neutral</NAME>");
        sb.append("          <GROUP>GOOD</GROUP>");
        sb.append("        </SCALEVALUE>");
        sb.append("        <SCALEVALUE>");
        sb.append("          <NAME>low-neg</NAME>");
        sb.append("          <GROUP>GOOD</GROUP>");
        sb.append("        </SCALEVALUE>");
        sb.append("        <SCALEVALUE>");
        sb.append("          <NAME>med-neg</NAME>");
        sb.append("        </SCALEVALUE>");
        sb.append("        <SCALEVALUE>");
        sb.append("          <NAME>high-neg</NAME>");
        sb.append("          <GROUP>BAD</GROUP>");
        sb.append("        </SCALEVALUE>");
        sb.append("        <SCALEVALUE>");
        sb.append("          <NAME>very-neg</NAME>");
        sb.append("          <GROUP>BAD</GROUP>");
        sb.append("        </SCALEVALUE>");
        sb.append("      </SCALE>");
        sb.append("    </ATTRIBUTE>");
        sb.append("    <ATTRIBUTE>");
        sb.append("      <NAME>qPM</NAME>");
        sb.append("      <DESCRIPTION>Performance and Mismatching</DESCRIPTION>");
        sb.append("      <SCALE>");
        sb.append("        <ORDER>DESC</ORDER>");
        sb.append("        <SCALEVALUE>");
        sb.append("          <NAME>in-line</NAME>");
        sb.append("          <GROUP>GOOD</GROUP>");
        sb.append("        </SCALEVALUE>");
        sb.append("        <SCALEVALUE>");
        sb.append("          <NAME>low</NAME>");
        sb.append("          <GROUP>GOOD</GROUP>");
        sb.append("        </SCALEVALUE>");
        sb.append("        <SCALEVALUE>");
        sb.append("          <NAME>medium</NAME>");
        sb.append("        </SCALEVALUE>");
        sb.append("        <SCALEVALUE>");
        sb.append("          <NAME>high</NAME>");
        sb.append("          <GROUP>BAD</GROUP>");
        sb.append("        </SCALEVALUE>");
        sb.append("        <SCALEVALUE>");
        sb.append("          <NAME>very-high</NAME>");
        sb.append("          <GROUP>BAD</GROUP>");
        sb.append("        </SCALEVALUE>");
        sb.append("      </SCALE>");
        sb.append("      <FUNCTION>");
        sb.append("        <LOW>0112301233112341233412344</LOW>");
        sb.append("      </FUNCTION>");
        sb.append("      <ATTRIBUTE>");
        sb.append("        <NAME>qP</NAME>");
        sb.append("        <DESCRIPTION>Performance</DESCRIPTION>");
        sb.append("        <SCALE>");
        sb.append("          <ORDER>DESC</ORDER>");
        sb.append("          <SCALEVALUE>");
        sb.append("            <NAME>in-line</NAME>");
        sb.append("            <GROUP>GOOD</GROUP>");
        sb.append("          </SCALEVALUE>");
        sb.append("          <SCALEVALUE>");
        sb.append("            <NAME>low</NAME>");
        sb.append("            <GROUP>GOOD</GROUP>");
        sb.append("          </SCALEVALUE>");
        sb.append("          <SCALEVALUE>");
        sb.append("            <NAME>medium</NAME>");
        sb.append("          </SCALEVALUE>");
        sb.append("          <SCALEVALUE>");
        sb.append("            <NAME>high</NAME>");
        sb.append("            <GROUP>BAD</GROUP>");
        sb.append("          </SCALEVALUE>");
        sb.append("          <SCALEVALUE>");
        sb.append("            <NAME>very-high</NAME>");
        sb.append("            <GROUP>BAD</GROUP>");
        sb.append("          </SCALEVALUE>");
        sb.append("        </SCALE>");
        sb.append("      </ATTRIBUTE>");
        sb.append("      <ATTRIBUTE>");
        sb.append("        <NAME>qM</NAME>");
        sb.append("        <DESCRIPTION>Mismatching</DESCRIPTION>");
        sb.append("        <SCALE>");
        sb.append("          <ORDER>DESC</ORDER>");
        sb.append("          <SCALEVALUE>");
        sb.append("            <NAME>in-line</NAME>");
        sb.append("            <GROUP>GOOD</GROUP>");
        sb.append("          </SCALEVALUE>");
        sb.append("          <SCALEVALUE>");
        sb.append("            <NAME>low</NAME>");
        sb.append("            <GROUP>GOOD</GROUP>");
        sb.append("          </SCALEVALUE>");
        sb.append("          <SCALEVALUE>");
        sb.append("            <NAME>medium</NAME>");
        sb.append("          </SCALEVALUE>");
        sb.append("          <SCALEVALUE>");
        sb.append("            <NAME>high</NAME>");
        sb.append("            <GROUP>BAD</GROUP>");
        sb.append("          </SCALEVALUE>");
        sb.append("          <SCALEVALUE>");
        sb.append("            <NAME>very-high</NAME>");
        sb.append("            <GROUP>BAD</GROUP>");
        sb.append("          </SCALEVALUE>");
        sb.append("        </SCALE>");
        sb.append("      </ATTRIBUTE>");
        sb.append("    </ATTRIBUTE>");
        sb.append("    <ATTRIBUTE>");
        sb.append("      <NAME>qRV1c</NAME>");
        sb.append("      <DESCRIPTION>Relative Volumes of this product in customer volumes</DESCRIPTION>");
        sb.append("      <SCALE>");
        sb.append("        <ORDER>DESC</ORDER>");
        sb.append("        <SCALEVALUE>");
        sb.append("          <NAME>low</NAME>");
        sb.append("          <GROUP>GOOD</GROUP>");
        sb.append("        </SCALEVALUE>");
        sb.append("        <SCALEVALUE>");
        sb.append("          <NAME>medium-low</NAME>");
        sb.append("          <GROUP>GOOD</GROUP>");
        sb.append("        </SCALEVALUE>");
        sb.append("        <SCALEVALUE>");
        sb.append("          <NAME>medium</NAME>");
        sb.append("        </SCALEVALUE>");
        sb.append("        <SCALEVALUE>");
        sb.append("          <NAME>high</NAME>");
        sb.append("          <GROUP>BAD</GROUP>");
        sb.append("        </SCALEVALUE>");
        sb.append("        <SCALEVALUE>");
        sb.append("          <NAME>very-high</NAME>");
        sb.append("          <GROUP>BAD</GROUP>");
        sb.append("        </SCALEVALUE>");
        sb.append("      </SCALE>");
        sb.append("    </ATTRIBUTE>");
        sb.append("  </ATTRIBUTE>");
        sb.append("</DEXi>");

        return sb.toString();
    }

}
