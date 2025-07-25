//package com.uniba.mining.tasks.exportdiag;
//
//import java.io.File;
//import java.util.ResourceBundle;
//
//import com.vp.plugin.ApplicationManager;
//import com.vp.plugin.diagram.IDiagramUIModel;
//import com.vp.plugin.model.IModelElement;
//import com.vp.plugin.model.IProject;
//import com.vp.plugin.model.IUseCase;
//import com.vp.plugin.model.factory.IModelElementFactory;
//
///**
// * 
// * pasqualeardimento
// */
//public class UseCaseInfo {
//
//	private static ResourceBundle messages;
//
//
//
//	public UseCaseInfo() {
//		messages = Language.getInstance().getMessages();
//	}
//
//	public void exportInformation(IProject project, File outputFile) {
//
//		new UseCaseInfo();
//		IDiagramUIModel[] diagrams = project.toDiagramArray();
//
//		// Crea una stringa per memorizzare l'output
//		StringBuilder output = new StringBuilder();
//
//		if (diagrams.length==0){
//			// Mostra un messaggio se non ci sono classi nel progetto
//			ApplicationManager.instance().getViewManager().showMessage(messages.getString("class.project.absence"));
//		}
//		else {
//			for (IDiagramUIModel diagram : diagrams) {
//				System.out.println("sono qui");
//
//				if (diagram.getType().equals("UseCaseDiagram")) {
//
//
//					// Retrieve all use case model elements into an array
//					IModelElement[] modelUseCaseElements = project.toAllLevelModelElementArray(IModelElementFactory.MODEL_TYPE_USE_CASE);
//
//					// assume the model element array is not empty
//					if (modelUseCaseElements != null && modelUseCaseElements.length > 0) {
//						// Insert header row in our CSV
//						output.append("use case id,name,description,attributes");
//
//						// Insert a new line
//						output.append("\n");
//
//						// Retrieve all use case model elements into an array 
//						//IModelElement[] modelElements = project.toAllLevelModelElementArray(IModelElementFactory.MODEL_TYPE_USE_CASE);
//
//						// assume the model element array is not empty  
//						//if (modelElements != null && modelElements.length > 0) {
//
//						// Create a StringBuffer to store the output
//						//				StringBuffer sb = new StringBuffer();
//
//						// Insert header row in our CSV
//						//	output.append("use case id,name,description");
//
//						// Insert a new line 
//						//	output.append("\n");
//
//						//IDiagramElement[] diagramElements = diagram.toDiagramElementArray();
//						// walk through its containing elements one by one
//						for (int i = 0; i < modelUseCaseElements.length; i++) {
//							if (modelUseCaseElements[i] instanceof IUseCase) {
//								IUseCase usecase = (IUseCase) modelUseCaseElements[i];
//
//								// Insert the use case's user ID and separator
//								output.append(usecase.getId());
//								output.append(",");
//
//								// Insert the use case's name and separator
//								output.append(usecase.getName());
//								output.append(",");
//
//								// Insert the use case's description and a new line
//								output.append(usecase.getDescription());
//								output.append("\n");	
//							}
//						}			
//
//						// Write the StringBuffer to file
//						FileWriter.writeToFile(output, outputFile);
//						//}
//
//					}
//				}
//			}
//		}
//	}
//}
package com.uniba.mining.tasks.exportdiag;

import java.util.HashSet;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.Set;

import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.model.IAssociation;
import com.vp.plugin.model.IActor;
import com.vp.plugin.model.IRelationship;
import com.vp.plugin.model.IRelationshipEnd;
import com.vp.plugin.model.IUseCase;
import com.vp.plugin.model.IModelElement;

public class UseCaseInfo {

	public static void appendActorInfo(StringBuilder output, IActor actor, ResourceBundle messages) {
		output.append(String.format("\nActor: %s", actor.getName() != null ? actor.getName() : "Unnamed"));

		// Actor ID
		if (actor.getId() != null) {
			output.append(String.format(", ID: %s", actor.getId()));
		}

		// Description
		if (actor.getDocumentation() != null && !actor.getDocumentation().isEmpty()) {
			output.append(String.format(", Description: %s", actor.getDocumentation()));
		}
		// Visibility
		if (actor.getVisibility() != null && !actor.getVisibility().isEmpty()) {
			output.append(String.format(", Visibility: %s", actor.getVisibility()));
		}

		// Flag
		output.append(", Flags: ");
		boolean hasFlag = false;

		if (actor.isAbstract()) {
			output.append("Abstract ");
			hasFlag = true;
		}
		if (actor.isFinalSpecialization()) {
			output.append("Final Specialization ");
			hasFlag = true;
		}
		if (actor.isLeaf()) {
			output.append("Leaf ");
			hasFlag = true;
		}
		if (actor.isRoot()) {
			output.append("Root ");
			hasFlag = true;
		}
		if (actor.isBusinessModel()) {
			output.append("Business Model ");
			hasFlag = true;
		}

		if (!hasFlag) {
			output.append("None");
		}

		output.append("\n");
	}

	public static void appendUseCaseInfo(StringBuilder output, IUseCase useCase, ResourceBundle messages) {
		output.append(String.format("\nUse Case: %s", useCase.getName()));
		if (useCase.getDocumentation() != null && !useCase.getDocumentation().isEmpty()) {
			output.append(String.format(", Description: %s", useCase.getDocumentation()));
		}
		if (useCase.getPreConditions() != null && useCase.getPreConditions().isEmpty()) {
			output.append(String.format(", Pre-conditions: %s", useCase.getPreConditions()));
		}
		if (useCase.getPostConditions() != null && useCase.getPostConditions().isEmpty()) {
			output.append(String.format(", Post-conditions: %s", useCase.getPostConditions()));
		}
		// Flag booleani (non tutti sono applicabili a UseCase, ma puoi filtrare quelli supportati)
		output.append(", Flags: ");
		boolean hasFlag = false;

		if (useCase.isAbstract()) {
			output.append("Abstract ");
			hasFlag = true;
		}
		if (useCase.isLeaf()) {
			output.append("Leaf ");
			hasFlag = true;
		}
		if (useCase.isRoot()) {
			output.append("Root ");
			hasFlag = true;
		}
		if (useCase.isBusinessModel()) {
			output.append("Business Model ");
			hasFlag = true;
		}

		if (!hasFlag) {
			output.append("None");
		}

		output.append("\n");
	}

	public static void appendRelationships(StringBuilder output, ResourceBundle messages, IModelElement element) {
		Set<String> printed = new HashSet<>();

		Iterator<IRelationship> toRels = element.toRelationshipIterator();
		while (toRels.hasNext()) {
			IRelationship rel = toRels.next();
			String relStr = formatUseCaseRelationship(rel, element, messages);
			if (relStr != null && printed.add(relStr)) {
				output.append("- ").append(relStr).append("\n");
			}
		}

		Iterator<IRelationship> fromRels = element.fromRelationshipIterator();
		while (fromRels.hasNext()) {
			IRelationship rel = fromRels.next();
			String relStr = formatUseCaseRelationship(rel, element, messages);
			if (relStr != null && printed.add(relStr)) {
				output.append("- ").append(relStr).append("\n");
			}
		}
	}

	public static void appendRelationships(StringBuilder output, ResourceBundle messages, IDiagramElement[] diagElements) {
		Set<String> printed = new HashSet<>();

		for (IDiagramElement diagramElement : diagElements) {
			IModelElement modelElement = diagramElement.getModelElement();

			Iterator<IRelationship> toRels = modelElement.toRelationshipIterator();
			while (toRels.hasNext()) {
				IRelationship rel = toRels.next();
				String relStr = formatUseCaseRelationship(rel, modelElement, messages);
				if (relStr != null && printed.add(relStr)) {
					output.append("- ").append(relStr).append("\n");
				}
			}

			Iterator<IRelationship> fromRels = modelElement.fromRelationshipIterator();
			while (fromRels.hasNext()) {
				IRelationship rel = fromRels.next();
				String relStr = formatUseCaseRelationship(rel, modelElement, messages);
				if (relStr != null && printed.add(relStr)) {
					output.append("- ").append(relStr).append("\n");
				}
			}
		}
	}

	private static String formatUseCaseRelationship(IRelationship rel, IModelElement base, ResourceBundle messages) {
	    String type = rel.getModelType();
	    IModelElement from = rel.getFrom();
	    IModelElement to = rel.getTo();

	    if (from == null || to == null) {
	        return null;
	    }

	    String fromName = from.getName() != null ? from.getName() : "Unknown";
	    String toName = to.getName() != null ? to.getName() : "Unknown";

	    // Determina il tipo di entità (Actor o Use Case)
	    String fromType = (from instanceof IActor) ? "Actor" : "Use Case";
	    String toType = (to instanceof IActor) ? "Actor" : "Use Case";

	    // Descrizione della relazione, se presente
	    String description = (rel.getDescription() != null && !rel.getDescription().trim().isEmpty())
	            ? String.format(" — Description: %s", rel.getDescription().trim())
	            : "";

	    // Supporta Extend, Include, Association, Generalization, ecc.
	    switch (type) {
	        case "Extend":
	        case "Include":
	        case "Association":
	        case "Dependency":
	        case "Realization":
	            return String.format("%s '%s' %s '%s'%s",
	                    fromType,
	                    fromName,
	                    messages.getString("relationship." + type.toLowerCase()),
	                    toName,
	                    description);
	        case "Generalization":
	            return String.format("%s '%s' %s %s '%s'%s",
	                    fromType,
	                    fromName,
	                    messages.getString("relationship.generalization"),
	                    toType,
	                    toName,
	                    description);
	        default: {
	            System.err.println("Not catched relation: " + type.toLowerCase());
	            return null;
	        }
	    }
	}



	private static void handleAssociation(StringBuilder output, IAssociation association, IModelElement source, ResourceBundle messages) {
		IAssociation model = (IAssociation) association;
		IRelationshipEnd fromEnd = model.getFromEnd();
		IRelationshipEnd toEnd = model.getToEnd();

		String fromName = fromEnd.getModelElement() != null ? fromEnd.getModelElement().getName() : "Unknown";
		String toName = toEnd.getModelElement() != null ? toEnd.getModelElement().getName() : "Unknown";

		if (fromEnd.getModelElement().equals(source)) {
			output.append(String.format("- %s is associated with %s\n", fromName, toName));
		} else {
			output.append(String.format("- %s is associated with %s\n", toName, fromName));
		}
	}
}
