package com.uniba.mining.dialogs;

import java.awt.Component;

import com.uniba.mining.feedback.DiagramUI;
import com.uniba.mining.plugin.Config;
import com.uniba.mining.utils.GUI;
import com.vp.plugin.view.IDialog;
import com.vp.plugin.view.IDialogHandler;

import javax.swing.*;
import java.awt.*;

public class FeedbackRequHandler implements IDialogHandler {

	private IDialog dialog;
	private JPanel rootPanel;
	private DiagramUI diagramUI;
	 private Runnable onDialogCloseCallback;  // Dichiarazione del campo

	@Override
	public boolean canClosed() {
		// Implementazione per determinare se la finestra di dialogo può essere chiusa
		// Esempio: verifica se tutti i dati sono stati salvati correttamente
		return true; // Ritorna true se la finestra può essere chiusa
	}

	@Override
	public Component getComponent() {
		// Creazione e configurazione del componente principale
		rootPanel = new JPanel(new BorderLayout());
		diagramUI = new DiagramUI(); // Inizializzazione di DiagramUI

		// Aggiunge il componente DiagramUI al pannello radice
		rootPanel.add(diagramUI.getComponent(), BorderLayout.CENTER);

		// Esempio di configurazione aggiuntiva del pannello radice
		// rootPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		return rootPanel;
	}

	@Override
	public void prepare(IDialog dialog) {
		GUI.prepareDialog(dialog, Config.FEEDBACK_LABELREQU);
	}

	@Override
	public void shown() {
		// Azioni da eseguire quando la finestra di dialogo viene mostrata
//        JOptionPane.showMessageDialog(rootPanel, "Feedback Request Handler is shown.",
//                "Feedback Request Handler", JOptionPane.INFORMATION_MESSAGE);
	}

	// Esempio di metodo per gestire l'azione di chiusura della finestra di dialogo
	private void closeDialog() {
		if (canClosed()) {
			dialog.close();
		} else {
			GUI.showErrorMessageDialog(rootPanel, Config.FEEDBACK_LABELREQU, "Cannot close the dialog");
		}
	}

}