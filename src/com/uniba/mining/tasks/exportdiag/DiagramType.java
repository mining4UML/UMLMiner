package com.uniba.mining.tasks.exportdiag;

import java.io.File;
import java.lang.reflect.Method;

import com.vp.plugin.model.IProject;

public enum DiagramType {
	CLASS_DIAGRAM(ClassInfo.class),
	USECASE_DIAGRAM(UseCaseInfo.class);
	// Aggiungi altri tipi di diagrammi se necessario
	// ...

	private final Class<?> diagramType;

	// Costruttore dell'enum che associa ogni tipo di diagramma a una classe specifica
	DiagramType(Class<?> diagramType) {
		this.diagramType = diagramType;
	}

	// Metodo di istanza per esportare le informazioni del diagramma
	public void exportInformation(IProject project, File selectedFile) {
		try {
			// Creazione di un'istanza della classe associata al tipo di diagramma
			Object diagramInstance = diagramType.getDeclaredConstructor().newInstance();

			// Ottieni il metodo "exportInformation" dalla classe associata
			Method exportMethod = diagramType.getMethod("exportInformation", com.vp.plugin.model.IProject.class, File.class);

			// Invoca il metodo sulla classe specifica
			exportMethod.invoke(diagramInstance, project, selectedFile);
		} catch (Exception e) {
			e.printStackTrace(); // Gestisci l'eccezione in modo appropriato
		}
	}
}