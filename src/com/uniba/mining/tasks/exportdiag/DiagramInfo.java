package com.uniba.mining.tasks.exportdiag;

import java.io.File;
import java.util.HashSet;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;

import com.uniba.mining.plugin.Config;
import com.uniba.mining.utils.exportXMLCustomized;
import com.vp.plugin.ApplicationManager;
import com.vp.plugin.ModelConvertionManager;
import com.vp.plugin.diagram.IClassDiagramUIModel;
import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.diagram.IUseCaseDiagramUIModel;
import com.vp.plugin.model.IActor;
import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IProject;
import com.vp.plugin.model.IUseCase;

import org.dom4j.*;

/**
 * 
 * Author: pasquale ardimento Last version: 24 july 2025
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
			messages = Language.getInstance("en").getMessages(); // fallback
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
			messages = Language.getInstance("en").getMessages(); // fallback in caso non inizializzato
		}
		return messages;
	}




	/**
	 * Checks if the specified Visual Paradigm project is empty, i.e., it contains
	 * no diagrams.
	 *
	 * @param project The Visual Paradigm project to check for emptiness.
	 * @throws Exception If the project is empty, an exception is thrown with a
	 *                   message indicating the absence of diagrams.
	 */
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
			throw new IllegalArgumentException("Diagram cannot be null");
		}

		appendDiagramInfo(output, diagram);
		IDiagramElement[] diagramElements = diagram.toDiagramElementArray();

		if (diagramElements.length == 0) {
			messages = Language.getInstance("en").getMessages();
			throw new Exception(messages.getString("class.project.elements.absence") + "\n"
					+ messages.getString("feedback.problem"));
		} else {
			if (diagram instanceof IClassDiagramUIModel) {
				for (IDiagramElement diagramElement : diagramElements) {
					IModelElement modelElement = diagramElement.getModelElement();

					if (modelElement instanceof IClass) {
						IClass classe = (IClass) modelElement;
						ClasseInfo.appendClassInfo(output, classe);
						output.append(ClasseInfo.getInfoAttributes(classe, messages));
						ClasseInfo.appendOperations(output, messages, classe);
						ClasseInfo.appendRelationships(output, messages, diagramElement, classe);
						appendViewInfo(output, diagramElement, classe);
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
					// Aggiungi relazioni tra attori e use case evitando duplicati
					//UseCaseInfo.appendRelationships(output,messages,  diagramElements);
				}
				UseCaseInfo.appendRelationships(output,messages, diagramElements);

			}

			while (output.length() > 0 && (output.charAt(output.length() - 1) == '\n'
					|| Character.isWhitespace(output.charAt(output.length() - 1)))) {
				output.deleteCharAt(output.length() - 1);
			}
			// Sostituisce 2 o più righe vuote consecutive con una sola
			//String cleaned = output.toString().replaceAll("(?m)(\\s*\\n){2,}", "\n\n");
			// Rimuove tutte le righe vuote
			String cleaned = output.toString().replaceAll("(?m)^[ \t]*\r?\n", "");  

			// Aggiorna il contenuto del StringBuilder
			output.setLength(0);
			output.append(cleaned);
		}
		return output.toString();
	}


	/**
	 * Questa versione funziona 11 novembre 2024
	 * @param project
	 * @param language
	 * @param diagram
	 * @return
	 * @throws Exception
	 */
	/*	public static String exportInformation(IProject project, String language, IDiagramUIModel diagram) throws Exception {
		// Ottieni i messaggi nella lingua specificata
		messages = Language.getInstance(language).getMessages();

		// Crea una stringa per memorizzare l'output
		StringBuilder output = new StringBuilder();

		// Controllo che il diagramma passato non sia null
		if (diagram == null) {
			throw new IllegalArgumentException("Diagram cannot be null");
		}

		// CANCELLARE
//		if (diagram instanceof IWebDiagramUIModel) {
//			appendDiagramInfo(output, diagram);
//			System.out.println(output.toString());	
//			Thread.sleep(10000);
//		}

		// Controllo se il diagramma è un diagramma di classe
		if (!(diagram instanceof IClassDiagramUIModel)) {
			throw new IllegalArgumentException("Only class diagrams are supported");
		}



		// Informazioni di base del diagramma
		appendDiagramInfo(output, diagram);

		// Itera sugli elementi del diagramma passato in input
		IDiagramElement[] diagramElements = diagram.toDiagramElementArray();
		for (IDiagramElement diagramElement : diagramElements) {
			IModelElement modelElement = diagramElement.getModelElement();

			if (modelElement instanceof IClass) {
				IClass classe = (IClass) modelElement;

				// Aggiunge informazioni sulle classi
				appendClassInfo(output, classe);

				// Aggiunge informazioni sugli attributi della classe
				output.append(getInfoAttributes(classe));

				// Aggiunge informazioni sulle operazioni della classe
				appendOperations(output, messages, classe);

				// Aggiunge informazioni sulle relazioni della classe
				appendRelationships(output, messages, diagramElement, classe);

				// Aggiungi le informazioni sulle relazioni semplici
	            //output.append(simpleRelationships(classe));

	            // Chiamata al metodo per le informazioni sulla vista principale o ausiliaria
	            appendViewInfo(output, diagramElement, classe);

			}

			// Aggiungi separatore tra le classi
			output.append("\n");
		}

		// Se non ci sono elementi nel diagramma, solleva un'eccezione
		if (diagramElements.length == 0) {
			messages = Language.getInstance("en").getMessages();
			throw new Exception(messages.getString("class.project.elements.absence") + "\n"
					+ messages.getString("feedback.problem"));
		}

		// Rimuovi caratteri di nuova riga e spazi vuoti finali
		while (output.length() > 0 && (output.charAt(output.length() - 1) == '\n' 
				|| Character.isWhitespace(output.charAt(output.length() - 1)))) {
			output.deleteCharAt(output.length() - 1);
		}

		// Restituisci l'output sotto forma di stringa
		return output.toString();
	}*/

	// Metodi privati per parti comuni

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
			output.append(String.format("\n%s %s %s %s%n",
					typeDescription, 
					messages.getString("uml.diagram.intro"),
					diagram.getName(),
					messages.getString("uml.diagram.contains")));
		} else {
			output.append(String.format("\n%s %s %s%n",
					typeDescription,
					messages.getString("uml.diagram.noname"),
					messages.getString("uml.diagram.contains")));
		}
	}

	// Metodo privato per aggiungere informazioni sulla vista principale o ausiliaria della classe
	private static void appendViewInfo(StringBuilder output, IDiagramElement diagramElement, IClass classe) {
		Optional<String> optionalMessage = Optional.ofNullable(diagramElement)
				.filter(element -> !element.isMasterView())
				.map(element -> {
					if (classe != null && classe.getMasterView() != null
							&& classe.getMasterView().getDiagramUIModel() != null) {
						return "\nClass " + classe.getName() + " is an auxiliary view. Master view is in "
								+ classe.getMasterView().getDiagramUIModel().getName() + " diagram";
					} else {
						return ""; // Se uno dei valori è null, restituisci una stringa vuota
					}
				});

		optionalMessage.ifPresent(message -> {
			output.append(message);
			output.append("\n");
		});
	}

	/*private static void appendClassInfo(StringBuilder output, IClass classe) {
		// Aggiungi informazioni sulle classi
		output.append(String.format("\nclass %s", classe.getName() != null ? classe.getName() : "---"));

		IModelElement parent = classe.getParent();
		if (parent != null) {
			output.append(String.format(" %s", parent.getName() != null ? " whose package is " + parent.getName() : "---"));
			// Se il diagramma è un IClassDiagramUIModel, puoi accedere al default package
			if (parent instanceof IClassDiagramUIModel) {
				IClassDiagramUIModel classDiagram = (IClassDiagramUIModel) parent;
				if (classDiagram.getDefaultPackage() != null) {
					output.append(String.format(" and default package: %s", classDiagram.getDefaultPackage().getName()));
				}
			}
			output.append(String.format("%n"));
		}
	}*/






	//	private static StringBuilder getRelationshipsInfo(IClass classe) {
	//	    StringBuilder relationships = new StringBuilder();
	//
	//	    Iterator<IRelationship> relationshipsIterator = classe.toRelationshipIterator();
	//	    while (relationshipsIterator.hasNext()) {
	//	        IRelationship relationship = relationshipsIterator.next();
	//
	//	        // Aggiungi le informazioni sulla relazione al StringBuilder
	//	        relationships.append(String.format("- %s %s %s%n",
	//	                relationship.getName() != null ? relationship.getName() : "---",
	//	                relationship.getModelType() != null ? relationship.getModelType() : "relationship type not defined",
	//	                relationship.getFrom().getName() != null ? relationship.getFrom().getName() : "from not defined",
	//	                relationship.getTo().getName() != null ? relationship.getTo().getName() : "to not defined"));
	//	    }
	//
	//	    return relationships;
	//	}



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
		output.append(String.format(" %s %s %s %s", messages.getString("diagrams.contains"), counterDiagrammi,
				messages.getString("diagrams.info"), messages.getString("diagrams.infoListing")));
		for (IClassDiagramUIModel classe : diagrammiClassi) {
			output.append(String.format("%n -%s ", classe.getName()));
		}
	}


	private int showLanguageSelectionDialog() {
		// Imposta la lingua in base alle preferenze dell'utente o utilizza
		// Locale.getDefault() per la lingua di default del sistema
		// Esempio: Lingua italiana
		Locale currentLocale = new Locale("it");

		// Carica il bundle delle risorse per la lingua corrente
		messages = ResourceBundle.getBundle("messages", currentLocale);

		// Creare un array di oggetti rappresentanti le opzioni della lingua nel dialog
		Object[] options = { messages.getString("language.italian"), // Opzione per l'italiano
				messages.getString("language.english") // Opzione per l'inglese
		};

		// Crea un JComboBox con le opzioni e il valore di default impostato a italiano
		JComboBox<Object> languageComboBox = new JComboBox<>(options);
		languageComboBox.setSelectedItem(options[0]); // Imposta il valore di default

		// Mostra un JOptionPane con il JComboBox
		int choice = JOptionPane.showOptionDialog(null, languageComboBox, messages.getString("plugin.select.language"),
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);

		// Restituisci l'indice dell'opzione selezionata
		return choice;
	}

	private int selectDiagramType() {
		Object[] optionsDiagram = { "Class Diagram", "Use Case Diagram" };
		int choiceDiagramType = JOptionPane.showOptionDialog(null, messages.getString("diagram.type.selection"),
				"Diagram Type Selection", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null,
				optionsDiagram, optionsDiagram[0]);

		return choiceDiagramType;
	}

	public static Document exportAsXML(IDiagramUIModel diagram) {
		Document xml = null;
		// Obtain the ModelConvertionManager
		IDiagramUIModel diagramA[] = {diagram};
		final String FILEXML_PATH ="XmlDiagrams";
		ModelConvertionManager convertionManager = ApplicationManager.instance().getModelConvertionManager(); 
		File  filePath= new File(FILEXML_PATH);
		convertionManager.exportXML(diagramA, filePath, true);
		try {
			xml= new exportXMLCustomized().getCustomizedXML("project.xml",
					diagram.getName(), filePath, true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return xml;
	}



}
