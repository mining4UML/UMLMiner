package com.uniba.mining.processfeedback.uml;

import com.uniba.mining.processfeedback.core.DomainViolationRule;
import com.uniba.mining.processfeedback.core.InterpretedViolation;
import com.uniba.mining.processfeedback.core.ProcessViolation;

public class UmlClassRefinementRule implements DomainViolationRule {

    @Override
    public boolean matches(ProcessViolation v) {
        return v != null
                && "Class".equalsIgnoreCase(v.getElementType())
                && (
                    contains(v.getActivityName(), "Class added")
                    || contains(v.getActivities(), "Class added")
                );
    }

    @Override
    public InterpretedViolation interpret(ProcessViolation v) {
        InterpretedViolation iv = new InterpretedViolation();

        String className = safeClassName(v.getElementName());

        iv.setCategory("Class Refinement Process Issue");
        iv.setObservedActivity(safe(v.getActivityName()));

        iv.setExpectedRelation(
                "The detected Declare constraint indicates that the class-related activity should have occurred in a specific process relation with: "
                + safe(v.getActivities()) + "."
        );

        iv.setProcessMeaning(
                "A class was introduced or placed in the diagram, but the observed sequence deviates from the expected class-refinement process."
        );

        iv.setDomainMeaning(buildDomainMeaning(v, className));

        iv.setLearningObjective(
                "Understand that, after introducing a class, the modeler should progressively refine it with meaningful attributes, operations, and relationships."
        );

        iv.setSuggestedAction(buildSuggestedAction(v, className));
        iv.setEvidence(buildEvidence(v));
        iv.setOriginalViolation(v);

        return iv;
    }

    private String buildDomainMeaning(ProcessViolation v, String className) {
        if (contains(v.getActivityName(), "Class added to Project")) {
            return "The class " + className
                    + " was introduced in the project model, but the observed process deviates from the expected class-refinement sequence.";
        }

        if (contains(v.getActivityName(), "Class added to ClassDiagram")) {
            return "The class " + className
                    + " was placed in the class diagram, but the observed process deviates from the expected sequence for refining and organizing class elements.";
        }

        return "The class " + className
                + " is involved in a class-related process deviation detected during the construction of the UML class diagram.";
    }

    private String buildSuggestedAction(ProcessViolation v, String className) {
        if (contains(v.getActivityName(), "Class added to Project")) {
            return "Review the class " + className
                    + " and verify whether it was subsequently refined with meaningful attributes, operations, or relationships.";
        }

        if (contains(v.getActivityName(), "Class added to ClassDiagram")) {
            return "Check whether the class " + className
                    + " is not only placed in the diagram but also properly connected, named, and refined according to the modeling task.";
        }

        return "Review the modeling steps involving the class " + className
                + " and check whether the expected subsequent refinement activity was performed.";
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

    private String safeClassName(String value) {
        return value == null || value.trim().isEmpty()
                ? "unnamed class"
                : value.trim();
    }
}