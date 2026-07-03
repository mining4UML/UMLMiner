package com.uniba.mining.processfeedback.ocpm;

public class ProcessDynamicsFinding {

    private String category;
    private String objectType;
    private String objectName;
    private String evidence;
    private String interpretation;
    private String suggestedAction;

    public String toPromptText() {
        return "Category: " + category + "\n"
                + "Object: " + objectType + " " + objectName + "\n"
                + "Evidence: " + evidence + "\n"
                + "Process interpretation: " + interpretation + "\n"
                + "Suggested action: " + suggestedAction + "\n";
    }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getObjectType() { return objectType; }
    public void setObjectType(String objectType) { this.objectType = objectType; }

    public String getObjectName() { return objectName; }
    public void setObjectName(String objectName) { this.objectName = objectName; }

    public String getEvidence() { return evidence; }
    public void setEvidence(String evidence) { this.evidence = evidence; }

    public String getInterpretation() { return interpretation; }
    public void setInterpretation(String interpretation) { this.interpretation = interpretation; }

    public String getSuggestedAction() { return suggestedAction; }
    public void setSuggestedAction(String suggestedAction) { this.suggestedAction = suggestedAction; }
}