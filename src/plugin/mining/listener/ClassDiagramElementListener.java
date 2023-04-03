package plugin.mining.listener;

import java.util.Arrays;

import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.model.IAttribute;
import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IOperation;
import com.vp.plugin.model.property.IModelProperty;

import plugin.mining.utils.Logger;

public class ClassDiagramElementListener extends DiagramElementListener {
    private final Logger logger = new Logger(ClassDiagramElementListener.class);

    private IAttribute[] attributes;
    private IOperation[] operations;

    public ClassDiagramElementListener(IClass classElement) {
        super(classElement);
        this.attributes = classElement.toAttributeArray();
        this.operations = classElement.toOperationArray();
    }

    @Override
    public void diagramElementPropertyChange(IDiagramElement diagramElement, String property) {
        super.diagramElementPropertyChange(diagramElement, property);
        IClass classElement = (IClass) diagramElement.getModelElement();

        if (classElement.attributeCount() < attributes.length) {
            logger.info("%s \"%s\" %s attribute removed", classElement.getModelType(), classElement.getName(),
                    classElement.getAttributeByName(property).toString());
            return;
        }

        if (classElement.attributeCount() > attributes.length) {
            logger.info("%s \"%s\" %s attribute added", classElement.getModelType(), classElement.getName(), property);
            return;
        }

        if (classElement.operationCount() < operations.length) {
            logger.info("%s \"%s\" %s operation removed", classElement.getModelType(), classElement.getName(),
                    property);
            return;
        }

        if (classElement.operationCount() > operations.length) {
            logger.info("%s \"%s\" %s operation added", classElement.getModelType(), classElement.getName(), property);
            return;
        }

        // IAttribute attribute = classElement.getAttributeByName(property);
        // if (attribute != null) {
        // logger.info("%s \"%s\" %s attribute changed", classElement.getModelType(),
        // classElement.getName(),
        // property);
        // return;
        // }

        // IOperation operation = classElement.getOperationByName(property);
        // if (operation != null) {
        // logger.info("%s \"%s\" %s operation changed", classElement.getModelType(),
        // classElement.getName(),
        // property);
        // }
    }

}
