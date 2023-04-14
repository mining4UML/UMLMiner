package plugin.mining.listeners;

import com.vp.plugin.diagram.IConnectorUIModel;
import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IDiagramElementListener;
import com.vp.plugin.diagram.IShapeUIModel;
import com.vp.plugin.model.IAttribute;
import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IOperation;

import plugin.mining.utils.Logger;

public class DiagramElementListener implements IDiagramElementListener {
    private static final Logger logger = new Logger(DiagramElementListener.class);
    private static final PropertyChangeListener propertyChangeListener = new PropertyChangeListener();
    private IModelElement modelElement;
    private String modelElementPreviousName;

    public DiagramElementListener(IModelElement modelElement) {
        this.modelElement = modelElement;
        modelElementPreviousName = modelElement.getName();

        modelElement.addPropertyChangeListener(propertyChangeListener);

        if (modelElement instanceof IClass) {
            IClass classElement = (IClass) modelElement;
            for (IAttribute attribute : classElement.toAttributeArray())
                attribute.addPropertyChangeListener(propertyChangeListener);
            for (IOperation attribute : classElement.toOperationArray())
                attribute.addPropertyChangeListener(propertyChangeListener);
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
        // logger.info("%s \"%s\" \"%s\" property change", modelElement.getModelType(),
        // modelElement.getName(), propertyName);
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
