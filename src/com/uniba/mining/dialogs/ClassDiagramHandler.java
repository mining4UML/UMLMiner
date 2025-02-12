package com.uniba.mining.dialogs;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.file.Path;

import javax.swing.JButton;
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
	private SwingWorker<Boolean, Void> worker;

	public ClassDiagramHandler(String ACTION_NAME, ExportDialog expo) {
		JDialog dialog = new JDialog();
		JLabel label = new JLabel("Generating in progress...");
		JButton stopButton = new JButton("Stop");

		dialog.setLayout(new BorderLayout());
		dialog.add(label, BorderLayout.CENTER);
		dialog.add(stopButton, BorderLayout.SOUTH);
		dialog.setSize(250, 120);
		dialog.setLocationRelativeTo(null);
		dialog.setModal(true);
		dialog.setTitle(ACTION_NAME);

		worker = new SwingWorker<Boolean, Void>() {
			@Override
			protected Boolean doInBackground() throws Exception {
				Path path = expo.getSelectedFile().toPath();
				String file = path.toString();
				DiagramCombinations generateDiagrams = new DiagramCombinations(file, this);  // Passa il worker
				return generateDiagrams.generateAllDiagramCombinations();  
			}

			@Override
			protected void done() {
				try {
					if (isCancelled()) {
						System.out.println("Process was cancelled.");
						GUI.showInformationMessageDialog(viewManager.getRootFrame(), ACTION_NAME, "Process interrupted.");
						return;
					}

					boolean success = get();
					dialog.dispose(); 
					if (success) {
						GUI.showInformationMessageDialog(viewManager.getRootFrame(), ACTION_NAME, "Diagrams successfully generated.");
					} else {
						GUI.showErrorMessageDialog(viewManager.getRootFrame(), ACTION_NAME, "Error. Some problems in diagrams generations.");
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		};


		// Aggiunge il listener per il pulsante di stop
		stopButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (worker != null) {
					worker.cancel(true); // Interrompe il worker
					dialog.dispose(); // Chiude la finestra di dialogo
					GUI.showInformationMessageDialog(viewManager.getRootFrame(), ACTION_NAME, "Process interrupted.");
				}
			}
		});

		// Esegui il worker
		worker.execute();

		// Visualizza la finestra di dialogo
		dialog.setVisible(true);
	}
}
