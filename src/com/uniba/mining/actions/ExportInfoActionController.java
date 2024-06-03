package com.uniba.mining.actions;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.ViewManager;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;
import com.uniba.mining.dialogs.ExportDialog;
import com.uniba.mining.dialogs.LanguageDialog;
import com.uniba.mining.dialogs.LanguageDialog.LanguageDiagramSelectionResult;
import com.uniba.mining.tasks.exportdiag.Language;
import com.uniba.mining.utils.Application;
import com.uniba.mining.utils.GUI;
import com.vp.plugin.model.*;
import com.uniba.mining.plugin.Config;

import java.io.File;
import java.util.ResourceBundle;


public class ExportInfoActionController implements VPActionController {

	private ResourceBundle messages;

	private static final ViewManager viewManager = Application.getViewManager();

	public void performAction(VPAction arg0) {

		// Utilizza la classe Language per ottenere l'oggetto messages
		this.messages = Language.getInstance().getMessages();

		LanguageDiagramSelectionResult selectionResult = new LanguageDialog(messages).showLanguageSelectionDialog();


		// If the user cancels the operation, return
		if (!selectionResult.isSelectionConfirmed()) {
			return;
		}

		// Retrieve the project
		IProject project = ApplicationManager.instance().getProjectManager().getProject();

		// Export Dialog
		ExportDialog expo = new ExportDialog();
		boolean next = expo.fileChooser(messages);

		if (next) {
			// Obtain the file path user selected

			File selectedFile = expo.getSelectedFile();

			// Add txt extension if the user hasn't specified it
			if (!selectedFile.getAbsolutePath().toLowerCase().endsWith(".txt")) {
				selectedFile = new File(selectedFile.getAbsolutePath() + ".txt");
			}

			// Export information based on the user's choice
			if(selectionResult.getDiagramType().exportInfo(project, selectedFile)) {

				// Print a message in Message Pane to tell the user the export was completed.
				ApplicationManager.instance().getViewManager().showMessage(messages.getString("plugin.output.export")
						+ selectedFile.getAbsolutePath());
				GUI.showInformationMessageDialog(viewManager.getRootFrame(), Config.EXPORT_INFO_ACTION, 
						Config.EXPORT_INFO_OK);
			}
			else
				GUI.showInformationMessageDialog(viewManager.getRootFrame(), Config.EXPORT_INFO_ACTION, 
						Config.EXPORT_INFO_ERROR);
				
		}

	}


	@Override
	public void update(VPAction arg0) {
		// TODO Auto-generated method stub

	}

}
