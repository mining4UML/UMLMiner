package com.uniba.mining.processfeedback.uml;

import com.uniba.mining.processfeedback.core.DomainViolationRule;
import com.uniba.mining.processfeedback.core.InterpretedViolation;
import com.uniba.mining.processfeedback.core.ProcessViolation;

public class UmlAggregationRule implements DomainViolationRule {

	@Override
	public boolean matches(ProcessViolation v) {
	    return v != null
	            && (
	                "Aggregation".equalsIgnoreCase(v.getElementType())
	                || contains(v.getActivityName(), "Aggregation")
	            );
	}

    @Override
    public InterpretedViolation interpret(ProcessViolation v) {
        InterpretedViolation iv = new InterpretedViolation();

        iv.setCategory("Aggregation Modeling Process Issue");
        iv.setObservedActivity(safe(v.getActivityName()));

        iv.setExpectedRelation(
                "The detected Declare constraint indicates that the aggregation-related activity should have occurred in a specific process relation with: "
                + safe(v.getActivities()) + "."
        );

        iv.setProcessMeaning(
                "A whole-part relationship was introduced or involved in a modeling sequence that deviates from the expected process."
        );

        iv.setDomainMeaning(
                "An aggregation represents a whole-part relationship and should be introduced only when the relation between the whole and its parts is clear."
        );

        iv.setLearningObjective(
                "Understand when and how to model whole-part relationships in UML class diagrams."
        );

        iv.setSuggestedAction(buildSuggestedAction(v));
        iv.setEvidence(buildEvidence(v));
        iv.setOriginalViolation(v);

        return iv;
    }

    private String buildSuggestedAction(ProcessViolation v) {
        if (isUnknown(v.getElementName())) {
            return "Review the aggregation-related step and verify whether the whole-part relationship was completed and placed coherently in the diagram.";
        }

        return "Review the aggregation " + safe(v.getElementName())
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