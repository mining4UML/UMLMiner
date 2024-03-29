package com.uniba.mining.dialogs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.event.HyperlinkEvent;

import com.uniba.mining.actions.AboutActionController;
import com.uniba.mining.plugin.Config;
import com.uniba.mining.utils.GUI;
import com.vp.plugin.view.IDialog;
import com.vp.plugin.view.IDialogHandler;

public class AboutDialogHandler implements IDialogHandler {
	private JTabbedPane rootPanel;

	//	private Component getHeaderPanel() {
	//		JPanel headerPanel = new JPanel();
	//
	//		return headerPanel;
	//	}

	private Component getInfoPanel() {
		JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
		JEditorPane infoEditorPane = new JEditorPane("text/html; charset=UTF-8", Config.PLUGIN_DESCRIPTION);
		Dimension infoTextAreaDimension = new Dimension(640, 100);

		infoEditorPane.setPreferredSize(infoTextAreaDimension);
		infoEditorPane.setOpaque(false);
		infoEditorPane.setEditable(false);
		infoEditorPane.addHyperlinkListener(e -> {
			if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
				try {
					Desktop.getDesktop().browse(e.getURL().toURI());
				} catch (IOException | URISyntaxException exception) {
					exception.printStackTrace();
				}
		});

		GUI.addAll(infoPanel, infoEditorPane);
		return infoPanel;
	}

	private Component getContactPanel() {
		JPanel contactPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
		JLabel homepageLabel = new JLabel("Homepage: ");
		JButton homepageButton = GUI.createLinkButton(Config.PLUGIN_HOMEPAGE, URI.create(Config.PLUGIN_HOMEPAGE));
		Box homepageBox = new Box(BoxLayout.LINE_AXIS);

		GUI.addAll(homepageBox, homepageLabel, homepageButton);
		contactPanel.setBorder(GUI.getDefaultTitledBorder("Contact Information"));
		contactPanel.add(homepageBox);
		return contactPanel;
	}

	private Component getContentPanel() {
		JPanel contentPanel = new JPanel();
		String logoImagePath = String.join("/", Config.IMAGES_PATH, "logo.jpg");
		ImageIcon logoImageIcon = GUI.loadImage(logoImagePath, "UML Miner Logo", 0.5f);
		JLabel logoLabel = new JLabel(Config.PLUGIN_VERSION, logoImageIcon, SwingConstants.CENTER);

		logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		logoLabel.setHorizontalTextPosition(SwingConstants.CENTER);
		logoLabel.setVerticalTextPosition(SwingConstants.BOTTOM);
		logoLabel.setForeground(Color.BLUE);
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.PAGE_AXIS));
		GUI.addAll(contentPanel, GUI.DEFAULT_PADDING, logoLabel, getInfoPanel(), getContactPanel(),
				getActionsPanel());
		return contentPanel;
	}


	private Component getTeamPanel() {
		
		JPanel teamPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));

		try {
			ClassLoader classLoader = getClass().getClassLoader();	    
			URL resource = classLoader.getResource(Config.PLUGIN_TEAM);

			JEditorPane infoTeamPane= new JEditorPane();

			infoTeamPane.setPage(resource);

			//JPanel teamPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
			//JEditorPane infoTeamPane = new JEditorPane("text/html; charset=UTF-8", Config.PLUGIN_TEAM);
			
			teamPanel.add(infoTeamPane, BorderLayout.CENTER);
			
			Dimension infoTextAreaDimension = new Dimension(640, 300);

			infoTeamPane.setPreferredSize(infoTextAreaDimension);
			infoTeamPane.setOpaque(false);
			infoTeamPane.setEditable(false);
			infoTeamPane.addHyperlinkListener(e -> {
				if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
					try {
						Desktop.getDesktop().browse(e.getURL().toURI());
					} catch (IOException | URISyntaxException exception) {
						exception.printStackTrace();
					}
			});

			teamPanel.setLayout(new BoxLayout(teamPanel, BoxLayout.PAGE_AXIS));
			GUI.addAll(teamPanel, GUI.DEFAULT_PADDING, 
					getContactPanel(),getActionsPanel());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return teamPanel;
	}

	private Component getActionsPanel() {
		JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
		JButton newsButton = GUI.createLinkButton("Check out the latest information of UML Miner",
				URI.create(Config.PLUGIN_HOMEPAGE));

		actionsPanel.add(newsButton);
		return actionsPanel;
	}


	private Component getLicensePanel() {
		JPanel licensePanel = new JPanel(new FlowLayout(FlowLayout.LEADING));

		try {

			ClassLoader classLoader = getClass().getClassLoader();	    
			URL resource = classLoader.getResource(Config.PLUGIN_LICENSEFILE);

			JEditorPane infoEditorPane= new JEditorPane();
			infoEditorPane.setPage(resource);

			JScrollPane editorScrollPane = new JScrollPane(infoEditorPane);
			editorScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

			JScrollPane scroolPane =new JScrollPane(infoEditorPane);

			scroolPane.setVisible(true);

			licensePanel.add(scroolPane, BorderLayout.CENTER);


			Dimension infoTextAreaDimension = new Dimension(640, 550);


			infoEditorPane.setPreferredSize(infoTextAreaDimension);
			infoEditorPane.setOpaque(false);
			infoEditorPane.setEditable(false);
			infoEditorPane.addHyperlinkListener(e -> {
				if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
					try {
						Desktop.getDesktop().browse(e.getURL().toURI());
					} catch (IOException | URISyntaxException exception) {
						exception.printStackTrace();
					}
			});

			licensePanel.setLayout(new BoxLayout(licensePanel, BoxLayout.PAGE_AXIS));
			GUI.addAll(licensePanel, GUI.DEFAULT_PADDING, scroolPane, 
					getContactPanel(),getActionsPanel());
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		return licensePanel;
	}


	@Override
	public boolean canClosed() {
		return true;
	}

	@Override
	public Component getComponent() {
		rootPanel = new JTabbedPane();

		rootPanel.addTab("About", getContentPanel());
		rootPanel.addTab("License", getLicensePanel());
		rootPanel.addTab("Team", getTeamPanel());

		return rootPanel;
	}

	@Override
	public void prepare(IDialog dialog) {
		GUI.prepareDialog(dialog, AboutActionController.ACTION_NAME);
	}

	@Override
	public void shown() {
		// Empty
	}

}
