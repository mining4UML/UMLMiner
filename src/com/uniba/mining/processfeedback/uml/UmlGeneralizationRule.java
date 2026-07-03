package com.uniba.mining.processfeedback.uml;

import com.uniba.mining.processfeedback.core.DomainViolationRule;
import com.uniba.mining.processfeedback.core.InterpretedViolation;
import com.uniba.mining.processfeedback.core.ProcessViolation;

public class UmlGeneralizationRule implements DomainViolationRule {

    @Override
    public boolean matches(ProcessViolation v) {
        return v != null
                && (
                    "Generalization".equalsIgnoreCase(v.getElementType())
                    || contains(v.getActivityName(), "Generalization")
                );
    }

    @Override
    public InterpretedViolation interpret(ProcessViolation v) {
        InterpretedViolation iv = new InterpretedViolation();

        iv.setCategory("Generalization Modeling Process Issue");

        iv.setObservedActivity(safe(v.getActivityName()));

        iv.setExpectedRelation(
                "The detected Declare constraint suggests that the generalization-related activity should have occurred in a specific relation with another modeling activity: "
                + safe(v.getActivities()) + "."
        );

        iv.setProcessMeaning(
                "An inheritance relationship was introduced or involved in a sequence that differs from the expected modeling process."
        );

        iv.setDomainMeaning(
                "A generalization represents an inheritance relationship and should normally be introduced when the superclass and subclass concepts are already clear."
        );

        iv.setLearningObjective(
                "Understand that inheritance should be modeled only when the involved classes and their abstraction relationship are well defined."
        );

        iv.setSuggestedAction(buildSuggestedAction(v));

        iv.setEvidence(buildEvidence(v));

        iv.setOriginalViolation(v);

        return iv;
    }

    private String buildSuggestedAction(ProcessViolation v) {
        if (isUnknown(v.getElementName())) {
            return "Review the generalization-related step and verify whether the inheritance relationship was completed and placed coherently in the diagram.";
        }

        return "Review the generalization " + safe(v.getElementName())
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
        if (value == null || value.trim().isEmpty()) {
            return;
        }

        if (sb.length() > 0) {
            sb.append("; ");
        }

        sb.append(label).append("=").append(value.trim());
    }

    private boolean contains(String value, String token) {
        return value != null
                && token != null
                && value.toLowerCase().contains(token.toLowerCase());
    }

    private boolean isUnknown(String value) {
        return value == null
                || value.trim().isEmpty()
                || "unknown".equalsIgnoreCase(value.trim());
    }

    private String safe(String value) {
        return value == null || value.trim().isEmpty()
                ? "not specified"
                : value.trim();
    }
}