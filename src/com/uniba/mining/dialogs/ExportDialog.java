package com.uniba.mining.dialogs;

import java.io.File;
import java.util.ResourceBundle;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.vp.plugin.ApplicationManager;

public class ExportDialog {

	private JFileChooser fileChooser= ApplicationManager.instance().getViewManager().createJFileChooser();


	public boolean fileChooser(ResourceBundle messages) {
		// Create File Chooser to let the user specify the output path
		//fileChooser = ApplicationManager.instance().getViewManager().createJFileChooser();
		fileChooser.setDialogTitle(messages.getString("plugin.title"));

		// Create a File Filter for TXT file type and set it as default
		FileFilter filter = new FileNameExtensionFilter("TXT File", "txt");
		fileChooser.addChoosableFileFilter(filter);
		fileChooser.setFileFilter(filter);

		// Show up the File Chooser dialog
		int returnVal = fileChooser.showSaveDialog(null);

		// If provided a file and press Save button
		if (returnVal != JFileChooser.APPROVE_OPTION) {
			return false; // User canceled the operation
		}
		return true;
	}

	//File selectedFile = fileChooser.getSelectedFile();
	public File getSelectedFile() {
		return fileChooser.getSelectedFile();
	}

}