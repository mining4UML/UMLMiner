package plugin.mining.listener;

import java.util.Arrays;
import java.util.HashSet;

import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IDiagramListener;
import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IModelElement;

import plugin.mining.utils.Logger;

public class DiagramListener implements IDiagramListener {
	private final Logger logger = new Logger(DiagramListener.class);
	private static final HashSet<String> propertiesIgnored = new HashSet<>(Arrays.asList("modified", "lastModified",
			"pmLastModified", "customizedSortDiagramElementIds"));
	private String diagramUIModelName;

	public DiagramListener() {
		// Empty
	}

	@Override
	public void diagramElementAdded(IDiagramUIModel diagramUIModel, IDiagramElement diagramElement) {
		IModelElement modelElement = diagramElement.getModelElement();
		if (modelElement.getName() != null)
			logger.info("%s element \"%s\" added to the diagram", modelElement.getModelType(),
					modelElement.getName());

		diagramElement.addDiagramElementListener(
				modelElement instanceof IClass ? new ClassDiagramElementListener((IClass) modelElement)
						: new DiagramElementListener(modelElement));
	}

	@Override
	public void diagramElementRemoved(IDiagramUIModel diagramUIModel, IDiagramElement diagramElement) {
		IModelElement modelElement = diagramElement.getModelElement();
		logger.info("%s element \"%s\" removed from the diagram", modelElement.getModelType(),
				modelElement.getName());
	}

	@Override
	public void diagramUIModelLoaded(IDiagramUIModel diagramUIModel) {
		logger.info("%s \"%s\" loaded", diagramUIModel.getType(), diagramUIModel.getName());

		for (IDiagramElement diagramElement : diagramUIModel.toDiagramElementArray()) {
			IModelElement modelElement = diagramElement.getModelElement();
			diagramElement.addDiagramElementListener(
					modelElement instanceof IClass ? new ClassDiagramElementListener((IClass) modelElement)
							: new DiagramElementListener(modelElement));
		}
	}

	@Override
	public void diagramUIModelRenamed(IDiagramUIModel diagramUIModel) {
		String diagramUIModelNewName = diagramUIModel.getName();
		if (diagramUIModelName != null)
			logger.info("%s \"%s\" renamed to \"%s\"", diagramUIModel.getType(), diagramUIModelName,
					diagramUIModelNewName);
		diagramUIModelName = diagramUIModelNewName;
	}

	@Override
	public void diagramUIModelPropertyChanged(IDiagramUIModel diagramUIModel, String property,
			Object propertyPreviousValue, Object propertyValue) {
		if (propertyPreviousValue != null && propertyValue != null && !propertiesIgnored.contains(property))
			logger.info("%s \"%s\" %s property changed from \"%s\" to \"%s\"", diagramUIModel.getType(),
					diagramUIModel.getName(), property, propertyPreviousValue, propertyValue);
	}

}
