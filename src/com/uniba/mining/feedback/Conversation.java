package com.uniba.mining.feedback;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.dom4j.Document;

public class Conversation implements Serializable {
	private static final long serialVersionUID = 1L;

	private StringBuilder conversationContent = new StringBuilder();
	private String title;
	private String sessionId = "";
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

	private List<String> requirementsList = new ArrayList<>();
	private List<String> processList = new ArrayList<>();
	private List<String> metricsList = new ArrayList<>();

	public Conversation() {}

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
		if (conversationContent == null)
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

	public void addRequirement(String requirement) {
		requirementsList.add(requirement != null ? requirement : "");
	}

	public void addProcess(String process) {
		processList.add(process != null ? process : "");
	}

	public void addMetric(String metric) {
		metricsList.add(metric != null ? metric : "");
	}

	public List<String> getRequirementsList() {
		return requirementsList;
	}

	public List<String> getProcessList() {
		return processList;
	}

	public List<String> getMetricsList() {
		return metricsList;
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


	public String getRequirements(int index) {
		return (requirementsList != null && index < requirementsList.size()) ? requirementsList.get(index) : "";
	}

	public String getProcess(int index) {
		return (processList != null && index < processList.size()) ? processList.get(index) : "";
	}

	public String getMetrics(int index) {
		return (metricsList != null && index < metricsList.size()) ? metricsList.get(index) : "";
	}
	
	public List<String> getQueryList() {
		return queryList;
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

	private String getSerializedOverview() {
		StringBuilder builder = new StringBuilder();
		String sep = "********** ";

		int numQueries = queryList != null ? queryList.size() : 0;
		int numResponses = responseList != null ? responseList.size() : 0;
		int numTimestamps = (timestampList != null) ? timestampList.size() : 0;

		int numItems = Math.min(numQueries, numResponses);

		for (int i = 0; i < numItems; i++) {
			String timestamp = (timestampList != null && i < numTimestamps)
					? timestampList.get(i)
							: "No timestamp available";

			builder.append(sep)
			.append("QUERY (").append(timestamp).append(") ").append(sep).append("\n")
			.append(queryList.get(i)).append("\n");
			builder.append(sep)
			.append("RESPONSE ").append(sep).append("\n")
			.append(responseList.get(i)).append("\n\n");
		}

		return builder.toString();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();

		builder.append("PROJECT ID: ").append(projectId).append("\n");
		builder.append("DIAGRAM ID: ").append(diagramId).append("\n");
		builder.append("Conversation ID: ").append(sessionId);
		if (title != null) {
			builder.append("\nTITLE: ").append(title);
		}
		builder.append("\n\n");

		String blockSeparator = "=".repeat(80);
		String subsectionSep = "---";

		int numItems = Math.min(Math.min(diagramAsTextList.size(), queryList.size()), responseList.size());
		for (int i = 0; i < numItems; i++) {
			String diagram = diagramAsTextList.get(i) != null ? diagramAsTextList.get(i) : "(no diagram)";
			String timestamp = (timestampList != null && i < timestampList.size() && timestampList.get(i) != null)
					? timestampList.get(i) : "unknown timestamp";
			String query = queryList.get(i) != null ? queryList.get(i) : "(no query)";
			String response = responseList.get(i) != null ? responseList.get(i) : "(no response)";
			String req = (requirementsList != null && i < requirementsList.size()) ? requirementsList.get(i) : null;
			String proc = (processList != null && i < processList.size()) ? processList.get(i) : null;
			String mets = (metricsList != null && i < metricsList.size()) ? metricsList.get(i) : null;

			builder.append(blockSeparator).append("\n");
			builder.append("### === FEEDBACK SESSION #").append(i + 1).append(" ===\n");
			builder.append(blockSeparator).append("\n\n");

			builder.append(subsectionSep).append(" DIAGRAM DESCRIPTION ").append(subsectionSep).append("\n")
			.append(diagram).append("\n\n");

			builder.append(subsectionSep).append(" QUERY (").append(timestamp).append(") ").append(subsectionSep).append("\n")
			.append(query).append("\n\n");

			if (req != null && !req.isEmpty()) {
				builder.append(subsectionSep).append(" REQUIREMENTS ").append(subsectionSep).append("\n")
				.append(req).append("\n\n");
			}

			if (proc != null && !proc.isEmpty()) {
				builder.append(subsectionSep).append(" PROCESS ").append(subsectionSep).append("\n")
				.append(proc).append("\n\n");
			}

			if (mets != null && !mets.isEmpty()) {
				builder.append(subsectionSep).append(" METRICS ").append(subsectionSep).append("\n")
				.append(mets).append("\n\n");
			}

			builder.append(subsectionSep).append(" RESPONSE ").append(subsectionSep).append("\n")
			.append(response).append("\n\n");
		}

		return builder.toString();
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		if (timestampList == null) timestampList = new ArrayList<>();
		if (processList == null) processList = new ArrayList<>();
		if (metricsList == null) metricsList = new ArrayList<>();
		if (requirementsList == null) requirementsList = new ArrayList<>();
		if (conversationContent == null) conversationContent = new StringBuilder();
	}
}
