package com.uniba.mining.dialogs;

import java.awt.GridLayout;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.uniba.mining.tasks.exportdiag.*;


public class LanguageDialog {
	private static final Map<Integer, String> languageCodeMap = new HashMap<>();

	ResourceBundle messages;

	public LanguageDialog(ResourceBundle messages) {
		this.messages=messages;

	}
	
	static {
		// Inizializza la mappa con le associazioni nome lingua -> codice lingua
		languageCodeMap.put(0, "it");
		languageCodeMap.put(1, "en");
		// Aggiungi altre lingue se necessario
	}

	public LanguageDiagramSelectionResult showLanguageSelectionDialog() {

		// Creare un array di oggetti rappresentanti le opzioni della lingua nel dialog
		Object[] languageOptions = {
				messages.getString("language.italian"),  // Opzione per l'italiano
				messages.getString("language.english")   // Opzione per l'inglese
		};

		// Creare un array di oggetti rappresentanti le opzioni del tipo di diagramma nel dialog
		Object[] diagramTypeOptions = {
				messages.getString("class.diagram"),  // Opzione per il diagramma delle classi
				messages.getString("usecase.diagram")  // Opzione per il diagramma dei casi d'uso
		};

		// Crea un JComboBox con le opzioni e il valore di default impostato a inglese per la lingua
		JComboBox<Object> languageComboBox = new JComboBox<>(languageOptions);
		languageComboBox.setSelectedItem(languageOptions[1]);  // Imposta il valore di default
		
		
		// Aggiunge il listener per la JComboBox languageComboBox
	    languageComboBox.addActionListener(e -> {
	     
	        // Ottieni il codice lingua dalla mappa
            String languageCode = languageCodeMap.get(languageComboBox.getSelectedIndex());
            
	        // Puoi anche chiamare il metodo setLanguage della classe Language per impostare la lingua
	         Language.setLanguage(languageCode);
	    });

		// Crea un JComboBox con le opzioni e il valore di default impostato al diagramma delle classi per il tipo di diagramma
		JComboBox<Object> diagramTypeComboBox = new JComboBox<>(diagramTypeOptions);
		diagramTypeComboBox.setSelectedItem(diagramTypeOptions[0]);  // Imposta il valore di default

		// Crea un pannello per contenere i JComboBox
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(2, 2));
		panel.add(new JLabel(messages.getString("plugin.select.language")));
		panel.add(languageComboBox);
		panel.add(new JLabel(messages.getString("plugin.select.diagramtype")));
		panel.add(diagramTypeComboBox);

		// Mostra un JOptionPane con il pannello
		int choice = JOptionPane.showOptionDialog(
				null,
				panel,
				messages.getString("plugin.title"),
				JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.INFORMATION_MESSAGE,
				null,
				null,
				null
				);

		//return 0; // Modifica questo valore in base alla tua implementazione effettiva
		// Restituisci i risultati della selezione
		return new LanguageDiagramSelectionResult(
				choice == JOptionPane.OK_OPTION,
				languageComboBox.getSelectedIndex(),
				diagramTypeComboBox.getSelectedIndex()
				);
	}

	// Classe per rappresentare i risultati della selezione di lingua e tipo di diagramma
	public static class LanguageDiagramSelectionResult {
		private final boolean selectionConfirmed;
		private final int languageIndex;
		private final int diagramTypeIndex;

		public LanguageDiagramSelectionResult(boolean selectionConfirmed, int languageIndex, int diagramTypeIndex) {
			this.selectionConfirmed = selectionConfirmed;
			this.languageIndex = languageIndex;
			this.diagramTypeIndex = diagramTypeIndex;
			//setLanguageIndex();
		}

		public boolean isSelectionConfirmed() {
			return selectionConfirmed;
		}

		public DiagramType getDiagramType() {
			return DiagramType.values()[diagramTypeIndex];
		}
		
	}

	//	// Enum per rappresentare i tipi di diagramma
	//	public enum LanguageDiagramType {
	//		CLASS_DIAGRAM,
	//		USE_CASE_DIAGRAM
	//	}

}
