package com.uniba.mining.actions;

import javax.swing.JFileChooser;

import com.uniba.mining.dialogs.ClassDiagramHandler;
import com.uniba.mining.dialogs.ExportDialog;
import com.uniba.mining.utils.Application;
import com.uniba.mining.utils.GUI;
import com.vp.plugin.ViewManager;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;

/**
 * 
 * Author: pasquale ardimento
 * Last version: 06 February 2024
 */
public class ClassDiagramActionControl implements VPActionController {

	public static final String ACTION_NAME = "Diagram classes generation";
	private static final ViewManager viewManager = Application.getViewManager();
	//private static String FILE_JSON="/Users/pasqualeardimento/Documents/GitHub/UMLMiner/assets/classi.json"; 

	@Override
	public void performAction(VPAction arg0) {
		// Chiamare il metodo per generare tutti i diagrammi

		JFileChooser fileChooser = GUI.createExportFileChooser("");

		// Export Dialog
		ExportDialog expo = new ExportDialog();
		boolean next = expo.fileChooser(ACTION_NAME,"json","JSON");

		if (next) {
			new ClassDiagramHandler(ACTION_NAME, expo);
		}
		else {
			
		}
			
	}

	@Override
	public void update(VPAction arg0) {
		// TODO Auto-generated method stub
	}
}