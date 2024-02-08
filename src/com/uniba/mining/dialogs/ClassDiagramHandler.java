package com.uniba.mining.dialogs;

import java.nio.file.Path;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.SwingWorker;

import com.uniba.mining.tasks.generator.DiagramCombinations;
import com.uniba.mining.utils.Application;
import com.uniba.mining.utils.GUI;
import com.vp.plugin.ViewManager;

public class ClassDiagramHandler extends JDialog {

	private static final long serialVersionUID = 1L;
	private static final ViewManager viewManager = Application.getViewManager();

	public ClassDiagramHandler(String ACTION_NAME, ExportDialog expo) {
		JDialog dialog = new JDialog();
		JLabel label = new JLabel("Generating in progress...");
		dialog.add(label);
		dialog.setSize(200, 100);
		dialog.setLocationRelativeTo(null);
		//dialog.setUndecorated(true);
		dialog.setModal(true); // Imposta la finestra di dialogo come modale per bloccare l'input utente
		dialog.setTitle(ACTION_NAME);
		
		//GUI.addAll(dialog, label);
		//GUI.showInformationMessageDialog(dialog, ACTION_NAME, "Generating in progress...");
		
		
		SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
			@Override
			protected Boolean doInBackground() throws Exception {
				Path path = expo.getSelectedFile().toPath();
				String file = path.toString();
				DiagramCombinations generateDiagrams = new DiagramCombinations(file);
				return generateDiagrams.generateAllDiagramCombinations();
			}

			@Override
			protected void done() {
				try {
					boolean success = get();
					dialog.dispose(); 
					if (success) {
						GUI.showInformationMessageDialog(viewManager.getRootFrame(), ACTION_NAME, "Diagrams successfully generated.");
					} else {
						GUI.showErrorMessageDialog(viewManager.getRootFrame(), ACTION_NAME, "Error. Some problems in diagrams generations.");
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				} finally {
					//dialog.dispose(); // Chiudi la finestra di dialogo dopo l'elaborazione
				}
			}
		};

		// Esegui il worker
		worker.execute();

		// Visualizza la finestra di dialogo
		dialog.setVisible(true);
	}
}
