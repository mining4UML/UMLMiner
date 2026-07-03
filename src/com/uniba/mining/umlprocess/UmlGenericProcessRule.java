package com.uniba.mining.umlprocess;

import com.uniba.mining.processfeedback.core.DomainViolationRule;
import com.uniba.mining.processfeedback.core.InterpretedViolation;
import com.uniba.mining.processfeedback.core.ProcessViolation;

public class UmlGenericProcessRule implements DomainViolationRule {

    @Override
    public boolean matches(ProcessViolation violation) {
        return violation != null && !isIrrelevant(violation);
    }

    @Override
    public InterpretedViolation interpret(ProcessViolation violation) {

        InterpretedViolation iv = new InterpretedViolation();

        iv.setCategory("UML Modeling Process Deviation");

        iv.setDomainMeaning(
                "The observed modeling action deviates from the expected UML modeling process."
        );

        iv.setLearningObjective(
                "Reflect on the order in which UML elements are introduced, refined, and connected."
        );

        iv.setSuggestedAction(
                "Review this modeling step and check whether the expected subsequent activity was completed."
        );

        iv.setEvidence(buildEvidence(violation));

        iv.setOriginalViolation(violation);

        return iv;
    }

    private boolean isIrrelevant(ProcessViolation violation) {
        return equalsIgnoreCase(violation.getConstraint(), "init")
                || equalsIgnoreCase(violation.getActivityName(), "Project opened")
                || equalsIgnoreCase(violation.getElementType(), "Project");
    }

    private String buildEvidence(ProcessViolation violation) {

        StringBuilder sb = new StringBuilder();

        appendIfPresent(sb, "Constraint", violation.getConstraint());
        appendIfPresent(sb, "Activities", violation.getActivities());
        appendIfPresent(sb, "Activity", violation.getActivityName());
        appendIfPresent(sb, "ActivityIndex", violation.getActivityIndex());
        appendIfPresent(sb, "ElementType", violation.getElementType());
        appendIfPresent(sb, "ElementName", violation.getElementName());
        appendIfPresent(sb, "PropertyName", violation.getPropertyName());
        appendIfPresent(sb, "PropertyValue", violation.getPropertyValue());
        appendIfPresent(sb, "RelationshipFrom", violation.getRelationshipFrom());
        appendIfPresent(sb, "RelationshipTo", violation.getRelationshipTo());
        appendIfPresent(sb, "ElementId", violation.getElementId());
        appendIfPresent(sb, "Description", violation.getDescription());

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

    private boolean equalsIgnoreCase(String value, String expected) {
        return value != null && value.equalsIgnoreCase(expected);
    }
}