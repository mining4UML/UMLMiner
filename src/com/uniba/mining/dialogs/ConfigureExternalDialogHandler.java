package com.uniba.mining.dialogs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.uniba.mining.actions.ConfigureExternalActionController;
import com.uniba.mining.plugin.Config;
import com.uniba.mining.plugin.ExternalTool;
import com.uniba.mining.utils.GUI;
import com.vp.plugin.view.IDialog;
import com.vp.plugin.view.IDialogHandler;

public class ConfigureExternalDialogHandler implements IDialogHandler {
	private JPanel rootPanel;
	private IDialog dialog; // Riferimento alla finestra di dialogo

	private Component getHeaderPanel() {
		JPanel headerPanel = new JPanel();
		return headerPanel;
	}

	private Component getContentPanel() {
		JPanel contentPanel = new JPanel();
		List<Component> externalToolComponents = new ArrayList<>();

		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.PAGE_AXIS));
		for (ExternalTool externalTool : ExternalTool.values()) {
			String externalToolPath = Config.getExternalToolPath(externalTool);
			String externalToolIconPath = String.join("/", Config.ICONS_PATH,
					externalTool.getName().toLowerCase() + ".png");
			ImageIcon externalToolImageIcon = GUI.loadImage(externalToolIconPath, externalTool.getName() + " icon", 25,
					25);
			JLabel externalToolLabel = GUI.createLabel(externalTool.getName(), externalToolImageIcon);
			JTextField externalToolTextField = new JTextField(
					Objects.requireNonNullElse(externalToolPath, "No path selected"), 25);
			Box externalToolInputBox = new Box(BoxLayout.PAGE_AXIS);
			JButton externalToolSelectButton = new JButton("Select");
			Box externalToolBox = new Box(BoxLayout.LINE_AXIS);

			externalToolTextField.setEnabled(false);
			externalToolLabel.setLabelFor(externalToolTextField);
			externalToolLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
			externalToolTextField.setAlignmentX(Component.LEFT_ALIGNMENT);
			externalToolInputBox.setAlignmentY(Component.BOTTOM_ALIGNMENT);
			externalToolSelectButton.setAlignmentY(Component.BOTTOM_ALIGNMENT);

			// Modifica: Chiudere la finestra dopo la selezione del file
			externalToolSelectButton.addActionListener(e -> {
				JFileChooser fileChooser = GUI.createSelectFileChooser(
						String.format("Select %s path", externalTool.getName()),
						ExternalTool.getExternalToolExecutableFileFilter(),
						ExternalTool.getExternalToolBashFileFilter(), ExternalTool.getExternalToolJarFileFilter());

				if (fileChooser.showOpenDialog(rootPanel) == JFileChooser.APPROVE_OPTION) {
					String filePath = fileChooser.getSelectedFile().getAbsolutePath();
					Config.setExternalToolPath(externalTool, filePath);
					externalToolTextField.setText(filePath);
					
					// Mostra un messaggio di conferma
			        GUI.showInformationMessageDialog(rootPanel, "Configuration", "The path has been successfully set!");

					// Chiudi la finestra di dialogo dopo la selezione
					if (dialog != null) {
						dialog.close();
					}
				}
			});

			GUI.addAll(externalToolInputBox, GUI.DEFAULT_PADDING, externalToolLabel, externalToolTextField);
			GUI.addAll(externalToolBox, GUI.DEFAULT_PADDING, externalToolInputBox, externalToolSelectButton);
			externalToolComponents.add(externalToolBox);
		}

		GUI.addAll(contentPanel, GUI.HIGH_PADDING, externalToolComponents.toArray(Component[]::new));

		return contentPanel;
	}

	private Component getActionsPanel() {
		JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
		return actionsPanel;
	}

	@Override
	public boolean canClosed() {
		return true;
	}

	@Override
	public Component getComponent() {
		rootPanel = new JPanel(new BorderLayout());

		rootPanel.add(getHeaderPanel(), BorderLayout.PAGE_START);
		rootPanel.add(getContentPanel(), BorderLayout.CENTER);
		rootPanel.add(getActionsPanel(), BorderLayout.PAGE_END);
		return rootPanel;
	}

	@Override
	public void prepare(IDialog dialog) {
		this.dialog = dialog; // Salviamo il riferimento alla finestra di dialogo
		GUI.prepareDialog(dialog, ConfigureExternalActionController.ACTION_NAME);
	}

	@Override
	public void shown() {
		// Empty
	}
}