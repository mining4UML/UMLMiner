package com.uniba.mining.feedback;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.dom4j.Document;

public class Conversation implements Serializable {
	private static final long serialVersionUID = 1L;

	private StringBuilder conversationContent=new StringBuilder();
	private String title;
	private String sessionId="";
	private String projectId;
	private String diagramId;
	private int queryId;

	private String diagramAsText;
	private List<String> diagramAsTextList = new ArrayList<>();
	private String query;
	private List<String> queryList = new ArrayList<>();
	private List<String> timestampList = new ArrayList<>();
	private transient Document diagramAsXML;
	private List<String> responseList = new ArrayList<>();
	private String prefixAnswer;

	private String requirements = "";
	private String process = "";
	private String metrics = "";

	public Conversation() {};
	public Conversation(String sessionId, String projectId, String diagramId, String diagramAsText,
			Document diagramAsXML, String query, String prefixAnswer) {
		this.sessionId = sessionId;
		this.projectId = projectId;
		this.diagramId = diagramId;
		this.queryId = calculateQueryId(prefixAnswer);
		this.prefixAnswer = prefixAnswer;
		this.diagramAsText = diagramAsText;
		addDiagramAsText(diagramAsText);
		this.diagramAsXML = diagramAsXML;
		this.query = query;
		addQuery(query);
		//timestampList.add(currentTimestamp());
		if(conversationContent==null)
			this.conversationContent = new StringBuilder();
	}

	private void addDiagramAsText(String diagramAsText) {
		diagramAsTextList.add(diagramAsText);
	}

	private void addQuery(String query) {
		queryList.add(query);
		timestampList.add(currentTimestamp());
	}

	private String currentTimestamp() {
		return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
	}

	public void appendMessage(String message, boolean answer) {
		if (conversationContent.length() > 0) {
			conversationContent.append("\n");
		}
		if (answer) {
			queryId = calculateQueryId(prefixAnswer);
		} else if (message.length() > 0) {
			responseList.add(message);
		}
		conversationContent.append(message);
	}

	private int calculateQueryId(String prefixAnswer) {
		int count = 1;
		if (conversationContent != null) {
			String content = conversationContent.toString();
			count = content.split(prefixAnswer, -1).length - 1;
			++count;
		}
		return count;
	}


	public void setQueryId(String prefixAnswer) {
		this.prefixAnswer = prefixAnswer;
		this.queryId = calculateQueryId(prefixAnswer);
	}
	public String getConversationContent() {
		return getSerializedOverview();
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public String getDiagramId() {
		return diagramId;
	}

	public void setDiagramId(String diagramId) {
		this.diagramId = diagramId;
	}

	public String getQueryId() {
		return String.valueOf(queryId);
	}

	public String getDiagramAsText() {
		return diagramAsText;
	}

	public void setDiagramAsText(String diagramAsText) {
		this.diagramAsText = diagramAsText;
		diagramAsTextList.add(diagramAsText);
	}

	public Document getDiagramAsXML() {
		return diagramAsXML;
	}

	public void setDiagramAsXML(Document diagramAsXML) {
		this.diagramAsXML = diagramAsXML;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
		addQuery(query);
	}

	public String getTitle() {
		if (title != null && !title.isEmpty()) {
			return title;
		} else if (conversationContent != null && conversationContent.length() > 0) {
			return conversationContent.substring(0, Math.min(12, conversationContent.length())) + "...";
		} else {
			return "No Title";
		}
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getRequirements() {
		return requirements;
	}

	public void setRequirements(String requirements) {
		this.requirements = requirements != null ? requirements : "";
	}

	public String getProcess() {
		return process;
	}

	public void setProcess(String process) {
		this.process = process != null ? process : "";
	}

	public String getMetrics() {
		return metrics;
	}

	public void setMetrics(String metrics) {
		this.metrics = metrics != null ? metrics : "";
	}


	private String getSerializedOverview() {
		StringBuilder builder = new StringBuilder();
		String sep = "********** ";

		int numQueries = queryList.size();

		if (responseList.size() < numQueries || timestampList.size() < numQueries) {
			System.err.println("[Warning] Inconsistent list sizes: queries=" + numQueries +
					", responses=" + responseList.size() + ", timestamps=" + timestampList.size());
			numQueries = Math.min(Math.min(queryList.size(), responseList.size()), timestampList.size());
		}

		for (int i = 0; i < numQueries; i++) {
			builder.append(sep + "QUERY (" + timestampList.get(i) + ") " + sep + "\n")
			.append(queryList.get(i)).append("\n");
			builder.append(sep + "RESPONSE " + sep + "\n")
			.append(responseList.get(i)).append("\n\n");
		}

		return builder.toString();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		String sep = "********** ";

		builder.append("PROJECT ID: ").append(projectId).append("\n");
		builder.append("DIAGRAM ID: ").append(diagramId).append("\n");
		builder.append("Conversation ID: ").append(sessionId);
		if (title != null) {
			builder.append("\nTITLE: ").append(title);
		}
		builder.append("\n\n");

		int numItems = Math.min(Math.min(diagramAsTextList.size(), queryList.size()), responseList.size());
		for (int i = 0; i < numItems; i++) {
			builder.append(sep + "DIAGRAM DESCRIPTION " + sep + "\n").append(diagramAsTextList.get(i)).append("\n");
			builder.append(sep + "QUERY (" + timestampList.get(i) + ") " + sep + "\n").append(queryList.get(i)).append("\n");
			builder.append(sep + "RESPONSE " + sep + "\n").append(responseList.get(i)).append("\n\n");
		}

		if (!requirements.isEmpty()) {
			builder.append(sep + "REQUIREMENTS " + sep + "\n").append(requirements).append("\n");
		}
		if (!process.isEmpty()) {
			builder.append(sep + "PROCESS " + sep + "\n").append(process).append("\n");
		}
		if (!metrics.isEmpty()) {
			builder.append(sep + "METRICS " + sep + "\n").append(metrics).append("\n");
		}

		return builder.toString();
	}
}

