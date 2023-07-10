package com.uniba.mining.dialogs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.io.IOException;

import javax.swing.JPanel;

import com.uniba.mining.utils.GUI;
import com.vp.plugin.view.IDialog;
import com.vp.plugin.view.IDialogHandler;

public class RumDialogHandler implements IDialogHandler {
	private JPanel rootPanel;

	@Override
	public boolean canClosed() {
		return true;
	}

	@Override
	public Component getComponent() {
		rootPanel = new JPanel(new BorderLayout());
		// TO DO
		return rootPanel;
	}

	@Override
	public void prepare(IDialog dialog) {
		// percorso completo del jar RuM
		String command = "D:\\Programmi\\RuM\\rum-0.6.9.jar";
		// Esegue il comando sopra indicato
		Runtime run = Runtime.getRuntime();
		Process proc;
		try {
			proc = run.exec("java -jar " + command);
			proc.waitFor(); // attende che RuM sia chiuso
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		GUI.prepareDialog(dialog, "Titolo GUI");
	}

	@Override
	public void shown() {
		// Empty
	}
}
