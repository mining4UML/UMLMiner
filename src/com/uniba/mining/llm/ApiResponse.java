package com.uniba.mining.llm;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ApiResponse {

    @JsonProperty("session_id")
    private String sessionId;

    @JsonProperty("project_id")
    private String projectId;

    @JsonProperty("response_id")
    private String responseId;

    @JsonProperty("query_id")
    private String queryId;

    private String answer;

    private long timestamp;

    private String query;
    
 // Costruttore vuoto
    public ApiResponse() {
    }

    // Costruttore, getter e setter
    public ApiResponse(String sessionId, String projectId, String responseId, String queryId, String answer, long timestamp, String query) {
        this.sessionId = sessionId;
        this.projectId = projectId;
        this.responseId = responseId;
        this.queryId = queryId;
        this.answer = answer;
        this.timestamp = timestamp;
        this.query = query;
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

    public String getResponseId() {
        return responseId;
    }

    public void setResponseId(String responseId) {
        this.responseId = responseId;
    }

    public String getQueryId() {
        return queryId;
    }

    public void setQueryId(String queryId) {
        this.queryId = queryId;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}
