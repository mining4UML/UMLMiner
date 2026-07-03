package com.uniba.mining.processfeedback.llm;

import java.util.List;

import com.uniba.mining.processfeedback.core.InterpretedViolation;

public class ProcessFeedbackPromptBuilder {

    public String build(
            List<InterpretedViolation> violations) {

        StringBuilder sb =
                new StringBuilder();

        sb.append(
                "The following process violations have already been interpreted.\n");

        sb.append(
                "Generate pedagogical feedback for the learner.\n\n");

        for (InterpretedViolation v : violations) {

            sb.append(v.toPromptText())
              .append("\n");
        }

        return sb.toString();
    }
}