package com.uniba.mining.tasks.exportdiag;

import java.io.File;
import java.util.ResourceBundle;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IProject;
import com.vp.plugin.model.IUseCase;
import com.vp.plugin.model.factory.IModelElementFactory;

/**
 * 
 * pasqualeardimento
 */
public class UseCaseInfo {

	private static ResourceBundle messages;



	public UseCaseInfo() {
		messages = Language.getInstance().getMessages();
	}

	public void exportInformation(IProject project, File outputFile) {

		new UseCaseInfo();
		IDiagramUIModel[] diagrams = project.toDiagramArray();

		// Crea una stringa per memorizzare l'output
		StringBuilder output = new StringBuilder();

		if (diagrams.length==0){
			// Mostra un messaggio se non ci sono classi nel progetto
			ApplicationManager.instance().getViewManager().showMessage(messages.getString("class.project.absence"));
		}
		else {
			for (IDiagramUIModel diagram : diagrams) {
				System.out.println("sono qui");

				if (diagram.getType().equals("UseCaseDiagram")) {


					// Retrieve all use case model elements into an array
					IModelElement[] modelUseCaseElements = project.toAllLevelModelElementArray(IModelElementFactory.MODEL_TYPE_USE_CASE);

					// assume the model element array is not empty
					if (modelUseCaseElements != null && modelUseCaseElements.length > 0) {
						// Insert header row in our CSV
						output.append("use case id,name,description,attributes");

						// Insert a new line
						output.append("\n");

						// Retrieve all use case model elements into an array 
						//IModelElement[] modelElements = project.toAllLevelModelElementArray(IModelElementFactory.MODEL_TYPE_USE_CASE);

						// assume the model element array is not empty  
						//if (modelElements != null && modelElements.length > 0) {

						// Create a StringBuffer to store the output
						//				StringBuffer sb = new StringBuffer();

						// Insert header row in our CSV
						//	output.append("use case id,name,description");

						// Insert a new line 
						//	output.append("\n");

						//IDiagramElement[] diagramElements = diagram.toDiagramElementArray();
						// walk through its containing elements one by one
						for (int i = 0; i < modelUseCaseElements.length; i++) {
							if (modelUseCaseElements[i] instanceof IUseCase) {
								IUseCase usecase = (IUseCase) modelUseCaseElements[i];

								// Insert the use case's user ID and separator
								output.append(usecase.getId());
								output.append(",");

								// Insert the use case's name and separator
								output.append(usecase.getName());
								output.append(",");

								// Insert the use case's description and a new line
								output.append(usecase.getDescription());
								output.append("\n");	
							}
						}			

						// Write the StringBuffer to file
						FileWriter.writeToFile(output, outputFile);
						//}

					}
				}
			}
		}
	}
}
