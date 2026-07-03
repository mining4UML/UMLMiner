package com.uniba.mining.processfeedback.uml;

import com.uniba.mining.processfeedback.core.DomainViolationRule;
import com.uniba.mining.processfeedback.core.InterpretedViolation;
import com.uniba.mining.processfeedback.core.ProcessViolation;

public class UmlDiagramPlacementRule implements DomainViolationRule {

    @Override
    public boolean matches(ProcessViolation v) {
        return v != null && (
                "ClassDiagram".equalsIgnoreCase(v.getElementType())
                || contains(v.getActivityName(), "ClassDiagram")
                || contains(v.getActivities(), "ClassDiagram")
        );
    }

    @Override
    public InterpretedViolation interpret(ProcessViolation v) {
        InterpretedViolation iv = new InterpretedViolation();

        String diagramName = safe(v.getElementName());

        iv.setCategory("Diagram Construction Sequence Issue");
        iv.setObservedActivity(safe(v.getActivityName()));

        iv.setExpectedRelation(
                "The detected Declare constraint indicates that the diagram-related activity should have occurred in a specific process relation with: "
                + safe(v.getActivities()) + "."
        );

        iv.setProcessMeaning(
                "A diagram creation, naming, or placement step occurred in a sequence that differs from the expected diagram-construction process."
        );

        iv.setDomainMeaning(
                "Creating model elements and arranging them coherently in a class diagram are distinct modeling activities."
        );

        iv.setLearningObjective(
                "Understand that diagram construction should support a clear and coherent representation of the model."
        );

        iv.setSuggestedAction(
                "Review the diagram " + diagramName
                + " and verify whether its creation, naming, and organization are coherent with the expected modeling sequence."
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