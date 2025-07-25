package com.uniba.mining.dialogs;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.*;

import com.uniba.mining.utils.Application;
import com.uniba.mining.utils.GUI;
import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.model.IProject;
import com.uniba.mining.tasks.exportdiag.DiagramInfo;

import java.awt.*;
import java.awt.event.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ConnectException;

import com.uniba.mining.feedback.Conversation;
import com.uniba.mining.feedback.ConversationListCellRenderer;
import com.uniba.mining.feedback.ConversationsSerializer;
import com.uniba.mining.feedback.ErrorUtils;
import com.uniba.mining.feedback.LimitedTextField;
import com.uniba.mining.feedback.QueryButtons;
import com.uniba.mining.feedback.RequirementsTextArea;
import com.uniba.mining.llm.ParsedResponse;
import com.uniba.mining.llm.RequestHandler;
import com.uniba.mining.plugin.Config;
import com.uniba.mining.sdmetrics.RunSDMetrics;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.dom4j.Document;

/**
 * The {@code FeedbackHandler} class is responsible for managing
 * feedback-related operations within the application. It provides
 * functionalities to display feedback panels, handle feedback submissions, and
 * manage the state of feedback-related data.
 * <p>
 * This class follows the singleton design pattern to ensure a single instance
 * is used throughout the application.
 * </p>
 * 
 * <p>
 * <strong>Author:</strong> Pasquale Ardimento
 * </p>
 */
public class FeedbackHandler {
	private static final String panelId = "feedbackPanel";
	private static final String prefixAnswer = Config.FEEDBACK_PREFIX_ANSWER;
	private static final String title = Config.FEEDBACK_TITLE;
	private LimitedTextField inputField;
	private JLabel charCountLabel;
	private static final String suffixCountLabel = Config.FEEDBACK_SUFFIX_COUNT_LABEL;
	// panel with chat content
	private JTextPane outputPane;
	private StyledDocument document;
	private DefaultListModel<Conversation> conversationListModel;
	private JList<Conversation> conversationList;
	private JButton newChatButton;
	//private JLabel conversationLabel;
	private JTextField conversationTitleField;
	private RequirementsTextArea requirementsTextArea;
	private String projectId;
	private IDiagramUIModel diagram;
	// Counter to track the total number of conversations
	private int conversationCounter = 0;
	private static final String PLACEHOLDER = Config.DIALOG_FEEDBACK_MESSAGE_PLACEHOLDER;

	private static FeedbackHandler instance;

	private static JPanel panel;
	// Pulsanti per query predefinite
	private QueryButtons queryButtons;

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	private void setDiagram(IDiagramUIModel diagram) {
		this.diagram = diagram;
	}

	private IDiagramUIModel getDiagram() {
		return diagram;
	}

	public void clearPanel(String diagramId) {
		conversationListModel.removeAllElements();
		conversationList.clearSelection();
		conversationTitleField.setText(buildTitle(null, diagramId));
	}

	public void clearPanel() {
		conversationListModel.removeAllElements();
		conversationList.clearSelection();
	}

	private FeedbackHandler() {

		inputField = new LimitedTextField();
		inputField.setText(PLACEHOLDER);
		inputField.setBackground(Color.WHITE);
		inputField.setBorder(BorderFactory.createLineBorder(Color.BLUE));

		String text = String.format("%d / %d %s", inputField.getText().length(), LimitedTextField.LIMIT,
				suffixCountLabel);
		charCountLabel = new JLabel(text);

		// Aggiungi DocumentListener tramite metodo privato
		addDocumentListener(inputField, charCountLabel);

		outputPane = new JTextPane();
		outputPane.setEditable(false);
		outputPane.setPreferredSize(new Dimension(400, 200));
		// white color
		outputPane.setBackground(new Color(255, 255, 255));
		inputField.setPreferredSize(new Dimension (outputPane.getWidth(),inputField.getPreferredSize().height));
		document = outputPane.getStyledDocument();

		conversationListModel = new DefaultListModel<>();

		conversationList = new JList<>(conversationListModel);
		conversationList.setBackground(new Color(225, 235, 245)); // Stesso colore LIGHT_BLUE
		conversationList.setSelectionBackground(new Color(100, 150, 230)); // Stesso colore DARK_BLUE
		conversationList.setSelectionForeground(Color.WHITE); // Testo bianco per la selezione


		conversationTitleField = new JTextField();
		conversationTitleField.setEditable(false);
		conversationTitleField.setHorizontalAlignment(SwingConstants.CENTER); // Centra il testo dell'etichetta
		//conversationTitleField.setForeground(new Color(34, 139, 34)); // Verde scuro
		conversationTitleField.setFont(conversationTitleField.getFont().deriveFont(Font.BOLD)); // Imposta l'etichetta in grassetto
		// Imposta il colore di sfondo della casella di testo a quello del panel
		conversationTitleField.setBackground(UIManager.getColor("Panel.background"));
		//conversationTitleField.setOpaque(true); // Necessario per garantire che il colore di sfondo sia visibile  

		// elimina il bordo
		conversationTitleField.setBorder(null);

		newChatButton = new JButton("New Chat");
		// Imposta il colore del testo a bianco
		//newChatButton.setForeground(Color.WHITE);
		// Imposta il colore di sfondo come quello usato in Eclipse per i pulsanti attivi
		//Color eclipseButtonBackground = UIManager.getColor("Button.select");
		//newChatButton.setBackground(eclipseButtonBackground);
		// Assicurati che il colore di sfondo sia visibile
		//newChatButton.setFocusPainted(false);
		//newChatButton.setBorderPainted(false);
		//newChatButton.setOpaque(false);
		// Imposta le dimensioni preferite del pulsante
		// Rimuovi il bordo del pulsante e il contenuto del testo
		newChatButton.setBorderPainted(false);
		newChatButton.setContentAreaFilled(false);
		newChatButton.setFocusPainted(false);
		Dimension buttonSize = new Dimension(24, 24);
		ImageIcon icon = GUI.getImageIcon("chat-add-icon.png");
		newChatButton.setIcon(icon);
		newChatButton.setPreferredSize(buttonSize);
		newChatButton.setToolTipText("Start a new feedback conversation for this diagram");

		conversationList.setCellRenderer(new ConversationListCellRenderer());

		// Crea l'etichetta
		//		conversationLabel = new JLabel("Conversation List");
		//		conversationLabel.setHorizontalAlignment(SwingConstants.CENTER); // Centra il testo dell'etichetta
		//		conversationLabel.setForeground(new Color(34, 139, 34)); // Verde scuro
		//		conversationLabel.setFont(conversationLabel.getFont().deriveFont(Font.BOLD)); // Imposta l'etichetta in
		// grassetto

		inputField.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				// aggiorna il pannello di feedback solo se il diagramma è cambiato
				if (!Application.getDiagram().equals(getDiagram())) {
					showFeedbackPanel(Application.getDiagram());
				}
				if (inputField.getText().equals(PLACEHOLDER)) {
					inputField.setText("");
				}
				requirementsTextArea.printReqFound(getDiagram());
			}

			@Override
			public void focusLost(FocusEvent e) {
				if (inputField.getText().isEmpty()) {
					inputField.setText(PLACEHOLDER);
				}
			}
		});

		// Modifica il listener dell'inputField
		setInputFieldListener();

		newChatButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					if (conversationListModel.size() < 2) {
						// Mostra il pannello di feedback solo se la dimensione è minore di 2
						showFeedbackPanel(Application.getDiagram());
					} else
						requirementsTextArea.printReqFound(getDiagram());
					createNewChat();
				} catch (Exception e1) {
					ErrorUtils.showDetailedErrorMessage(e1);
				}
			}
		});



		conversationList.setCellRenderer(new ConversationListCellRenderer());

		conversationList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				JList<Conversation> list = (JList<Conversation>) e.getSource();
				int index = list.locationToIndex(e.getPoint());
				if (index > -1) {
					Rectangle cellBounds = list.getCellBounds(index, index);
					Point pointWithinCell = new Point(e.getX() - cellBounds.x, e.getY() - cellBounds.y);

					ConversationListCellRenderer renderer = (ConversationListCellRenderer) list.getCellRenderer();
					Component component = renderer.getListCellRendererComponent(
							list, 
							list.getModel().getElementAt(index), 
							index, 
							list.isSelectedIndex(index), 
							list.hasFocus()
							);

					if (component instanceof JPanel) {
						JPanel panel = (JPanel) component;
						Component iconComponent = panel.getComponent(1); // L'icona dovrebbe essere il secondo componente
						if (iconComponent.getBounds().contains(pointWithinCell) && SwingUtilities.isLeftMouseButton(e)) {
							// L'icona è stata cliccata con il tasto sinistro del mouse
							list.setSelectedIndex(index);
							showPopupMenu(e);
						}
					}
				}
			}
		});


		//initQueryButtons();
		queryButtons = new QueryButtons(inputField,PLACEHOLDER);

		//initRequirements();
		requirementsTextArea = new RequirementsTextArea(getDiagram()); 


		addFocusListenerToOutputPane();

		JPopupMenu popupMenu = new JPopupMenu();
		JMenuItem copyItem = new JMenuItem("Copy");
		// JMenuItem clearItem = new JMenuItem("Clear");
		copyItem.addActionListener(e -> outputPane.copy());
		// clearItem.addActionListener(e -> outputPane.setText(""));
		popupMenu.add(copyItem);
		// popupMenu.add(clearItem);
		outputPane.setComponentPopupMenu(popupMenu);

	}


	private static void addDocumentListener(JTextField textField, JLabel label) {
		textField.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				updateCharCount();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				updateCharCount();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				updateCharCount();
			}

			private void updateCharCount() {
				String text = String.format("%d / %d %s", textField.getText().length(), LimitedTextField.LIMIT,
						suffixCountLabel);
				label.setText(text);

				label.setText(text);
			}
		});
	}

	public void handleButtonClick(String text) {
		inputField.setText(text);
		inputField.postActionEvent();
		inputField.setText(PLACEHOLDER);
	}

	private void setInputFieldListener() {
		inputField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!inputField.getText().isEmpty()) {
					try {
						// Verifica se il progetto è vuoto; se sì, viene lanciata un'eccezione con un
						// messaggio che indica l'assenza di diagrammi
						DiagramInfo.isProjectEmpty(Application.getProject());
						System.out.println(Application.getProject().getName());
						// Ottieni l'ID della sessione dalla conversazione selezionata
						Conversation selectedConversation = conversationList.getSelectedValue();

						String sessionId = selectedConversation != null ? selectedConversation.getSessionId()
								: generateSessionId();

						// Chiama processUserInput() passando sessionId e input
						try {
							processUserInput(sessionId);
						} catch (Exception processInputException) {
							ErrorUtils.showDetailedErrorMessage(processInputException);
						}
					} catch (Exception projectInputException) {
						ErrorUtils.showDetailedErrorMessage(projectInputException);
					}
				}
			}
		});
	}

	private void addFocusListenerToOutputPane() {
	    ResourceBundle messages = DiagramInfo.getMessages();

	    outputPane.addFocusListener(new FocusListener() {

	        @Override
	        public void focusGained(FocusEvent e) {
	            IDiagramUIModel currentDiagram = Application.getDiagram();

	            if (currentDiagram == null) {
	                // Mostra un messaggio utente o logga senza lanciare eccezione
	                System.err.println(messages.getString("diagram.project.nodiagram") + "\n"
	                        + messages.getString("feedback.problem"));
	                Application.getViewManager().removeMessagePaneComponent(panelId);
	                return;
	            }

	            // Verifica se il diagramma attuale è diverso da quello già caricato
	            if (diagram == null || !currentDiagram.equals(getDiagram())) {
	                showFeedbackPanel(currentDiagram);
	            } else {
	                requirementsTextArea.printReqFound(getDiagram());
	            }
	        }

	        @Override
	        public void focusLost(FocusEvent e) {
	            // Nessuna azione richiesta
	        }
	    });
	}


	private void processUserInput(String sessionId) throws ConnectException, IOException, Exception {
		// Acquisisco il testo dall'inputField
		String inputText = inputField.getText();

		updateConversation(inputText, sessionId);
		inputField.setText("");
	}

	/**
	 * Handles the feedback process by sending a request to the server and obtaining a response.
	 *
	 * <p>This method creates an instance of {@link RequestHandler} with the provided conversation 
	 * details and the current project ID. It then calls the {@link RequestHandler#sendRequestAndGetResponse()}
	 * method to perform the request and retrieve the response from the server. If an exception occurs 
	 * during this process, it is caught and a detailed error message is displayed using 
	 * {@link ErrorUtils#showDetailedErrorMessage(Exception)}.
	 *
	 * @param conversation the {@link Conversation} object containing the details of the current conversation,
	 *                     including session ID, diagram ID, query ID, diagram text, and query text.
	 * @return the response from the server as a {@link String}, or {@code null} if an exception occurs.
	 * @throws IOException if an I/O error occurs during the request process.
	 */

	public String handleFeedback(Conversation conversation) {
		try {
			RequestHandler requestHandler = new RequestHandler(projectId, conversation);
			ParsedResponse risposta = requestHandler.sendRequestAndGetResponse();
			//return requestHandler.sendRequestAndGetResponse().getAnswer();
			return risposta.getAnswer();
		} catch (IOException e) {
			ErrorUtils.showDetailedErrorMessage(e);
		}
		return null;
	}

	//	private void updateConversation(String inputText, String sessionId)
	//			throws ConnectException, IOException, Exception {
	//
	//		String diagramAsText = DiagramInfo.exportInformation(Application.getProject(), "en", diagram);
	//		org.dom4j.Document diagramAsXML = DiagramInfo.exportAsXML(diagram);
	//		
	//		// CANCELLARE SDMETRICS calculateMetrics)
	//		try {
	//			RunSDMetrics.calculateMetrics(getDiagram());
	//		} catch (IOException e) {
	//			// TODO Auto-generated catch block
	//			e.printStackTrace();
	//		}
	//		// FINE CANCELLARE
	//		
	//		boolean empty = false;
	//
	//		if (!inputText.isEmpty()) {
	//			// Aggiungi il testo alla conversazione corrente solo se non è vuoto
	//			if (conversationListModel.isEmpty()) {
	//				Conversation newConversation = createNewConversation(sessionId, inputText, 
	//						diagramAsText, diagramAsXML);
	//				conversationListModel.addElement(newConversation);
	//				conversationList.setSelectedValue(newConversation, true);
	//				empty = true;
	//			}
	//
	//			Conversation currentConversation = conversationList.getSelectedValue();
	//
	//			if (currentConversation != null) {
	//				// currentConversation.appendMessage(answer + "\n" + response);
	//
	//				if (!empty) {
	//					// add text description of diagrams
	//					currentConversation.setDiagramAsText(diagramAsText);
	//					// add query
	//					currentConversation.setQuery(inputText);
	//					// add xml description of diagrams
	//					currentConversation.setDiagramAsXML(diagramAsXML);
	//				}
	//
	//				String answer = prefixAnswer + inputText;
	//				currentConversation.appendMessage(answer, true);
	//
	//				String response = handleFeedback(currentConversation);
	//				appendToPane(answer);
	//				if (response != null) {
	//					response = response.replaceFirst("^[\\r\\n>-]+", "");
	//					appendToPane(response);
	//				}
	//				else
	//					throw new Exception("no response from the server");
	//				// currentConversation.appendMessage(answer);
	//				currentConversation.appendMessage(response, false);
	//				// currentConversation.setResponse(response);
	//				conversationListModel.set(conversationList.getSelectedIndex(), currentConversation);
	//				conversationTitleField.setText(buildTitle(getDiagramTitle(), currentConversation.getTitle()));
	//
	//			}
	//
	//			// serializeConversations();
	//			String diagramId = Application.getIDCurrentDiagram();
	//			serializeConversations(diagramId);
	//
	//			conversationList.revalidate();
	//			conversationList.repaint();
	//		}
	//	}

	private void updateConversation(String inputText, String sessionId)
			throws ConnectException, IOException, Exception {

		String diagramAsText = DiagramInfo.exportInformation(Application.getProject(), "en", diagram);
		org.dom4j.Document diagramAsXML = DiagramInfo.exportAsXML(diagram);

		boolean newConvCreated = false;

		if (!inputText.isEmpty()) {
			if (conversationListModel.isEmpty() || conversationList.getSelectedValue() == null) {
				if (sessionId == null) sessionId = generateSessionId();
				Conversation newConversation = createNewConversation(sessionId, inputText, diagramAsText, diagramAsXML);
				conversationListModel.addElement(newConversation);
				conversationList.setSelectedValue(newConversation, true);
				newConvCreated = true;
			}

			Conversation currentConversation = conversationList.getSelectedValue();

			if (currentConversation != null) {
				if (!newConvCreated) {
					currentConversation.setProjectId(projectId);
					if(currentConversation.getSessionId().equals(""))
						currentConversation.setSessionId(generateSessionId());
					if(currentConversation.getQueryId().equals("0"))
						currentConversation.setQueryId(prefixAnswer);
					currentConversation.setDiagramAsText(diagramAsText);
					currentConversation.setDiagramId(getDiagram().getId());
					currentConversation.setDiagramAsXML(diagramAsXML);
					currentConversation.setQuery(inputText);
					setRequirementsIfPresent(currentConversation);
				}

				String queryText = inputText;
				if (inputText.toLowerCase().contains("modeling feedback")) {
					queryText += "\n\nViolations detected by RUM:\n";
				} else if (inputText.toLowerCase().contains(Config.FEEDBACK_BUTTON_QUALITY.toLowerCase())) {
					String metricsSummary = RunSDMetrics.readSdmetricsOutput(getDiagram());
					currentConversation.setQuery(Config.QUALITYPROMPT);
					currentConversation.setMetrics(metricsSummary);
					queryText = Config.QUALITYPROMPT;
				}

				String answer = prefixAnswer + queryText;
				currentConversation.appendMessage(answer, true);

				String response = handleFeedback(currentConversation);
				appendToPane(answer);
				if (response != null) {
					response = response.replaceFirst("^[\\r\\n>-]+", "");
					appendToPane(response);
				} else {
					throw new Exception("No response from the server.");
				}

				currentConversation.appendMessage(response, false);
				conversationListModel.set(conversationList.getSelectedIndex(), currentConversation);
				conversationTitleField.setText(buildTitle(getDiagram(), currentConversation.getTitle()));
			}

			serializeConversations(Application.getIDCurrentDiagram());
			conversationList.revalidate();
			conversationList.repaint();
		}
	}



	private String getDiagramTitle() {
		IDiagramUIModel diagram = Application.getDiagram();
		if (diagram != null && diagram.getName() != null) {
			return diagram.getName() + " - ";
		} else if (diagram != null) {
			return diagram.getType() + " ";
		} else
			return "";
	}

	private String generateSessionId() {
		// Incrementa il contatore delle conversazioni di 1
		conversationCounter++;
		// Restituisci l'ID della sessione corrispondente al numero totale di
		// conversazioni
		return String.valueOf(conversationCounter);
	}

	private void serializeConversations(String diagramId) {
		List<Conversation> conversations = new ArrayList<>();
		for (int i = 0; i < conversationListModel.size(); i++) {
			conversations.add(conversationListModel.getElementAt(i));
		}
		// Serializza l'intera lista di conversazioni
		ConversationsSerializer.serializeConversations(conversations, diagramId);
	}


	private void setRequirementsIfPresent(Conversation conversation) {
		String requirementsText = requirementsTextArea.getRequirementsTextArea().getText();
		if (requirementsText != null && !requirementsText.trim().equals(Config.DIAGRAM_ABSENT_REQUIREMENT)) {
			conversation.setRequirements(requirementsText);
		}
	}


	private Conversation createNewConversation(String sessionId, String query, 
			String diagramAsText, org.dom4j.Document diagramAsXML) {

		// Utilizzo del costruttore con i parametri
		Conversation newConversation = 
				new Conversation(sessionId, 
						projectId, getDiagram().getId(), 
						diagramAsText,
						diagramAsXML,
						query, prefixAnswer);

		setRequirementsIfPresent(newConversation); 

		newConversation.appendMessage(outputPane.getText(), false);
		System.out.println(newConversation.toString());
		return newConversation;
	}

	/**
	 * Displays a popup menu with options to rename, delete, or export a
	 * conversation when a mouse event occurs. The popup menu appears at the
	 * location of the mouse event.
	 *
	 * @param e the MouseEvent that triggered the popup menu
	 */
	private void showPopupMenu(MouseEvent e) {
		JPopupMenu popupMenu = new JPopupMenu();

		JMenuItem renameItem = new JMenuItem("Rename...");
		renameItem.setIcon(GUI.getImageIcon("ellipsis_icon.png"));

		JMenuItem deleteItem = new JMenuItem("Delete");
		deleteItem.setIcon(GUI.getImageIcon("delete.png"));

		JMenuItem exportItem = new JMenuItem("Export as TxT");
		exportItem.setIcon(GUI.getImageIcon("txt.png"));

		renameItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				renameConversation();
			}
		});
		deleteItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				deleteConversation();
			}
		});

		exportItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				exportConversationAsText();
			}
		});

		popupMenu.add(renameItem);
		popupMenu.add(deleteItem);
		popupMenu.add(exportItem);
		popupMenu.show(conversationList, e.getX(), e.getY());
	}

	private void exportConversationAsText() {
		int selectedIndex = conversationList.getSelectedIndex();
		if (selectedIndex != -1) { // Verifica se è stata selezionata una conversazione
			Conversation selectedConversation = conversationList.getSelectedValue();
			if (selectedConversation != null) {
				JFileChooser fileChooser = new JFileChooser();
				int option = fileChooser.showSaveDialog(null);
				if (option == JFileChooser.APPROVE_OPTION) {
					try (BufferedWriter writer = new BufferedWriter(
							new FileWriter(fileChooser.getSelectedFile() + ".txt"))) {
						writer.write(selectedConversation.toString()); // Supponiamo che toString() restituisca il testo
						// della conversazione
						JOptionPane.showMessageDialog(null, "Conversation exported successfully!");
					} catch (IOException ex) {
						JOptionPane.showMessageDialog(null, "Error exporting conversation: " + ex.getMessage());
					}
				}
			} else {
				JOptionPane.showMessageDialog(null, "No conversation selected!");
			}
		} else {
			JOptionPane.showMessageDialog(null, "No conversation selected!");
		}
	}

	// Metodo per esportare tutte le conversazioni come testo
	private void exportAllConversationsAsText() {
		Conversation selectedConversation = conversationList.getSelectedValue();
		if (selectedConversation != null) {
			JFileChooser fileChooser = new JFileChooser();
			int option = fileChooser.showSaveDialog(null);
			if (option == JFileChooser.APPROVE_OPTION) {
				try (BufferedWriter writer = new BufferedWriter(
						new FileWriter(fileChooser.getSelectedFile() + ".txt"))) {
					writer.write(selectedConversation.toString()); // Supponiamo che toString() restituisca il testo
					// della conversazione
					JOptionPane.showMessageDialog(null, "Conversation exported successfully!");
				} catch (IOException ex) {
					JOptionPane.showMessageDialog(null, "Error exporting conversation: " + ex.getMessage());
				}
			}
		} else {
			JOptionPane.showMessageDialog(null, "No conversation selected!");
		}
	}

	// Metodo per eliminare la conversazione selezionata
	private void deleteConversation() {
		int selectedIndex = conversationList.getSelectedIndex();
		if (selectedIndex != -1) { // Verifica se è stata selezionata una conversazione
			// Conversation selectedConversation = conversationList.getSelectedValue();

			int choice = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete the conversation?",
					"Delete Conversation", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, GUI.getImageIcon());

			// Se l'utente conferma l'eliminazione, procedi con la cancellazione
			if (choice == JOptionPane.YES_OPTION) {
				conversationListModel.remove(selectedIndex); // Rimuovi la conversazione dal modello dei dati
				// serializeConversations();
				serializeConversations(Application.getDiagram().getId());

				// Seleziona un'altra conversazione dopo l'eliminazione
				int conversationCount = conversationListModel.getSize();
				if (conversationCount > 0) {
					// Seleziona la prima conversazione dopo l'eliminazione
					conversationList.setSelectedIndex(0);
				}

				conversationList.revalidate();
				conversationList.repaint();
			}

		}
	}

	private void renameConversation() {
		Conversation selectedConversation = conversationList.getSelectedValue();
		if (selectedConversation != null) {

			// Mostra una finestra di dialogo di input con un'icona personalizzata
			String newTitle = (String) JOptionPane.showInputDialog(null, "Enter new conversation title:",
					"Conversation title", JOptionPane.PLAIN_MESSAGE, GUI.getImageIcon(), null, null);

			if (newTitle != null && !newTitle.isEmpty()) {
				selectedConversation.setTitle(newTitle);
				// Aggiorna il testo nella casella di testo del titolo della conversazione
				//conversationTitleField.setText(buildTitle(getDiagramTitle(), newTitle));
				conversationTitleField.setText(buildTitle(getDiagram(),newTitle));
				// Aggiorna la visualizzazione della lista delle conversazioni
				conversationListModel.setElementAt(selectedConversation, conversationList.getSelectedIndex());
				// serializeConversations();
				serializeConversations(Application.getDiagram().getId());
			}
		}
	}

	public static FeedbackHandler getInstance() {
		if (instance == null) {
			instance = new FeedbackHandler();
		}
		return instance;
	}

	//	public void showFeedbackPanel(IProject project) {
	//		setProjectId(Application.getProject().getId());
	//
	//		if (panel == null)
	//			createPanel();
	//		else {
	//			// after opened
	//			clearPanel();
	//			loadSerializedConversations();
	//		}
	//		Application.getViewManager().showMessagePaneComponent(id, title, panel);
	//	}

	public static boolean toBeClosed(IProject project) {
		boolean toBeClosed = false;
		if (project.toDiagramArray().length == 0)
			toBeClosed = true;
		return toBeClosed;
	}

	public static void closeFeedBackPanel() {
		Application.getViewManager().removeMessagePaneComponent(panelId);
	}

	public void showFeedbackPanel(IDiagramUIModel diagramUIModel) {
		setProjectId(Application.getProject().getId());
		setDiagram(diagramUIModel);

		if (getDiagram() == null) {
			 // Chiudi il pannello di feedback
			Application.getViewManager().removeMessagePaneComponent(panelId);
		    return;
		}

		if (panel == null) {
			createPanel();
		} else {
			clearPanel(getDiagram().getId());
			// Personalizzazione dei bottoni qui
			if (queryButtons != null) {
				boolean isClassDiagram = getDiagram().getType().equals("ClassDiagram");
				queryButtons.customizeItems(true, isClassDiagram);
			}
			loadSerializedConversations(getDiagram().getId());
		}

		Application.getViewManager().showMessagePaneComponent(panelId, title, panel);
		forceUpdateView();
		requirementsTextArea.printReqFound(getDiagram());
	}

	/**
	 * Forces the update of the panel's view. This method ensures that any changes
	 * made to the panel's components or layout are reflected immediately.
	 * <p>
	 * It achieves this by calling {@code repaint()} and {@code revalidate()} on the
	 * panel, which triggers a re-rendering and layout validation.
	 * </p>
	 */
	private void forceUpdateView() {
		panel.repaint();
		panel.revalidate();
	}


	/*
	 * // Azzera i contenuti visivi
inputField.setText("");
outputPane.setText("");
	 */
	//	private void createNewChat() throws Exception {
	//		// Ottieni il testo dalla JTextPane
	//		String query = outputPane.getText();
	//
	//		// Controlla se l'inputText non è vuoto o uguale al messaggio di feedback
	//		// predefinito
	//		if (!query.isEmpty() && !query.equals(PLACEHOLDER)) {
	//			// i valori sessionId, projectId, etc.
	//			String sessionId = generateSessionId();
	//			String diagramAsText = DiagramInfo.exportInformation(Application.getProject(), "en", diagram);
	//			Document diagramAsXML = DiagramInfo.exportAsXML(getDiagram());
	//			// Crea una nuova istanza di Conversation
	//			Conversation newConversation = new Conversation(sessionId, projectId, getDiagram().getId(), 
	//					diagramAsText, diagramAsXML,
	//					query, prefixAnswer);
	//
	//			// Aggiungi la nuova istanza di Conversation alla lista delle conversazioni
	//			conversationListModel.addElement(newConversation);
	//
	//			// Aggiorna la visualizzazione della GUI
	//			conversationList.revalidate();
	//			conversationList.repaint();
	//
	//			// Imposta la nuova istanza di Conversation come selezionata nella lista delle
	//			// conversazioni
	//			conversationList.setSelectedValue(newConversation, true);
	//			System.out.println(
	//					"\n\ndimensione della lista delle conversazioni:" + conversationList.getModel().getSize() + "\n\n");
	//
	//			// Azzera il contenuto della JTextPane
	//			outputPane.setText("");
	//
	//		}
	//		else
	//			inputField.requestFocusInWindow();
	//	}

	private void createNewChat() throws Exception {
		inputField.setText("");
		outputPane.setText("");
		// Ottieni il testo dalla JTextPane
		String query = outputPane.getText();

		//String sessionId = generateSessionId();
		//String diagramAsText = DiagramInfo.exportInformation(Application.getProject(), "en", diagram);
		//Document diagramAsXML = DiagramInfo.exportAsXML(getDiagram());
		// Crea una nuova istanza di Conversation
		Conversation newConversation = new Conversation();
		/*Conversation newConversation = new Conversation(sessionId, projectId, getDiagram().getId(), 
				diagramAsText, diagramAsXML,
				query, prefixAnswer);*/

		// Aggiungi la nuova istanza di Conversation alla lista delle conversazioni
		conversationListModel.addElement(newConversation);

		// Aggiorna la visualizzazione della GUI
		conversationList.revalidate();
		conversationList.repaint();

		// Imposta la nuova istanza di Conversation come selezionata nella lista delle
		// conversazioni
		conversationList.setSelectedValue(newConversation, true);
		System.out.println(
				"\n\ndimensione della lista delle conversazioni:" + conversationList.getModel().getSize() + "\n\n");

		// Azzera il contenuto della JTextPane
		outputPane.setText("");

		//}
		//else
		inputField.requestFocusInWindow();
	}

	private static JLabel createHelpLabel() {
		JLabel helpLabel = new JLabel("?");
		helpLabel.setFont(new Font("Arial", Font.BOLD, 14));
		helpLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

		// Impostazione del tooltip con il testo di aiuto
		helpLabel.setToolTipText("<html><body style='width: 200px;'>"
				+ "Use these buttons or type your own question to get information about the diagram:<br><br>"
				+ "<b>Add</b>: Provide suggestions for adding processes or contents to this diagram.<br>"
				+ "<b>Improvements</b>: List suggestions to improve this diagram.<br>"
				+ "<b>Issues</b>: List issues in this diagram.<br>"
				+ "<b>Explain</b>: Explain this diagram.</body></html>");

		return helpLabel;
	}

	private JPanel createPanel() {
		panel = new JPanel(new BorderLayout());

		// Pannello principale diviso in due parti: sinistra e destra
		JPanel mainPanel = new JPanel(new BorderLayout());

		//		newChatButton.addActionListener(e -> {
		//			try {
		//				createNewChat();
		//			} catch (Exception e1) {
		//				GUI.showErrorMessageDialog(Application.getViewManager().getRootFrame(), "Feedback", e1.getMessage());
		//			}
		//		});

		// Pannello sinistro contiene il pulsante "New Chat" e la lista delle
		// conversazioni
		JPanel leftPanel = new JPanel(new BorderLayout());
		leftPanel.add(newChatButton, BorderLayout.NORTH);
		JScrollPane listScrollPane = new JScrollPane(conversationList);
		listScrollPane.setPreferredSize(new Dimension(200, 200));
		leftPanel.add(listScrollPane, BorderLayout.CENTER);
		//leftPanel.add(conversationLabel, BorderLayout.NORTH);

		mainPanel.add(leftPanel, BorderLayout.WEST);

		// Right panel contains the conversationTitleField, outputPane, and inputField
		JPanel centralPanel = new JPanel(new BorderLayout());

		// Panel to hold conversationTitleField
		JPanel northPanel = new JPanel(new BorderLayout());
		northPanel.add(conversationTitleField, BorderLayout.CENTER);
		//northPanel.add(requirementsTextArea.getPreviewRequirements(), BorderLayout.EAST);

		// Imposta la dimensione preferita del northPanel a 24 pixel di altezza
		northPanel.setPreferredSize(new Dimension(northPanel.getPreferredSize().width, 24));


		// Add the north panel to the right panel
		centralPanel.add(northPanel, BorderLayout.NORTH);

		// Add requirementsTextArea to the east region in a JScrollPane
		//rightPanel = requirementsTextArea.addRTextArea(rightPanel);

		// Add outputPane in a JScrollPane to the center region
		centralPanel.add(new JScrollPane(outputPane), BorderLayout.CENTER);

		// Crea un nuovo pannello per inputField e buttonPanel con GridBagLayout
		JPanel inputAndButtonPanel = new JPanel(new GridBagLayout());

		// Vincoli per buttonPanel (sopra)
		GridBagConstraints gbcButtonPanel = new GridBagConstraints();
		gbcButtonPanel.gridx = 0;
		gbcButtonPanel.gridy = 0;
		gbcButtonPanel.anchor = GridBagConstraints.CENTER;
		gbcButtonPanel.insets = new Insets(5, 5, 5, 5); // Margine

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));

		// Aggiungi il testo descrittivo a sinistra del punto interrogativo
		JLabel instructionLabel = new JLabel("Click or type for feedback: ");
		buttonPanel.add(instructionLabel);

		buttonPanel = queryButtons.addButtons(buttonPanel, getDiagram());
		for (Component c : buttonPanel.getComponents()) {
			c.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					refreshIfDiagramChanged();
				}
			});
		}
		buttonPanel.add(createHelpLabel());

		inputAndButtonPanel.add(buttonPanel, gbcButtonPanel); // Aggiungi buttonPanel con i vincoli

		inputAndButtonPanel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				IDiagramUIModel currentDiagram = Application.getDiagram();
				if (!currentDiagram.equals(getDiagram())) {
					showFeedbackPanel(currentDiagram);
				}
			}
		});

		// Vincoli per inputField (sotto)
		GridBagConstraints gbcInputField = new GridBagConstraints();
		gbcInputField.gridx = 0;
		gbcInputField.gridy = 1;
		gbcInputField.fill = GridBagConstraints.HORIZONTAL;
		gbcInputField.weightx = 1.0; // Espande orizzontalmente
		gbcInputField.insets = new Insets(5, 5, 5, 5); // Margine

		inputAndButtonPanel.add(inputField, gbcInputField); // Aggiungi inputField con i vincoli

		// Vincoli per charCountLabel (sotto)
		GridBagConstraints gbcCharCountLabel = new GridBagConstraints();
		gbcCharCountLabel.gridx = 1;
		gbcCharCountLabel.gridy = 1;
		gbcCharCountLabel.anchor = GridBagConstraints.WEST;
		gbcCharCountLabel.insets = new Insets(5, 5, 5, 5); // Margine

		inputAndButtonPanel.add(charCountLabel, gbcCharCountLabel); // Aggiungi charCountLabel con i vincoli

		centralPanel.add(inputAndButtonPanel, BorderLayout.SOUTH);

		mainPanel.add(centralPanel, BorderLayout.CENTER);

		// Crea il pannello destro e imposta il layout BorderLayout
		JPanel rightPanel = new JPanel(new BorderLayout());
		rightPanel.add(requirementsTextArea.getPreviewRequirements(), BorderLayout.NORTH);
		// Aggiungi l'etichetta "Requirements Preview" sopra l'area di testo (NORTH)
		rightPanel.add(requirementsTextArea.getPreviewRequirements(), BorderLayout.NORTH);
		// Aggiungi l'area di testo (CENTRO) usando addRTextArea
		requirementsTextArea.addRTextArea(rightPanel);

		// Crea un pannello per i pulsanti (usiamo FlowLayout per posizionarli affiancati)
		JPanel buttonRightPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

		// Crea il pulsante "Export as TXT"
		JButton saveButton = new JButton("Export as TXT");
		saveButton.setToolTipText("Click to export the diagram requirements as a text file");
		saveButton.addActionListener(e -> requirementsTextArea.exportTextToFile());


		// Crea il pulsante "Load"
		JButton loadButton = new JButton("Load Req.");
		loadButton.setToolTipText("Click to load a text file with the diagram requirements.");
		loadButton.addActionListener(e -> {
			// Crea il FeedbackRequHandler
			FeedbackRequHandler dialogHandler = new FeedbackRequHandler();
			// Mostra il dialogo
			Application.getViewManager().showDialog(dialogHandler);
			// Impostiamo il callback che verrà eseguito quando il dialogo verrà chiuso
			requirementsTextArea.printReqFound(diagram); 
		});

		// Aggiungi i pulsanti al pannello
		buttonRightPanel.add(saveButton);
		buttonRightPanel.add(loadButton);

		// Aggiungi il pannello dei pulsanti sotto l'area di testo (SOUTH) nel pannello destro
		rightPanel.add(buttonRightPanel, BorderLayout.SOUTH);



		// Add requirementsTextArea to the east region in a JScrollPane
		//rightPanel = requirementsTextArea.addRTextArea(rightPanel);

		// Aggiungi il pannello destro al pannello principale nella parte est
		mainPanel.add(rightPanel, BorderLayout.EAST);

		panel.add(mainPanel, BorderLayout.CENTER);

		// imposto l'id del progetto in base al progetto corrente
		projectId = Application.getProject().getId();
		loadSerializedConversations();

		conversationList.addListSelectionListener(e -> {
			Conversation selectedConversation = conversationList.getSelectedValue();

			if (selectedConversation != null) {
				conversationTitleField.setText(
						buildTitle(getDiagram(), selectedConversation.getTitle()));

				String conversationContent = selectedConversation.getConversationContent();
				outputPane.setText("");
				String[] lines = conversationContent.split("\n");
				for (String line : lines) {
					Color textColor = line.startsWith("You:") ? Color.BLUE : Color.BLACK;
					appendToPane(line + "\n", textColor);

				}
			} else {
				conversationTitleField.setText(buildTitle(null,getDiagramTitle()));
				outputPane.setText("");
			}
		});

		return panel;
	}

	private void refreshIfDiagramChanged() {
		IDiagramUIModel currentDiagram = Application.getDiagram();
		if (!currentDiagram.equals(getDiagram())) {
			showFeedbackPanel(currentDiagram);
		}
	}

	private String buildTitle(IDiagramUIModel diagram, String chatTitle) {
		ResourceBundle messages = DiagramInfo.getMessages();  // oppure rendi messages privato e accedi solo con metodi

		String typeDescription = DiagramInfo.getTypeDescription(diagram);

		String diagramName = (diagram != null && diagram.getName() != null && !diagram.getName().isEmpty())
				? diagram.getName()
						: messages.getString("uml.diagram.noname");

		chatTitle = (chatTitle != null && !chatTitle.isEmpty())
				? chatTitle
						: messages.getString("uml.chat.untitled");

		return String.format("%s: %s – Chat: %s", typeDescription, diagramName, chatTitle);
	}

	public void loadSerializedConversations() {
		if (getDiagram().getId() != null) {
			List<Conversation> serializedConversations = ConversationsSerializer
					.deserializeConversations(getDiagram().getId());
			// serve reimpostare il valore di conversationCounter? controllare
			conversationCounter = 0;
			if (serializedConversations != null) {
				int maxSessionId = 0; // Inizializza il valore massimo dell'ID della sessione
				for (Conversation conversation : serializedConversations) {
					conversationListModel.addElement(conversation);
					int sessionId = Integer.parseInt(conversation.getSessionId());
					// Trova il valore massimo dell'ID della sessione
					if (sessionId > maxSessionId) {
						maxSessionId = sessionId;
					}
					System.out.println("*****-" + conversation.getSessionId() + "*****-");
				}
				// Imposta il contatore delle conversazioni sul valore massimo trovato
				conversationCounter = maxSessionId;

				// Assign focus to the first element in the conversation list model
				if (conversationListModel.size() > 0) {
					conversationList.setSelectedIndex(0);
				}
			}
		}
	}

	public void loadSerializedConversations(String diagramId) {
		if (diagramId != null) {
			List<Conversation> serializedConversations = ConversationsSerializer.deserializeConversations(diagramId);
			// serve reimpostare il valore di conversationCounter? controllare
			conversationCounter = 0;
			if (serializedConversations != null) {
				int maxSessionId = 0; // Inizializza il valore massimo dell'ID della sessione
				for (Conversation conversation : serializedConversations) {
					conversationListModel.addElement(conversation);
					int sessionId = Integer.parseInt(conversation.getSessionId());
					// Trova il valore massimo dell'ID della sessione
					if (sessionId > maxSessionId) {
						maxSessionId = sessionId;
					}
					System.out.println("*****-" + conversation.getSessionId() + "*****-");
				}
				// Imposta il contatore delle conversazioni sul valore massimo trovato
				conversationCounter = maxSessionId;

				// Assign focus to the first element in the conversation list model
				if (conversationListModel.size() > 0) {
					conversationList.setSelectedIndex(0);
					System.out.println(conversationList.getSelectedValue().getTitle());
				}
			}
		}
	}

	/**
	 * Appends the given text to a text pane with a specific color based on the
	 * text's prefix. If the text starts with the prefix defined by
	 * {@code prefixAnswer}, it will be appended in blue. Otherwise, it will be
	 * appended in black.
	 *
	 * @param text the text to append to the pane
	 */
	private void appendToPane(String text) {
		if (text.startsWith(prefixAnswer)) {
			appendToPane(text, Color.BLUE);
		} else {
			appendToPane(text, Color.BLACK);
		}
	}

	private void appendToPane(String text, Color color) {
		Style style = document.addStyle("Style", null);
		StyleConstants.setForeground(style, color);
		try {
			// Controlla se è necessario aggiungere una nuova linea prima del testo
			if (document.getLength() > 0) { // verifica che ci sia del testo nel documento
				String lastChar = document.getText(document.getLength() - 1, 1);
				if (!lastChar.equals("\n")) { // verifica che l'ultimo carattere non sia una nuova linea
					text = "\n" + text; // aggiungi una nuova linea prima del testo
				}
			}
			document.insertString(document.getLength(), text, style);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}
	public void hideFeedbackPanelIfShown(IDiagramUIModel diagram) {
	    if (panel != null && diagram != null && diagram.equals(getDiagram())) {
	        System.out.println("[FeedbackHandler] Hiding feedback panel for diagram: " + diagram.getName());
	        Application.getViewManager().removeMessagePaneComponent(panelId);
	        panel = null;
	        setDiagram(null);
	    } else if (diagram == null && panel != null) {
	        System.out.println("[FeedbackHandler] Hiding feedback panel (no diagram currently open)");
	        Application.getViewManager().removeMessagePaneComponent(panelId);
	        panel = null;
	        setDiagram(null);
	    } else {
	        System.out.println("[FeedbackHandler] No feedback panel to hide or diagram does not match.");
	    }
	}


}