package com.uniba.mining.tasks.exportdiag;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;

import com.uniba.mining.utils.exportXMLCustomized;
import com.vp.plugin.ApplicationManager;
import com.vp.plugin.ModelConvertionManager;
import com.vp.plugin.diagram.IClassDiagramUIModel;
import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.diagram.IUseCaseDiagramUIModel;
import com.vp.plugin.model.IActor;
import com.vp.plugin.model.IAssociation;
import com.vp.plugin.model.IAssociationEnd;
import com.vp.plugin.model.IAttribute;
import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IOperation;
import com.vp.plugin.model.IParameter;
import com.vp.plugin.model.IProject;
import com.vp.plugin.model.IRelationship;
import com.vp.plugin.model.IRelationshipEnd;
import com.vp.plugin.model.IUseCase;

import org.dom4j.Document;

/**
 * Author: Pasquale Ardimento
 * Last version: updated for LLM-oriented diagram textual export.
 */
public class DiagramInfo {

	private static ResourceBundle messages;

	public DiagramInfo() {
		messages = Language.getInstance().getMessages();
	}

	public DiagramInfo(String language) {
		messages = Language.getInstance(language).getMessages();
	}

	public static String getTypeDescription(IDiagramUIModel diagram) {
		if (messages == null) {
			messages = Language.getInstance("en").getMessages();
		}

		if (diagram instanceof IClassDiagramUIModel) {
			return messages.getString("diagram.type.class");
		} else if (diagram instanceof IUseCaseDiagramUIModel) {
			return messages.getString("diagram.type.use");
		} else {
			return messages.getString("diagram.type.generic");
		}
	}

	public static ResourceBundle getMessages() {
		if (messages == null) {
			messages = Language.getInstance("en").getMessages();
		}
		return messages;
	}

	public static void isProjectEmpty(IProject project) throws Exception {
		messages = Language.getInstance("en").getMessages();
		IDiagramUIModel[] diagrams = project.toDiagramArray();

		if (diagrams.length == 0) {
			throw new Exception(
					messages.getString("class.project.absence") + "\n" + messages.getString("feedback.problem"));
		}
	}

	public static String exportInformation(IProject project, String language, IDiagramUIModel diagram) throws Exception {
		messages = Language.getInstance(language).getMessages();
		StringBuilder output = new StringBuilder();

		if (diagram == null) {
			messages = Language.getInstance("en").getMessages();
			throw new Exception(messages.getString("diagram.project.nodiagram") + "\n"
					+ messages.getString("feedback.problem"));
		}

		appendDiagramInfo(output, diagram);

		IDiagramElement[] diagramElements = diagram.toDiagramElementArray();

		if (diagramElements.length == 0) {
			messages = Language.getInstance("en").getMessages();
			throw new Exception(messages.getString("diagram.project.elements.absence") + "\n"
					+ messages.getString("feedback.problem"));
		}

		if (diagram instanceof IClassDiagramUIModel) {
			for (IDiagramElement diagramElement : diagramElements) {
				IModelElement modelElement = diagramElement.getModelElement();

				if (modelElement instanceof IClass) {
					IClass classe = (IClass) modelElement;
					appendClassInfoForLLM(output, classe, diagramElement);
					output.append("\n");
				}
			}
		} else if (diagram instanceof IUseCaseDiagramUIModel) {
			for (IDiagramElement diagramElement : diagramElements) {
				IModelElement modelElement = diagramElement.getModelElement();

				if (modelElement instanceof IUseCase) {
					UseCaseInfo.appendUseCaseInfo(output, (IUseCase) modelElement, messages);
				} else if (modelElement instanceof IActor) {
					UseCaseInfo.appendActorInfo(output, (IActor) modelElement, messages);
				}

				output.append("\n");
			}

			UseCaseInfo.appendRelationships(output, messages, diagramElements);
		}

		while (output.length() > 0 && Character.isWhitespace(output.charAt(output.length() - 1))) {
			output.deleteCharAt(output.length() - 1);
		}

		String cleaned = output.toString().replaceAll("(?m)^[ \\t]*\\r?\\n", "");

		output.setLength(0);
		output.append(cleaned);

		return output.toString();
	}

	private static void appendDiagramInfo(StringBuilder output, IDiagramUIModel diagram) {
		String diagramName = diagram.getName();
		String typeDescription;

		if (diagram instanceof IClassDiagramUIModel) {
			typeDescription = messages.getString("diagram.type.class");
		} else if (diagram instanceof IUseCaseDiagramUIModel) {
			typeDescription = messages.getString("diagram.type.use");
		} else {
			typeDescription = messages.getString("diagram.type.generic");
		}

		if (diagramName != null) {
			output.append(String.format("%s %s %s %s%n",
					typeDescription,
					messages.getString("uml.diagram.intro"),
					diagram.getName(),
					messages.getString("uml.diagram.contains")));
		} else {
			output.append(String.format("%s %s %s%n",
					typeDescription,
					messages.getString("uml.diagram.noname"),
					messages.getString("uml.diagram.contains")));
		}
	}

	private static void appendClassInfoForLLM(StringBuilder output, IClass classe, IDiagramElement diagramElement) {
		String className = safeName(classe.getName(), "UnnamedClass");

		output.append("\nCLASS ").append(className).append("\n");

		IModelElement parent = classe.getParent();
		if (parent != null && hasText(parent.getName())) {
			output.append("PACKAGE: ").append(parent.getName().trim()).append("\n");
		}

		appendAttributesForLLM(output, classe);
		appendOperationsForLLM(output, classe);
		appendRelationshipsForLLM(output, classe);

		if (diagramElement != null
				&& !diagramElement.isMasterView()
				&& classe.getMasterView() != null
				&& classe.getMasterView().getDiagramUIModel() != null) {

			output.append("VIEW: auxiliary; master diagram: ")
					.append(classe.getMasterView().getDiagramUIModel().getName())
					.append("\n");
		}
	}

	private static void appendAttributesForLLM(StringBuilder output, IClass classe) {
		output.append("ATTRIBUTES:\n");

		IAttribute[] attributes = classe.toAttributeArray();

		if (attributes == null || attributes.length == 0) {
			output.append("- none\n");
			return;
		}

		for (IAttribute attribute : attributes) {
			String name = safeName(attribute.getName(), "unnamedAttribute");
			String type = safeName(attribute.getTypeAsString(), "unspecified");
			String visibility = safeName(attribute.getVisibility(), "unspecified");

			output.append("- ")
					.append(name)
					.append(" : ")
					.append(type)
					.append(" [visibility: ")
					.append(visibility)
					.append("]");

			String initialValue = attribute.getInitialValue();
			if (hasText(initialValue)) {
				output.append(" [default: ").append(initialValue.trim()).append("]");
			}

			output.append("\n");
		}
	}

	private static void appendOperationsForLLM(StringBuilder output, IClass classe) {

	    LinkedHashSet<String> constructors = new LinkedHashSet<>();
	    LinkedHashSet<String> operations = new LinkedHashSet<>();

	    IOperation[] ops = classe.toOperationArray();

	    if (ops == null || ops.length == 0) {
	        output.append("CONSTRUCTORS:\n");
	        output.append("- none\n");
	        output.append("OPERATIONS:\n");
	        output.append("- none\n");
	        return;
	    }

	    for (IOperation operation : ops) {

	        StringBuilder signature = new StringBuilder();

	        signature.append(safeName(operation.getName(), "unnamedOperation"));
	        signature.append("(");

	        IParameter[] parameters = operation.toParameterArray();

	        if (parameters != null && parameters.length > 0) {
	            for (int i = 0; i < parameters.length; i++) {

	                IParameter parameter = parameters[i];

	                if (i > 0) {
	                    signature.append(", ");
	                }

	                signature.append(
	                        safeName(parameter.getName(), "param"))
	                        .append(" : ")
	                        .append(
	                                safeName(parameter.getTypeAsString(),
	                                        "unspecified"));
	            }
	        }

	        signature.append(")");

	        boolean isConstructor =
	                operation.getName() != null
	                && operation.getName().trim()
	                        .equals(classe.getName());

	        if (isConstructor) {

	            constructors.add(signature.toString());

	        } else {

	            signature.append(" : ")
	                    .append(
	                            safeName(
	                                    operation.getReturnTypeAsString(),
	                                    "void"))
	                    .append(" [visibility: ")
	                    .append(
	                            safeName(
	                                    operation.getVisibility(),
	                                    "unspecified"))
	                    .append("]");

	            operations.add(signature.toString());
	        }
	    }

	    output.append("CONSTRUCTORS:\n");

	    if (constructors.isEmpty()) {
	        output.append("- none\n");
	    } else {
	        for (String c : constructors) {
	            output.append("- ").append(c).append("\n");
	        }
	    }

	    output.append("OPERATIONS:\n");

	    if (operations.isEmpty()) {
	        output.append("- none\n");
	    } else {
	        for (String op : operations) {
	            output.append("- ").append(op).append("\n");
	        }
	    }
	}
	private static void appendRelationshipsForLLM(StringBuilder output, IClass classe) {
		output.append("RELATIONSHIPS:\n");

		LinkedHashSet<String> relationships = new LinkedHashSet<>();

		Iterator<IRelationship> fromIterator = classe.fromRelationshipIterator();
		while (fromIterator.hasNext()) {
			IRelationship relationship = fromIterator.next();
			addFormattedRelationship(relationships, relationship);
		}

		Iterator<IRelationship> toIterator = classe.toRelationshipIterator();
		while (toIterator.hasNext()) {
			IRelationship relationship = toIterator.next();
			addFormattedRelationship(relationships, relationship);
		}

		Iterator<?> fromEndIterator = classe.fromRelationshipEndIterator();
		while (fromEndIterator.hasNext()) {
			IRelationshipEnd relationshipEnd = (IRelationshipEnd) fromEndIterator.next();
			if (relationshipEnd != null) {
				addFormattedRelationship(relationships, relationshipEnd.getEndRelationship());
			}
		}

		Iterator<?> toEndIterator = classe.toRelationshipEndIterator();
		while (toEndIterator.hasNext()) {
			IRelationshipEnd relationshipEnd = (IRelationshipEnd) toEndIterator.next();
			if (relationshipEnd != null) {
				addFormattedRelationship(relationships, relationshipEnd.getEndRelationship());
			}
		}

		if (relationships.isEmpty()) {
			output.append("- none\n");
			return;
		}

		for (String relationship : relationships) {
			output.append("- ").append(relationship).append("\n");
		}
	}

	private static void addFormattedRelationship(Set<String> relationships, IRelationship relationship) {
		String formatted = formatRelationshipForLLM(relationship);

		if (hasText(formatted)) {
			relationships.add(formatted);
		}
	}

	private static String formatRelationshipForLLM(IRelationship relationship) {
		if (relationship == null || !hasText(relationship.getModelType())) {
			return null;
		}

		String type = relationship.getModelType().trim();

		IModelElement from = relationship.getFrom();
		IModelElement to = relationship.getTo();

		String fromName = from != null ? safeName(from.getName(), from.getNickname(), "unknown") : "unknown";
		String toName = to != null ? safeName(to.getName(), to.getNickname(), "unknown") : "unknown";

		if ("Generalization".equals(type)) {
		    return toName + " inherits from " + fromName;
		}

		if ("Association".equals(type) && relationship instanceof IAssociation) {
			return formatAssociationForLLM((IAssociation) relationship, fromName, toName);
		}

		return type + " from " + fromName + " to " + toName;
	}

	private static String formatAssociationForLLM(IAssociation association, String fromName, String toName) {
		IRelationshipEnd fromEnd = association.getFromEnd();
		IRelationshipEnd toEnd = association.getToEnd();

		String fromMultiplicity = "unspecified";
		String toMultiplicity = "unspecified";

		if (fromEnd instanceof IAssociationEnd) {
			fromMultiplicity = safeMultiplicity(((IAssociationEnd) fromEnd).getMultiplicity());
		}

		if (toEnd instanceof IAssociationEnd) {
			toMultiplicity = safeMultiplicity(((IAssociationEnd) toEnd).getMultiplicity());
		}

		String relationshipName = safeName(association.getName(), "");
		String label = relationshipName.isBlank()
				? "Association"
				: "Association '" + relationshipName + "'";

		return label + " between " + fromName + " and " + toName
				+ " [" + fromName + " multiplicity: " + fromMultiplicity
				+ "; " + toName + " multiplicity: " + toMultiplicity + "]";
	}

	private static String safeMultiplicity(String value) {
		if (!hasText(value) || "Unspecified".equalsIgnoreCase(value.trim())) {
			return "unspecified";
		}

		return value.trim();
	}

	private static boolean hasText(String value) {
		return value != null && !value.trim().isEmpty();
	}

	private static String safeName(String value, String fallback) {
		if (hasText(value)) {
			return value.trim();
		}

		if (hasText(fallback)) {
			return fallback.trim();
		}

		return "";
	}

	private static String safeName(String value1, String value2, String fallback) {
		if (hasText(value1)) {
			return value1.trim();
		}

		if (hasText(value2)) {
			return value2.trim();
		}

		if (hasText(fallback)) {
			return fallback.trim();
		}

		return "";
	}

	static void printInfoProject(IProject project, IDiagramUIModel[] diagrams, StringBuilder output) {
		String projectName = project.getName();
		output.append(String.format("%s %s", messages.getString("project.info"), projectName));

		int counterDiagrammi = 0;
		Set<IClassDiagramUIModel> diagrammiClassi = new HashSet<IClassDiagramUIModel>();

		for (IDiagramUIModel diagram : diagrams) {
			if (diagram.getType().equals("ClassDiagram")) {
				++counterDiagrammi;
				diagrammiClassi.add((IClassDiagramUIModel) diagram);
			}
		}

		output.append(String.format(" %s %s %s %s",
				messages.getString("diagrams.contains"),
				counterDiagrammi,
				messages.getString("diagrams.info"),
				messages.getString("diagrams.infoListing")));

		for (IClassDiagramUIModel classe : diagrammiClassi) {
			output.append(String.format("%n -%s ", classe.getName()));
		}
	}

	private int showLanguageSelectionDialog() {
		Locale currentLocale = new Locale("it");
		messages = ResourceBundle.getBundle("messages", currentLocale);

		Object[] options = {
				messages.getString("language.italian"),
				messages.getString("language.english")
		};

		JComboBox<Object> languageComboBox = new JComboBox<>(options);
		languageComboBox.setSelectedItem(options[0]);

		int choice = JOptionPane.showOptionDialog(
				null,
				languageComboBox,
				messages.getString("plugin.select.language"),
				JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.INFORMATION_MESSAGE,
				null,
				null,
				null);

		return choice;
	}

	private int selectDiagramType() {
		Object[] optionsDiagram = { "Class Diagram", "Use Case Diagram" };

		int choiceDiagramType = JOptionPane.showOptionDialog(
				null,
				messages.getString("diagram.type.selection"),
				"Diagram Type Selection",
				JOptionPane.DEFAULT_OPTION,
				JOptionPane.INFORMATION_MESSAGE,
				null,
				optionsDiagram,
				optionsDiagram[0]);

		return choiceDiagramType;
	}

	public static Document exportAsXML(IDiagramUIModel diagram) {
		Document xml = null;

		IDiagramUIModel diagramA[] = { diagram };
		final String FILEXML_PATH = "XmlDiagrams";

		ModelConvertionManager convertionManager =
				ApplicationManager.instance().getModelConvertionManager();

		File filePath = new File(FILEXML_PATH);
		convertionManager.exportXML(diagramA, filePath, true);

		try {
			xml = new exportXMLCustomized().getCustomizedXML(
					"project.xml",
					diagram.getName(),
					filePath,
					true);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return xml;
	}
}