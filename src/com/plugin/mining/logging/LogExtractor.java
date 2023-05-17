package com.plugin.mining.logging;

import java.util.Arrays;
import java.util.HashMap;

import com.plugin.mining.logging.LogActivity.ActionType;
import com.plugin.mining.logging.LogActivity.ModelType;
import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.model.IAssociation;
import com.vp.plugin.model.IAssociationEnd;
import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IHasChildrenBaseModelElement;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IOperation;
import com.vp.plugin.model.IRelationship;

public class LogExtractor {

    public static final String DEFAULT_VALUE = "unknown";
    private static final HashMap<IHasChildrenBaseModelElement, IModelElement> modelElementsParent = new HashMap<>();

    private LogExtractor() {
        // Empty
    }

    public static void addModelElementParent(IHasChildrenBaseModelElement childElement, IModelElement modelElement) {
        modelElementsParent.put(childElement, modelElement);
    }

    public static IModelElement getModelElementParent(IHasChildrenBaseModelElement childElement) {
        return modelElementsParent.get(childElement);
    }

    public static <T> T getOrDefault(T value, T fallbackValue) {
        return value != null ? value : fallbackValue;
    }

    public static String getOrDefault(String value) {
        return value != null ? value : DEFAULT_VALUE;
    }

    public static String extractStringValue(Object value) {
        if (value instanceof IModelElement)
            return ((IModelElement) value).getId();
        if (value instanceof IModelElement[])
            return Arrays.toString(Arrays.stream(((IModelElement[]) value)).map(IModelElement::getName)
                    .toArray(String[]::new));
        if (value instanceof Number)
            return ((Number) value).toString();
        if (value instanceof Boolean)
            return ((Boolean) value).toString();
        if (value instanceof Object[])
            return Arrays.toString((Object[]) value);
        return (String) value;
    }

    public static String extractAggregationKind(IAssociationEnd associationEnd) {
        String aggregationKind = associationEnd.getAggregationKind();
        if (aggregationKind.equals("shared"))
            return "Aggregation";
        if (aggregationKind.equals("composite"))
            return "Composition";
        return "None";
    }

    public static String extractModelStereotype(IModelElement modelElement) {
        if (modelElement instanceof IClass) {
            IClass classElement = (IClass) modelElement;
            return String.join(" ", classElement.stereotypesCount() > 0
                    ? classElement.toStereotypesArray()[0]
                    : "",
                    "Class");
        }
        return null;
    }

    public static String extractModelType(IModelElement modelElement) {
        String stereotype = extractModelStereotype(modelElement);
        if (stereotype != null)
            return stereotype;

        if (modelElement instanceof IOperation) {
            IOperation operation = (IOperation) modelElement;
            IModelElement parentElement = getModelElementParent(operation);
            if (operation.getName().equals(parentElement.getName()))
                return "Constructor";
        }

        if (modelElement instanceof IAssociation) {
            IAssociation association = (IAssociation) modelElement;
            IAssociationEnd fromAssociationEnd = (IAssociationEnd) association.getFromEnd();
            IAssociationEnd toAssociationEnd = (IAssociationEnd) association.getToEnd();
            String fromAggregationKind = extractAggregationKind(fromAssociationEnd);
            String toAggregationKind = extractAggregationKind(toAssociationEnd);
            boolean fromIsNone = fromAggregationKind.equals("None");
            boolean toIsNone = toAggregationKind.equals("None");

            if (fromIsNone && toIsNone)
                return "Association";

            if (fromIsNone)
                return toAggregationKind;

            if (toIsNone)
                return fromAggregationKind;

            return String.format("Association[from=%s,to=%s]", fromAggregationKind, toAggregationKind);
        }

        return modelElement.getModelType();
    }

    public static String extractModelName(IModelElement modelElement) {
        return getOrDefault(modelElement.getName(), DEFAULT_VALUE);
    }

    public static LogActivity extractLogActivity(ActionType actionType, IModelElement modelElement) {
        IDiagramElement[] diagramElements = modelElement.getDiagramElements();
        System.out.println(Arrays.toString(diagramElements));
        if ((actionType == ActionType.ADD && diagramElements.length > 1)
                || (actionType == ActionType.REMOVE && diagramElements.length > 0))
            return LogActivity.getInstance(actionType, ModelType.VIEW.getName());

        if (modelElement instanceof IRelationship)
            return LogActivity.getInstance(actionType, ModelType.RELATIONSHIP.getName());

        return LogActivity.getInstance(actionType, modelElement.getModelType());
    }
}