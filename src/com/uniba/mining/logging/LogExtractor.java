package com.uniba.mining.logging;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.uniba.mining.logging.LogActivity.ActionType;
import com.uniba.mining.logging.LogActivity.ModelType;
import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.model.IAssociation;
import com.vp.plugin.model.IAssociationEnd;
import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IMessage;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IOperation;
import com.vp.plugin.model.IRelationship;

public class LogExtractor {

    public static final String DEFAULT_VALUE = "unknown";
    private static final Map<String, IDiagramUIModel> diagramUIModels = new HashMap<>();
    private static final Map<String, IModelElement> parentModelElements = new HashMap<>();

    private LogExtractor() {
        // Empty
    }

    public static <T> T getOrDefault(T value, T fallbackValue) {
        return value != null ? value : fallbackValue;
    }

    public static String getOrDefault(String value) {
        return value != null ? value : DEFAULT_VALUE;
    }

    public static void addParentModelElement(IModelElement childElement, IModelElement modelElement) {
        parentModelElements.put(childElement.getId(), modelElement);
    }

    public static void addParentModelElementRecursive(IModelElement modelElement) {
        if (modelElement.childCount() == 0)
            return;

        for (IModelElement childElement : modelElement.toChildArray()) {
            addParentModelElement(childElement, modelElement);
            addParentModelElementRecursive(childElement);
        }
    }

    public static IModelElement getParentModelElement(IModelElement childElement) {
        return parentModelElements.get(childElement.getId());
    }

    public static void addDiagramUIModel(IModelElement modelElement, IDiagramUIModel diagramUIModel) {
        diagramUIModels.put(modelElement.getId(), diagramUIModel);
    }

    public static IDiagramUIModel getDiagramUIModel(IModelElement modelElement) {
        return diagramUIModels.get(modelElement.getId());
    }

    public static String extractStringValue(Object value) {
        if (value instanceof IModelElement) {
            //return ((IModelElement) value).getId();
        	 return ((IModelElement) value).getName();
        }
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
            IModelElement parentModelElement = getParentModelElement(operation);
            if (operation.getName().equals(parentModelElement.getName()))
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

        if (modelElement instanceof IMessage) {
            IMessage message = (IMessage) modelElement;
            IModelElement messageActionType = message.getActionType();
            if (messageActionType != null)
                return String.join(" ", message.getActionType().getName(), message.getModelType());
        }

        return getOrDefault(modelElement.getModelType());
    }

    public static String extractSourceType(ModelType sourceModelType, IDiagramUIModel diagramUIModel) {
        if (sourceModelType == ModelType.DIAGRAM)
            return diagramUIModel.getType();

        return sourceModelType.getName();
    }

    public static String extractModelName(IModelElement modelElement) {
        return getOrDefault(modelElement.getName());
    }

    public static LogActivity extractLogActivity(ActionType actionType, IModelElement modelElement) {
        IDiagramElement[] diagramElements = modelElement.getDiagramElements();
        if ((actionType == ActionType.ADD && diagramElements.length > 1)
                || (actionType == ActionType.REMOVE && diagramElements.length > 0))
            return LogActivity.getInstance(actionType, ModelType.VIEW);

        if (modelElement instanceof IRelationship)
            return LogActivity.getInstance(actionType, ModelType.RELATIONSHIP);

        return LogActivity.getInstance(actionType, modelElement);
    }
}
