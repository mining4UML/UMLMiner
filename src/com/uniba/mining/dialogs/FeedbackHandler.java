package com.uniba.mining.dialogs;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.*;

import com.uniba.mining.utils.Application;
import com.uniba.mining.utils.GUI;
import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.model.IProject;
import com.uniba.mining.tasks.exportdiag.ClassInfo;

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
import com.uniba.mining.llm.ApiRequest;
import com.uniba.mining.llm.ApiResponse;
import com.uniba.mining.llm.RestClient;
import com.uniba.mining.plugin.Config;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

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
	private JTextPane outputPane;
	private StyledDocument document;
	private DefaultListModel<Conversation> conversationListModel;
	private JList<Conversation> conversationList;
	private JButton newChatButton;
	private JLabel conversationLabel;
	private JTextField conversationTitleField;
	private String projectId;
	private IDiagramUIModel diagram;
	// Counter to track the total number of conversations
	private int conversationCounter = 0;
	private static final String PLACEHOLDER = Config.DIALOG_FEEDBACK_MESSAGE_PLACEHOLDER;

	private static FeedbackHandler instance;

	private static JPanel panel;

	// Pulsanti per query predefinite
	private JButton addButton;
	private JButton improvementsButton;
	private JButton issuesButton;
	private JButton explainButton;

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
		conversationTitleField.setText(diagramId);
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
		document = outputPane.getStyledDocument();

		conversationListModel = new DefaultListModel<>();
		conversationList = new JList<>(conversationListModel);

		conversationTitleField = new JTextField();
		conversationTitleField.setEditable(false);
		conversationTitleField.setHorizontalAlignment(SwingConstants.CENTER); // Centra il testo dell'etichetta
		conversationTitleField.setForeground(new Color(34, 139, 34)); // Verde scuro
		conversationTitleField.setFont(conversationTitleField.getFont().deriveFont(Font.BOLD)); // Imposta l'etichetta
																								// in grassetto
		// Imposta il colore di sfondo della casella di testo a quello del panel
		conversationTitleField.setBackground(UIManager.getColor("Panel.background"));

		newChatButton = new JButton("New Chat");
		newChatButton.setForeground(Color.BLUE);
		// newChatButton.setFocusPainted(false);
		// newChatButton.setBorderPainted(false);
		// newChatButton.setOpaque(true);

		// Imposta le dimensioni preferite del pulsante
		Dimension buttonSize = new Dimension(50, 30); // Larghezza: 100px, Altezza: 30px
		newChatButton.setPreferredSize(buttonSize);
		newChatButton.setToolTipText("Start a new chat for this diagram");

		conversationList.setCellRenderer(new ConversationListCellRenderer());

		// Crea l'etichetta
		conversationLabel = new JLabel("Conversation List");
		conversationLabel.setHorizontalAlignment(SwingConstants.CENTER); // Centra il testo dell'etichetta
		conversationLabel.setForeground(new Color(34, 139, 34)); // Verde scuro
		conversationLabel.setFont(conversationLabel.getFont().deriveFont(Font.BOLD)); // Imposta l'etichetta in
																						// grassetto

		inputField.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				// aggiorna il pannello di feedback solo se il diagramma è cambiato
				if (!Application.getDiagram().equals(getDiagram()))
					showFeedbackPanel(Application.getDiagram());
				if (inputField.getText().equals(PLACEHOLDER)) {
					inputField.setText("");
				}
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
					}
					createNewChat();
				} catch (Exception e1) {
					ErrorUtils.showDetailedErrorMessage(e1);
				}
			}
		});

		conversationList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (SwingUtilities.isRightMouseButton(e)) {
					int index = conversationList.locationToIndex(e.getPoint());
					if (index > -1) {
						conversationList.setSelectedIndex(index);
						showPopupMenu(e);
					}
				}
			}
		});

		initQueryButtons();

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

	/**
	 * Query Buttons initialization
	 */
	private void initQueryButtons() {
		addButton = new JButton("Add Contents");
		addButton.setToolTipText(Config.FEEDBACK_BUTTON_ADD);
		addButton.setForeground(Color.BLUE);
		addButton.addActionListener(e -> handleButtonClick(Config.FEEDBACK_BUTTON_ADD));

		improvementsButton = new JButton("Improvements");
		improvementsButton.setToolTipText(Config.FEEDBACK_BUTTON_IMROVEMENT);
		improvementsButton.setForeground(Color.BLUE);
		improvementsButton.addActionListener(e -> handleButtonClick(Config.FEEDBACK_BUTTON_IMROVEMENT));

		issuesButton = new JButton("Issues");
		issuesButton.setToolTipText(Config.FEEDBACK_BUTTON_ISSUES);
		issuesButton.setForeground(Color.BLUE);
		issuesButton.addActionListener(e -> handleButtonClick(Config.FEEDBACK_BUTTON_ISSUES));

		explainButton = new JButton("Explain");
		explainButton.setToolTipText(Config.FEEDBACK_BUTTON_EXPLAIN);
		explainButton.setForeground(Color.BLUE);
		explainButton.addActionListener(e -> handleButtonClick(Config.FEEDBACK_BUTTON_EXPLAIN));
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

	private void handleButtonClick(String text) {
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
						ClassInfo.isProjectEmpty(Application.getProject());
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
		// Aggiunge un listener di focus all'outputPane
		outputPane.addFocusListener(new FocusListener() {

			// Metodo chiamato quando il focus viene guadagnato sull'outputPane
			@Override
			public void focusGained(FocusEvent e) {
				// aggiorna il pannello di feedback solo se il diagramma è cambiato
				if (!Application.getDiagram().equals(getDiagram()))
					showFeedbackPanel(Application.getDiagram());
			}

			// Metodo chiamato quando il focus viene perso sull'outputPane
			@Override
			public void focusLost(FocusEvent e) {
				// Implementazione non necessaria per il focus perso
				// Potrebbe essere vuoto se non ci sono azioni specifiche da eseguire
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
	 * Send and get response from the server
	 * 
	 * @param conversation
	 * @return Return the obtained response
	 * @throws ConnectException
	 * @throws IOException
	 */
	private String sendRequestAndGetResponse(Conversation conversation) throws ConnectException, IOException {
		// Creazione del dialogo di attesa
		JDialog dialog = createWaitDialog();
		AtomicReference<String> responseRef = new AtomicReference<>();
		AtomicReference<Exception> exceptionRef = new AtomicReference<>();

		// Creazione e avvio di un SwingWorker per gestire la richiesta al server
		SwingWorker<String, Void> worker = new SwingWorker<>() {
			@Override
			protected String doInBackground() throws Exception {
				// Effettua la richiesta al server e ottiene la risposta
				ApiRequest request = new ApiRequest(conversation.getSessionId(), projectId, conversation.getDiagramId(),
						conversation.getQueryId(), conversation.getDiagramAsText(), conversation.getQuery());
				RestClient client = new RestClient();
				ApiResponse response = client.sendRequest(request);
				responseRef.set(response.getAnswer());
				return response.getAnswer();
			}

			@Override
			protected void done() {
				try {
					get(); // Ottieni il risultato della richiesta e gestisci le eccezioni
				} catch (Exception e) {
					exceptionRef.set(e); // Memorizza l'eccezione
				} finally {
					dialog.dispose(); // Chiude il dialogo di attesa
				}
			}
		};

		worker.execute(); // Avvia il lavoro in background

		// Mostra il dialogo di attesa in modo modale
		dialog.setVisible(true);

		// Controlla se c'è stata un'eccezione e rilanciala
		if (exceptionRef.get() != null) {
			Exception e = exceptionRef.get();
			if (e instanceof ConnectException) {
				throw (ConnectException) e;
			} else if (e instanceof IOException) {
				throw (IOException) e;
			} else {
				throw new RuntimeException(e); // Per altre eccezioni non previste
			}
		}

		// Restituisci la risposta ottenuta
		return responseRef.get();
	}

	/**
	 * Creates a modal JDialog that displays a "Please wait" message with a loading
	 * animation. The dialog is centered on the screen and does not allow resizing
	 * or closing via the close button. A Timer is used to animate ellipsis in the
	 * message to indicate processing.
	 *
	 * @return A JDialog instance configured with a loading message and animation.
	 */
	private JDialog createWaitDialog() {
		JDialog dialog = new JDialog((Frame) null, "Please wait", true);
		JLabel label = new JLabel("Processing, please wait");
		label.setHorizontalAlignment(SwingConstants.CENTER);
		dialog.getContentPane().add(label);
		dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		dialog.setSize(300, 100);
		dialog.setLocationRelativeTo(null); // Centra il dialogo sullo schermo
		dialog.setResizable(false); // Disabilita l'icona di ingrandimento

		// Rimuove il pulsante di chiusura dalla finestra di dialogo
		dialog.setUndecorated(true);
		dialog.getRootPane().setWindowDecorationStyle(JRootPane.PLAIN_DIALOG);

		// Creazione di un timer per aggiornare il testo del label con i puntini
		// sospensivi
		Timer timer = new Timer(500, e -> {
			String text = label.getText();
			if (text.endsWith("...")) {
				label.setText("Processing, please wait");
			} else {
				label.setText(text + ".");
			}
		});
		timer.start();

		// Interrompi il timer quando il dialogo viene chiuso
		dialog.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosed(java.awt.event.WindowEvent windowEvent) {
				timer.stop();
			}
		});

		return dialog;
	}

	private void updateConversation(String inputText, String sessionId)
			throws ConnectException, IOException, Exception {

		String diagramAsText = ClassInfo.exportInformation(Application.getProject(), "en", diagram);
		boolean empty = false;

		if (!inputText.isEmpty()) {
			// Aggiungi il testo alla conversazione corrente solo se non è vuoto
			if (conversationListModel.isEmpty()) {
				Conversation newConversation = createNewConversation(sessionId, inputText, diagramAsText);
				conversationListModel.addElement(newConversation);
				conversationList.setSelectedValue(newConversation, true);
				empty = true;
			}

			Conversation currentConversation = conversationList.getSelectedValue();

			if (currentConversation != null) {
				// currentConversation.appendMessage(answer + "\n" + response);

				if (!empty) {
					// add text description of diagrams
					currentConversation.setDiagramAsText(diagramAsText);
					// add query
					currentConversation.setQuery(inputText);
				}

				String answer = prefixAnswer + inputText;
				currentConversation.appendMessage(answer, true);

				String response = sendRequestAndGetResponse(currentConversation);
				appendToPane(answer);
				if (response != null)
					appendToPane(response);
				else
					throw new Exception("no response from the server");
				// currentConversation.appendMessage(answer);
				currentConversation.appendMessage(response, false);
				// currentConversation.setResponse(response);
				conversationListModel.set(conversationList.getSelectedIndex(), currentConversation);
				conversationTitleField.setText(getDiagramTitle() + currentConversation.getTitle());

			}

			// serializeConversations();
			String diagramId = Application.getIDCurrentDiagram();
			serializeConversations(diagramId);

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

	private Conversation createNewConversation(String sessionId, String query, String diagramAsText) {

		// Utilizzo del costruttore con i parametri
		Conversation newConversation = new Conversation(sessionId, projectId, getDiagram().getId(), diagramAsText,
				query, prefixAnswer);
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
		JMenuItem renameItem = new JMenuItem("Rename");
		JMenuItem deleteItem = new JMenuItem("Delete");
		JMenuItem exportItem = new JMenuItem("Export as Text");

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
				conversationTitleField.setText(getDiagramTitle() + newTitle);
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

		if (panel == null)
			createPanel();
		else {
			clearPanel(getDiagram().getId());
			// Load serialized conversations with the diagramId
			loadSerializedConversations(getDiagram().getId());
		}
		Application.getViewManager().showMessagePaneComponent(panelId, title, panel);
		forceUpdateView();
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

	private void createNewChat() throws Exception {
		// Ottieni il testo dalla JTextPane
		String query = outputPane.getText();

		// Controlla se l'inputText non è vuoto o uguale al messaggio di feedback
		// predefinito
		if (!query.isEmpty() && !query.equals(PLACEHOLDER)) {
			// i valori sessionId, projectId, etc.
			String sessionId = generateSessionId();
			String diagramAsText = ClassInfo.exportInformation(Application.getProject(), "en", diagram);
			// Crea una nuova istanza di Conversation
			Conversation newConversation = new Conversation(sessionId, projectId, getDiagram().getId(), diagramAsText,
					query, prefixAnswer);

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

		}
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

		// Pannello sinistro contiene il pulsante "New Chat" e la lista delle
		// conversazioni
		JPanel leftPanel = new JPanel(new BorderLayout());
		newChatButton.addActionListener(e -> {
			try {
				createNewChat();
			} catch (Exception e1) {
				GUI.showErrorMessageDialog(Application.getViewManager().getRootFrame(), "Feedback", e1.getMessage());
			}
		});

		leftPanel.add(newChatButton, BorderLayout.SOUTH);
		JScrollPane listScrollPane = new JScrollPane(conversationList);
		listScrollPane.setPreferredSize(new Dimension(200, 200));
		leftPanel.add(listScrollPane, BorderLayout.CENTER);
		leftPanel.add(conversationLabel, BorderLayout.NORTH);

		mainPanel.add(leftPanel, BorderLayout.WEST);

		// Pannello destro contiene il conversationTitleField, l'outputPane e
		// l'inputField
		JPanel rightPanel = new JPanel(new BorderLayout());
		rightPanel.add(conversationTitleField, BorderLayout.NORTH);
		rightPanel.add(new JScrollPane(outputPane), BorderLayout.CENTER);

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
		buttonPanel.add(createHelpLabel());
		buttonPanel.add(addButton);
		buttonPanel.add(improvementsButton);
		buttonPanel.add(issuesButton);
		buttonPanel.add(explainButton);

		inputAndButtonPanel.add(buttonPanel, gbcButtonPanel); // Aggiungi buttonPanel con i vincoli

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

		rightPanel.add(inputAndButtonPanel, BorderLayout.SOUTH);

		mainPanel.add(rightPanel, BorderLayout.CENTER);

		panel.add(mainPanel, BorderLayout.CENTER);

		// imposto l'id del progetto in base al progetto corrente
		projectId = Application.getProject().getId();
		loadSerializedConversations();

		conversationList.addListSelectionListener(e -> {
			Conversation selectedConversation = conversationList.getSelectedValue();

			if (selectedConversation != null) {
				conversationTitleField.setText(getDiagramTitle() + selectedConversation.getTitle());
				String conversationContent = selectedConversation.getConversationContent();
				outputPane.setText("");
				String[] lines = conversationContent.split("\n");
				for (String line : lines) {
					Color textColor = line.startsWith("You:") ? Color.BLUE : Color.BLACK;
					appendToPane(line + "\n", textColor);

				}
			} else {
				conversationTitleField.setText(getDiagramTitle());
				outputPane.setText("");
			}
		});

		return panel;
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
}