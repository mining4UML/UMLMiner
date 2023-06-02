package com.uniba.mining.listeners;

import com.uniba.mining.listeners.property.PropertyChangeListenerFactory;
import com.uniba.mining.logging.LogExtractor;
import com.uniba.mining.logging.Logger;
import com.vp.plugin.diagram.IConnectorUIModel;
import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IDiagramElementListener;
import com.vp.plugin.diagram.IShapeUIModel;
import com.vp.plugin.model.IAttribute;
import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IEndRelationship;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IOperation;
import com.vp.plugin.model.IParameter;
import com.vp.plugin.model.IRelationshipEnd;
import com.vp.plugin.model.ITemplateParameter;

public class DiagramElementListener implements IDiagramElementListener {
    private static final Logger logger = new Logger(DiagramElementListener.class);
    private IModelElement modelElement;
    private String modelElementPreviousName;

    public DiagramElementListener(IDiagramElement diagramElement) {
        this.modelElement = diagramElement.getModelElement();
        modelElementPreviousName = modelElement.getName();

        if (PropertyChangeListenerFactory.getPropertyChangeListener(modelElement) != null)
            return;

        modelElement.addPropertyChangeListener(PropertyChangeListenerFactory.getInstance(modelElement));

        if (modelElement instanceof IClass) {
            IClass classElement = (IClass) modelElement;
            IAttribute[] attributes = classElement.toAttributeArray();
            IOperation[] operations = classElement.toOperationArray();
            ITemplateParameter[] templateParameters = classElement.toTemplateParameterArray();
            if (attributes != null)
                for (IAttribute attribute : attributes) {
                    LogExtractor.addDiagramUIModel(attribute, diagramElement.getDiagramUIModel());
                    attribute.addPropertyChangeListener(
                            PropertyChangeListenerFactory
                                    .getInstance(attribute));
                }
            if (operations != null)
                for (IOperation operation : operations) {
                    LogExtractor.addDiagramUIModel(operation, diagramElement.getDiagramUIModel());
                    operation.addPropertyChangeListener(
                            PropertyChangeListenerFactory
                                    .getInstance(operation));
                    for (IParameter parameter : operation.toParameterArray())
                        parameter.addPropertyChangeListener(PropertyChangeListenerFactory.getInstance(parameter));
                }
            if (templateParameters != null)
                for (ITemplateParameter templateParameter : classElement.toTemplateParameterArray()) {
                    LogExtractor.addDiagramUIModel(templateParameter, diagramElement.getDiagramUIModel());
                    templateParameter.addPropertyChangeListener(PropertyChangeListenerFactory.getInstance(
                            templateParameter));
                }
        } else if (modelElement instanceof IEndRelationship) {
            IEndRelationship endRelationship = (IEndRelationship) modelElement;
            IRelationshipEnd relationshipFromEnd = endRelationship.getFromEnd();
            IRelationshipEnd relationshipToEnd = endRelationship.getToEnd();

            if (relationshipFromEnd != null)
                relationshipFromEnd
                        .addPropertyChangeListener(PropertyChangeListenerFactory.getInstance(relationshipFromEnd));

            if (relationshipToEnd != null)
                relationshipToEnd
                        .addPropertyChangeListener(PropertyChangeListenerFactory.getInstance(relationshipToEnd));
        }
    }

    @Override
    public void childAdded(IDiagramElement diagramElement, IShapeUIModel shapeUIModel) {
        IModelElement childElement = shapeUIModel.getModelElement();
        logger.info("\"%s\" child added to %s \"%s\"", childElement.getName(), modelElement.getModelType(),
                modelElement.getName());
    }

    @Override
    public void childRemoved(IDiagramElement diagramElement, IShapeUIModel shapeUIModel) {
        IModelElement childElement = shapeUIModel.getModelElement();
        logger.info("\"%s\" child removed from %s \"%s\"", childElement.getName(), modelElement.getModelType(),
                modelElement.getName());
    }

    @Override
    public void diagramElementPropertyChange(IDiagramElement diagramElement, String propertyName) {
        // Empty
    }

    @Override
    public void diagramElementUndeleted(IDiagramElement diagramElement) {
        logger.info("%s \"%s\" undeleted", modelElement.getModelType(),
                modelElement.getName());
    }

    @Override
    public void fromConnectorAdded(IDiagramElement diagramElement, IConnectorUIModel connectorUIModel) {
        logger.info("%s \"%s\" added from connector", modelElement.getModelType(),
                modelElement.getName());
    }

    @Override
    public void fromConnectorRemoved(IDiagramElement diagramElement, IConnectorUIModel connectorUIModel) {
        logger.info("%s \"%s\" removed from connector", modelElement.getModelType(),
                modelElement.getName());
    }

    @Override
    public void nameUpdated(IDiagramElement diagramElement) {
        logger.info("%s \"%s\" renamed to \"%s\"", modelElement.getModelType(),
                modelElementPreviousName, modelElement.getName());
        modelElementPreviousName = modelElement.getName();
    }

    @Override
    public void propertyUpdated(IDiagramElement diagramElement) {
        // Empty
    }

    @Override
    public void toConnectorAdded(IDiagramElement diagramElement, IConnectorUIModel connectorUIModel) {
        logger.info("%s \"%s\" added to connector", modelElement.getModelType(),
                modelElement.getName());
    }

    @Override
    public void toConnectorRemoved(IDiagramElement diagramElement, IConnectorUIModel connectorUIModel) {
        logger.info("%s \"%s\" removed to connector", modelElement.getModelType(),
                modelElement.getName());
    }

}
