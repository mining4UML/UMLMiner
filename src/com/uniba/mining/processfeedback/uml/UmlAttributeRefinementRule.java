package com.uniba.mining.processfeedback.uml;

import com.uniba.mining.processfeedback.core.DomainViolationRule;
import com.uniba.mining.processfeedback.core.InterpretedViolation;
import com.uniba.mining.processfeedback.core.ProcessViolation;

public class UmlAttributeRefinementRule implements DomainViolationRule {

    @Override
    public boolean matches(ProcessViolation v) {
        return v != null
                && (
                    "Attribute".equalsIgnoreCase(v.getElementType())
                    || contains(v.getActivityName(), "Attribute")
                );
    }

    @Override
    public InterpretedViolation interpret(ProcessViolation v) {
        InterpretedViolation iv = new InterpretedViolation();

        String attributeName = safe(v.getElementName());

        iv.setCategory("Attribute Refinement Process Issue");
        iv.setObservedActivity(safe(v.getActivityName()));

        iv.setExpectedRelation(
                "The detected Declare constraint indicates that the attribute-related activity should have occurred in a specific process relation with: "
                + safe(v.getActivities()) + "."
        );

        iv.setProcessMeaning(
                "An attribute was added or modified in a sequence that deviates from the expected attribute-refinement process."
        );

        iv.setDomainMeaning(
                "Attributes should normally be completed with meaningful names and types as part of the class specification process."
        );

        iv.setLearningObjective(
                "Understand that attributes in UML class diagrams should be fully specified and consistently refined."
        );

        iv.setSuggestedAction(
                "Review the attribute " + attributeName
                + " and verify whether its name and type were properly defined in the expected modeling sequence."
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

    private String safe(String value) {
        return value == null || value.trim().isEmpty()
                ? "not specified"
                : value.trim();
    }
}