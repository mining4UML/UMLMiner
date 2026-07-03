package com.uniba.mining.llm;

import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

public class LocalOpenAIProvider implements LLMProvider {

	private final OpenAICompatibleClient client;
	public static String LAST_DEBUG_PROMPT = "";

	public LocalOpenAIProvider() {

		String baseUrl = LLMConfig.getBaseUrl();
		String model = LLMConfig.getModel();

		System.out.println("Using LLM endpoint: " + baseUrl);
		System.out.println("Using LLM model: " + model);

		this.client = new OpenAICompatibleClient(baseUrl, model, LLMConfig.getApiKey());
	}

	@Override
	public ApiResponse sendRequest(ApiRequest request) throws IOException {

		String systemPrompt = "You are an educational UML tutor. "
				+ "Provide concise, constructive, pedagogical feedback. "
				+ "Use only the information provided in the student question, requirements, and diagram description. "
				+ "Do not mention internal tools, metrics, logs, RUM, SDMetrics, or process violations unless they are explicitly included in the student question. "
				+ "Do not give the full solution. "
				+ "Focus on helping the student improve the UML model through concrete but non-prescriptive guidance.";
		String userPrompt = buildUserPrompt(request);

		String answer = client.generate(systemPrompt, userPrompt);

		return new ApiResponse(request.getSessionId(), request.getProjectId(), UUID.randomUUID().toString(),
				request.getQueryId(), answer, Instant.now().getEpochSecond(), request.getQuery());
	}

	private String buildUserPrompt(ApiRequest request) {

		if (isProcessFeedbackRequest(request)) {
			return request.getQuery();
		}

		StringBuilder sb = new StringBuilder();

		sb.append("Student question:\n");
		sb.append(nullToEmpty(request.getQuery()));
		sb.append("\n\n");

		sb.append("Requirements:\n");
		sb.append(nullToEmpty(request.getRequirements()));
		sb.append("\n\n");

		sb.append("Diagram as text:\n");
		sb.append(nullToEmpty(request.getDiagramAsText()));

		if (hasText(request.getProcess())) {
			sb.append("\n\n");
			sb.append("Modeling-process information:\n");
			sb.append(request.getProcess());
		}

		if (hasText(request.getMetrics())) {
			sb.append("\n\n");
			sb.append("Design-quality metrics:\n");
			sb.append(request.getMetrics());
		}

		String finalPrompt = sb.toString();
		LAST_DEBUG_PROMPT = finalPrompt;
		return finalPrompt;
	}

	private boolean hasText(String value) {
		return value != null && !value.trim().isEmpty();
	}

	private boolean isProcessFeedbackRequest(ApiRequest request) {
		String q = request.getQuery();
		return q != null && q.contains("You are an expert in UML modeling process diagnostics");
	}

	private String nullToEmpty(String value) {
		return value == null ? "" : value;
	}
}