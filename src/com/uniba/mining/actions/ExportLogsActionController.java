package com.uniba.mining.actions;

import java.awt.Component;
import java.awt.Window;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;

import com.uniba.mining.logging.LogStreamer;
import com.uniba.mining.utils.Application;
import com.uniba.mining.utils.GUI;
import com.vp.plugin.ViewManager;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;
import com.uniba.mining.utils.FileSelectionUtil;

public class ExportLogsActionController implements VPActionController {
	private static final String SECOND_ACTION_NAME = "Select Export Folder";
	private static final String FIRST_ACTION_NAME = "Select Logs to Export";
	
	private static final ViewManager viewManager = Application.getViewManager();

	//    @Override
	//    public void performAction(VPAction vpAction) {
	//
	//        if (LogStreamer.countLogs() == 0) {
	//            GUI.showWarningMessageDialog(viewManager.getRootFrame(), ACTION_NAME, "No logs found.");
	//            return;
	//        }
	//
	//        JFileChooser fileChooser = GUI.createExportFileChooser(ACTION_NAME);
	//        if (fileChooser.showOpenDialog(viewManager.getRootFrame()) == JFileChooser.APPROVE_OPTION) {
	//            LogStreamer.exportLogs(fileChooser.getSelectedFile().toPath());
	//            GUI.showInformationMessageDialog(viewManager.getRootFrame(), ACTION_NAME, "Logs successfully exported.");
	//        }
	//
	//    }

	@Override
	public void performAction(VPAction vpAction) {
		if (LogStreamer.countLogs() == 0) {
			GUI.showWarningMessageDialog(viewManager.getRootFrame(), FIRST_ACTION_NAME, "No logs found.");
			return;
		}

		File logsDir = LogStreamer.getLogsDirectory().toFile();
		File[] logFiles = (logsDir.exists() && logsDir.isDirectory()) 
				? logsDir.listFiles(LogStreamer::isLogFile) 
						: new File[0];

		if (logFiles.length == 0) {
			GUI.showWarningMessageDialog(viewManager.getRootFrame(), FIRST_ACTION_NAME, "No log files found.");
			return;
		}

		// Converti Component in Window per evitare problemi di compatibilità
		Component parentComponent = viewManager.getRootFrame();
		Window parentWindow = (parentComponent instanceof Window) ? (Window) parentComponent : SwingUtilities.getWindowAncestor(parentComponent);

		// Usa la finestra come riferimento per il dialogo
		File[] selectedFiles = GUI.showFileSelectionDialog(parentWindow, logsDir, FIRST_ACTION_NAME);

		if (selectedFiles.length == 0) {
			GUI.showInformationMessageDialog(viewManager.getRootFrame(), FIRST_ACTION_NAME, "No files selected.");
			return;
		}
	
		
		JFileChooser directoryChooser = GUI.createExportFileChooser(SECOND_ACTION_NAME);       
		
		int userSelection = directoryChooser.showOpenDialog(viewManager.getRootFrame());
		if (userSelection == JFileChooser.APPROVE_OPTION) {
		    File selectedDirectory = directoryChooser.getSelectedFile();
		    // Ora selectedDirectory rappresenta la directory scelta
		    LogStreamer.exportLogs(selectedDirectory.toPath(), selectedFiles);
		    GUI.showInformationMessageDialog(viewManager.getRootFrame(), SECOND_ACTION_NAME, "Logs successfully exported.");
		} else {
		    GUI.showInformationMessageDialog(viewManager.getRootFrame(), SECOND_ACTION_NAME, "No directory selected.");
		}

	}





	@Override
	public void update(VPAction vpAction) {
		// Empty
	}



}
