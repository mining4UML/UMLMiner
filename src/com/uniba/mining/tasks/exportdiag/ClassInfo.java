package com.uniba.mining.tasks.exportdiag;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;

import com.uniba.mining.logging.LogExtractor;
import com.vp.plugin.ApplicationManager;
import com.vp.plugin.diagram.IClassDiagramUIModel;
import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IDiagramUIModel;
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

/**
 * 
 * Author: pasquale ardimento Last version: 06 February 2024
 */
public class ClassInfo {

	private static ResourceBundle messages;

	public ClassInfo() {
		messages = Language.getInstance().getMessages();
	}

	public ClassInfo(String language) {
		messages = Language.getInstance(language).getMessages();
	}

	public static void exportInformation(IProject project, File outputFile) {

		// messages = Language.getMessages();
		IDiagramUIModel[] diagrams = project.toDiagramArray();

		// Crea una stringa per memorizzare l'output
		StringBuilder output = new StringBuilder();

		if (diagrams.length == 0) {
			// Mostra un messaggio se non ci sono classi nel progetto
			ApplicationManager.instance().getViewManager().showMessage(messages.getString("class.project.absence"));
		} else {

			System.out.println("numero diagrammi:" + diagrams.length);
			printInfoProject(project, diagrams, output);
			output.append(String.format(messages.getString("rows.separator")));

			for (IDiagramUIModel diagram : diagrams) {

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

							if (classe.getMasterView().getModelElement().getName() != null)
								System.out.println("*********" + classe.getMasterView().getModelElement().getName());

							System.out.println("Classe: " + classe.getName());

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
							output.append(!diagramElement.isMasterView()
									? "\nClass " + classe.getName() + " is an auxiliary view. Master view is in "
									+ modelElement.getMasterView().getDiagramUIModel().getName() + " diagram"
									: "");

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
					FileWriter.writeToFile(output, outputFile);
					output.setLength(0);
				} // chiusura if
			} // chiusura iterazione sui diagrammi
		} // chiusura else quando ci sono diagrammi
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

	public static String exportInformation(IProject project, String language, IDiagramUIModel diagram) throws Exception {
		// Ottieni i messaggi nella lingua specificata
		messages = Language.getInstance(language).getMessages();

		// Crea una stringa per memorizzare l'output
		StringBuilder output = new StringBuilder();

		// Controllo che il diagramma passato non sia null
		if (diagram == null) {
			throw new IllegalArgumentException("Diagram cannot be null");
		}

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
	}

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

	private static void appendClassInfo(StringBuilder output, IClass classe) {
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
	}

	private static void appendOperations(StringBuilder output, ResourceBundle messages, IClass classe) {
		// Aggiungi informazioni sulle operazioni della classe
		IOperation[] operazioni = classe.toOperationArray();
		if (operazioni != null && operazioni.length > 0) {
			output.append(String.format("\n%s %s\n", classe.getName(),
					messages.getString("class.operations")));
			for (IOperation operazione : operazioni) {
				output.append(String.format("- %s %s(",
						operazione.getVisibility() != null ? operazione.getVisibility() : "visibilità non definita",
								operazione.getName()));

				// Aggiungi parametri dell'operazione se presenti
				IParameter[] parametri = operazione.toParameterArray();
				if (parametri != null && parametri.length > 0) {
					for (IParameter parametro : parametri) {
						output.append(String.format("%s: %s, ", parametro.getName(), parametro.getTypeAsString()));
					}
					// Rimuovi l'ultima virgola aggiunta
					output.setLength(output.length() - 2);
				}
				output.append(")");

				output.append(String.format((": %s"),
						operazione.getReturnTypeAsString() != null ? operazione.getReturnTypeAsString() : "void"));

				output.append("\n");
			}
		} else {
			output.append(String.format("\n%s %s", messages.getString("class.operations.empty"),
					classe.getName()));
		}
	}

	/**
	 * Appends relationship information of the class to the provided StringBuilder.
	 * Retrieves both "To" and "From" relationships and appends them to the output if present.
	 *
	 * @param output The StringBuilder where the relationship information will be appended.
	 * @param messages ResourceBundle containing localized messages for output formatting.
	 * @param diagramElement The diagram element associated with the class.
	 * @param classe The class for which relationships are to be retrieved and appended.
	 */
	private static void appendRelationships(StringBuilder output, ResourceBundle messages, IDiagramElement diagramElement, IClass classe) {
	    // Retrieve "To" relationships information of the class
	    StringBuilder relationships = getToRelationshipsInfo(classe);
	    // Retrieve "From" relationships information of the class
	    relationships.append(getFromRelationshipsInfo(classe));
	    
        // Relazioni di associazione
	    relationships.append(fromEndRelationships(classe));
	    
	    // Append relationships information to output if there are any relationships
	    if (relationships.length() > 0) {
	        output.append(String.format("\n%s %s :\n", messages.getString("class.relationships"), classe.getName()));
	        output.append(relationships.toString());
	    }
	}



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

	/**
	 * 
	 * @param classe
	 * @return
	 */
	private static StringBuilder getToRelationshipsInfo(IClass classe) {
	    StringBuilder relationships = new StringBuilder();
	    
	    Iterator<IRelationship> relationshipsIterator = classe.toRelationshipIterator();
	    while (relationshipsIterator.hasNext()) {
	        IRelationship relationship = relationshipsIterator.next();

	        // Estrai le informazioni sulla relazione
	        String relationshipName = relationship.getName() != null ? relationship.getName().trim() : "";
	        String relationshipType = relationship.getModelType() != null ? relationship.getModelType().trim() : "relationship type not defined";
	        String fromName = relationship.getFrom() != null && relationship.getFrom().getName() != null ? " from "+ relationship.getFrom().getName().trim() : "from not defined";
	        String toName = relationship.getTo() != null && relationship.getTo().getName() != null ? " to " + relationship.getTo().getName().trim() : "to not defined";

	        // Aggiungi le informazioni sulla relazione al StringBuilder
	        relationships.append(String.format("- %s%s%s%s%n", relationshipName, relationshipType, fromName, toName));
	    }

	    return relationships;
	}


	// Metodo che acquisisce informazioni per le relazioni "From"
	private static StringBuilder getFromRelationshipsInfo(IClass classe) {
	    StringBuilder relationships = new StringBuilder();

	    Iterator<IRelationship> relationshipsIterator = classe.fromRelationshipIterator();
	    while (relationshipsIterator.hasNext()) {
	        IRelationship relationship = relationshipsIterator.next();

	        // Estrai le informazioni sulla relazione
	        String relationshipName = relationship.getName() != null ? relationship.getName().trim() : "";
	        String relationshipType = relationship.getModelType() != null ? relationship.getModelType().trim() : "relationship type not defined";
	        String fromName = relationship.getFrom() != null && relationship.getFrom().getName() != null ? " from "+ relationship.getFrom().getName().trim() : "from not defined";
	        String toName = relationship.getTo() != null && relationship.getTo().getName() != null ? " to " + relationship.getTo().getName().trim() : "to not defined";

	        // Aggiungi le informazioni sulla relazione al StringBuilder
	        relationships.append(String.format("- %s%s%s%s%n", relationshipName, relationshipType, fromName, toName));
	    }

	    return relationships;
	}





	private static void printInfoProject(IProject project, IDiagramUIModel[] diagrams, StringBuilder output) {
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

	private static StringBuilder getInfoAttributes(IClass classe) {

		StringBuilder output = new StringBuilder();
		// Ottieni gli attributi della classe
		IAttribute[] attributi = classe.toAttributeArray();

		// Assumi che l'array di attributi non sia vuoto
		if (attributi != null && attributi.length > 0) {

			// Aggiungi informazioni sulla classe all'output
			output.append(String.format("%n%s %s", classe.getName(), messages.getString("class.attributes.contains")));

			for (IAttribute attributo : attributi) {
				// Aggiungi informazioni sull'attributo all'output
				output.append(String.format("%n- %s ", attributo.getName()));

				// Aggiungi informazioni sulla visibilità e il tipo dell'attributo
				output.append(String.format(" %s %s, %s %s %s %s", messages.getString("class.attributes.visibility"),
						attributo.getVisibility(), messages.getString("class.attributes.type"),
						LogExtractor.extractStringValue(attributo.getType()),
						messages.getString("class.attributes.scope"), attributo.getScope()));
				// attributo.getType()));

				// Aggiungi informazioni sul valore di default dell'attributo
				String valoreDefault = attributo.getInitialValue();

				if (valoreDefault != null && !valoreDefault.isEmpty()) {
					output.append(
							String.format(" %s '%s'", messages.getString("class.attributes.default"), valoreDefault));
				}

				// Vai a capo dopo ogni attributo
				output.append("; ");
			}
		} else {
			// Nessun attributo presente
			output.append(String.format("%n%s %s ", classe.getName(), messages.getString("class.attributes.empty")));
		}

		return output;

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

	private static StringBuilder simpleRelationships(IModelElement _base) {
		Collection lCollection = new ArrayList(); // <IRelationship>
		StringBuilder out = new StringBuilder();

		if (_base.getParent() != null && _base.getParent().getParent() != null) {
			System.out.println(_base.getParent().getParent().getName() != null ? _base.getParent().getParent().getName()
					: "nome non presente nel definire a chi appartiene");
		} else {
			System.out.println("Parent o Parent Model è null");
		}

		{
			// from base TO opposite
			Iterator lIter = _base.fromRelationshipIterator();
			while (lIter.hasNext()) {
				IRelationship lRelationship = (IRelationship) lIter.next();
				lCollection.add(lRelationship);
			}
		}
		{
			// FROM opposite to base
			Iterator lIter = _base.toRelationshipIterator();
			while (lIter.hasNext()) {
				IRelationship lRelationship = (IRelationship) lIter.next();
				if (_base.equals(lRelationship.getFrom())) {
					// ignore, it is SELF, already included in "from base to opposite"
				} else {
					lCollection.add(lRelationship);
				}
			}
		}

		IRelationship[] lRelationships = new IRelationship[lCollection.size()];
		lCollection.toArray(lRelationships);

		for (IRelationship relazione : lRelationships) {
			Optional<String> masterRelazione = Optional.ofNullable(relazione.getMasterView())
					.map(masterView -> masterView.getDiagramUIModel()).map(diagramUIModel -> diagramUIModel.getName());

			Optional<String> masterBase = Optional.ofNullable(_base.getMasterView())
					.map(masterView -> masterView.getDiagramUIModel()).map(diagramUIModel -> diagramUIModel.getName());

			// Confrontiamo i valori solo se entrambi sono presenti
			if (masterRelazione.isPresent() && masterBase.isPresent()
					&& masterRelazione.get().equals(masterBase.get())) {
				// Relazioni di generalizzazione ed altre di dipendenza
				if (!(relazione instanceof IAssociationEnd)) {
					out.append(String.format("%n- "));

					// Se è definito un nome per la relazione lo aggiunge
					String lName = relazione.getNickname();

					if (_base.equals(relazione.getFrom())) {
						if (_base.equals(relazione.getTo())) {
							out.append(String.format(" %s", messages.getString("class.association.reflective")));
						} else {
							if (relazione.getModelType().equals("Generalization")) {
								if (_base.getParent() != null && _base.getParent().getParent() != null) {
									out.append(String.format(" %s %s %s %s", _base.getNickname(),
											messages.getString("class.relationship.generalization"),
											relazione.getTo().getNickname(), _base.getParent().getParent().getName()));
								} else {
									out.append(String.format(" %s %s %s", _base.getNickname(),
											messages.getString("class.relationship.generalization"),
											relazione.getTo().getNickname()));
								}
							} else {
								out.append(String.format(" %s %s %s %s %s", _base.getNickname(),
										messages.getString("class.relationship.inrelation"), relazione.getModelType(),
										messages.getString("class.relationship.with"),
										relazione.getTo().getNickname()));
							}
						}
					} else {
						if (relazione.getModelType().equals("Generalization")) {
							if (_base.equals(relazione.getTo())) {
								out.append(String.format("%s %s %s", relazione.getTo().getName(),
										messages.getString("class.relationship.specialization"),
										relazione.getFrom().getNickname()));
							} else {
								out.append(String.format(" %s %s %s", relazione.getTo().getName(),
										messages.getString("class.relationship.generalization"), _base.getNickname()));
							}
						} else {
							if (_base.equals(relazione.getTo())) {
								out.append(String.format(" %s %s %s %s %s", _base.getNickname(),
										messages.getString("class.relationship.inrelation"), relazione.getModelType(),
										messages.getString("class.relationship.with"), relazione.getFrom().getName()));
							} else {
								out.append(String.format(" %s is in relation of %s with %s",
										relazione.getTo().getName(), relazione.getModelType(), _base.getNickname()));
							}
						}
					}

					if (lName != null) {
						out.append(String.format(" %s '%s'", messages.getString("class.relationship.name"), lName));
					}
				}
			}
		}

		return out;
	}

	private static StringBuilder fromEndRelationships(IModelElement _base) {
		Collection<IRelationship> lCollection = new ArrayList<IRelationship>(); // <IRelationship>
		StringBuilder out = new StringBuilder();
		{
			// from base TO opposite
			Iterator lIter = _base.fromRelationshipEndIterator();
			while (lIter.hasNext()) {
				IRelationshipEnd lRelationshipEnd = (IRelationshipEnd) lIter.next();
				IRelationship lRelationship = lRelationshipEnd.getEndRelationship();
				lCollection.add(lRelationship);
			}
		}
		{
			// FROM opposite to base
			Iterator lIter = _base.toRelationshipEndIterator();
			while (lIter.hasNext()) {
				IRelationshipEnd lRelationshipEnd = (IRelationshipEnd) lIter.next();
				IRelationship lRelationship = lRelationshipEnd.getEndRelationship();
				if (_base.equals(lRelationship.getFrom())) {
					// ignore, it is SELF, already included in "from base to opposite"
				} else {
					lCollection.add(lRelationship);
				}
			}
		}

		IRelationship[] lRelationships = new IRelationship[lCollection.size()];
		lCollection.toArray(lRelationships);

		for (IRelationship relazione : lRelationships) {
			System.out.println(relazione.getModelType());

			Optional.ofNullable(relazione.getMasterView()).map(masterView -> masterView.getDiagramUIModel())
			.map(diagramUIModel -> diagramUIModel.getName()).ifPresent(diagramName -> {
				if (diagramName.equals(Optional.ofNullable(_base.getMasterView())
						.map(baseMasterView -> baseMasterView.getDiagramUIModel())
						.map(baseDiagramUIModel -> baseDiagramUIModel.getName()).orElse(null))) {

					if (relazione.getModelType().equals("Association")) {
						out.append(handleAssociationRelationship(_base, relazione));
					} else {
						out.append(handleNonAssociationRelationship(relazione, _base));
					}
				}
			});
		}

		return out;

	}
	

	private static StringBuilder handleAssociationRelationship(IModelElement _base, IRelationship relazione) {
		StringBuilder out = new StringBuilder();
		IAssociation model = (IAssociation) relazione;

		IRelationshipEnd end = model.getToEnd();

		if (end instanceof IAssociationEnd) {
			IAssociationEnd association = (IAssociationEnd) end;
			/*
			 * out.append(String.format("%n%s %s %s%n",
			 * association.getModelElement().getName(),
			 * messages.getString("conjunction.end"),
			 * association.getOppositeEnd().getModelElement().getName()));
			 */

			if (association != null && association instanceof IAssociationEnd) {

				if (relazione.getMasterView().getDiagramUIModel().getName()
						.equals(_base.getMasterView().getDiagramUIModel().getName())) {

					// System.out.println(_base+" TTTTTTTTT "+ association.getAggregationKind());
					out.append("\n- ").append(
							association.getAggregationKind() == "none" && association.getAggregationKind() != null
							? messages.getString("class.association.aggregationkind")
									: messages.getString("empty"));

					out.append(DirectionRelationship(relazione, _base));

					out.append(" ").append(association.getModelElement().getName()).append(" ")
					.append(messages.getString("class.association.multiplicity")).append(" ")
					.append(association.getMultiplicity()).append(" ")
					.append(messages.getString("conjunction.end")).append(" ")
					.append(association.getOppositeEnd().getModelElement().getName()).append(" ")
					.append(messages.getString("class.association.multiplicity")).append(" ")
					.append(((IAssociationEnd) association.getOppositeEnd()).getMultiplicity());
				}
			}
		}

		return out;
	}

	private static StringBuilder handleNonAssociationRelationship(IRelationship relazione, IModelElement _base) {
		StringBuilder out = new StringBuilder();

		out.append(String.format("%n- Tipo della relazione %s", relazione.getModelType()));
		out.append(" Nome della relazione: ");
		String lName = relazione.getNickname();
		out.append((lName == null || lName.length() == 0) ? "Unnamed" : lName);

		out.append(DirectionRelationship(relazione, _base));

		return out;
	}

	private static StringBuilder DirectionRelationship(IRelationship relazione, IModelElement _base) {
		StringBuilder out = new StringBuilder();

		// out.append(" Relazione ");
		if (_base.equals(relazione.getFrom())) {
			if (_base.equals(relazione.getTo())) {
				out.append("Riflessiva");
			} else {
				// out.append("To "); // from base TO opposite model
				out.append(String.format(", From %s To %s, ", _base.getNickname(), relazione.getTo().getNickname()));

			}
		} else {

			if (_base.equals(relazione.getTo())) {
				out.append(", From " + relazione.getFrom().getName() + " TO " + _base.getNickname() + ",");
			} else {
				out.append(", From " + relazione.getTo().getName() + " TO " + _base.getNickname() + ",");
			}

		}
		return out;

	}

}
