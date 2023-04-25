package plugin.mining.listeners;

import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.model.IProject;
import com.vp.plugin.model.IProjectDiagramListener;

import plugin.mining.logging.Logger;

public class ProjectDiagramListener implements IProjectDiagramListener {
	private final Logger logger = new Logger(ProjectDiagramListener.class);
	private static final DiagramListener diagramListener = new DiagramListener();

	public ProjectDiagramListener() {
		// Empty
	}

	@Override
	public void diagramAdded(IProject project, IDiagramUIModel diagramUIModel) {
		logger.info(String.format("%s \"%s\" added", diagramUIModel.getType(),
				diagramUIModel.getName()));
		diagramUIModel.addDiagramListener(diagramListener);
	}

	@Override
	public void diagramRemoved(IProject project, IDiagramUIModel diagramUIModel) {
		logger.info(String.format("%s \"%s\" removed", diagramUIModel.getType(),
				diagramUIModel.getName()));
	}

}
