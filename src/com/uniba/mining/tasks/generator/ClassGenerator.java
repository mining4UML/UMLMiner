package com.uniba.mining.tasks.generator;

import java.util.Random;

import org.json.JSONArray;
import org.json.JSONObject;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.model.IAttribute;
import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IDataType;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IOperation;
import com.vp.plugin.model.IParameter;
import com.vp.plugin.model.IProject;
import com.vp.plugin.model.factory.IModelElementFactory;

public class ClassGenerator {
	private Random random;

	private IDataType typeInt = null;
	private IDataType typeString = null;
	private IDataType typeBoolean = null;
	private IDataType typeVoid = null;

	// // Aggiungi un'enumerazione per i tipi di relazione
	// 	private enum dataType {
	// 	    GENERALIZATION, ASSOCIATION, AGGREGATION, REALIZATION
	// 	}

	public ClassGenerator() {
		this.random = new Random();
	}

	public IClass generateClass(String className) {
		loadDataType();
		IClass newClass = IModelElementFactory.instance().createClass();
		newClass.setName(className);

		// Genera attributi casuali
		int numAttributes = random.nextInt(3) + 1; // Genera tra 1 e 3 attributi
		for (int j = 0; j < numAttributes; j++) {
			IAttribute attribute = IModelElementFactory.instance().createAttribute();
			attribute.setName("Attribute" + j);
			attribute.setType(getRandomDataType());
			attribute.setVisibility(IAttribute.VISIBILITY_PRIVATE);
			newClass.addAttribute(attribute);
		}

		// Genera operazioni casuali
		int numOperations = random.nextInt(2) + 1; // Genera tra 1 e 2 operazioni
		for (int k = 0; k < numOperations; k++) {
			IOperation operation = IModelElementFactory.instance().createOperation();
			operation.setName("Operation" + k);
			operation.setVisibility(IOperation.VISIBILITY_PUBLIC);
			operation.setReturnType(typeBoolean);

			// Genera parametri casuali
			int numParameters = random.nextInt(2) + 1; // Genera tra 1 e 2 parametri
			for (int l = 0; l < numParameters; l++) {
				IParameter parameter = IModelElementFactory.instance().createParameter();
				parameter.setName("param" + l);
				parameter.setType(typeString);
				operation.addParameter(parameter);
			}

			newClass.addOperation(operation);
		}

		return newClass;
	}

	private IDataType getRandomDataType() {
		int numType = 1 + random.nextInt(4);
		switch (numType) {
		case 1:
			return typeInt;
		case 2:
			return typeString;
		case 3:
			return typeBoolean;
		case 4:
			return typeVoid;
		default:
			return null;
		}
	}

	public void loadDataType() {
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

	/**
	 * Analizza le operazioni da un JSON e le aggiunge alla classe UML.
	 * @param operationsArray L'array JSON delle operazioni.
	 * @param classExt L'oggetto ClassExt a cui aggiungere le operazioni.
	 */
	private void parseOperations(JSONArray operationsArray, ClassExt classExt) {
		for (int i = 0; i < operationsArray.length(); i++) {
			JSONObject operationJson = operationsArray.getJSONObject(i);

			// Creiamo un'operazione UML
			IOperation operation = IModelElementFactory.instance().createOperation();
			operation.setName(operationJson.getString("name"));

			// Verifichiamo se l'operazione è opzionale
			boolean isOptional = operationJson.optBoolean("optional", false);

			// Aggiungiamo l'operazione alla classe
			classExt.getClasse().addOperation(operation);

			// Se è opzionale, la segniamo in ClassExt
			classExt.setOperationOptional(operation, isOptional);
		}
	}
}



