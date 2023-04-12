package plugin.mining.listener;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IDiagramListener;
import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.model.IModelElement;

import plugin.mining.utils.Logger;

public class DiagramListener implements IDiagramListener {
	private static final Logger logger = new Logger(DiagramListener.class);
	private static final Set<String> propertiesAllowed = new HashSet<>(Arrays.asList());
	private String diagramUIModelPreviousName;
	private Map<String, DiagramElementListener> diagramElementListenerMap = new HashMap<>();
	private Set<IModelElement> modelElements = new HashSet<>();

	public DiagramListener() {
		// Empty
	}

	@Override
	public void diagramElementAdded(IDiagramUIModel diagramUIModel, IDiagramElement diagramElement) {
		IModelElement modelElement = diagramElement.getModelElement();
		modelElements.add(modelElement);

		if (diagramElementListenerMap.containsKey(diagramElement.getId()))
			return;

		logger.info("%s element %sadded to the diagram", modelElement.getModelType(),
				modelElement.getName() != null ? String.format("\"%s\" ", modelElement.getName()) : "");
		DiagramElementListener diagramElementListener = new DiagramElementListener(modelElement);
		diagramElementListenerMap.put(diagramElement.getId(), diagramElementListener);
		diagramElement.addDiagramElementListener(diagramElementListener);
	}

	@Override
	public void diagramElementRemoved(IDiagramUIModel diagramUIModel, IDiagramElement diagramElement) {
		IDiagramElement[] diagramElements = diagramUIModel.toDiagramElementArray();
		IModelElement modelElementRemoved = modelElements.stream()
				.filter(modelElement -> Arrays.stream(diagramElements)
						.noneMatch(t -> t.getModelElement().equals(modelElement)))
				.findFirst().orElse(null);

		if (modelElementRemoved == null)
			return;

		logger.info("%s element %sremoved from the diagram", modelElementRemoved.getModelType(),
				modelElementRemoved.getName() != null ? String.format("\"%s\" ", modelElementRemoved.getName()) : "");
		diagramElement.removeDiagramElementListener(diagramElementListenerMap.get(diagramElement.getId()));
		modelElements.remove(modelElementRemoved);
	}

	@Override
	public void diagramUIModelLoaded(IDiagramUIModel diagramUIModel) {
		logger.info("%s \"%s\" loaded", diagramUIModel.getType(), diagramUIModel.getName());

		IDiagramElement[] diagramElements = diagramUIModel.toDiagramElementArray();
		for (IDiagramElement diagramElement : diagramElements) {
			IModelElement modelElement = diagramElement.getModelElement();
			modelElements.add(modelElement);
			DiagramElementListener diagramElementListener = new DiagramElementListener(modelElement);
			diagramElementListenerMap.put(diagramElement.getId(), diagramElementListener);
			diagramElement.addDiagramElementListener(diagramElementListener);
		}
	}

	@Override
	public void diagramUIModelRenamed(IDiagramUIModel diagramUIModel) {
		if (diagramUIModelPreviousName != null)
			logger.info("%s \"%s\" renamed to \"%s\"", diagramUIModel.getType(),
					diagramUIModelPreviousName,
					diagramUIModel.getName());

		diagramUIModelPreviousName = diagramUIModel.getName();
	}

	@Override
	public void diagramUIModelPropertyChanged(IDiagramUIModel diagramUIModel, String propertyName,
			Object propertyPreviousValue, Object propertyValue) {
		if (propertyPreviousValue == null || propertyValue == null || !propertiesAllowed.contains(propertyName))
			return;

		logger.info("%s \"%s\" %s property changed from \"%s\" to \"%s\"", diagramUIModel.getType(),
				diagramUIModel.getName(), propertyName, propertyPreviousValue, propertyValue);
	}

}
