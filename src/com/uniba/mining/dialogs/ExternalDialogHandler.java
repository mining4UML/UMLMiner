package com.uniba.mining.dialogs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.lang.ProcessHandle.Info;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.swing.JPanel;

import com.uniba.mining.plugin.Config;
import com.uniba.mining.plugin.ExternalTool;
import com.uniba.mining.utils.Application;
import com.uniba.mining.utils.GUI;
import com.vp.plugin.view.IDialog;
import com.vp.plugin.view.IDialogHandler;

public class ExternalDialogHandler implements IDialogHandler {
	private String externalToolName;
	private JPanel rootPanel;
	private IDialog dialog;

	public ExternalDialogHandler(String externalToolName) {
		this.externalToolName = externalToolName;
	}

	@Override
	public boolean canClosed() {
		return true;
	}

	@Override
	public Component getComponent() {
		rootPanel = new JPanel(new BorderLayout());
		return rootPanel;
	}

	@Override
	public void prepare(IDialog dialog) {
		this.dialog = dialog;
		GUI.prepareDialog(dialog, "Titolo GUI");
	}

	@Override
	public void shown() {
		ExternalTool externalTool = ExternalTool.getExternalTool(externalToolName);
		String externalToolPath = Config.getExternalToolPath(externalTool);
		String[] command = ExternalTool.getExecutionCommand(externalToolPath);
		ProcessBuilder processBuilder = new ProcessBuilder(command);

		try {
			File externalToolDirectory = Path.of(externalToolPath).getParent().toFile();
			Path externalToolLogPath = externalToolDirectory.toPath().resolve("ext.log");
			File externalToolLog = Files.exists(externalToolLogPath) ? externalToolLogPath.toFile()
					: Files.createFile(externalToolLogPath).toFile();
			processBuilder.directory(externalToolDirectory);
			processBuilder.redirectErrorStream(true);
			processBuilder.redirectOutput(externalToolLog);
			Process process = processBuilder.start();
			Info processInfo = process.toHandle().info();
			System.out.println("Command: " + processInfo.command());
			System.out.println("Arguments: " + processInfo.arguments());
			System.out.println("Command Line: " + processInfo.commandLine());
			process.waitFor();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			Thread.currentThread().interrupt();
		}
		Application.runDelayed(() -> dialog.close());
	}
}
