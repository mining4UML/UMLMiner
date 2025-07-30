package com.uniba.mining.sdmetrics;

import java.util.HashMap;
import java.util.Map;

public class MetricLabels {

    public static Map<String, String> getDefaultLabels() {
        Map<String, String> labels = new HashMap<>();

        // Actor metrics
        labels.put("NumActorGens", "Actor Generalizations");

        // UseCase metrics
        labels.put("NumAss", "Associations");
        labels.put("ExtPts", "Extension Points");
        labels.put("Including", "Including Relationships");
        labels.put("Included", "Included Relationships");
        labels.put("Extended", "Extended Use Cases");
        labels.put("Extending", "Extending Use Cases");
        labels.put("NumUseCaseGens", "Use Case Generalizations");
        labels.put("Diags", "Diagrams per Use Case");

        // Class metrics
        labels.put("NumAttr", "Attributes per Class");
        labels.put("NumOps", "Operations per Class");
        labels.put("NumPubOps", "Public Operations");
        labels.put("Setters", "Setters");
        labels.put("Getters", "Getters");
        labels.put("Nesting", "Nesting Level");
        labels.put("IFImpl", "Interfaces Implemented");
        labels.put("NOC", "Number of Children");
        labels.put("NumDesc", "Descendant Classes");
        labels.put("NumAnc", "Ancestor Classes");
        labels.put("DIT", "Depth of Inheritance Tree");
        labels.put("CLD", "Class Leaf Depth");
        labels.put("OpsInh", "Inherited Operations");
        labels.put("AttrInh", "Inherited Attributes");
        labels.put("Dep_Out", "Outgoing Dependencies");
        labels.put("Dep_In", "Incoming Dependencies");
        labels.put("NumAssEl_ssc", "Associations with Same Class");
        labels.put("NumAssEl_sb", "Associations with Subclass");
        labels.put("NumAssEl_nsb", "Associations with Non-Subclass");
        labels.put("EC_Attr", "External Coupling (Attributes)");
        labels.put("IC_Attr", "Internal Coupling (Attributes)");
        labels.put("EC_Par", "External Coupling (Parameters)");
        labels.put("IC_Par", "Internal Coupling (Parameters)");
        labels.put("Connectors", "Total Connectors");
        labels.put("InstSpec", "Instance Specifications");
        labels.put("LLInst", "Low-Level Instances");
        labels.put("MsgSent", "Messages Sent");
        labels.put("MsgRecv", "Messages Received");
        labels.put("MsgSelf", "Self Messages");
        labels.put("Diags", "Diagrams per Class");

        return labels;
    }
}