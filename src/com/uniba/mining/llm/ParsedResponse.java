package com.uniba.mining.llm;

public class ParsedResponse {
    private String answer;
    private String question;
    private String context;
    private String systemMessage;

    // Costruttore
    public ParsedResponse(String answer, String question, String context, String systemMessage) {
        this.answer = answer;
        this.question = question;
        this.context = context;
        this.systemMessage = systemMessage;
    }

    // Getter per ogni campo
    public String getAnswer() {
        return answer;
    }

    public String getQuestion() {
        return question;
    }

    public String getContext() {
        return context;
    }

    public String getSystemMessage() {
        return systemMessage;
    }

    @Override
    public String toString() {
        return "ParsedResponse{" +
                "answer='" + answer + '\'' +
                ", question='" + question + '\'' +
                ", context='" + context + '\'' +
                ", systemMessage='" + systemMessage + '\'' +
                '}';
    }
}

