package com.uniba.mining.actions;
// DA CANCELLARE

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.uniba.mining.tasks.generator.RelationshipGenerator;
import com.vp.plugin.ApplicationManager;
import com.vp.plugin.DiagramManager;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;
import com.vp.plugin.diagram.IClassDiagramUIModel;
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

import alice.tuprologx.pj.model.List;

// Aggiungi un'enumerazione per i tipi di relazione
//enum RelationshipType {
//	GENERALIZATION, ASSOCIATION, AGGREGATION, REALIZATION
//}

public class ClassDiagramActionControlBackup implements VPActionController {

	private IDataType typeInt = null;
	private IDataType typeString = null;
	private IDataType typeBoolean = null;
	private IDataType typeVoid = null;

	@Override
	public void performAction(VPAction arg0) {
		// Chiamare il metodo per generare un diagramma
		generateClassDiagramFromJson();
	}

	private void generateClassDiagramFromJson() {
		loadDataType();


		// Extract JSON content
		String jsonString = loadJsonFromFile("/Users/pasqualeardimento/Documents/GitHub/UMLMiner/assets/ddd.json");
		JSONObject jsonDiagram = new JSONObject(jsonString);

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
		String diagramPackageName = packageName.substring(0, packageName.lastIndexOf('.'));
		outerPackageElement.setName(diagramPackageName);


		String basePackageName = packageName.substring(packageName.lastIndexOf('.')+1);

		// definisco il nome del package
		basePackage.setName(basePackageName);

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



		// Extract classes from JSON
		JSONArray jsonClasses = jsonDiagram.getJSONArray("classi");
		//IClass[] classes = new IClass[jsonClasses.length()];
		ArrayList<IClass> classes = new ArrayList<IClass>();

		for (int i = 0; i < jsonClasses.length(); i++) {

			JSONObject jsonClass = jsonClasses.getJSONObject(i);

			// Aggiungi questo controllo per verificare se la classe è opzionale
			if (jsonClass.optBoolean("optional", false) && !jsonClass.optBoolean("selected", false)) {
				// La classe è opzionale e non è selezionata, salta questa iterazione
				continue;
			}

			IClass newClass = IModelElementFactory.instance().createClass();
			String id = newClass.getId();

			// Costruisci il nome completo della classe includendo il package
			// String fullClassName = packageName.isEmpty() ? jsonClass.getString("nome") : packageName + "." + jsonClass.getString("nome");

			String name = jsonClass.getString("nome").isEmpty() ? "nomeClass": jsonClass.getString("nome") ;
			//newClass.setName(jsonClass.getString("nome"));
			newClass.setName(name);


			// Extract attributes from JSON
			JSONArray jsonAttributes = jsonClass.getJSONArray("attributi");
			for (int j = 0; j < jsonAttributes.length(); j++) {
				JSONObject jsonAttribute = jsonAttributes.getJSONObject(j);
				IAttribute attribute = IModelElementFactory.instance().createAttribute();
				attribute.setName(jsonAttribute.getString("nome"));
				attribute.setType(jsonAttribute.getString("tipo")); 
				attribute.setVisibility(IAttribute.VISIBILITY_PRIVATE);
				newClass.addAttribute(attribute);
			}

			// Extract operations from JSON
			if (jsonClass.has("operazioni")) {
				JSONArray jsonOperations = jsonClass.getJSONArray("operazioni");
				if (!jsonOperations.isEmpty()) {
					jsonOperations = jsonClass.getJSONArray("operazioni");
					for (int k = 0; k < jsonOperations.length(); k++) {
						JSONObject jsonOperation = jsonOperations.getJSONObject(k);
						IOperation operation = IModelElementFactory.instance().createOperation();
						operation.setName(jsonOperation.getString("nome"));
						operation.setVisibility(IOperation.VISIBILITY_PUBLIC);
						operation.setReturnType(jsonOperation.getString("tipoRitorno")); // Assuming all operations return boolean, modify accordingly

						// Extract parameters from JSON
						JSONArray jsonParameters = jsonOperation.getJSONArray("parametri");
						for (int l = 0; l < jsonParameters.length(); l++) {
							JSONObject jsonParameter = jsonParameters.getJSONObject(l);
							IParameter parameter = IModelElementFactory.instance().createParameter();
							parameter.setName(jsonParameter.getString("nome"));
							parameter.setType(jsonParameter.getString("tipo")); 
							operation.addParameter(parameter);
						}

						newClass.addOperation(operation);
					}
				}
			}

			// Extract constructors from JSON
			JSONArray jsonConstructors = jsonClass.getJSONArray("costruttori");
			for (int m = 0; m < jsonConstructors.length(); m++) {
				JSONObject jsonConstructor = jsonConstructors.getJSONObject(m);
				IOperation constructor = IModelElementFactory.instance().createOperation();
				constructor.setName(jsonClass.getString("nome"));
				constructor.setVisibility(IOperation.VISIBILITY_PUBLIC);
				constructor.setReturnType(typeVoid); // Assuming all constructors return void, modify accordingly

				// Extract parameters from JSON
				JSONArray jsonParametersConstructor = jsonConstructor.getJSONArray("parametri");
				for (int n = 0; n < jsonParametersConstructor.length(); n++) {
					JSONObject jsonParameter = jsonParametersConstructor.getJSONObject(n);
					IParameter parameter = IModelElementFactory.instance().createParameter();
					parameter.setName(jsonParameter.getString("nome"));
					parameter.setType(jsonParameter.getString("tipo")); // Assuming all parameters are of type string, modify accordingly
					constructor.addParameter(parameter);
				}

				newClass.addOperation(constructor);
			}

			// metto in relazione di parent-child gli elementi
			packageElement.addChild(newClass);

			// Crea la forma della classe nel diagramma
			IClassUIModel classShape = (IClassUIModel) diagramManager.createDiagramElement(diagram, newClass);
			classShape.setBounds(100 + i * 120, 200, 100, 70);
			basePackageShape.addChild(classShape);
			classShape.setRequestResetCaption(true);
			classShape.fitSize();

			classes.add(newClass);
		}


		// Aggiungi relazioni definite nel JSON utilizzando RelationshipGenerator
		// Cicla attraverso le classi per aggiungere relazioni definite nel JSON
		for (IClass currentClass : classes) {
			// Esegui il metodo aggiornato per aggiungere le relazioni
			RelationshipGenerator.addRelationsFromJson(diagramManager, diagram, basePackageShape, classes, jsonDiagram, currentClass);
		}
		// Aggiungi relazioni casuali tra le classi
		//RelationshipGenerator.addRandomRelations(diagramManager, diagram, basePackageShape, classes);
		//RelationshipGenerator.addRandomRelations(diagramManager, diagram, basePackageShape, classes, jsonDiagram);
		// dovrebbe servire a ridefinire il layout in base all'effettivo contenuto
		basePackageShape.fitSize();

		diagram.setName(jsonDiagram.optString("diagramName"));
		// Show up the diagram
		diagramManager.openDiagram(diagram);
		diagramManager.layout(diagram, DiagramManager.LAYOUT_AUTO);
	}

	// Aggiungi un nuovo metodo per caricare il JSON da un file
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

	private void loadDataType() {
		IProject project = ApplicationManager.instance().getProjectManager().getProject();
		IModelElement[] datatypes = project.toAllLevelModelElementArray(IModelElementFactory.MODEL_TYPE_DATA_TYPE);
		if (datatypes != null && datatypes.length > 0) {
			for (int i = 0; i < datatypes.length; i++) {
				if ("int".equals(datatypes[i].getName())) {
					typeInt = (IDataType) datatypes[i];
				}
				if ("boolean".equals(datatypes[i].getName())) {
					typeBoolean = (IDataType) datatypes[i];
				}
				if ("string".equals(datatypes[i].getName())) {
					typeString = (IDataType) datatypes[i];
				}
				if ("void".equals(datatypes[i].getName())) {
					typeVoid = (IDataType) datatypes[i];
				}
			}
		}
	}

	@Override
	public void update(VPAction arg0) {
		// TODO Auto-generated method stub
	}
}