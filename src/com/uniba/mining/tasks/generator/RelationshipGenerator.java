package com.uniba.mining.tasks.generator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONObject;

import com.vp.plugin.DiagramManager;
import com.vp.plugin.diagram.IClassDiagramUIModel;
import com.vp.plugin.diagram.connector.IAssociationUIModel;
import com.vp.plugin.diagram.shape.IClassUIModel;
import com.vp.plugin.diagram.shape.IPackageUIModel;
import com.vp.plugin.model.IAssociation;
import com.vp.plugin.model.IAssociationEnd;
import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IGeneralization;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IRealization;
import com.vp.plugin.model.factory.IModelElementFactory;

public class RelationshipGenerator {
	private static Random random = new Random();
	private static String RELNOAGGIUNTA="relazione non aggiunta perchè classe opzionale non selezionata";

	private static String DEFAULT_MOLTEPLICITY ="*";
	// Aggiungi un'enumerazione per i tipi di relazione
	public enum RelationshipType {
		GENERALIZATION, ASSOCIATION, AGGREGATION, REALIZATION
	}

	public RelationshipGenerator() {
		this.random = new Random();
	}

	/*public static void addRandomRelations(DiagramManager diagramManager, IClassDiagramUIModel diagram,
			IPackageUIModel basePackageShape, IClass[] classes) {
		for (IClass currentClass : classes) {
			RelationshipType relationshipType = getRandomRelationshipType();
			switch (relationshipType) {
			case GENERALIZATION:
				IClass superClass = getRandomClassExcept(classes, currentClass);
				createGeneralization(diagramManager, diagram, basePackageShape, superClass, currentClass);
				break;
			case ASSOCIATION:
				IClass targetClass = getRandomClassExcept(classes, currentClass);
				createAssociation(diagramManager, diagram, basePackageShape, currentClass, targetClass);
				break;
			case REALIZATION:
				IClass interfaceClass = getRandomClassExcept(classes, currentClass);
				createRealization(diagramManager, diagram, basePackageShape, interfaceClass, currentClass);
				break;
			case AGGREGATION:
				IClass targetAggClass = getRandomClassExcept(classes, currentClass);
				createAggregation(diagramManager, diagram, basePackageShape, currentClass, targetAggClass);
				break;
			}
		}
	}*/


	public static void addRelationsFromJson(DiagramManager diagramManager, IClassDiagramUIModel diagram,
			IPackageUIModel basePackageShape, List<IClass> classes, JSONObject jsonDiagram, IClass currentClass) {
		// Check if the class has defined generalization in the JSON
		String className = currentClass.getName();
		JSONObject jsonClass = findJsonClass(jsonDiagram, className);

		if (jsonClass != null) {
			if (jsonClass.has("generalizzazione") && !jsonClass.isNull("generalizzazione")) {
				String generalizationName = jsonClass.getString("generalizzazione");
				if (!generalizationName.isEmpty()) {
					// Find the superclass in the list of classes
					IClass superClass = getClassByName(classes, generalizationName);

					if (superClass != null) {
						// Get the subclass
						IClass subClass = getClassByName(classes, jsonClass.getString("nome"));

						// Create generalization
						createGeneralization(diagramManager, diagram, basePackageShape, superClass, subClass, generalizationName);
					}
				}
			}

			// Check if the class has defined associations in the JSON
			if (jsonClass.has("associazione") && !jsonClass.isNull("associazione") ) {
				JSONArray jsonAssociations = jsonClass.getJSONArray("associazione");
				for (int i = 0; i < jsonAssociations.length(); i++) {
					JSONObject jsonAssociation = jsonAssociations.getJSONObject(i);
					String associationName = jsonAssociation.getString("nome");
					String targetClassName = jsonAssociation.getString("destinazione");
					String multiplicity = jsonAssociation.getString("molteplicita");
					String multiplicityDestination = DEFAULT_MOLTEPLICITY;

					if (jsonAssociation.has("molteplicitaDestinazione"))
						multiplicityDestination = jsonAssociation.getString("molteplicitaDestinazione");

					IClass targetClass = getClassByName(classes, targetClassName);
					IClass sourceClass = getClassByName(classes, className);

					if (targetClass != null && sourceClass != null)// Create association
						createAssociation(diagramManager, diagram, basePackageShape, 
								sourceClass, targetClass, associationName, multiplicity,multiplicityDestination );
				}
			}

			if (jsonClass.has("aggregazione") && !jsonClass.isNull("aggregazione")) {
				JSONArray jsonAggregations = jsonClass.getJSONArray("aggregazione");
				for (int i = 0; i < jsonAggregations.length(); i++) {
					JSONObject jsonAggregation = jsonAggregations.getJSONObject(i);
					String aggregationName = jsonAggregation.getString("nome");
					String targetClassName = jsonAggregation.getString("destinazione");
					String multiplicity = jsonAggregation.getString("molteplicita");
					String multiplicityDestination = DEFAULT_MOLTEPLICITY;
					if(jsonAggregation.has("molteplicitaDestinazione"))
						multiplicityDestination = jsonAggregation.getString("molteplicitaDestinazione");

					IClass targetClass = getClassByName(classes, targetClassName);
					IClass sourceClass = getClassByName(classes, className);

					// Create aggregation
					if (targetClass != null && sourceClass != null)
						createAggregation(diagramManager, diagram, basePackageShape, 
								sourceClass, targetClass, aggregationName, multiplicity, multiplicityDestination);
				}
			}

			// Implement similar logic for other relationship types if needed
		}
	}

	private static JSONObject findJsonClass(JSONObject jsonDiagram, String className) {
		JSONArray jsonClasses = jsonDiagram.getJSONArray("classi");
		for (int i = 0; i < jsonClasses.length(); i++) {
			JSONObject jsonClass = jsonClasses.getJSONObject(i);
			if (jsonClass.getString("nome").equals(className)) {
				return jsonClass;
			}
		}
		return null;
	}


	private static IClass getClassByName(List<IClass> classes, String className) {
		for (IClass clazz : classes) {
			if (clazz.getName().equals(className)) {
				return clazz;
			}
		}
		return null;
	}

	public static void addRandomRelations(DiagramManager diagramManager, IClassDiagramUIModel diagram,
			IPackageUIModel basePackageShape, ArrayList<IClass> classes, JSONObject jsonClass) {
		for (IClass currentClass : classes) {
			// Call the modified method to add relations from JSON for the current class
			addRelationsFromJson(diagramManager, diagram, basePackageShape, classes, jsonClass, currentClass);
		}
	}


	private static void createGeneralization(DiagramManager diagramManager, IClassDiagramUIModel diagram,
			IPackageUIModel basePackageShape, IClass superClass, IClass subClass,
			String generalizationName) {
		if (superClass != null && subClass != null) {
			IGeneralization generalizationModel = IModelElementFactory.instance().createGeneralization();
			generalizationModel.setFrom(superClass);
			generalizationModel.setTo(subClass);
			generalizationModel.setName(generalizationName);
			IClassUIModel superClassShape = findClassShape(basePackageShape, superClass);
			IClassUIModel subClassShape = findClassShape(basePackageShape, subClass);
			if(superClassShape!= null &&  subClassShape!=null)
				diagramManager.createConnector(diagram, generalizationModel, superClassShape, subClassShape, null);
		}
		else
			System.out.println(RELNOAGGIUNTA);
	}


	private static void createAssociation(DiagramManager diagramManager, IClassDiagramUIModel diagram,
			IPackageUIModel basePackageShape, IClass sourceClass, IClass targetClass,
			String associationName, String multiplicity, String multiplicityDestination) {

		if (sourceClass != null && targetClass != null) {

			IAssociation associationModel = IModelElementFactory.instance().createAssociation();
			associationModel.setTo(sourceClass);
			associationModel.setFrom(targetClass);
			associationModel.setName(associationName);


			IAssociationEnd associationFromEnd = (IAssociationEnd) associationModel.getFromEnd();
			associationFromEnd.setMultiplicity(multiplicityDestination);
			IAssociationEnd associationToEnd = (IAssociationEnd) associationModel.getToEnd();
			associationToEnd.setMultiplicity(multiplicity);
			IClassUIModel sourceClassShape = findClassShape(basePackageShape, sourceClass);
			IClassUIModel targetClassShape = findClassShape(basePackageShape, targetClass);
			if (sourceClassShape != null && targetClassShape != null) {
				IAssociationUIModel associationConnector = (IAssociationUIModel) diagramManager.createConnector(diagram,
						associationModel, sourceClassShape, targetClassShape, null);
				associationConnector.setRequestResetCaption(true);
			}
		}
		else {
			System.out.println(RELNOAGGIUNTA);
		}

	}

	// create aggregation association between subclass and AggregationClass
	//	IAssociationEnd aggregationFromEnd = (IAssociationEnd) aggregationModel.getFromEnd();
	//	// specify from end as aggregation as well as the multiplicity
	//	aggregationFromEnd.setAggregationKind(IAssociationEnd.AGGREGATION_KIND_AGGREGATION);
	//	aggregationFromEnd.setMultiplicity("1");
	//	aggregationFromEnd.setName("subclass");
	//	// specify details for to end.
	//	IAssociationEnd aggregationToEnd = (IAssociationEnd) aggregationModel.getToEnd();
	//	aggregationToEnd.setMultiplicity("0..*");
	//	aggregationToEnd.setName("aggregation");
	//	// create aggregation connector on diagram
	//	IAssociationUIModel aggregationConnector = (IAssociationUIModel) diagramManager.createConnector(diagram, aggregationModel, subClassShape, aggregrationClassShape, null);
	//	aggregationConnector.setRequestResetCaption(true);


	private static void createAggregation(DiagramManager diagramManager, IClassDiagramUIModel diagram,
			IPackageUIModel basePackageShape, IClass sourceClass, IClass targetClass,
			String aggregationName, String multiplicity, String multiplicityDestinatation) {
		IAssociation aggregationModel = IModelElementFactory.instance().createAssociation();
		if (sourceClass != null && targetClass != null) {

			aggregationModel.setFrom(sourceClass);
			aggregationModel.setTo(targetClass);
			aggregationModel.setName(aggregationName);

			IAssociationEnd aggregationFromEnd = (IAssociationEnd) aggregationModel.getFromEnd();
			aggregationFromEnd.setMultiplicity(multiplicityDestinatation);

			IAssociationEnd aggregationToEnd = (IAssociationEnd) aggregationModel.getToEnd();
			aggregationToEnd.setMultiplicity(multiplicity);

			Random random = new Random();
			int tipoAggr = 1 + random.nextInt(2); // Genera un numero compreso tra 1 e 2

			switch(tipoAggr) {
			case 1:
				aggregationToEnd.setAggregationKind(IAssociationEnd.AGGREGATION_KIND_AGGREGATION);
				aggregationToEnd.setName("aggregation");
				break;
			case 2:
				aggregationToEnd.setAggregationKind(IAssociationEnd.AGGREGATION_KIND_COMPOSITED);
				aggregationToEnd.setName("composition");
				break;
			}

			IClassUIModel sourceClassShape = findClassShape(basePackageShape, sourceClass);
			IClassUIModel targetClassShape = findClassShape(basePackageShape, targetClass);
			if (sourceClassShape != null && targetClassShape != null) {
				IAssociationUIModel associationConnector = (IAssociationUIModel) diagramManager.createConnector(diagram,
						aggregationModel, sourceClassShape, targetClassShape, null);
				associationConnector.setRequestResetCaption(true);
			}
		}
		else
			System.out.println(RELNOAGGIUNTA);
	}



	private static void createRealization(DiagramManager diagramManager, IClassDiagramUIModel diagram,
			IPackageUIModel basePackageShape, IClass interfaceClass, IClass subClass) {

		if (interfaceClass != null && subClass != null) {

			IRealization realizationModel = IModelElementFactory.instance().createRealization();
			realizationModel.setFrom(interfaceClass);
			realizationModel.setTo(subClass);
			IClassUIModel interfaceClassShape = findClassShape(basePackageShape, interfaceClass);
			IClassUIModel subClassShape = findClassShape(basePackageShape, subClass);
			if (interfaceClassShape != null && subClassShape != null)
				diagramManager.createConnector(diagram, realizationModel, interfaceClassShape, subClassShape, null);
		}
		else
			System.out.println(RELNOAGGIUNTA);
	}


	private static IClassUIModel findClassShape(IPackageUIModel packageShape, IClass targetClass) {
		for (int i = 0; i < packageShape.childrenCount(); i++) {
			IModelElement child = packageShape.getChildAt(i).getModelElement();
			if (child instanceof IClass && child.equals(targetClass)) {
				return (IClassUIModel) packageShape.getChildAt(i);
			}
		}
		return null;
	}



	private static RelationshipType getRandomRelationshipType() {
		int typeIndex = random.nextInt(RelationshipType.values().length);
		return RelationshipType.values()[typeIndex];
	}

	private static IClass getRandomClassExcept(IClass[] classes, IClass currentClass) {
		IClass targetClass;
		do {
			targetClass = classes[random.nextInt(classes.length)];
		} while (targetClass.equals(currentClass));
		return targetClass;
	}
}

