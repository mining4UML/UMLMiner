package plugin.mining.listeners;

import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IHasChildrenBaseModelElement;

import plugin.mining.utils.Logger;

public class ClassPropertyChangeListener implements PropertyChangeListener {
    private static final Logger logger = new Logger(ClassPropertyChangeListener.class);
    private IClass classElement;

    public ClassPropertyChangeListener(IClass classElement) {
        this.classElement = classElement;
    }

    @Override
    public void propertyChange(String propertyName, Object oldValue, Object newValue) {
        if (propertyName.equals("childAdded")) {
            IHasChildrenBaseModelElement childElement = (IHasChildrenBaseModelElement) newValue;
            childElement.addPropertyChangeListener(new PropertyChangeListenerFactory(childElement));
            logger.info("%s \"%s\" \"%s\" %s added", classElement.getModelType(),
                    classElement.getName(), childElement.getName(),
                    childElement.getModelType());
        } else if (propertyName.equals("childRemoved")) {
            IHasChildrenBaseModelElement childElement = (IHasChildrenBaseModelElement) oldValue;
            logger.info("%s \"%s\" \"%s\" %s removed", classElement.getModelType(),
                    classElement.getName(), childElement.getName(),
                    childElement.getModelType());
        }
    }

}
