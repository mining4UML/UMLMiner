package plugin.mining.listener;

import java.util.Arrays;
import java.util.HashSet;

import com.vp.plugin.diagram.IClassDiagramUIModel;
import com.vp.plugin.diagram.IConnectorUIModel;
import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IDiagramElementListener;
import com.vp.plugin.diagram.IShapeUIModel;
import com.vp.plugin.diagram.property.IDiagramElementProperty;
import com.vp.plugin.diagram.shape.IClassUIModel;
import com.vp.plugin.model.IAttribute;
import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IModel;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.property.IModelProperty;

import plugin.mining.utils.Logger;

public class DiagramElementListener implements IDiagramElementListener {
    private final Logger logger = new Logger(DiagramElementListener.class);
    private static final HashSet<String> propertiesIgnored = new HashSet<>(Arrays.asList("modified", "lastModified",
            "pmLastModified", "customizedSortDiagramElementIds"));
    private String modelElementName;

    public DiagramElementListener(IModelElement modelElement) {
        modelElementName = modelElement.getName();
    }

    @Override
    public void childAdded(IDiagramElement diagramElement, IShapeUIModel shapeUIModel) {
        IModelElement modelElement = diagramElement.getModelElement();
        IModelElement childElement = shapeUIModel.getModelElement();
        logger.info("\"%s\" child added to \"%s\"", childElement.getName(),
                modelElement.getName());
    }

    @Override
    public void childRemoved(IDiagramElement diagramElement, IShapeUIModel shapeUIModel) {
        IModelElement modelElement = diagramElement.getModelElement();
        IModelElement childElement = shapeUIModel.getModelElement();
        logger.info("\"%s\" child removed from \"%s\"", childElement.getName(),
                modelElement.getName());
    }

    @Override
    public void diagramElementPropertyChange(IDiagramElement diagramElement, String property) {
        // Empty
    }

    @Override
    public void diagramElementUndeleted(IDiagramElement diagramElement) {
        IModelElement modelElement = diagramElement.getModelElement();
        logger.info("\"%s\" %s undeleted", modelElement.getModelType(),
                modelElement.getName());
    }

    @Override
    public void fromConnectorAdded(IDiagramElement diagramElement, IConnectorUIModel connectorUIModel) {
        // Empty
    }

    @Override
    public void fromConnectorRemoved(IDiagramElement diagramElement, IConnectorUIModel connectorUIModel) {
        // Empty
    }

    @Override
    public void nameUpdated(IDiagramElement diagramElement) {
        IModelElement modelElement = diagramElement.getModelElement();
        String modelElementNewName = modelElement.getName();
        logger.info("%s \"%s\" renamed to \"%s\"", modelElement.getModelType(),
                modelElementName, modelElementNewName);
        modelElementName = modelElementNewName;
    }

    @Override
    public void propertyUpdated(IDiagramElement diagramElement) {
        // Empty
    }

    @Override
    public void toConnectorAdded(IDiagramElement diagramElement, IConnectorUIModel connectorUIModel) {
        // Empty
    }

    @Override
    public void toConnectorRemoved(IDiagramElement diagramElement, IConnectorUIModel connectorUIModel) {
        // Empty
    }

}
