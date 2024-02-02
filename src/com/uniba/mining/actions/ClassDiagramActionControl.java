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
