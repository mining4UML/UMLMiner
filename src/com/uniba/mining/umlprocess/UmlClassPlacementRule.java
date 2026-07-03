package com.uniba.mining.umlprocess;

import com.uniba.mining.processfeedback.core.DomainViolationRule;
import com.uniba.mining.processfeedback.core.InterpretedViolation;
import com.uniba.mining.processfeedback.core.ProcessViolation;

public class UmlClassPlacementRule implements DomainViolationRule {

    @Override
    public boolean matches(ProcessViolation v) {
        return v != null
                && "Class".equalsIgnoreCase(v.getElementType())
                && contains(v.getActivities(), "Class added to Project")
                && contains(v.getActivities(), "Class added to ClassDiagram");
    }

    @Override
    public InterpretedViolation interpret(ProcessViolation v) {
        InterpretedViolation iv = new InterpretedViolation();

        iv.setCategory("Class Placement Sequence Issue");

        iv.setDomainMeaning(
                "A class was created in the model repository, but the expected diagram-level placement sequence was not respected."
        );

        iv.setLearningObjective(
                "Understand the distinction between creating UML elements and placing them coherently in the class diagram."
        );

        iv.setSuggestedAction(
                "Verify whether the class is correctly represented in the diagram view after being created."
        );

        iv.setEvidence(buildEvidence(v));

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