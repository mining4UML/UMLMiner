package com.uniba.mining.llm;

import java.io.StringWriter;
import java.time.Instant;

import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ApiRequest {
	@JsonProperty("session_id")
	private String sessionId;

	@JsonProperty("project_id")
	private String projectId;

	@JsonProperty("diagramId")
	private String diagramId;

	@JsonProperty("query_id")
	private String queryId;

	@JsonProperty("diagram_as_text")
	private String diagramAsText;

	@JsonProperty("diagram_as_xml")
	private String diagramAsXML;

	@JsonProperty("requirements")
	private String requirements;

	@JsonProperty("process")
	private String process;

	@JsonProperty("metrics")
	private String metrics;

	@JsonProperty("timestamp")
	private long timestamp;

	@JsonProperty("user")
	private String user;

	private String query;

	public ApiRequest(String sessionId, String projectId,
					  String diagramId, String queryId,
					  String diagramAsText, Document diagramAsXML,
					  String requirements, String process, String metrics,
					  String user, String query) {
		this.sessionId = sessionId;
		this.projectId = projectId;
		this.diagramId = diagramId;
		this.queryId = queryId;
		this.diagramAsText = diagramAsText;
		this.diagramAsXML = convertDocumentToString(diagramAsXML);
		this.requirements = requirements;
		this.process = process;
		this.metrics = metrics;
		this.user = user;
		this.query = query;
		this.timestamp = Instant.now().getEpochSecond();
	}

	private String convertDocumentToString(Document doc) {
		try {
			StringWriter writer = new StringWriter();
			OutputFormat format = OutputFormat.createPrettyPrint();
			XMLWriter xmlWriter = new XMLWriter(writer, format);
			xmlWriter.write(doc);
			xmlWriter.close();
			return writer.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// Getter e Setter
	public String getSessionId() { return sessionId; }
	public void setSessionId(String sessionId) { this.sessionId = sessionId; }

	public String getProjectId() { return projectId; }
	public void setProjectId(String projectId) { this.projectId = projectId; }

	public String getDiagramId() { return diagramId; }
	public void setDiagramId(String diagramId) { this.diagramId = diagramId; }

	public String getQueryId() { return queryId; }
	public void setQueryId(String queryId) { this.queryId = queryId; }

	public String getDiagramAsText() { return diagramAsText; }
	public void setDiagramAsText(String diagramAsText) { this.diagramAsText = diagramAsText; }

	public String getDiagramAsXml() { return diagramAsXML; }
	public void setDiagramAsText(Document diagramAsXML) { this.diagramAsXML = convertDocumentToString(diagramAsXML); }

	public String getRequirements() { return requirements; }
	public void setRequirements(String requirements) { this.requirements = requirements; }

	public String getProcess() { return process; }
	public void setProcess(String process) { this.process = process; }

	public String getMetrics() { return metrics; }
	public void setMetrics(String metrics) { this.metrics = metrics; }

	public long getTimestamp() { return timestamp; }
	//public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

	public String getUser() { return user; }
	public void setUsername(String username) { this.user = username; }

	public String getQuery() { return query; }
	public void setQuery(String query) { this.query = query; }
}
