package com.uniba.mining.feedback;

public class Conversation {
	private StringBuilder conversationContent;
	private String title;

	public Conversation() {
		conversationContent = new StringBuilder();
	}

	public void appendMessage(String message) {
		conversationContent.append(message).append("\n");
	}

	public String getConversationContent() {
		return conversationContent.toString();
	}

	public void clearConversation() {
		conversationContent.setLength(0);
	}

	public void setTitle(String title) {
		this.title = title;

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
}