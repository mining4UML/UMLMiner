package com.uniba.mining.llm;

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

    private String query;

    /**
     * 
     * @param sessionId
     * @param projectId
     * @param diagramId
     * @param queryId
     * @param diagramAsText
     * @param query
     */
    public ApiRequest(String sessionId, String projectId, 
    		String diagramId, String queryId, String diagramAsText, String query) {
        this.sessionId = sessionId;
        this.projectId = projectId;
        this.diagramId = diagramId;
        this.queryId = queryId;
        this.diagramAsText = diagramAsText;
        this.query = query;
    }

    // Getter e setter
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
        return queryId;
    }

    public void setQueryId(String queryId) {
        this.queryId = queryId;
    }

    public String getDiagramAsText() {
        return diagramAsText;
    }

    public void setDiagramAsText(String diagramAsText) {
        this.diagramAsText = diagramAsText;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
    
}
