package plugin.mining.listener;

import java.util.Arrays;
import java.util.HashSet;

import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.model.IAttribute;
import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IOperation;

import plugin.mining.utils.Logger;

public class ClassDiagramElementListener extends DiagramElementListener {
    private final Logger logger = new Logger(ClassDiagramElementListener.class);
    private static final HashSet<String> propertiesAllowed = new HashSet<>(Arrays.asList("pDpCmc", "lPtEtCm"));

    private IAttribute[] attributes;
    private IOperation[] operations;

    public ClassDiagramElementListener(IClass classElement) {
        super(classElement);
        this.attributes = classElement.toAttributeArray();
        this.operations = classElement.toOperationArray();
    }

    @Override
    public void diagramElementPropertyChange(IDiagramElement diagramElement, String propertyName) {
        super.diagramElementPropertyChange(diagramElement, propertyName);
        if (!propertiesAllowed.contains(propertyName))
            return;
        IClass classElement = (IClass) diagramElement.getModelElement();
        IAttribute[] newAttributes = classElement.toAttributeArray();
        IOperation[] newOperations = classElement.toOperationArray();

        if (newAttributes.length < attributes.length) {
            IAttribute removedAttribute = Arrays.stream(attributes).filter(
                    attribute -> Arrays.stream(newAttributes).noneMatch(newAttribute -> newAttribute.equals(attribute)))
                    .findFirst().orElse(null);
            if (removedAttribute != null)
                logger.info("%s \"%s\" \"%s\" attribute removed", classElement.getModelType(),
                        classElement.getName(),
                        removedAttribute.getName());

            attributes = newAttributes;
            operations = newOperations;
            return;
        }

        if (newAttributes.length > attributes.length) {
            IAttribute addedAttribute = Arrays.stream(newAttributes).filter(
                    newAttribute -> Arrays.stream(attributes).noneMatch(attribute -> attribute.equals(newAttribute)))
                    .findFirst().orElse(null);
            if (addedAttribute != null)
                logger.info("%s \"%s\" \"%s\" attribute added", classElement.getModelType(),
                        classElement.getName(),
                        addedAttribute.getName());

            attributes = newAttributes;
            operations = newOperations;
            return;
        }

        if (newOperations.length < operations.length) {
            IOperation removedOperation = Arrays.stream(operations).filter(
                    operation -> Arrays.stream(newOperations).noneMatch(newOperation -> newOperation.equals(operation)))
                    .findFirst().orElse(null);
            if (removedOperation != null)
                logger.info("%s \"%s\" \"%s\" operation removed", classElement.getModelType(),
                        classElement.getName(),
                        removedOperation.getName());

            attributes = newAttributes;
            operations = newOperations;
            return;
        }

        if (newOperations.length > operations.length) {
            IOperation addedOperation = Arrays.stream(newOperations).filter(
                    newOperation -> Arrays.stream(operations).noneMatch(operation -> operation.equals(newOperation)))
                    .findFirst().orElse(null);
            if (addedOperation != null)
                logger.info("%s \"%s\" \"%s\" operation added", classElement.getModelType(),
                        classElement.getName(),
                        addedOperation.getName());

            attributes = newAttributes;
            operations = newOperations;
            return;
        }
        IAttribute changedAttribute = Arrays.stream(newAttributes)
                .filter(newAttribute -> Arrays.stream(attributes).noneMatch(attribute -> attribute.getName().equals(
                        newAttribute.getName())))
                .findFirst()
                .orElse(null);
        if (changedAttribute != null) {
            IAttribute previousAttribute = Arrays.stream(attributes)
                    .filter(attribute -> Arrays.stream(newAttributes)
                            .noneMatch(newAttribute -> newAttribute.getName().equals(
                                    attribute.getName())))
                    .findFirst()
                    .orElse(null);
            if (previousAttribute != null)
                logger.info("%s \"%s\" attribute changed from \"%s\" to \"%s\"", classElement.getModelType(),
                        classElement.getName(), previousAttribute.getName(),
                        changedAttribute.getName());

            attributes = newAttributes;
            operations = newOperations;
            return;
        }

        IOperation changedOperation = Arrays.stream(newOperations)
                .filter(newOperation -> Arrays.stream(operations).noneMatch(operation -> operation.getName().equals(
                        newOperation.getName())))
                .findFirst()
                .orElse(null);
        if (changedOperation != null) {
            IOperation previousOperation = Arrays.stream(operations)
                    .filter(operation -> Arrays.stream(newOperations)
                            .noneMatch(newOperation -> newOperation.getName().equals(
                                    operation.getName())))
                    .findFirst()
                    .orElse(null);
            if (previousOperation != null)
                logger.info("%s \"%s\" operation changed from \"%s\" to \"%s\"", classElement.getModelType(),
                        classElement.getName(), previousOperation.getName(),
                        changedOperation.getName());

            attributes = newAttributes;
            operations = newOperations;
        }
    }

}
