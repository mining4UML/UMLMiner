//package com.uniba.mining.actions;
//
//import java.awt.Point;
//
//import com.vp.plugin.ApplicationManager;
//import com.vp.plugin.DiagramManager;
//import com.vp.plugin.action.VPAction;
//import com.vp.plugin.action.VPActionController;
//import com.vp.plugin.diagram.IClassDiagramUIModel;
//import com.vp.plugin.diagram.connector.IAssociationUIModel;
//import com.vp.plugin.diagram.shape.IClassUIModel;
//import com.vp.plugin.diagram.shape.IPackageUIModel;
//import com.vp.plugin.model.IAssociation;
//import com.vp.plugin.model.IAssociationClass;
//import com.vp.plugin.model.IAssociationEnd;
//import com.vp.plugin.model.IAttribute;
//import com.vp.plugin.model.IClass;
//import com.vp.plugin.model.IDataType;
//import com.vp.plugin.model.IGeneralization;
//import com.vp.plugin.model.IModelElement;
//import com.vp.plugin.model.IOperation;
//import com.vp.plugin.model.IPackage;
//import com.vp.plugin.model.IParameter;
//import com.vp.plugin.model.IProject;
//import com.vp.plugin.model.IRealization;
//import com.vp.plugin.model.factory.IModelElementFactory;
//
//public class ClassDiagramActionControl implements VPActionController {
//	
//	private IDataType typeInt = null;
//	private IDataType typeString = null;
//	private IDataType typeBoolean = null;
//	private IDataType typeVoid = null;
//	
//	@Override
//	public void performAction(VPAction arg0) {
//		loadDataType();
//		
//		// create blank class diagram
//		DiagramManager diagramManager = ApplicationManager.instance().getDiagramManager();
//		IClassDiagramUIModel diagram = (IClassDiagramUIModel) diagramManager.createDiagram(DiagramManager.DIAGRAM_TYPE_CLASS_DIAGRAM);
//			
//		// create base package model
//		IPackage basePackage = IModelElementFactory.instance().createPackage();
//		basePackage.setName("Base Package");
//		// create base package shape
//		IPackageUIModel basePackageShape = (IPackageUIModel) diagramManager.createDiagramElement(diagram, basePackage);
//		basePackageShape.setBounds(94, 79, 717, 516);
//		// set to automatic calculate the initial caption position
//		basePackageShape.setRequestResetCaption(true);
//		
//		// create superclass model
//		IClass superClass = IModelElementFactory.instance().createClass();
//		superClass.setName("SuperClass");
//		basePackage.addChild(superClass);
//		
//		// create attribute for superclass model
//		IAttribute attribute = IModelElementFactory.instance().createAttribute();
//		attribute.setName("superClassAttribute");
//		attribute.setType(typeInt);
//		attribute.setVisibility(IAttribute.VISIBILITY_PRIVATE);
//		superClass.addAttribute(attribute);
//		
//		// create operation for superclass model
//		IOperation superclassOperation = IModelElementFactory.instance().createOperation();
//		superclassOperation.setName("superClassOperation");
//		superclassOperation.setVisibility(IOperation.VISIBILITY_PUBLIC);
//		superclassOperation.setReturnType(typeBoolean);
//		
//		// define parameters for operation
//		IParameter parameter1 = IModelElementFactory.instance().createParameter();
//		parameter1.setName("param1");
//		parameter1.setType(typeString);
//		superclassOperation.addParameter(parameter1);
//		
//		IParameter parameter2 = IModelElementFactory.instance().createParameter();
//		parameter2.setName("param2");
//		parameter2.setType(typeInt);
//		superclassOperation.addParameter(parameter2);
//		
//		superClass.addOperation(superclassOperation);
//		
//		// create superclass shape
//		IClassUIModel superClassShape = (IClassUIModel) diagramManager.createDiagramElement(diagram, superClass);
//		superClassShape.setBounds(208, 144, 274, 57);
//		basePackageShape.addChild(superClassShape);
//		superClassShape.setRequestResetCaption(true);
//		
//		// create interface class model
//		IClass interfaceClass = IModelElementFactory.instance().createClass();
//		interfaceClass.setName("InterfaceClass");
//		basePackage.addChild(interfaceClass);
//		// specify the stereotype as "Interface"
//		interfaceClass.addStereotype("Interface");
//		
//		// create operation for interface class
//		IOperation interfaceOperation = IModelElementFactory.instance().createOperation();
//		interfaceOperation.setName("interfaceOperation");
//		interfaceOperation.setVisibility(IOperation.VISIBILITY_PUBLIC);
//		interfaceOperation.setReturnType(typeVoid);		
//		interfaceClass.addOperation(interfaceOperation);
//		
//		// create interface class shape
//		IClassUIModel interfaceClassShape = (IClassUIModel) diagramManager.createDiagramElement(diagram, interfaceClass);
//		interfaceClassShape.setBounds(514, 144, 141, 53);
//		basePackageShape.addChild(interfaceClassShape);
//		interfaceClassShape.setRequestResetCaption(true);		
//		
//		// create subclass model
//		IClass subClass = IModelElementFactory.instance().createClass();
//		subClass.setName("SubClass");
//		basePackage.addChild(subClass);
//		IClassUIModel subClassShape = (IClassUIModel) diagramManager.createDiagramElement(diagram, subClass);
//		subClassShape.setBounds(208, 294, 272, 53);
//		// set subclass to show inherited operations
//		subClassShape.setShowDerivedOperations(true);
//		basePackageShape.addChild(subClassShape);
//		subClassShape.setRequestResetCaption(true);
//		
//		// create a normal class which will have normal association to subclass
//		IClass classWithAssociation = IModelElementFactory.instance().createClass();
//		classWithAssociation.setName("ClassWithAssociation");
//		basePackage.addChild(classWithAssociation);
//		IClassUIModel classWithAssociationShape = (IClassUIModel) diagramManager.createDiagramElement(diagram, classWithAssociation);
//		classWithAssociationShape.setBounds(632, 294, 126, 40);
//		basePackageShape.addChild(classWithAssociationShape);
//		classWithAssociationShape.setRequestResetCaption(true);
//		
//		// create a normal class which will have aggregation association to subclass
//		IClass aggregrationClass = IModelElementFactory.instance().createClass();
//		aggregrationClass.setName("AggregationClass");
//		basePackage.addChild(aggregrationClass);
//		IClassUIModel aggregrationClassShape = (IClassUIModel) diagramManager.createDiagramElement(diagram, aggregrationClass);
//		aggregrationClassShape.setBounds(143, 521, 128, 40);
//		basePackageShape.addChild(aggregrationClassShape);
//		aggregrationClassShape.setRequestResetCaption(true);
//		
//		// create a normal class which will have composition association to subclass
//		IClass compositionClass = IModelElementFactory.instance().createClass();
//		compositionClass.setName("CompositionClass");
//		basePackage.addChild(compositionClass);
//		IClassUIModel compositionClassShape = (IClassUIModel) diagramManager.createDiagramElement(diagram, compositionClass);
//		compositionClassShape.setBounds(271, 443, 131, 40);
//		basePackageShape.addChild(compositionClassShape);
//		compositionClassShape.setRequestResetCaption(true);
//		
//		// create an association class
//		IClass associationClass = IModelElementFactory.instance().createClass();
//		associationClass.setName("AssociationClass");
//		basePackage.addChild(associationClass);
//		IClassUIModel associationClassShape = (IClassUIModel) diagramManager.createDiagramElement(diagram, associationClass);
//		associationClassShape.setBounds(492, 384, 102, 40);
//		basePackageShape.addChild(associationClassShape);
//		associationClassShape.setRequestResetCaption(true);
//		
//		// create generalization relationship from superclass to subclass
//		IGeneralization generalizationModel = IModelElementFactory.instance().createGeneralization();
//		generalizationModel.setFrom(superClass); 
//		generalizationModel.setTo(subClass);
//		// create generalization connector on diagram
//		diagramManager.createConnector(diagram, generalizationModel, superClassShape, subClassShape, null);
//		
//		// create realization relationship from interface class to subclass
//		IRealization realizationModel = IModelElementFactory.instance().createRealization();
//		realizationModel.setFrom(interfaceClass);
//		realizationModel.setTo(subClass);
//		// create realization connector on diagram
//		diagramManager.createConnector(diagram, realizationModel, interfaceClassShape, subClassShape, null);
//		
//		// create normal association between subclass to "ClassWithAssociation"
//		IAssociation associationModel = IModelElementFactory.instance().createAssociation();
//		associationModel.setName("AssociationClass");
//		associationModel.setFrom(subClass);
//		associationModel.setTo(classWithAssociation);
//		// specify multiplicity for from & to end 
//		IAssociationEnd associationFromEnd = (IAssociationEnd) associationModel.getFromEnd();
//		associationFromEnd.setMultiplicity("*");
//		IAssociationEnd associationToEnd = (IAssociationEnd) associationModel.getToEnd();
//		associationToEnd.setMultiplicity("*");	
//		// create association connector on diagram
//		IAssociationUIModel associationConnector = (IAssociationUIModel) diagramManager.createConnector(diagram, associationModel, subClassShape, classWithAssociationShape, null);
//		// set to automatic calculate the initial caption position	
//		associationConnector.setRequestResetCaption(true);
//		
//		// create aggregation association between subclass and AggregationClass
//		IAssociation aggregationModel = IModelElementFactory.instance().createAssociation();
//		aggregationModel.setFrom(subClass);
//		aggregationModel.setTo(aggregrationClass);
//		IAssociationEnd aggregationFromEnd = (IAssociationEnd) aggregationModel.getFromEnd();
//		// specify from end as aggregation as well as the multiplicity
//		aggregationFromEnd.setAggregationKind(IAssociationEnd.AGGREGATION_KIND_AGGREGATION);
//		aggregationFromEnd.setMultiplicity("1");
//		aggregationFromEnd.setName("subclass");
//		// specify details for to end.
//		IAssociationEnd aggregationToEnd = (IAssociationEnd) aggregationModel.getToEnd();
//		aggregationToEnd.setMultiplicity("0..*");
//		aggregationToEnd.setName("aggregation");
//		// create aggregation connector on diagram
//		IAssociationUIModel aggregationConnector = (IAssociationUIModel) diagramManager.createConnector(diagram, aggregationModel, subClassShape, aggregrationClassShape, null);
//		aggregationConnector.setRequestResetCaption(true);
//		
//		// create composition association between subclass and CompositionClass
//		IAssociation compositionModel = IModelElementFactory.instance().createAssociation();
//		compositionModel.setFrom(subClass);
//		compositionModel.setTo(compositionClass);
//		IAssociationEnd compositionFromEnd = (IAssociationEnd) compositionModel.getFromEnd();
//		// specify from end as composition as well as the multiplicity
//		compositionFromEnd.setAggregationKind(IAssociationEnd.AGGREGATION_KIND_COMPOSITED);
//		compositionFromEnd.setMultiplicity("1");
//		compositionFromEnd.setName("subclass");
//		// specify details for to end.
//		IAssociationEnd compositionToEnd = (IAssociationEnd) compositionModel.getToEnd();
//		compositionToEnd.setMultiplicity("1..*");
//		compositionToEnd.setName("composition");
//		// create composition connector on diagram
//		IAssociationUIModel compositionConnector = (IAssociationUIModel) diagramManager.createConnector(diagram, compositionModel, subClassShape, compositionClassShape, new Point[] {new Point(366, 348), new Point(366, 422)});
//		compositionConnector.setRequestResetCaption(true);
//		
//		// create association class relationship
//		IAssociationClass associationClassModel = IModelElementFactory.instance().createAssociationClass();
//		associationClassModel.setFrom(associationModel);
//		associationClassModel.setTo(associationClass);
//		// create association connector on diagram
//		diagramManager.createConnector(diagram, associationClassModel, associationConnector, associationClassShape, null);
//		
//		// show up the diagram		
//		diagramManager.openDiagram(diagram);
//	}
//	
//	public void loadDataType() {
//		IProject project = ApplicationManager.instance().getProjectManager().getProject();
//		IModelElement[] datatypes = project.toAllLevelModelElementArray(IModelElementFactory.MODEL_TYPE_DATA_TYPE);
//		if (datatypes != null && datatypes.length > 0) {
//			for (int i = 0; i < datatypes.length; i++) {
//				if ("int".equals(datatypes[i].getName())) {
//					typeInt = (IDataType) datatypes[i];
//				}
//				if ("boolean".equals(datatypes[i].getName())) {
//					typeBoolean = (IDataType) datatypes[i];
//				}
//				if ("String".equals(datatypes[i].getName())) {
//					typeString = (IDataType) datatypes[i];
//				}
//				if ("void".equals(datatypes[i].getName())) {
//					typeVoid = (IDataType) datatypes[i];
//				}
//			}
//		}		
//	}
//	
//	@Override
//	public void update(VPAction arg0) {
//		// TODO Auto-generated method stub
//		
//	}
//
//}
package com.uniba.mining.actions;

import java.util.Random;

import com.uniba.mining.tasks.generator.ClassGenerator;
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


//Aggiungi un'enumerazione per i tipi di relazione
enum RelationshipType {
	GENERALIZATION, ASSOCIATION, AGGREGATION, REALIZATION
}


public class ClassDiagramActionControl implements VPActionController {

	private IDataType typeInt = null;
	private IDataType typeString = null;
	private IDataType typeBoolean = null;
	private IDataType typeVoid = null;

	@Override
	public void performAction(VPAction arg0) {
		// Chiamare il metodo per generare un diagramma
		generateClassDiagram();
	}

	private void generateClassDiagram() {
		loadDataType();

		// create blank class diagram
		DiagramManager diagramManager = ApplicationManager.instance().getDiagramManager();
		IClassDiagramUIModel diagram = (IClassDiagramUIModel) diagramManager
				.createDiagram(DiagramManager.DIAGRAM_TYPE_CLASS_DIAGRAM);

		// create base package model
		IPackage basePackage = IModelElementFactory.instance().createPackage();
		basePackage.setName("Base Package");
		// create base package shape
		IPackageUIModel basePackageShape = (IPackageUIModel) diagramManager.createDiagramElement(diagram, basePackage);
		basePackageShape.setBounds(94, 79, 717, 516);
		// set to automatic calculate the initial caption position
		basePackageShape.setRequestResetCaption(true);

		// Generare classi, attributi e relazioni casuali
		Random random = new Random();
		int numClasses = 3 + random.nextInt(5); // Genera tra 3 e 7 classi
		IClass[] classes = new IClass[numClasses];
		
		ClassGenerator classGenerator = new ClassGenerator();
		
		for (int i = 0; i < numClasses; i++) {
			IClass newClass = IModelElementFactory.instance().createClass();
			newClass.setName("Class" + i);

			// Aggiungi attributi casuali
			int numAttributes = random.nextInt(3) + 1; // Genera tra 1 e 3 attributi
			for (int j = 0; j < numAttributes; j++) {
				IAttribute attribute = IModelElementFactory.instance().createAttribute();
				attribute.setName("Attribute" + j);
				Random randomType = new Random();
				int numType= 1+randomType.nextInt(4);
				switch(numType) {
				case 1: 
					attribute.setType(typeInt);
					break;
				case 2:
					attribute.setType(typeString);
					break;
				case 3:
					attribute.setType(typeBoolean);
					break;
				case 4:
					attribute.setType(typeVoid);
					break;
				}
				
				attribute.setVisibility(IAttribute.VISIBILITY_PRIVATE);
				newClass.addAttribute(attribute);
			}

			// Aggiungi operazioni casuali
			int numOperations = random.nextInt(2) + 1; // Genera tra 1 e 2 operazioni
			for (int k = 0; k < numOperations; k++) {
				IOperation operation = IModelElementFactory.instance().createOperation();
				operation.setName("Operation" + k);
				operation.setVisibility(IOperation.VISIBILITY_PUBLIC);
				operation.setReturnType(typeBoolean);

				// Aggiungi parametri casuali
				int numParameters = random.nextInt(2) + 1; // Genera tra 1 e 2 parametri
				for (int l = 0; l < numParameters; l++) {
					IParameter parameter = IModelElementFactory.instance().createParameter();
					parameter.setName("param" + l);
					parameter.setType(typeString);
					operation.addParameter(parameter);
				}

				newClass.addOperation(operation);
			}

			// Crea la forma della classe nel diagramma
			IClassUIModel classShape = (IClassUIModel) diagramManager.createDiagramElement(diagram, newClass);
			classShape.setBounds(100 + i * 120, 200, 100, 70);
			basePackageShape.addChild(classShape);
			classShape.setRequestResetCaption(true);
			classShape.fitSize();

			classes[i] = newClass;
			//classes[i] = classGenerator.generateClass("Class" + i);
		}

		// Aggiungi relazioni casuali tra le classi
		RelationshipGenerator.addRandomRelations(diagramManager, diagram, basePackageShape, classes);

		// dovrebbe servire a ridefinire il layout in base all'effettivo contenuto
		basePackageShape.fitSize();
		
		// Show up the diagram
		diagramManager.openDiagram(diagram);
		diagramManager.layout(diagram, DiagramManager.LAYOUT_AUTO); 
	}



//	private void addRandomRelations(DiagramManager diagramManager, IClassDiagramUIModel diagram,
//			IPackageUIModel basePackageShape, IClass[] classes) {
//
//		for (IClass currentClass : classes) {
//			// Assegna ogni classe a una relazione
//			RelationshipType relationshipType = getRandomRelationshipType();
//			switch (relationshipType) {
//			case GENERALIZATION:
//				IClass superClass = getRandomClassExcept(classes, currentClass);
//				createGeneralization(diagramManager, diagram, basePackageShape, superClass, currentClass);
//				break;
//			case ASSOCIATION:
//				IClass targetClass = getRandomClassExcept(classes, currentClass);
//				createAssociation(diagramManager, diagram, basePackageShape, currentClass, targetClass);
//				break;
//			case REALIZATION:
//				IClass interfaceClass = getRandomClassExcept(classes, currentClass);
//				createRealization(diagramManager, diagram, basePackageShape, interfaceClass, currentClass);
//				break;
//			case AGGREGATION:
//				IClass targetAggClass = getRandomClassExcept(classes, currentClass);
//				createAggregation(diagramManager, diagram, basePackageShape, currentClass, targetAggClass);
//				break;
//			}
//		}
//	}




//	private RelationshipType getRandomRelationshipType() {
//		Random random = new Random();
//		int typeIndex = random.nextInt(RelationshipType.values().length);
//		return RelationshipType.values()[typeIndex];
//	}
//
//	private IClass getRandomClassExcept(IClass[] classes, IClass currentClass) {
//		Random random = new Random();
//		IClass targetClass;
//		do {
//			targetClass = classes[random.nextInt(classes.length)];
//		} while (targetClass.equals(currentClass));
//		return targetClass;
//	}
//
//
//
//	private IClass getRandomInterface(IClass[] classes) {
//		Random random = new Random();
//		IClass interfaceClass;
//
//		do {
//			interfaceClass = classes[random.nextInt(classes.length)];
//		} while (!isInterface(interfaceClass));
//
//		return interfaceClass;
//	}

	// Verifica se un'interfaccia ha lo stereotipo "Interface"
//	private boolean isInterface(IClass interfaceClass) {
//		IStereotype[] stereotypes = interfaceClass.toStereotypeModelArray();
//		for (IStereotype stereotype : stereotypes) {
//			if ("Interface".equals(stereotype.getName())) {
//				return true;
//			}
//		}
//		return false;
//	}



//	private void createGeneralization(DiagramManager diagramManager, IClassDiagramUIModel diagram,
//			IPackageUIModel basePackageShape, IClass superClass, IClass subClass) {
//		IGeneralization generalizationModel = IModelElementFactory.instance().createGeneralization();
//		generalizationModel.setFrom(superClass);
//		generalizationModel.setTo(subClass);
//		IClassUIModel superClassShape = findClassShape(basePackageShape, superClass);
//		IClassUIModel subClassShape = findClassShape(basePackageShape, subClass);
//		diagramManager.createConnector(diagram, generalizationModel, superClassShape, subClassShape, null);
//	}


//	private void createAssociation(DiagramManager diagramManager, IClassDiagramUIModel diagram,
//			IPackageUIModel basePackageShape, IClass sourceClass, IClass targetClass) {
//		IAssociation associationModel = IModelElementFactory.instance().createAssociation();
//		associationModel.setFrom(sourceClass);
//		associationModel.setTo(targetClass);
//		IAssociationEnd associationFromEnd = (IAssociationEnd) associationModel.getFromEnd();
//		associationFromEnd.setMultiplicity("*");
//		IAssociationEnd associationToEnd = (IAssociationEnd) associationModel.getToEnd();
//		associationToEnd.setMultiplicity("*");
//		IClassUIModel sourceClassShape = findClassShape(basePackageShape, sourceClass);
//		IClassUIModel targetClassShape = findClassShape(basePackageShape, targetClass);
//		IAssociationUIModel associationConnector = (IAssociationUIModel) diagramManager.createConnector(diagram,
//				associationModel, sourceClassShape, targetClassShape, null);
//		associationConnector.setRequestResetCaption(true);
//	}

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


//	private void createAggregation(DiagramManager diagramManager, IClassDiagramUIModel diagram,
//			IPackageUIModel basePackageShape, IClass sourceClass, IClass targetClass) {
//		IAssociation aggregationModel = IModelElementFactory.instance().createAssociation();
//		aggregationModel.setFrom(sourceClass);
//		aggregationModel.setTo(targetClass);
//
//		IAssociationEnd aggregationFromEnd = (IAssociationEnd) aggregationModel.getFromEnd();
//		aggregationFromEnd.setMultiplicity("*");
//
//		IAssociationEnd aggregationToEnd = (IAssociationEnd) aggregationModel.getToEnd();
//		aggregationToEnd.setMultiplicity("*");
//
//		Random random = new Random();
//		int tipoAggr = 1 + random.nextInt(2); // Genera un numero compreso tra 1 e 2
//
//		switch(tipoAggr) {
//		case 1:
//			aggregationToEnd.setAggregationKind(IAssociationEnd.AGGREGATION_KIND_AGGREGATION);
//			aggregationToEnd.setName("aggregation");
//			break;
//		case 2:
//			aggregationToEnd.setAggregationKind(IAssociationEnd.AGGREGATION_KIND_COMPOSITED);
//			aggregationToEnd.setName("composition");
//			break;
//		}
//
//		IClassUIModel sourceClassShape = findClassShape(basePackageShape, sourceClass);
//		IClassUIModel targetClassShape = findClassShape(basePackageShape, targetClass);
//		IAssociationUIModel associationConnector = (IAssociationUIModel) diagramManager.createConnector(diagram,
//				aggregationModel, sourceClassShape, targetClassShape, null);
//		associationConnector.setRequestResetCaption(true);
//	}
//
//
//
//	private void createRealization(DiagramManager diagramManager, IClassDiagramUIModel diagram,
//			IPackageUIModel basePackageShape, IClass interfaceClass, IClass subClass) {
//		IRealization realizationModel = IModelElementFactory.instance().createRealization();
//		realizationModel.setFrom(interfaceClass);
//		realizationModel.setTo(subClass);
//		IClassUIModel interfaceClassShape = findClassShape(basePackageShape, interfaceClass);
//		IClassUIModel subClassShape = findClassShape(basePackageShape, subClass);
//		diagramManager.createConnector(diagram, realizationModel, interfaceClassShape, subClassShape, null);
//	}
//
//	private IClassUIModel findClassShape(IPackageUIModel packageShape, IClass targetClass) {
//		for (int i = 0; i < packageShape.childrenCount(); i++) {
//			IModelElement child = packageShape.getChildAt(i).getModelElement();
//			if (child instanceof IClass && child.equals(targetClass)) {
//				return (IClassUIModel) packageShape.getChildAt(i);
//			}
//		}
//		return null;
//	}

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

	@Override
	public void update(VPAction arg0) {
		// TODO Auto-generated method stub
	}
}
