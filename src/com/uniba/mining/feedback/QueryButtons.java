//package com.uniba.mining.feedback;
//
//import java.awt.Color;
//
//import javax.swing.JButton;
//import javax.swing.JPanel;
//import javax.swing.JTextField;
//
//import com.uniba.mining.plugin.Config;
//
//public class QueryButtons {
//	
//	private JButton addButton;
//	private JButton improvementsButton;
//	private JButton issuesButton;
//	private JButton explainButton;
//	private JTextField inputField;
//	private String PLACEHOLDER;
//	private final Color foreGroundColor = Color.BLUE;
//	
//	public QueryButtons(JTextField inputField, String placeholder) {
//		this.inputField = inputField;
//		this.PLACEHOLDER = placeholder;
//		
//		addButton = new JButton("Contents");
//		addButton.setToolTipText(Config.FEEDBACK_BUTTON_ADD);
//		addButton.setForeground(foreGroundColor);
//		addButton.addActionListener(e -> handleButtonClick(Config.FEEDBACK_BUTTON_ADD));
//
//		improvementsButton = new JButton("Improvements");
//		improvementsButton.setToolTipText(Config.FEEDBACK_BUTTON_IMROVEMENT);
//		improvementsButton.setForeground(foreGroundColor);
//		improvementsButton.addActionListener(e -> handleButtonClick(Config.FEEDBACK_BUTTON_IMROVEMENT));
//
//		issuesButton = new JButton("Issues");
//		issuesButton.setToolTipText(Config.FEEDBACK_BUTTON_ISSUES);
//		issuesButton.setForeground(foreGroundColor);
//		issuesButton.addActionListener(e -> handleButtonClick(Config.FEEDBACK_BUTTON_ISSUES));
//
//		explainButton = new JButton("Explain");
//		explainButton.setToolTipText(Config.FEEDBACK_BUTTON_EXPLAIN);
//		explainButton.setForeground(foreGroundColor);
//		explainButton.addActionListener(e -> handleButtonClick(Config.FEEDBACK_BUTTON_EXPLAIN));
//	}
//	
//	private void handleButtonClick(String text) {
//		inputField.setText(text);
//		inputField.postActionEvent();
//		inputField.setText(PLACEHOLDER);
//	}
//	
//	public JPanel addButtons(JPanel buttonPanel) {
//		buttonPanel.add(addButton);
//		buttonPanel.add(improvementsButton);
//		buttonPanel.add(issuesButton);
//		buttonPanel.add(explainButton);
//		return buttonPanel;
//	}
//}

package com.uniba.mining.feedback;

import java.awt.Color;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;

import com.uniba.mining.plugin.Config;

public class QueryButtons {

    private final Color foreGroundColor = Color.BLUE;

    private JTextField inputField;
    private String PLACEHOLDER;

    public QueryButtons(JTextField inputField, String placeholder) {
        this.inputField = inputField;
        this.PLACEHOLDER = placeholder;
    }

    private void handleButtonClick(String text) {
        inputField.setText(text);
        inputField.postActionEvent();
        inputField.setText(PLACEHOLDER);
    }

    public JPanel addButtons(JPanel buttonPanel) {

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

        JMenuItem modelingItem = new JMenuItem("Modeling Feedback");
        modelingItem.setToolTipText(Config.FEEDBACK_BUTTON_MODELING);
        modelingItem.addActionListener(e -> handleButtonClick(Config.FEEDBACK_BUTTON_MODELING));

        JMenuItem qualityItem = new JMenuItem("Design Quality Feedback");
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
}
