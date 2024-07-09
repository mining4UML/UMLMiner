package com.uniba.mining.feedback;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Conversation implements Serializable {
	private static final long serialVersionUID = 1L;

	private StringBuilder conversationContent;
	private String title;
	private String sessionId;
	private String projectId;
	private int queryId;

	private String diagramAsText;
	private List<String> diagramAsTextList = new ArrayList<String>();

	private String query;
	private List<String> queryList = new ArrayList<String>();

	private List<String> responseList = new ArrayList<String>();
	private String prefixAnswer;

	// Costruttore che genera automaticamente il query ID incrementale
	public Conversation(String sessionId, String projectId, String diagramAsText, String query, String prefixAnswer) {
		this.sessionId = sessionId;
		this.projectId = projectId;

		// Calcola il query ID basato sul numero di occorrenze del prefisso della query
		// nel conversationContent
		System.out.println(calculateQueryId(prefixAnswer));
		this.queryId = calculateQueryId(prefixAnswer);
		this.prefixAnswer = prefixAnswer;

		this.diagramAsText = diagramAsText;
		addDiagramAsText(diagramAsText);

		this.query = query;
		addQuery(query);

		this.conversationContent = new StringBuilder();
	}

	private void addDiagramAsText(String diagramAsText) {
		diagramAsTextList.add(diagramAsText);
	}

	private void addQuery(String query) {
		queryList.add(query);
	}

	// Metodo per aggiungere messaggi al contenuto della conversazione
	public void appendMessage(String message, boolean answer) {
		if (conversationContent.length() > 0) {
			// aggiunta di una nuova riga per accodare su riga differente il nuovo messaggio
			conversationContent.append("\n");
		}
		if (answer) {
			queryId = calculateQueryId(prefixAnswer);
			// addQuery(message);
		} else if (message.length() > 0) {
			responseList.add(message);
		}
		conversationContent.append(message);
	}

	// Metodo per calcolare il query ID
	private int calculateQueryId(String prefixAnswer) {
		// Conta il numero di occorrenze della stringa "You:" nel conversationContent
		int count = 1;
		if (conversationContent != null) {
			String content = conversationContent.toString();
			count = content.split(prefixAnswer, -1).length - 1;
			++count; // Aggiungi 1 perché il query ID inizia da 1
		}
		return count;
	}

	// Metodo per ottenere il contenuto della conversazione come stringa
	public String getConversationContent() {
		return conversationContent.toString();
	}

	// Getter e setter per sessionId
	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	// Getter e setter per projectId
	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	// Getter e setter per queryId
	public String getQueryId() {
		return String.valueOf(queryId);
	}

	// Getter e setter per diagramAsText
	public String getDiagramAsText() {
		return diagramAsText;
	}

	public void setDiagramAsText(String diagramAsText) {
		this.diagramAsText = diagramAsText;
		diagramAsTextList.add(diagramAsText);
	}

	// Getter e setter per query
	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
		addQuery(query);
	}

	public String getTitle() {
		if (title != null && title != "") {
			return title;
		} else if (conversationContent != null && conversationContent.length() > 0) {
			// Se il testo è presente ma più corto di 8 caratteri, restituisce l'intero
			// testo.
			// Altrimenti, restituisce i primi 8 caratteri del testo.
			return conversationContent.substring(0, Math.min(12, conversationContent.length())) + "...";
		} else {
			// Se il testo è assente, restituisce una stringa predefinita come "No Title" o
			// simile.
			return "No Title";
		}
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		String sep = "********** ";

		builder.append("PROJECT ID: ");
		builder.append(projectId);
		builder.append("\n");
		builder.append("Conversation ID: ");
		builder.append(sessionId);
		if (title != null) {
			builder.append("\nTITLE: ").append(title);
		}
		builder.append("\n\n");

		// Itera attraverso le liste queryIdList, queryList, responseList
		int numItems = Math.min(diagramAsTextList.size(), Math.min(queryList.size(), responseList.size()));
		for (int i = 0; i < numItems; i++) {
			builder.append(sep + "DIAGRAM DESCRIPTION " + sep + "\n").append(diagramAsTextList.get(i)).append("\n");
			builder.append(sep + "QUERY " + sep + "\n").append(queryList.get(i)).append("\n");
			builder.append(sep + "RESPONSE " + sep + "\n").append(responseList.get(i)).append("\n");
			builder.append("\n"); // Aggiunge una linea vuota tra le conversazioni
		}

		return builder.toString();
	}

//	// Metodo toString per rappresentare l'oggetto Conversation come stringa
//	@Override
//	public String toString() {
//		return "Conversation{" + "sessionId='" + sessionId + '\'' + ", projectId='" + projectId + '\'' + ", queryId='"
//				+ queryId + '\'' + ", diagramAsText='" + diagramAsText + '\'' + ", query='" + query + '\'' + ", title='"
//				+ title + '\'' + ", conversationContent='" + conversationContent + '\'' + '}';
//	}

}
//import java.io.Serializable;
//import java.util.ArrayList;
//import java.util.List;
//
//public class Conversation implements Serializable {
//	private static final long serialVersionUID = 1L;
//
//	private StringBuilder conversationContent;
//	private String title;
//	private String sessionId;
//	private String projectId;
//	private int queryId;
//	private List<String> diagramAsTextList;
//	private List<String> queryList;
//	private List<String> responseList;
//	private String prefixAnswer;
//
//	// Costruttore che genera automaticamente il query ID incrementale
//	public Conversation(String sessionId, String projectId, String diagramAsText, String query, String prefixAnswer) {
//		this.sessionId = sessionId;
//		this.projectId = projectId;
//		this.queryId = calculateQueryId(prefixAnswer);
//		this.prefixAnswer = prefixAnswer;
//		this.conversationContent = new StringBuilder();
//		this.diagramAsTextList = new ArrayList<>();
//		this.queryList = new ArrayList<>();
//		this.responseList = new ArrayList<>();
//		setDiagramAsText(diagramAsText);
//		setQuery(query);
//	}
//
//	// Metodo per aggiungere messaggi al contenuto della conversazione e alla
//	// responseList
//	public void appendMessage(String message, boolean answer) {
//		if (conversationContent.length() > 0) {
//			// aggiunta di una nuova riga per accodare su riga differente il nuovo messaggio
//			conversationContent.append("\n");
//		}
//		if (answer)
//			queryId = calculateQueryId(prefixAnswer);
//	}
//
//	// Metodo per calcolare il query ID
//	private int calculateQueryId(String prefixAnswer) {
//		// Conta il numero di occorrenze della stringa "You:" nel conversationContent
//		int count = 1;
//		if (conversationContent != null) {
//			String content = conversationContent.toString();
//			count = content.split(prefixAnswer, -1).length - 1;
//			++count; // Aggiungi 1 perché il query ID inizia da 1
//		}
//		return count;
//	}
//
//	// Metodo per ottenere il contenuto della conversazione come stringa
//	public String getConversationContent() {
//		return conversationContent.toString();
//	}
//
//	// Getter e setter per sessionId
//	public String getSessionId() {
//		return sessionId;
//	}
//
//	public void setSessionId(String sessionId) {
//		this.sessionId = sessionId;
//	}
//
//	// Getter e setter per projectId
//	public String getProjectId() {
//		return projectId;
//	}
//
//	public void setProjectId(String projectId) {
//		this.projectId = projectId;
//	}
//
//	// Getter per queryId
//	public String getQueryId() {
//		return String.valueOf(queryId);
//	}
//
//	// Getter e setter per diagramAsText
//	public String getDiagramAsText() {
//		if (!diagramAsTextList.isEmpty()) {
//			return diagramAsTextList.get(diagramAsTextList.size() - 1); // Restituisce l'ultimo valore inserito
//		}
//		return null;
//	}
//
//	public void setDiagramAsText(String diagramAsText) {
//		this.diagramAsTextList.add(diagramAsText); // Aggiunge il valore al contenitore
//	}
//
//	public void updateDiagramAsText(String newDiagramAsText) {
//		if (!diagramAsTextList.isEmpty()) {
//			diagramAsTextList.set(diagramAsTextList.size() - 1, newDiagramAsText); // Aggiorna l'ultimo valore inserito
//		} else {
//			setDiagramAsText(newDiagramAsText); // Se la lista è vuota, aggiungi il nuovo valore come diagramAsText
//		}
//	}
//
//	// Getter e setter per query
//	public String getQuery() {
//		if (!queryList.isEmpty()) {
//			return queryList.get(queryList.size() - 1); // Restituisce l'ultimo valore inserito
//		}
//		return null;
//	}
//
//	public void updateQuery(String newQuery) {
//		if (!queryList.isEmpty()) {
//			queryList.set(queryList.size() - 1, newQuery); // Aggiorna l'ultimo valore inserito
//		} else {
//			setQuery(newQuery); // Se la lista è vuota, aggiungi il nuovo valore come query
//		}
//	}
//
//	public void setQuery(String query) {
//		this.queryList.add(query); // Aggiunge il valore al contenitore
//		queryId = calculateQueryId(prefixAnswer); // Aggiorna queryId ogni volta che viene impostato un nuovo query
//	}
//
//	public List<String> getQueryList() {
//		return queryList;
//	}
//
//	public void setQueryList(List<String> queryList) {
//		this.queryList = queryList;
//	}
//
//	public List<String> getDiagramAsTextList() {
//		return diagramAsTextList;
//	}
//
//	// Getter e setter privati per responseList
//	public void setResponse(String response) {
//		this.responseList.add(response); // Aggiunge il valore al contenitore
//	}
//
//	private String getResponse(int index) {
//		if (index >= 0 && index < responseList.size()) {
//			return responseList.get(index);
//		}
//		return null;
//	}
//
//	// Metodo pubblico per ottenere tutte le risposte
//	public List<String> getResponseList() {
//		return responseList;
//	}
//
//	public String getTitle() {
//		if (title != null && !title.isEmpty()) {
//			return title;
//		} else if (conversationContent != null && conversationContent.length() > 0) {
//			// Se il testo è presente ma più corto di 8 caratteri, restituisce l'intero
//			// testo.
//			// Altrimenti, restituisce i primi 8 caratteri del testo.
//			return conversationContent.substring(0, Math.min(12, conversationContent.length())) + "...";
//		} else {
//			// Se il testo è assente, restituisce una stringa predefinita come "No Title" o
//			// simile.
//			return "No Title";
//		}
//	}
//
//	public void setTitle(String title) {
//		this.title = title;
//	}
//
//	public String toString() {
//		StringBuilder sb = new StringBuilder();
//		try {
//			sb.append("LIST OF CONVERSATIONS OF");
//			sb.append("\nProject Id: ").append(projectId);
//			sb.append("\nConversation title: ").append(title).append("\n");
//
//			int size = Math.max(Math.max(diagramAsTextList.size(), queryList.size()), responseList.size());
//
//			sb.append(diagramAsTextList.size() + " " + queryList.size() + " " + responseList.size());
//
//			for (int i = 0; i < size; i++) {
//				sb.append("\n***** DIAGRAM DESCRIPTION *****\n");
//				sb.append(i < diagramAsTextList.size() ? diagramAsTextList.get(i) : "");
//
//				sb.append("\n***** QUERY *****\n");
//				sb.append(i < queryList.size() ? queryList.get(i) : "");
//
//				sb.append("\n***** RESPONSE *****\n");
//				sb.append(i < responseList.size() ? responseList.get(i) : "");
//			}
//		} catch (Exception e) {
//			ErrorUtils.showDetailedErrorMessage(e);
//			sb.append(" (error generating string representation: ").append(e.getMessage()).append(")");
//		}
//
//		return sb.toString();
//	}

//}
