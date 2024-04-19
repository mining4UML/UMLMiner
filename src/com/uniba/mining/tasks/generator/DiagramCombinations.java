package com.uniba.mining.tasks.generator;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.DiagramManager;
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

import netscape.javascript.JSException;

/**
 * 
 * Author: pasquale ardimento Last version: 07 February 2024
 */

public class DiagramCombinations {

	private String file;
	// private IDataType typeInt = null;
	// private IDataType typeString = null;
	// private IDataType typeBoolean = null;
	private IDataType typeVoid = null;

	public DiagramCombinations(String file) {
		this.file = file;

	}

	public boolean generateAllDiagramCombinations() {
		boolean generated = false;

		// Carica il tuo file JSON
		String jsonString = loadJsonFromFile(file);
		JSONObject jsonDiagram = new JSONObject(jsonString);

		// Estrai le classi obbligatorie dal JSON
		List<ClassExt> mandatoryClasses = extractMandatoryClasses(jsonDiagram);
		System.out.println("Numero complessivo classi:" + mandatoryClasses.size());

		for (ClassExt classe : mandatoryClasses) {
			System.out.print(classe.getClasse().getName() + "--");
		}

		List<IClass> mand = new ArrayList<IClass>(), opt = new ArrayList<IClass>();

		for (ClassExt classe : mandatoryClasses) {
			if (classe.getOptional()) {
				opt.add(classe.getClasse());
			} else
				mand.add(classe.getClasse());

		}
		System.out.println("Numero mandatory:" + mand.size());
		System.out.println("Numero optional:" + opt.size());

		// Estrai le classi obbligatorie dal JSON
		// List<IClass> optionalClasses = extractOptionalClasses(jsonDiagram);
		// System.out.println(optionalClasses.size());

		// Genera tutte le combinazioni di diagrammi
		List<List<IClass>> diagramCombinations = generateDiagramCombinations(mand, opt);

		for (List<IClass> combination : diagramCombinations) {
			for (IClass clazz : combination) {
				System.out.println(clazz.getName());
			}
			System.out.println("----"); // Separatore tra combinazioni
		}

		// Stampa le combinazioni
		for (int i = 0; i < diagramCombinations.size(); i++) {
			List<IClass> currentCombination = diagramCombinations.get(i);
			System.out.println(currentCombination.size());

			// Chiamare il metodo per generare il diagramma utilizzando la combinazione
			// corrente
			if (currentCombination.size() > 0)
				System.out.println("Numero di classi:" + currentCombination.size());
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

	// private List<ClassExt> extractOptionalClasses(JSONObject jsonDiagram) {
	// List<ClassExt> optionalClasses = new ArrayList<>();
	// JSONArray jsonClasses = jsonDiagram.getJSONArray("classi");
	//
	// for (int i = 0; i < jsonClasses.length(); i++) {
	// JSONObject jsonClass = jsonClasses.getJSONObject(i);
	// IClass newClass = buildClass(jsonDiagram, jsonClass, "optional");
	// // JSONObject jsonClass = jsonClasses.getJSONObject(i);
	// //
	// // if ( ! jsonClass.getBoolean("optional") ) {
	// // System.out.println("Optional: "+jsonClass.get("nome"));
	// // IClass newClass = createClassFromJson(jsonClass);
	// //
	// // System.out.println("Mandatory: "+jsonClass.get("nome"));
	// // //for (IClass currentClass : classes) {
	// //
	// // // Aggiungi attributi
	// // System.out.println(newClass.getName());
	// // JSONArray jsonAttributes = getJsonClassByName(jsonDiagram,
	// newClass.getName()).optJSONArray("attributi");
	// // if (jsonAttributes != null) {
	// // for (int j = 0; j < jsonAttributes.length(); j++) {
	// // JSONObject jsonAttribute = jsonAttributes.getJSONObject(j);
	// // IAttribute attribute = IModelElementFactory.instance().createAttribute();
	// // attribute.setName(jsonAttribute.getString("nome"));
	// // attribute.setType(jsonAttribute.getString("tipo"));
	// // attribute.setVisibility(IAttribute.VISIBILITY_PRIVATE);
	// // newClass.addAttribute(attribute);
	// // }
	// // }
	// //
	// // // Aggiungi operazioni
	// // JSONArray jsonOperations = getJsonClassByName(jsonDiagram,
	// newClass.getName()).optJSONArray("operazioni");
	// // if (jsonOperations != null) {
	// // for (int k = 0; k < jsonOperations.length(); k++) {
	// // JSONObject jsonOperation = jsonOperations.getJSONObject(k);
	// // IOperation operation = IModelElementFactory.instance().createOperation();
	// // operation.setName(jsonOperation.getString("nome"));
	// // operation.setVisibility(IOperation.VISIBILITY_PUBLIC);
	// // operation.setReturnType(jsonOperation.getString("tipoRitorno"));
	// //
	// // // Aggiungi parametri
	// // JSONArray jsonParameters = jsonOperation.getJSONArray("parametri");
	// // for (int l = 0; l < jsonParameters.length(); l++) {
	// // JSONObject jsonParameter = jsonParameters.getJSONObject(l);
	// // IParameter parameter = IModelElementFactory.instance().createParameter();
	// // parameter.setName(jsonParameter.getString("nome"));
	// // parameter.setType(jsonParameter.getString("tipo"));
	// // operation.addParameter(parameter);
	// // }
	// //
	// // newClass.addOperation(operation);
	// // }
	// // }
	// //
	// // // Aggiungi costruttori
	// // JSONArray jsonConstructors = getJsonClassByName(jsonDiagram,
	// newClass.getName())
	// // .optJSONArray("costruttori");
	// // if (jsonConstructors != null) {
	// // for (int m = 0; m < jsonConstructors.length(); m++) {
	// // JSONObject jsonConstructor = jsonConstructors.getJSONObject(m);
	// // IOperation constructor =
	// IModelElementFactory.instance().createOperation();
	// // constructor.setName(newClass.getName());
	// // constructor.setVisibility(IOperation.VISIBILITY_PUBLIC);
	// // constructor.setReturnType(typeVoid);
	// //
	// // // Aggiungi parametri
	// // JSONArray jsonParametersConstructor =
	// jsonConstructor.getJSONArray("parametri");
	// // for (int n = 0; n < jsonParametersConstructor.length(); n++) {
	// // JSONObject jsonParameter = jsonParametersConstructor.getJSONObject(n);
	// // IParameter parameter = IModelElementFactory.instance().createParameter();
	// // parameter.setName(jsonParameter.getString("nome"));
	// // parameter.setType(jsonParameter.getString("tipo"));
	// // constructor.addParameter(parameter);
	// // }
	// //
	// // newClass.addOperation(constructor);
	// // }
	// // }
	//
	//
	// if (newClass!= null)
	// optionalClasses.add(newClass);
	// }
	//
	// return optionalClasses;
	// }

	private ClassExt buildClass(JSONObject jsonDiagram, JSONObject jsonClass, String value) {
		// JSONArray jsonClasses = jsonDiagram.getJSONArray("classi");

		// for (int i = 0; i < jsonClasses.length(); i++) {
		// JSONObject jsonClass = jsonClasses.getJSONObject(i);
		IClass newClass = createClassFromJson(jsonClass);
		// IClass newClass = IModelElementFactory.instance().createClass();
		// System.out.println(newClass.getClass());

		System.out.println("Mandatory: " + jsonClass.get("nome"));
		// for (IClass currentClass : classes) {

		// Aggiungi attributi
		// System.out.println(newClass.getName());
		// JSONArray jsonAttributes = jsonClass.optJSONArray("attributi");
		// System.out.println("Numero di attributi:"+jsonAttributes.length());
		// if (jsonAttributes != null) {
		// for (int j = 0; j < jsonAttributes.length(); j++) {
		// JSONObject jsonAttribute = jsonAttributes.getJSONObject(j);
		// //IAttribute attribute = IModelElementFactory.instance().createAttribute();
		// // lo creo per questa classe e non per l'intero progetto
		// IAttribute attribute = newClass.createAttribute();
		// attribute.setName(jsonAttribute.getString("nome"));
		// attribute.setType(jsonAttribute.getString("tipo"));
		// attribute.setVisibility(IAttribute.VISIBILITY_PRIVATE);
		// //newClass.addAttribute(attribute);
		// System.out.println("*** " + attribute.getName());
		// }
		// }
		//
		// System.out.println(newClass.getName()+" "+newClass.attributeCount());
		//
		//
		//
		// // Aggiungi operazioni
		// JSONArray jsonOperations = getJsonClassByName(jsonDiagram,
		// newClass.getName()).optJSONArray("operazioni");
		// if (jsonOperations != null) {
		// for (int k = 0; k < jsonOperations.length(); k++) {
		// JSONObject jsonOperation = jsonOperations.getJSONObject(k);
		// //IOperation operation = IModelElementFactory.instance().createOperation();
		// IOperation operation = newClass.createOperation();
		// operation.setName(jsonOperation.getString("nome"));
		// operation.setVisibility(IOperation.VISIBILITY_PUBLIC);
		// operation.setReturnType(jsonOperation.getString("tipoRitorno"));
		//
		// // Aggiungi parametri
		// JSONArray jsonParameters = jsonOperation.getJSONArray("parametri");
		// for (int l = 0; l < jsonParameters.length(); l++) {
		// JSONObject jsonParameter = jsonParameters.getJSONObject(l);
		// //IParameter parameter = IModelElementFactory.instance().createParameter();
		// IParameter parameter = operation.createParameter();
		// parameter.setName(jsonParameter.getString("nome"));
		// parameter.setType(jsonParameter.getString("tipo"));
		// operation.addParameter(parameter);
		// }
		//
		// //newClass.addOperation(operation);
		// }
		// }
		//
		// // Aggiungi costruttori
		// JSONArray jsonConstructors = getJsonClassByName(jsonDiagram,
		// newClass.getName())
		// .optJSONArray("costruttori");
		// if (jsonConstructors != null) {
		// for (int m = 0; m < jsonConstructors.length(); m++) {
		// JSONObject jsonConstructor = jsonConstructors.getJSONObject(m);
		// //IOperation constructor = IModelElementFactory.instance().createOperation();
		// IOperation constructor =newClass.createOperation();
		// constructor.setName(newClass.getName());
		// constructor.setVisibility(IOperation.VISIBILITY_PUBLIC);
		// constructor.setReturnType(typeVoid);
		//
		// // Aggiungi parametri
		// JSONArray jsonParametersConstructor =
		// jsonConstructor.getJSONArray("parametri");
		// for (int n = 0; n < jsonParametersConstructor.length(); n++) {
		// JSONObject jsonParameter = jsonParametersConstructor.getJSONObject(n);
		// //IParameter parameter = IModelElementFactory.instance().createParameter();
		// IParameter parameter = constructor.createParameter();
		// parameter.setName(jsonParameter.getString("nome"));
		// parameter.setType(jsonParameter.getString("tipo"));
		// constructor.addParameter(parameter);
		// }
		//
		// //newClass.addOperation(constructor);
		// }
		// }

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
		// Extract JSON content
		String jsonString = loadJsonFromFile(file);
		jsonDiagram = new JSONObject(jsonString);

		// Estrai il nome del package dal JSON (se presente)
		String packageName = jsonDiagram.optString("package", "");

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

		String diagramPackageName = packageName.lastIndexOf('.') != -1
				? packageName.substring(0, packageName.lastIndexOf('.')) + index
				: packageName + index;

		// non va bene se la sintassi del package non presenta il punto
		// String diagramPackageName = packageName.substring(0,
		// packageName.lastIndexOf('.'))+"pasquale"+index;
		outerPackageElement.setName(diagramPackageName);

		String basePackageName = packageName.lastIndexOf('.') != -1
				? packageName.substring(packageName.lastIndexOf('.')) + index
				: packageName + index;

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

		for (IClass currentClass : classes) {
			System.out.println(currentClass.getName());

			// metto in relazione di parent-child gli elementi
			packageElement.addChild(currentClass);

			// Crea la forma della classe nel diagramma
			IClassUIModel classShape = (IClassUIModel) diagramManager.createDiagramElement(diagram, currentClass);
			classShape.setBounds(100, 200, 100, 70);
			basePackageShape.addChild(classShape);
			classShape.setRequestResetCaption(true);
			classShape.fitSize();
		}

		// Cicla attraverso le classi per aggiungere relazioni definite nel JSON
		for (IClass currentClass : classes) {
			// Esegui il metodo aggiornato per aggiungere le relazioni
			RelationshipGenerator.addRelationsFromJson(diagramManager, diagram, basePackageShape, classes, jsonDiagram,
					currentClass);
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
	}

	// private JSONObject getJsonClassByName(JSONObject jsonDiagram, String
	// className) {
	// JSONArray jsonClasses = jsonDiagram.getJSONArray("classi");
	// for (int i = 0; i < jsonClasses.length(); i++) {
	// JSONObject jsonClass = jsonClasses.getJSONObject(i);
	// if (jsonClass.getString("nome").equals(className)) {
	// return jsonClass;
	// }
	// }
	// return new JSONObject(); // Restituisce un oggetto vuoto se la classe non è
	// trovata
	// }

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
							IAttribute attribute = newClass.createAttribute();
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
					// IOperation operation = IModelElementFactory.instance().createOperation();
					IOperation operation = newClass.createOperation();
					operation.setName(jsonOperation.getString("nome"));
					operation.setVisibility(IOperation.VISIBILITY_PUBLIC);
					operation.setReturnType(jsonOperation.getString("tipoRitorno"));

					// Extract parameters from JSON
					JSONArray jsonParameters = jsonOperation.getJSONArray("parametri");
					for (int l = 0; l < jsonParameters.length(); l++) {
						JSONObject jsonParameter = jsonParameters.getJSONObject(l);
						// IParameter parameter = IModelElementFactory.instance().createParameter();
						IParameter parameter = operation.createParameter();
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

	// private void loadDataType() {
	// IProject project =
	// ApplicationManager.instance().getProjectManager().getProject();
	// IModelElement[] datatypes =
	// project.toAllLevelModelElementArray(IModelElementFactory.MODEL_TYPE_DATA_TYPE);
	// if (datatypes != null && datatypes.length > 0) {
	// for (int i = 0; i < datatypes.length; i++) {
	// if ("int".equals(datatypes[i].getName())) {
	// typeInt = (IDataType) datatypes[i];
	// }
	// if ("boolean".equals(datatypes[i].getName())) {
	// typeBoolean = (IDataType) datatypes[i];
	// }
	// if ("string".equals(datatypes[i].getName())) {
	// typeString = (IDataType) datatypes[i];
	// }
	// if ("void".equals(datatypes[i].getName())) {
	// typeVoid = (IDataType) datatypes[i];
	// }
	// }
	// }
	// }

}
