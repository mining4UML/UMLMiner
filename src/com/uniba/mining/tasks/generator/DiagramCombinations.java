package com.uniba.mining.tasks.generator;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.SwingWorker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.uniba.mining.logging.LogExtractor;
import com.uniba.mining.tasks.exportdiag.DiagramInfo;
import com.vp.plugin.ApplicationManager;
import com.vp.plugin.DiagramManager;
import com.vp.plugin.ProjectManager;
import com.vp.plugin.diagram.IClassDiagramUIModel;
import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.diagram.shape.IClassUIModel;
import com.vp.plugin.diagram.shape.IPackageUIModel;
import com.vp.plugin.model.IAttribute;
import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IDataType;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IOperation;
import com.vp.plugin.model.IPackage;
import com.vp.plugin.model.IParameter;
import com.vp.plugin.model.IProject;
import com.vp.plugin.model.factory.IModelElementFactory;

import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

/**
 * 
 * Author: pasquale ardimento Last version: 07 February 2024
 */

public class DiagramCombinations {
	private String file;
	private IDataType typeVoid = null;
	private SwingWorker<?, ?> worker; // Riferimento al SwingWorker

	public DiagramCombinations(String file, SwingWorker<?, ?> worker) {
		this.file = file;
		this.worker = worker;
	}

	public boolean generateAllDiagramCombinations() {
		boolean generated = false;

		// Carica il file JSON
		String jsonString = loadJsonFromFile(file);
		JSONObject jsonDiagram = new JSONObject(jsonString);

		// Estrai le classi obbligatorie dal JSON
		List<ClassExt> mandatoryClasses = extractMandatoryClasses(jsonDiagram);
		System.out.println("Numero complessivo classi:" + mandatoryClasses.size());

		List<IClass> mand = new ArrayList<>(), opt = new ArrayList<>();

		for (ClassExt classe : mandatoryClasses) {
			if (worker.isCancelled()) {  
				System.out.println("Process interrupted, stopping class extraction...");
				return false;
			}
			if (classe.getOptional()) {
				opt.add(classe.getClasse());
			} else {
				mand.add(classe.getClasse());
			}
		}

		System.out.println("Numero mandatory:" + mand.size());
		System.out.println("Numero optional:" + opt.size());

		// Genera tutte le combinazioni di diagrammi
		List<List<IClass>> diagramCombinations = generateDiagramCombinations(mand, opt);

		for (List<IClass> combination : diagramCombinations) {
			if (worker.isCancelled()) {
				System.out.println("Process interrupted before processing combinations.");
				return false;
			}
			for (IClass clazz : combination) {
				System.out.println(clazz.getName());
			}
			System.out.println("----"); // Separatore tra combinazioni
		}

		// Generazione dei diagrammi
		for (int i = 0; i < diagramCombinations.size(); i++) {
			if (worker.isCancelled()) {
				System.out.println("Process interrupted before generating diagrams.");
				return false;
			}

			List<IClass> currentCombination = diagramCombinations.get(i);
			System.out.println(currentCombination.size());

			if (currentCombination.size() > 0) {
				System.out.println("Numero di classi:" + currentCombination.size());
			}

			generateClassDiagramFromClasses(jsonDiagram, currentCombination, i);
		}

		return true;
	}




	private List<ClassExt> extractMandatoryClasses(JSONObject jsonDiagram) {
		List<ClassExt> mandatoryClasses = new ArrayList<>();
		JSONArray jsonClasses = jsonDiagram.getJSONArray("classi");

		for (int i = 0; i < jsonClasses.length(); i++) {
			JSONObject jsonClass = jsonClasses.getJSONObject(i);
			ClassExt newClass = buildClass(jsonDiagram, jsonClass, "optional");

			if (newClass != null) {
				System.out.println(newClass.getClasse().getName() + " " + newClass.getClasse().attributeCount());
				mandatoryClasses.add(newClass);
			}
		}

		for (ClassExt classe : mandatoryClasses) {
			System.out.println(classe.getClasse().getName() + " " + classe.getClasse().attributeCount());
		}
		return mandatoryClasses;
	}

	private ClassExt buildClass(JSONObject jsonDiagram, JSONObject jsonClass, String value) {
		// JSONArray jsonClasses = jsonDiagram.getJSONArray("classi");

		// for (int i = 0; i < jsonClasses.length(); i++) {
		// JSONObject jsonClass = jsonClasses.getJSONObject(i);
		IClass newClass = createClassFromJson(jsonClass);
		// IClass newClass = IModelElementFactory.instance().createClass();
		// System.out.println(newClass.getClass());

		System.out.println("Mandatory: " + jsonClass.get("nome"));

		System.out.println(newClass.getName() + " " + newClass.attributeCount());

		try {
			ClassExt newClassExt = new ClassExt(jsonClass.getBoolean(value), newClass);
			System.out.println(newClassExt.getClasse().getName() + " " + newClassExt.getClasse().attributeCount());
			return newClassExt;
		} catch (JSONException ex) {
			return null;
		}

	}

	private boolean isClassDiagramNameExists(String diagramName) {
		// Ottieni l'istanza del gestore dei progetti

		IProject project = ApplicationManager.instance().getProjectManager().getProject();

		// Ottieni il progetto corrente
		// IProject project = projectManager.getProject();

		// Ottieni tutti i diagrammi nel progetto
		IDiagramUIModel[] diagrams = project.toDiagramArray();

		// Itera attraverso i diagrammi per verificare se il nome esiste già
		for (IDiagramUIModel diagram : diagrams) {
			if (diagram.getType().equals("ClassDiagram")) {
				String existingDiagramName = diagram.getName();

				// Confronta il nome del diagramma esistente con quello fornito
				if (existingDiagramName != null && existingDiagramName.equals(diagramName)) {
					// Il nome del diagramma delle classi già esiste
					return true;
				}
			}
		}

		// Il nome del diagramma delle classi non esiste
		return false;
	}

	private void generateClassDiagramFromClasses(JSONObject jsonDiagram, List<IClass> classes, int index) {
		if (worker.isCancelled()) {
			System.out.println("Process interrupted before generating a class diagram.");
			return;
		}	
		// Extract JSON content
		String jsonString = loadJsonFromFile(file);
		jsonDiagram = new JSONObject(jsonString);

		// Estrai il nome del package dal JSON (se presente)
		String packageName = jsonDiagram.optString("package", "");


		// provo a creare un nuovo progetto - 06 febbario 2025
		ProjectManager projectManager = ApplicationManager.instance().getProjectManager();
		projectManager.newProject();

		// create blank class diagram
		DiagramManager diagramManager = ApplicationManager.instance().getDiagramManager();
		IClassDiagramUIModel diagram = (IClassDiagramUIModel) diagramManager
				.createDiagram(DiagramManager.DIAGRAM_TYPE_CLASS_DIAGRAM);

		// create base package model
		IPackage basePackage = IModelElementFactory.instance().createPackage();

		// create base package model
		IPackage outerPackage = IModelElementFactory.instance().createPackage();
		// ottengo il riferimento all'elemento package
		IModelElement outerPackageElement = (IModelElement) outerPackage;
		// assegno il nome all'elemento di modello

		//		String diagramPackageName = packageName.lastIndexOf('.') != -1
		//				? packageName.substring(0, packageName.lastIndexOf('.')) + index
		//						: packageName + index;

		String diagramPackageName = "default";

		// non va bene se la sintassi del package non presenta il punto
		// String diagramPackageName = packageName.substring(0,
		// packageName.lastIndexOf('.'))+"pasquale"+index;
		outerPackageElement.setName(diagramPackageName);

		//		String basePackageName = packageName.lastIndexOf('.') != -1
		//				? packageName.substring(packageName.lastIndexOf('.')) + index
		//						: packageName + index;

		String basePackageName = "default";

		// String basePackageName =
		// packageName.substring(packageName.lastIndexOf('.'))+index;

		// definisco il nome del package
		basePackage.setName(basePackageName != "" ? basePackageName : "noname");

		// create base package shape
		IPackageUIModel basePackageShape = (IPackageUIModel) diagramManager.createDiagramElement(diagram, basePackage);

		// ottengo il riferimento all'elemento package
		IModelElement packageElement = (IModelElement) basePackage;
		// assegno il nome all'elemento di modello
		packageElement.setName(packageName);

		// definisco il package di riferimento del diagramma
		diagram.setDefaultPackage(outerPackageElement);

		basePackageShape.setBounds(94, 79, 717, 516);
		// set to automatic calculate the initial caption position
		basePackageShape.setRequestResetCaption(true);

		List<IClass> newClasses = new ArrayList<>();
		Map<IClass, IClass> classMapping = new HashMap<>(); // Per tenere traccia delle classi copiate

		for (IClass currentClass : classes) {
			System.out.println(currentClass.getName());

			// Creazione della nuova classe
			IClass newClass = copyClass(currentClass);
			newClasses.add(newClass);
			classMapping.put(currentClass, newClass); // Salvo la corrispondenza

			// Aggiungo la nuova classe al package
			packageElement.addChild(newClass);

			// Crea la forma della classe nel diagramma
			IClassUIModel classShape = (IClassUIModel) diagramManager.createDiagramElement(diagram, newClass);
			classShape.setBounds(100, 200, 100, 70);
			basePackageShape.addChild(classShape);
			classShape.setRequestResetCaption(true);
			classShape.fitSize();

			LogExtractor.addDiagramUIModel(newClass, diagram);
		}

		// Aggiungere le relazioni alle nuove classi
		for (IClass originalClass : classes) {
			IClass newCurrentClass = classMapping.get(originalClass); // Trova la copia

			if (newCurrentClass != null) {
				// Usa le nuove classi invece delle vecchie
				RelationshipGenerator.addRelationsFromJson(diagramManager, diagram, basePackageShape, newClasses, jsonDiagram, newCurrentClass);
			}
		}
		// dovrebbe servire a ridefinire il layout in base all'effettivo contenuto
		basePackageShape.fitSize();

		boolean end = true;
		String diagName = jsonDiagram.optString("diagramName") + index;
		do {
			if (!isClassDiagramNameExists(diagName)) {
				diagram.setName(diagName);
				end = false;
			} else {
				++index;
				diagName = jsonDiagram.optString("diagramName") + index;
			}
		} while (end);

		// Show up the diagram
		diagramManager.openDiagram(diagram);
		diagramManager.layout(diagram, DiagramManager.LAYOUT_AUTO);

		File storageDir = ApplicationManager.instance().getWorkspaceLocation();

		File projectFile = new File(storageDir,"Student-" + index + ".vpp");
		// provo a salvare questo progetto

		// Esporta il diagramma in formato XML
		Document xmlDiagram = DiagramInfo.exportAsXML(diagram);

		// Salva il documento XML usando il percorso del file e il nome del diagramma
		saveXMLDocument(xmlDiagram, projectFile.getParent(), "Student-" + index);

		if (xmlDiagram != null) {
			System.out.println("Diagramma esportato con successo in formato XML.");
		} else {
			System.err.println("Errore nell'esportazione del diagramma XML.");
		}

		projectManager.saveProjectAs(projectFile);
	}


	private static void saveXMLDocument(Document xmlDiagram, String projectPath, String diagramName) {
		try {
			// Percorso del file XML nella stessa directory del progetto
			File xmlFile = Paths.get(projectPath, diagramName + ".xml").toFile();

			// Crea un oggetto OutputFormat per formattare l'XML in modo leggibile
			OutputFormat format = OutputFormat.createPrettyPrint();

			// Crea un oggetto XMLWriter per scrivere il documento XML su file
			XMLWriter writer = new XMLWriter(new FileOutputStream(xmlFile), format);

			// Scrive il documento XML su file
			writer.write(xmlDiagram);

			// Chiude lo scrittore
			writer.close();

			System.out.println("Diagramma XML salvato in: " + xmlFile.getAbsolutePath());
		} catch (Exception e) {
			System.err.println("Errore nel salvataggio dell'XML: " + e.getMessage());
		}
	}


	private IClass copyClass(IClass currentClass) {
		// Crea una nuova classe
		IClass newClass = IModelElementFactory.instance().createClass();

		// Copia il nome
		newClass.setName(currentClass.getName());

		// Copia gli attributi
		for (IAttribute attr : currentClass.toAttributeArray()) {
			IAttribute newAttr = IModelElementFactory.instance().createAttribute();
			newAttr.setName(attr.getName());
			newAttr.setType((String) attr.getType());
			newClass.addAttribute(newAttr);
		}

		// Copia le operazioni/metodi
		for (IOperation op : currentClass.toOperationArray()) {
			IOperation newOp = IModelElementFactory.instance().createOperation();
			newOp.setName(op.getName());
			newOp.setReturnType((String) op.getReturnType());

			for (IParameter param : op.toParameterArray()) {
				IParameter newParam = IModelElementFactory.instance().createParameter();
				newParam.setName(param.getName());
				newParam.setType((String) param.getType());
				newOp.addParameter(newParam);
			}

			newClass.addOperation(newOp);
		}

		return newClass;
	}


	private IClass createClassFromJson(JSONObject jsonClass) {
		IClass newClass = IModelElementFactory.instance().createClass();
		// String id = newClass.getId();

		// Costruisci il nome completo della classe includendo il package
		String name = jsonClass.getString("nome").isEmpty() ? "nomeClasse" : jsonClass.getString("nome");
		newClass.setName(name);

		System.out.println("\n*****\n*****\n");

		if (jsonClass.has("attributi")) {
			Object attributiObj = jsonClass.get("attributi");
			if (attributiObj instanceof JSONArray) {
				JSONArray jsonAttributes = (JSONArray) attributiObj;
				if (!jsonAttributes.isEmpty()) {
					for (int j = 0; j < jsonAttributes.length(); j++) {
						JSONObject jsonAttribute = jsonAttributes.optJSONObject(j);
						if (jsonAttribute != null) {
							IAttribute attribute = IModelElementFactory.instance().createAttribute();//newClass.createAttribute();
							attribute.setName(jsonAttribute.getString("nome"));
							attribute.setType(jsonAttribute.getString("tipo"));
							attribute.setVisibility(IAttribute.VISIBILITY_PRIVATE);
							// aggiunge come figlio
							newClass.addAttribute(attribute);
						}
					}
				}
			}
		}

		// Extract operations from JSON
		if (jsonClass.has("operazioni")) {
			JSONArray jsonOperations = jsonClass.getJSONArray("operazioni");
			if (!jsonOperations.isEmpty()) {
				jsonOperations = jsonClass.getJSONArray("operazioni");
				for (int k = 0; k < jsonOperations.length(); k++) {
					JSONObject jsonOperation = jsonOperations.getJSONObject(k);
					IOperation operation = IModelElementFactory.instance().createOperation();
					//IOperation operation = newClass.createOperation();
					operation.setName(jsonOperation.getString("nome"));
					operation.setVisibility(IOperation.VISIBILITY_PUBLIC);
					operation.setReturnType(jsonOperation.getString("tipoRitorno"));

					// Extract parameters from JSON
					JSONArray jsonParameters = jsonOperation.getJSONArray("parametri");
					for (int l = 0; l < jsonParameters.length(); l++) {
						JSONObject jsonParameter = jsonParameters.getJSONObject(l);
						IParameter parameter = IModelElementFactory.instance().createParameter();
						//IParameter parameter = operation.createParameter();
						parameter.setName(jsonParameter.getString("nome"));
						parameter.setType(jsonParameter.getString("tipo"));
						operation.addParameter(parameter);
					}

					// aggiunge come figlio
					newClass.addOperation(operation);
				}
			}
		}

		// Extract constructors from JSON
		JSONArray jsonConstructors = jsonClass.optJSONArray("costruttori");
		if (jsonConstructors != null) {
			for (int m = 0; m < jsonConstructors.length(); m++) {
				JSONObject jsonConstructor = jsonConstructors.getJSONObject(m);
				// IOperation constructor = IModelElementFactory.instance().createOperation();
				IOperation constructor = newClass.createOperation();
				constructor.setName(jsonClass.getString("nome"));
				constructor.setVisibility(IOperation.VISIBILITY_PUBLIC);
				constructor.setReturnType(typeVoid);

				// Extract parameters from JSON
				JSONArray jsonParametersConstructor = jsonConstructor.getJSONArray("parametri");
				for (int n = 0; n < jsonParametersConstructor.length(); n++) {
					JSONObject jsonParameter = jsonParametersConstructor.getJSONObject(n);
					// IParameter parameter = IModelElementFactory.instance().createParameter();
					IParameter parameter = constructor.createParameter();
					parameter.setName(jsonParameter.getString("nome"));
					parameter.setType(jsonParameter.getString("tipo"));
					// aggiunge come figlio
					constructor.addParameter(parameter);
				}

				// aggiunge come figlio
				newClass.addOperation(constructor);
			}
		}

		return newClass;
	}

	private List<List<IClass>> generateDiagramCombinations(List<IClass> mandatoryClasses,
			List<IClass> optionalClasses) {
		List<List<IClass>> combinations = new ArrayList<>();
		int numOptionalClasses = optionalClasses.size();

		for (IClass mandClass : mandatoryClasses) {
			System.out.println(mandClass.getName());
		}

		// Utilizza un approccio binario per generare tutte le combinazioni possibili
		// per le classi opzionali
		for (int i = 0; i < (1 << numOptionalClasses); i++) {
			List<IClass> currentCombination = new ArrayList<>(mandatoryClasses);

			for (int j = 0; j < numOptionalClasses; j++) {
				if ((i & (1 << j)) > 0) {
					currentCombination.add(optionalClasses.get(j));
				}
			}

			combinations.add(currentCombination);
		}

		System.out.println(combinations.size());
		return combinations;
	}

	private String loadJsonFromFile(String filePath) {
		try {
			// Leggi il contenuto del file come stringa JSON
			byte[] jsonData = Files.readAllBytes(Paths.get(filePath));
			return new String(jsonData);
		} catch (Exception e) {
			e.printStackTrace(); // Aggiungi la gestione eccezioni appropriata per la tua applicazione
			return "{}"; // Restituisci un oggetto JSON vuoto in caso di errore
		}
	}

}
