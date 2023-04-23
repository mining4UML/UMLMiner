package plugin.mining.listeners;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IDiagramListener;
import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.model.IModelElement;

import plugin.mining.logging.LogActivity;
import plugin.mining.logging.Logger;
import plugin.mining.logging.LogActivity.Type;

public class DiagramListener implements IDiagramListener {
	private static final Logger logger = new Logger(DiagramListener.class);
	private String diagramUIModelPreviousName;
	private final Set<IModelElement> modelElements = new HashSet<>();

	public DiagramListener() {
		// Empty
	}

	@Override
	public void diagramElementAdded(IDiagramUIModel diagramUIModel, IDiagramElement diagramElement) {
		IModelElement modelElement = diagramElement.getModelElement();
		modelElements.add(modelElement);

		Logger.createEvent(LogActivity.instance(Type.ADD, modelElement.getClass()), modelElement);
		logger.info("%s element added to the diagram", modelElement.getModelType());

		DiagramElementListener diagramElementListener = new DiagramElementListener(modelElement);
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

		Logger.createEvent(LogActivity.instance(Type.ADD, modelElementRemoved.getClass()), modelElementRemoved);
		logger.info("%s element %sremoved from the diagram", modelElementRemoved.getModelType(),
				modelElementRemoved.getName() != null ? String.format("\"%s\" ", modelElementRemoved.getName()) : "");
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
			Object oldValue, Object newValue) {

		logger.info("%s \"%s\" %s property changed from \"%s\" to \"%s\"", diagramUIModel.getType(),
				diagramUIModel.getName(), propertyName, oldValue, newValue);
	}

}
