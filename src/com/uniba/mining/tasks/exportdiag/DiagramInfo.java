package com.uniba.mining.tasks.exportdiag;

import java.io.File;
import java.util.HashSet;
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
import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IProject;

import org.dom4j.*;

/**
 * 
 * Author: pasquale ardimento Last version: 06 February 2024
 */
public class DiagramInfo {

	private static ResourceBundle messages;

	public DiagramInfo() {
		messages = Language.getInstance().getMessages();
	}

	public DiagramInfo(String language) {
		messages = Language.getInstance(language).getMessages();
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
	/*
	public static String exportInformation(IProject project, String language) throws Exception {

		messages = Language.getInstance(language).getMessages();
		IDiagramUIModel[] diagrams = project.toDiagramArray();

		// Crea una stringa per memorizzare l'output
		StringBuilder output = new StringBuilder();

		if (diagrams.length == 0) {
			// Mostra un messaggio se non ci sono diagrammi delle classi nel progetto
			throw new Exception(
					messages.getString("class.project.absence") + "\n" + messages.getString("feedback.problem"));
		}
		// Controllo del numero di diagrammi di tipo classe
		int classDiagramCount = 0;
		for (IDiagramUIModel diagram : diagrams) {
			if (diagram instanceof IClassDiagramUIModel) {
				classDiagramCount++;
			}
		}

		if (classDiagramCount == 0) {
			throw new Exception(
					messages.getString("class.project.absence") + "\n" + messages.getString("feedback.problem"));
		} else {

			System.out.println("numero diagrammi:" + diagrams.length);
			printInfoProject(project, diagrams, output);
			output.append(String.format(messages.getString("rows.separator")));

			int numElements = 0;

			for (IDiagramUIModel diagram : diagrams) {
				System.out.println(diagram.getType());
				Thread.sleep(10000);

				if (diagram.getType().equals("ClassDiagram")) {
					// Ora puoi accedere alle informazioni del diagramma

					String diagramName = diagram.getName();
					String diagramType = diagram.getType();

					// if (diagram instanceof IClassDiagramUIModel) {
					IClassDiagramUIModel classInfo = (IClassDiagramUIModel) diagram;
					if (classInfo.getDefaultPackage() != null)
						System.out.println(classInfo.getDefaultPackage().getName());

					// }
					// Informazioni del diagramma
					System.out.println("Diagramma: " + diagramName + " - Tipo: " + diagramType);

					if (diagram.getName() != null) {
						output.append(String.format("\n%s %s %s%n", messages.getString("class.diagram.intro"),
								diagram.getName(), messages.getString("class.diagram.contains")));

					} else {
						output.append(String.format("\n%s %s %s%n", messages.getString("class.diagram.intro"),
								messages.getString("class.diagran.noname"),
								messages.getString("class.diagram.contains")));
					}

					IDiagramElement[] diagramElements = diagram.toDiagramElementArray();
					numElements += diagramElements.length;
					for (IDiagramElement diagramElement : diagramElements) {
						IModelElement modelElement = diagramElement.getModelElement();

						if (modelElement instanceof IClass) {
							IClass classe = (IClass) modelElement;

							output.append(String.format("- %s", classe.getName() != null ? classe.getName() : "---"));
							IModelElement parent = classe.getParent();
							if (parent != null) {
								output.append(String.format("  %s",
										parent.getName() != null ? " whose package is " + parent.getName() : "---"));
								output.append(String.format(" and default package: %s",
										classInfo.getDefaultPackage().getName()));
								output.append(String.format("%n"));
							}
						}
					}

					// Assumi che l'array di elementi del modello non sia vuoto
					// if (modelClassElements != null && modelClassElements.length > 0) {

					for (IDiagramElement diagramElement : diagramElements) {
						IModelElement modelElement = diagramElement.getModelElement();

						// Itera sugli elementi del modello delle classi
						// for (IDiagramElement modelElement : modelClassElements) {

						if (modelElement instanceof IClass) {
							IClass classe = (IClass) modelElement;

							if (classe.getParent() != null && classe.getParent().getParent() != null) {
								System.out.println(classe.getParent().getParent().getName() != null
										? classe.getParent().getParent().getName()
												: "non c'è parent model");
							} else {
								System.out.println("Parent o Parent Model è null");
							}

							Optional.ofNullable(classe).map(c -> c.getMasterView()).map(mv -> mv.getModelElement())
							.map(me -> me.getName()).ifPresent(name -> System.out.println("*********" + name));

							Optional.ofNullable(classe).ifPresent(c -> System.out.println("Classe: " + c.getName()));

							output.append(getInfoAttributes(classe));

							// Aggiungi informazioni sulle operazioni della classe
							IOperation[] operazioni = classe.toOperationArray();
							if (operazioni != null && operazioni.length > 0) {
								output.append(String.format("\n%s %s\n", classe.getName(),
										messages.getString("class.operations")));
								for (IOperation operazione : operazioni) {
									output.append(String.format("- %s %s(",
											operazione.getVisibility() != null ? operazione.getVisibility()
													: " visibilità non definita",
													operazione.getName()));

									// Aggiungi parametri dell'operazione se presenti
									IParameter[] parametri = operazione.toParameterArray();
									if (parametri != null && parametri.length > 0) {
										// output.append(". I parametri sono: ");
										for (IParameter parametro : parametri) {
											output.append(String.format("%s: %s, ", parametro.getName(),
													parametro.getTypeAsString()));
										}
										// Rimuovi l'ultima virgola aggiunta
										output.setLength(output.length() - 2);
									}
									output.append(")");

									output.append(String.format((": %s"),
											operazione.getReturnTypeAsString() != null
											? operazione.getReturnTypeAsString()
													: "void"));

									// Vai a capo dopo ogni operazione
									output.append("\n");
								}
							} else {
								output.append(String.format("\n%s %s", messages.getString("class.operations.empty"),
										classe.getName()));
							}

							Iterator<IRelationship> ex = classe.toRelationshipIterator();

							// prova metodo relazioni
							StringBuilder simpleRel = simpleRelationships(classe);
							boolean almenoUnaRelazione = false;

							if (simpleRel != null) {
								output.append(String.format("%n %s %s:", messages.getString("class.relationships"),
										classe.getName()));
								output.append(simpleRel.toString());
								almenoUnaRelazione = true;
							}

							StringBuilder assRel = fromEndRelationships(classe);
							if (assRel != null) {
								if (almenoUnaRelazione == false)
									output.append(String.format("%n %s %s:", messages.getString("class.relationships"),
											classe.getName()));
								output.append(assRel.toString());
							}

							// info sul fatto che la classe sia in una view master o auxiliary
							Optional<String> optionalMessage = Optional.ofNullable(diagramElement)
									.filter(element -> !element.isMasterView()) // Verifica se diagramElement non è una
									// vista principale
									.map(element -> {
										if (modelElement != null && modelElement.getMasterView() != null
												&& modelElement.getMasterView().getDiagramUIModel() != null) {
											return "\nClass " + classe.getName()
											+ " is an auxiliary view. Master view is in "
											+ modelElement.getMasterView().getDiagramUIModel().getName()
											+ " diagram";
										} else {
											return ""; // Se uno dei valori è null, restituisci una stringa vuota
										}
									});

							output.append(optionalMessage.orElse(""));

						}

						// Vai a capo tra le classi
						output.append("\n");
					}

					// Rimuovi tutti i caratteri di nuova riga alla fine
					while (output.length() > 0 && output.charAt(output.length() - 1) == '\n') {
						output.deleteCharAt(output.length() - 1);
					}

					// Write the StringBuilder content to the file
					// add two separation rows
					output.append(String.format(messages.getString("rows.separator")));

					// output.setLength(0);
				} // chiusura if
			} // chiusura iterazione sui diagrammi
			if (numElements == 0) {
				// andrebbe separata la gestione della lingua del feedback dalla lingua usata
				// per le GUI
				messages = Language.getInstance("en").getMessages();
				throw new Exception(messages.getString("class.project.elements.absence") + "\n"
						+ messages.getString("feedback.problem"));
			}
		} // chiusura else quando ci sono diagrammi
		// Restituisci l'output sotto forma di stringa
		return output.toString();
	}
	 */
	public static String exportInformation(IProject project, String language, IDiagramUIModel diagram) throws Exception {
		// Ottieni i messaggi nella lingua specificata
		messages = Language.getInstance(language).getMessages();
		// Crea una stringa per memorizzare l'output
		StringBuilder output = new StringBuilder();
		// Controllo che il diagramma passato non sia null
		if (diagram == null) {
			throw new IllegalArgumentException("Diagram cannot be null");
		}
		// Informazioni di base del diagramma
		appendDiagramInfo(output, diagram);
		// Elementi contenuti nel diagramma
		IDiagramElement[] diagramElements = diagram.toDiagramElementArray();
		// Se non ci sono elementi nel diagramma, solleva un'eccezione
		if (diagramElements.length == 0) {
			messages = Language.getInstance("en").getMessages();
			throw new Exception(messages.getString("class.project.elements.absence") + "\n"
					+ messages.getString("feedback.problem"));
		}
		// altrimenti itera sugli elementi del diagramma passato in input
		else  {		
			for (IDiagramElement diagramElement : diagramElements) {
				IModelElement modelElement = diagramElement.getModelElement();

				if (modelElement instanceof IClass && diagram instanceof IClassDiagramUIModel) {
					// Aggiungi informazioni specifiche per le classi in un diagramma di classi
					IClass classe = (IClass) modelElement;
					ClasseInfo.appendClassInfo(output, classe);
					output.append(ClasseInfo.getInfoAttributes(classe, messages));
					ClasseInfo.appendOperations(output, messages, classe);
					ClasseInfo.appendRelationships(output, messages, diagramElement, classe);
					appendViewInfo(output, diagramElement, classe);
				} /*else if (diagram instanceof IWebDiagramUIModel)  {
					String modelType = modelElement.getModelType(); // retrieve model type as string
					output.append(modelType);
				}*/
				/*else if (modelElement instanceof IUseCase && diagram instanceof IUseCaseDiagramUIModel) {
	            // Aggiungi informazioni specifiche per i casi d'uso in un diagramma di casi d'uso
	           IUseCase useCase = (IUseCase) modelElement;
	            appendUseCaseInfo(output, useCase);
	            appendActors(output, useCase);
	            appendAssociations(output, messages, diagramElement, useCase);
	        } else if (modelElement instanceof IComponent && diagram instanceof IComponentDiagramUIModel) {
	            // Aggiungi informazioni specifiche per i componenti in un diagramma di componenti
	            /*IComponent component = (IComponent) modelElement;
	            appendComponentInfo(output, component);
	            appendInterfaces(output, messages, component);
	            appendDependencies(output, messages, diagramElement, component);
			}*/

				// Aggiungi separatore tra gli elementi
				output.append("\n");
			}
			// Rimuovi caratteri di nuova riga e spazi vuoti finali
			while (output.length() > 0 && (output.charAt(output.length() - 1) == '\n' 
					|| Character.isWhitespace(output.charAt(output.length() - 1)))) {
				output.deleteCharAt(output.length() - 1);
			}
		}

		// Restituisci l'output sotto forma di stringa
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
		// Aggiungi informazioni di base sul diagramma
		String diagramName = diagram.getName();
		if (diagramName != null) {
			output.append(String.format("\n%s %s %s%n", messages.getString("class.diagram.intro"),
					diagram.getName(), messages.getString("class.diagram.contains")));
		} else {
			output.append(String.format("\n%s %s %s%n", messages.getString("class.diagram.intro"),
					messages.getString("class.diagram.noname"),
					messages.getString("class.diagram.contains")));
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
