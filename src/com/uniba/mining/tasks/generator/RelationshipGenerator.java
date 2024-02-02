package com.uniba.mining.tasks.generator;

import java.util.Random;

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
	
	// Aggiungi un'enumerazione per i tipi di relazione
	public enum RelationshipType {
	    GENERALIZATION, ASSOCIATION, AGGREGATION, REALIZATION
	}

	public RelationshipGenerator() {
		this.random = new Random();
	}

	public static void addRandomRelations(DiagramManager diagramManager, IClassDiagramUIModel diagram,
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
	}
	
	private static void createGeneralization(DiagramManager diagramManager, IClassDiagramUIModel diagram,
			IPackageUIModel basePackageShape, IClass superClass, IClass subClass) {
		IGeneralization generalizationModel = IModelElementFactory.instance().createGeneralization();
		generalizationModel.setFrom(superClass);
		generalizationModel.setTo(subClass);
		IClassUIModel superClassShape = findClassShape(basePackageShape, superClass);
		IClassUIModel subClassShape = findClassShape(basePackageShape, subClass);
		diagramManager.createConnector(diagram, generalizationModel, superClassShape, subClassShape, null);
	}


	private static void createAssociation(DiagramManager diagramManager, IClassDiagramUIModel diagram,
			IPackageUIModel basePackageShape, IClass sourceClass, IClass targetClass) {
		IAssociation associationModel = IModelElementFactory.instance().createAssociation();
		associationModel.setFrom(sourceClass);
		associationModel.setTo(targetClass);
		IAssociationEnd associationFromEnd = (IAssociationEnd) associationModel.getFromEnd();
		associationFromEnd.setMultiplicity("*");
		IAssociationEnd associationToEnd = (IAssociationEnd) associationModel.getToEnd();
		associationToEnd.setMultiplicity("*");
		IClassUIModel sourceClassShape = findClassShape(basePackageShape, sourceClass);
		IClassUIModel targetClassShape = findClassShape(basePackageShape, targetClass);
		IAssociationUIModel associationConnector = (IAssociationUIModel) diagramManager.createConnector(diagram,
				associationModel, sourceClassShape, targetClassShape, null);
		associationConnector.setRequestResetCaption(true);
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
			IPackageUIModel basePackageShape, IClass sourceClass, IClass targetClass) {
		IAssociation aggregationModel = IModelElementFactory.instance().createAssociation();
		aggregationModel.setFrom(sourceClass);
		aggregationModel.setTo(targetClass);

		IAssociationEnd aggregationFromEnd = (IAssociationEnd) aggregationModel.getFromEnd();
		aggregationFromEnd.setMultiplicity("*");

		IAssociationEnd aggregationToEnd = (IAssociationEnd) aggregationModel.getToEnd();
		aggregationToEnd.setMultiplicity("*");

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
		IAssociationUIModel associationConnector = (IAssociationUIModel) diagramManager.createConnector(diagram,
				aggregationModel, sourceClassShape, targetClassShape, null);
		associationConnector.setRequestResetCaption(true);
	}



	private static void createRealization(DiagramManager diagramManager, IClassDiagramUIModel diagram,
			IPackageUIModel basePackageShape, IClass interfaceClass, IClass subClass) {
		IRealization realizationModel = IModelElementFactory.instance().createRealization();
		realizationModel.setFrom(interfaceClass);
		realizationModel.setTo(subClass);
		IClassUIModel interfaceClassShape = findClassShape(basePackageShape, interfaceClass);
		IClassUIModel subClassShape = findClassShape(basePackageShape, subClass);
		diagramManager.createConnector(diagram, realizationModel, interfaceClassShape, subClassShape, null);
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

