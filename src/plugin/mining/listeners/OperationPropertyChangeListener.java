package plugin.mining.listeners;

import com.vp.plugin.model.IOperation;

import plugin.mining.utils.Logger;

public class OperationPropertyChangeListener implements PropertyChangeListener {
    private static final Logger logger = new Logger(OperationPropertyChangeListener.class);
    private IOperation operation;

    public OperationPropertyChangeListener(IOperation operation) {
        this.operation = operation;
    }

    @Override
    public void propertyChange(String propertyName, Object oldValue, Object newValue) {
        if (propertyName.equals("name"))
            logger.info("%s \"%s\" %s changed from \"%s\" to \"%s\"",
                    operation.getParent().getModelType(),
                    operation.getParent().getName(), operation
                            .getModelType(),
                    oldValue,
                    newValue);
    }
}
