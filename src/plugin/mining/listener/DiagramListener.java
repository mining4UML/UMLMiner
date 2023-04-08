package plugin.mining.listener;

import java.util.Arrays;
import java.util.HashSet;

import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IDiagramListener;
import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.model.IModelElement;

import plugin.mining.utils.Logger;

public class DiagramListener implements IDiagramListener {
	private static final Logger logger = new Logger(DiagramListener.class);
	private static final HashSet<String> propertiesAllowed = new HashSet<>(Arrays.asList());
	private String diagramUIModelPreviousName;
	private IModelElement[] modelElements;

	public DiagramListener() {
		// Empty
	}

	@Override
	public void diagramElementAdded(IDiagramUIModel diagramUIModel, IDiagramElement diagramElement) {
		IModelElement modelElement = diagramElement.getModelElement();
		if (modelElement.getName() != null)
			logger.info("%s element \"%s\" added to the diagram", modelElement.getModelType(),
					modelElement.getName());

		diagramElement.addDiagramElementListener(new DiagramElementListener(modelElement));

		modelElements = Arrays.stream(diagramUIModel.toDiagramElementArray())
				.map(t -> t.getModelElement()).toArray(IModelElement[]::new);
	}

	@Override
	public void diagramElementRemoved(IDiagramUIModel diagramUIModel, IDiagramElement diagramElement) {
		IDiagramElement[] diagramElements = diagramUIModel.toDiagramElementArray();
		IModelElement modelElementRemoved = Arrays.stream(modelElements)
				.filter(modelElement -> Arrays.stream(diagramElements)
						.noneMatch(t -> t.getModelElement().equals(modelElement)))
				.findFirst().orElse(null);

		if (modelElementRemoved != null) {
			logger.info("%s element \"%s\" removed from the diagram", modelElementRemoved.getModelType(),
					modelElementRemoved.getName());
		}

		modelElements = Arrays.stream(
				diagramElements)
				.map(t -> t.getModelElement()).filter(t -> t != null).toArray(IModelElement[]::new);
	}

	@Override
	public void diagramUIModelLoaded(IDiagramUIModel diagramUIModel) {
		logger.info("%s \"%s\" loaded", diagramUIModel.getType(), diagramUIModel.getName());

		IDiagramElement[] diagramElements = diagramUIModel.toDiagramElementArray();
		modelElements = Arrays.stream(
				diagramElements)
				.map(t -> t.getModelElement()).toArray(IModelElement[]::new);
		for (IDiagramElement diagramElement : diagramElements) {
			IModelElement modelElement = diagramElement.getModelElement();
			diagramElement.addDiagramElementListener(new DiagramElementListener(modelElement));
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
		if (propertyPreviousValue != null && propertyValue != null && propertiesAllowed.contains(propertyName))
			logger.info("%s \"%s\" %s property changed from \"%s\" to \"%s\"", diagramUIModel.getType(),
					diagramUIModel.getName(), propertyName, propertyPreviousValue, propertyValue);
	}

}
