package com.uniba.mining.feedback;

import java.awt.Color;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.uniba.mining.plugin.Config;

public class QueryButtons {
	
	private JButton addButton;
	private JButton improvementsButton;
	private JButton issuesButton;
	private JButton explainButton;
	private JTextField inputField;
	private String PLACEHOLDER;
	private final Color foreGroundColor = Color.BLUE;
	
	public QueryButtons(JTextField inputField, String placeholder) {
		this.inputField = inputField;
		this.PLACEHOLDER = placeholder;
		
		addButton = new JButton("Add Contents");
		addButton.setToolTipText(Config.FEEDBACK_BUTTON_ADD);
		addButton.setForeground(foreGroundColor);
		addButton.addActionListener(e -> handleButtonClick(Config.FEEDBACK_BUTTON_ADD));

		improvementsButton = new JButton("Improvements");
		improvementsButton.setToolTipText(Config.FEEDBACK_BUTTON_IMROVEMENT);
		improvementsButton.setForeground(foreGroundColor);
		improvementsButton.addActionListener(e -> handleButtonClick(Config.FEEDBACK_BUTTON_IMROVEMENT));

		issuesButton = new JButton("Issues");
		issuesButton.setToolTipText(Config.FEEDBACK_BUTTON_ISSUES);
		issuesButton.setForeground(foreGroundColor);
		issuesButton.addActionListener(e -> handleButtonClick(Config.FEEDBACK_BUTTON_ISSUES));

		explainButton = new JButton("Explain");
		explainButton.setToolTipText(Config.FEEDBACK_BUTTON_EXPLAIN);
		explainButton.setForeground(foreGroundColor);
		explainButton.addActionListener(e -> handleButtonClick(Config.FEEDBACK_BUTTON_EXPLAIN));
	}
	
	private void handleButtonClick(String text) {
		inputField.setText(text);
		inputField.postActionEvent();
		inputField.setText(PLACEHOLDER);
	}
	
	public JPanel addButtons(JPanel buttonPanel) {
		buttonPanel.add(addButton);
		buttonPanel.add(improvementsButton);
		buttonPanel.add(issuesButton);
		buttonPanel.add(explainButton);
		return buttonPanel;
	}
}