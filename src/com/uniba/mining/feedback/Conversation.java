package com.uniba.mining.feedback;

import java.io.Serializable;

public class Conversation implements Serializable {
	private static final long serialVersionUID = 1L;

	private StringBuilder conversationContent;
	private String title;
	private String sessionId;
	private String projectId;
	private String queryId;
	private String diagramAsText;
	private String query;

	// Costruttore completo per inizializzare tutti i campi
	public Conversation(String sessionId, String projectId, String queryId, String diagramAsText, String query) {
		this.sessionId = sessionId;
		this.projectId = projectId;
		this.queryId = queryId;
		this.diagramAsText = diagramAsText;
		this.query = query;
		this.conversationContent = new StringBuilder();
	}

	// Metodo per aggiungere messaggi al contenuto della conversazione
	public void appendMessage(String message) {
		if (conversationContent.length() > 0) {
			// aggiunta di una nuova riga per accodare su riga differente il nuovo messaggio
			conversationContent.append("\n");
		}
		conversationContent.append(message);
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
		return queryId;
	}

	public void setQueryId(String queryId) {
		this.queryId = queryId;
	}

	// Getter e setter per diagramAsText
	public String getDiagramAsText() {
		return diagramAsText;
	}

	public void setDiagramAsText(String diagramAsText) {
		this.diagramAsText = diagramAsText;
	}

	// Getter e setter per query
	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
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

	// Metodo toString per rappresentare l'oggetto Conversation come stringa
	@Override
	public String toString() {
		return "Conversation{" + "sessionId='" + sessionId + '\'' + ", projectId='" + projectId + '\'' + ", queryId='"
				+ queryId + '\'' + ", diagramAsText='" + diagramAsText + '\'' + ", query='" + query + '\'' + ", title='"
				+ title + '\'' + ", conversationContent='" + conversationContent + '\'' + '}';
	}
}
