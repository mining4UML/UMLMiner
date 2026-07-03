package com.uniba.mining.processfeedback.uml;

import com.uniba.mining.processfeedback.core.DomainViolationRule;
import com.uniba.mining.processfeedback.core.InterpretedViolation;
import com.uniba.mining.processfeedback.core.ProcessViolation;

public class UmlAssociationRule implements DomainViolationRule {

    @Override
    public boolean matches(ProcessViolation v) {
        return v != null
                && (
                    "Association".equalsIgnoreCase(v.getElementType())
                    || contains(v.getActivityName(), "Association")
                );
    }

    @Override
    public InterpretedViolation interpret(ProcessViolation v) {
        InterpretedViolation iv = new InterpretedViolation();

        iv.setCategory("Association Modeling Process Issue");
        iv.setObservedActivity(safe(v.getActivityName()));

        iv.setExpectedRelation(
                "The detected Declare constraint indicates that the association-related activity should have occurred in a specific process relation with: "
                + safe(v.getActivities()) + "."
        );

        iv.setProcessMeaning(
                "An association was introduced or involved in a modeling sequence that deviates from the expected process."
        );

        iv.setDomainMeaning(
                "An association represents a structural relationship between classes and should be introduced when the connected concepts are already meaningful."
        );

        iv.setLearningObjective(
                "Understand that associations should be created and refined coherently after identifying the involved classes."
        );

        iv.setSuggestedAction(buildSuggestedAction(v));
        iv.setEvidence(buildEvidence(v));
        iv.setOriginalViolation(v);

        return iv;
    }

    private String buildSuggestedAction(ProcessViolation v) {
        if (isUnknown(v.getElementName())) {
            return "Review the association-related step and verify whether the relationship was properly completed and connected in the diagram.";
        }

        return "Review the association " + safe(v.getElementName())
                + " and verify whether the expected follow-up modeling activity was completed.";
    }

    private String buildEvidence(ProcessViolation v) {
        StringBuilder sb = new StringBuilder();

        appendIfPresent(sb, "Constraint", v.getConstraint());
        appendIfPresent(sb, "Activities", v.getActivities());
        appendIfPresent(sb, "Activity", v.getActivityName());
        appendIfPresent(sb, "ActivityIndex", v.getActivityIndex());
        appendIfPresent(sb, "ElementType", v.getElementType());
        appendIfPresent(sb, "ElementName", v.getElementName());
        appendIfPresent(sb, "RelationshipFrom", v.getRelationshipFrom());
        appendIfPresent(sb, "RelationshipTo", v.getRelationshipTo());
        appendIfPresent(sb, "ElementId", v.getElementId());

        return sb.toString();
    }

    private void appendIfPresent(StringBuilder sb, String label, String value) {
        if (value == null || value.trim().isEmpty()) return;
        if (sb.length() > 0) sb.append("; ");
        sb.append(label).append("=").append(value.trim());
    }

    private boolean contains(String value, String token) {
        return value != null && token != null
                && value.toLowerCase().contains(token.toLowerCase());
    }

    private boolean isUnknown(String value) {
        return value == null || value.trim().isEmpty()
                || "unknown".equalsIgnoreCase(value.trim());
    }

    private String safe(String value) {
        return value == null || value.trim().isEmpty()
                ? "not specified"
                : value.trim();
    }
}