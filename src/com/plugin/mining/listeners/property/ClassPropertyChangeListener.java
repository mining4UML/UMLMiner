package com.plugin.mining.listeners.property;

import java.util.Arrays;

import com.plugin.mining.logging.LogActivity;
import com.plugin.mining.logging.LogActivity.ActionType;
import com.plugin.mining.utils.Application;
import com.plugin.mining.logging.LogExtractor;
import com.plugin.mining.logging.Logger;
import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IHasChildrenBaseModelElement;
import com.vp.plugin.model.IModelElement;

/**
 * 
 * @author pasqualeardimento
 *
 */

class ClassPropertyChangeListener extends AbstractPropertyChangeListener<IClass> {
	IModelElement[] templateParameters;

	ClassPropertyChangeListener(IClass classElement) {
		this.templateParameters = classElement.toTemplateParameterArray();
	}

	@Override
	public void propertyChange(IClass classElement, String propertyName, Object oldValue, Object newValue) {
		if (propertyName.equals("childAdded")) {
			IHasChildrenBaseModelElement childElement = (IHasChildrenBaseModelElement) newValue;
			LogActivity logActivity = LogActivity.getInstance(ActionType.ADD, childElement.getModelType());

			Application.runDelayed(() -> {
				Logger.createEvent(logActivity, childElement);
				childElement.addPropertyChangeListener(PropertyChangeListenerFactory.getInstance(childElement));
			});

		} else if (propertyName.equals("childRemoved")) {
			IHasChildrenBaseModelElement childElement = (IHasChildrenBaseModelElement) oldValue;
			LogActivity logActivity = LogActivity.getInstance(ActionType.REMOVE, childElement.getModelType());

			Logger.createEvent(logActivity, childElement);

		} else if (propertyName.equals("templateParameters")) {
			IModelElement[] newTemplateParameters = (IModelElement[]) newValue;

			if (templateParameters == null || newTemplateParameters.length > templateParameters.length) {
				IModelElement newTemplateParameter = templateParameters == null ? newTemplateParameters[0]
						: Arrays.stream(newTemplateParameters)
								.filter(t1 -> Arrays.stream(templateParameters).noneMatch(t2 -> t2.equals(t1)))
								.findFirst().orElse(null);
				if (newTemplateParameter != null) {
					LogExtractor.addDiagramUIModel(newTemplateParameter, LogExtractor.getDiagramUIModel(classElement));
					Logger.createEvent(LogActivity.ADD_TEMPLATE_PARAMETER, newTemplateParameter);
					newTemplateParameter
							.addPropertyChangeListener(
									PropertyChangeListenerFactory.getInstance(newTemplateParameter));
				}
			} else if (newTemplateParameters.length < templateParameters.length) {
				IModelElement oldTemplateParameter = Arrays.stream(templateParameters)
						.filter(t1 -> Arrays.stream(newTemplateParameters).noneMatch(t2 -> t2.equals(t1)))
						.findFirst().orElse(null);
				Logger.createEvent(LogActivity.REMOVE_TEMPLATE_PARAMETER, oldTemplateParameter);
			}
			templateParameters = newTemplateParameters;
		} else {
			String propertyValue = LogExtractor.extractStringValue(newValue);

			Logger.createEvent(LogActivity.UPDATE_CLASS, classElement, propertyName, propertyValue);
		}
	}

}
