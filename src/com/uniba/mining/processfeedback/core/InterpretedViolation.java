package com.uniba.mining.processfeedback.core;

public class InterpretedViolation {

    private String category;
    private String domainMeaning;
    private String learningObjective;
    private String suggestedAction;
    private String evidence;

    private String observedActivity;
    private String expectedRelation;
    private String processMeaning;

    private ProcessViolation originalViolation;

    public String toPromptText() {

        StringBuilder sb = new StringBuilder();

        appendIfPresent(sb, "Category", category);
        appendIfPresent(sb, "Observed activity", observedActivity);
        appendIfPresent(sb, "Expected relation", expectedRelation);
        appendIfPresent(sb, "Process meaning", processMeaning);
        appendIfPresent(sb, "Domain meaning", domainMeaning);
        appendIfPresent(sb, "Learning objective", learningObjective);
        appendIfPresent(sb, "Suggested action", suggestedAction);
        appendIfPresent(sb, "Evidence", evidence);

        return sb.toString();
    }

    private void appendIfPresent(StringBuilder sb, String label, String value) {
        if (value == null || value.trim().isEmpty()) {
            return;
        }

        sb.append(label)
          .append(": ")
          .append(value.trim())
          .append("\n");
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDomainMeaning() {
        return domainMeaning;
    }

    public void setDomainMeaning(String domainMeaning) {
        this.domainMeaning = domainMeaning;
    }

    public String getLearningObjective() {
        return learningObjective;
    }

    public void setLearningObjective(String learningObjective) {
        this.learningObjective = learningObjective;
    }

    public String getSuggestedAction() {
        return suggestedAction;
    }

    public void setSuggestedAction(String suggestedAction) {
        this.suggestedAction = suggestedAction;
    }

    public String getEvidence() {
        return evidence;
    }

    public void setEvidence(String evidence) {
        this.evidence = evidence;
    }

    public String getObservedActivity() {
        return observedActivity;
    }

    public void setObservedActivity(String observedActivity) {
        this.observedActivity = observedActivity;
    }

    public String getExpectedRelation() {
        return expectedRelation;
    }

    public void setExpectedRelation(String expectedRelation) {
        this.expectedRelation = expectedRelation;
    }

    public String getProcessMeaning() {
        return processMeaning;
    }

    public void setProcessMeaning(String processMeaning) {
        this.processMeaning = processMeaning;
    }

    public ProcessViolation getOriginalViolation() {
        return originalViolation;
    }

    public void setOriginalViolation(ProcessViolation originalViolation) {
        this.originalViolation = originalViolation;
    }
}