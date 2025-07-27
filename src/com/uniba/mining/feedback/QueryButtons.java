package com.uniba.mining.feedback;

import java.awt.Color;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;

import com.uniba.mining.plugin.Config;
import com.vp.plugin.diagram.IDiagramUIModel;

/**
 * Manages the creation and behavior of feedback-related buttons 
 * in the UML Miner interface for Visual Paradigm.
 * 
 * <p>This class provides two interactive button menus:
 * one for general diagram feedback (e.g., contents, improvements, issues, explanations),
 * and one for analysis feedback (e.g., modeling process and design quality).
 * 
 * <p>The buttons generate predefined prompts and inject them into a 
 * user input field, facilitating interaction with the underlying LLM-based feedback system.
 * 
 * <p>It supports conditional activation of the analysis feedback items depending 
 * on the diagram type, enabling a dynamic and context-aware interface.
 * 
 * @author pasqualeardimento
 */


public class QueryButtons {

	private final Color foreGroundColor = Color.BLUE;

	private JTextField inputField;
	private String PLACEHOLDER;
	private JMenuItem modelingItem;
	private JMenuItem qualityItem;

	public QueryButtons(JTextField inputField, String placeholder) {
		this.inputField = inputField;
		this.PLACEHOLDER = placeholder;
	}

	private void handleButtonClick(String text) {
		inputField.setText(text);
		inputField.postActionEvent();
		inputField.setText(PLACEHOLDER);
	}

	public JPanel addButtons(JPanel buttonPanel, IDiagramUIModel diagram) {
		// ------------------------ Menu 1: Diagram Feedback ------------------------
		JButton diagramButton = new JButton("Diagram Feedback ▾");
		diagramButton.setForeground(foreGroundColor);

		JPopupMenu diagramMenu = new JPopupMenu();

		JMenuItem contentsItem = new JMenuItem("Add Contents");
		contentsItem.setToolTipText(Config.FEEDBACK_BUTTON_ADD);
		contentsItem.addActionListener(e -> handleButtonClick(Config.FEEDBACK_BUTTON_ADD));

		JMenuItem improvementsItem = new JMenuItem("List Improvements");
		improvementsItem.setToolTipText(Config.FEEDBACK_BUTTON_IMROVEMENT);
		improvementsItem.addActionListener(e -> handleButtonClick(Config.FEEDBACK_BUTTON_IMROVEMENT));

		JMenuItem issuesItem = new JMenuItem("List Issues");
		issuesItem.setToolTipText(Config.FEEDBACK_BUTTON_ISSUES);
		issuesItem.addActionListener(e -> handleButtonClick(Config.FEEDBACK_BUTTON_ISSUES));

		JMenuItem explainItem = new JMenuItem("Explain Diagram");
		explainItem.setToolTipText(Config.FEEDBACK_BUTTON_EXPLAIN);
		explainItem.addActionListener(e -> handleButtonClick(Config.FEEDBACK_BUTTON_EXPLAIN));

		diagramMenu.add(contentsItem);
		diagramMenu.add(improvementsItem);
		diagramMenu.add(issuesItem);
		diagramMenu.add(explainItem);

		diagramButton.addActionListener(e -> {
			diagramMenu.show(diagramButton, 0, diagramButton.getHeight());
		});

		// ------------------------ Menu 2: Analysis Feedback ------------------------
		JButton analysisButton = new JButton("Analysis Feedback ▾");
		analysisButton.setForeground(foreGroundColor);

		JPopupMenu analysisMenu = new JPopupMenu();

		// ↪︎ Salvo nelle variabili di istanza
		modelingItem = new JMenuItem("Modeling Feedback");
		modelingItem.setToolTipText(Config.FEEDBACK_BUTTON_MODELING);
		modelingItem.addActionListener(e -> handleButtonClick(Config.FEEDBACK_BUTTON_MODELING));

		qualityItem = new JMenuItem("Design Quality Feedback");
		qualityItem.setToolTipText(Config.FEEDBACK_BUTTON_QUALITY);
		qualityItem.addActionListener(e -> handleButtonClick(Config.FEEDBACK_BUTTON_QUALITY));

		analysisMenu.add(modelingItem);
		analysisMenu.add(qualityItem);

		analysisButton.addActionListener(e -> {
			analysisMenu.show(analysisButton, 0, analysisButton.getHeight());
		});

		// Aggiunta dei due menu al pannello
		buttonPanel.add(diagramButton);
		buttonPanel.add(analysisButton);

		return buttonPanel;
	}

	/**
	 * Enables or disables the analysis feedback menu items based on the given flags.
	 * <p>
	 * This method is used to control the availability of the "Modeling Feedback"
	 * and "Design Quality Feedback" options in the "Analysis Feedback" menu.
	 * It should be called after the {@code addButtons} method has initialized the menu items.
	 *
	 * @param modelingEnabled {@code true} to enable the "Modeling Feedback" menu item; {@code false} to disable it.
	 * @param qualityEnabled {@code true} to enable the "Design Quality Feedback" menu item; {@code false} to disable it.
	 */
	public void customizeItems(boolean modelingEnabled, boolean qualityEnabled) {
		if (modelingItem != null) modelingItem.setEnabled(modelingEnabled);
		if (qualityItem != null) qualityItem.setEnabled(qualityEnabled);
	}


}
