package com.uniba.mining.processfeedback.parser;

import java.util.List;

import com.uniba.mining.processfeedback.core.InterpretedViolation;

public class ProcessFeedbackPromptBuilder {

	public String build(List<InterpretedViolation> violations) {

		StringBuilder sb = new StringBuilder();

		sb.append("The following items are pedagogically interpreted UML process violations.\n");
		sb.append("Generate feedback only from these interpreted violations.\n\n");

		sb.append("STRICT OUTPUT RULES:\n");
		sb.append("- Do not summarize the dataset.\n");
		sb.append("- Do not add generic UML examples.\n");
		sb.append("- Do not suggest external tools or resources.\n");
		sb.append("- Do not invent classes, attributes, relationships, or data types.\n");
		sb.append("- Use only the evidence provided below.\n");
		sb.append("- Return at most 5 feedback items.\n");
		sb.append("- Each item must contain: Issue, Evidence, Suggested action, Rationale.\n\n");

		sb.append("EXPECTED FORMAT:\n");
		sb.append("- Issue: <specific process issue>\n");
		sb.append("  Evidence: <copy relevant evidence>\n");
		sb.append("  Suggested action: <concrete action based on evidence>\n");
		sb.append("  Rationale: <why this matters for UML modeling>\n\n");

		sb.append("INTERPRETED VIOLATIONS:\n\n");

		for (InterpretedViolation v : violations) {
			sb.append(v.toPromptText());
			sb.append("\n---\n");
		}

		return sb.toString();
	}
}