package com.uniba.mining.feedback;

import java.io.Serializable;

public class Conversation implements Serializable {
	private static final long serialVersionUID = 1L;

	private StringBuilder conversationContent;
	private String title;
	private String sessionId;
	private String projectId;
	private int queryId;
	private String diagramAsText;
	private String query;

	// Costruttore che genera automaticamente il query ID incrementale
	public Conversation(String sessionId, String projectId, String diagramAsText, String query, String prefixAnswer) {
		this.sessionId = sessionId;
		this.projectId = projectId;

		// Calcola il query ID basato sul numero di occorrenze del prefisso della query
		// nel conversationContent
		this.queryId = calculateQueryId(prefixAnswer);

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

	// Metodo per calcolare il query ID
	private int calculateQueryId(String prefixAnswer) {
		// Conta il numero di occorrenze della stringa "You:" nel conversationContent
		int count = 1;
		if (conversationContent != null) {
			String content = conversationContent.toString();
			count = content.split(prefixAnswer, -1).length - 1;
			count++; // Aggiungi 1 perché il query ID inizia da 1
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
