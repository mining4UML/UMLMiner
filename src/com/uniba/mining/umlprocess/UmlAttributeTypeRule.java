package com.uniba.mining.umlprocess;

import com.uniba.mining.processfeedback.core.DomainViolationRule;
import com.uniba.mining.processfeedback.core.InterpretedViolation;
import com.uniba.mining.processfeedback.core.ProcessViolation;

public class UmlAttributeTypeRule implements DomainViolationRule {

    @Override
    public boolean matches(ProcessViolation v) {
        return v != null && (
                contains(v.getActivities(), "Attribute")
                || contains(v.getActivityName(), "Attribute")
                || contains(v.getPropertyName(), "type")
        );
    }

    @Override
    public InterpretedViolation interpret(ProcessViolation v) {
        InterpretedViolation iv = new InterpretedViolation();

        iv.setCategory("Incomplete Attribute Modeling");

        iv.setEvidence(buildEvidence(v));

        iv.setDomainMeaning(
                "An attribute-related modeling step did not follow the expected UML modeling sequence."
        );

        iv.setLearningObjective(
                "Understand that UML attributes should be completely specified, including name and type."
        );

        iv.setSuggestedAction(
                "Review the attribute-related modeling step and verify whether the expected refinement was completed."
        );

        iv.setOriginalViolation(v);

        return iv;
    }

    private String buildEvidence(ProcessViolation v) {
        StringBuilder sb = new StringBuilder();

        appendIfPresent(sb, "Constraint", v.getConstraint());
        appendIfPresent(sb, "Activities", v.getActivities());
        appendIfPresent(sb, "Activity", v.getActivityName());
        appendIfPresent(sb, "ActivityIndex", v.getActivityIndex());
        appendIfPresent(sb, "ElementType", v.getElementType());
        appendIfPresent(sb, "ElementName", v.getElementName());
        appendIfPresent(sb, "PropertyName", v.getPropertyName());
        appendIfPresent(sb, "PropertyValue", v.getPropertyValue());
        appendIfPresent(sb, "RelationshipFrom", v.getRelationshipFrom());
        appendIfPresent(sb, "RelationshipTo", v.getRelationshipTo());
        appendIfPresent(sb, "ElementId", v.getElementId());
        appendIfPresent(sb, "Description", v.getDescription());

        return sb.toString();
    }

    private void appendIfPresent(StringBuilder sb, String label, String value) {
        if (value == null || value.trim().isEmpty()) {
            return;
        }

        if (sb.length() > 0) {
            sb.append("; ");
        }

        sb.append(label)
          .append("=")
          .append(value.trim());
    }

    private boolean contains(String value, String token) {
        return value != null
                && token != null
                && value.toLowerCase().contains(token.toLowerCase());
    }
}