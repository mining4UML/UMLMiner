package com.uniba.mining.processfeedback.ocpm;

import java.util.List;

public class ObjectCentricProcessPromptBuilder {

    public String build(List<ProcessDynamicsFinding> findings) {

        StringBuilder sb = new StringBuilder();

        sb.append("The following findings describe object-centric process dynamics observed in the UML modeling log.\n");
        sb.append("These findings are based on the construction process, not only on the final diagram.\n\n");

        sb.append("Generate concise process-aware feedback for the learner.\n");
        sb.append("Do not invent UML elements.\n");
        sb.append("Do not provide generic UML tutorials.\n");
        sb.append("Use only the evidence reported below.\n\n");

        sb.append("OUTPUT FORMAT:\n");
        sb.append("- Issue: <process-dynamics issue>\n");
        sb.append("  Evidence: <observed process evidence>\n");
        sb.append("  Suggested action: <concrete reflection or correction>\n");
        sb.append("  Rationale: <why this matters for the modeling process>\n\n");

        sb.append("PROCESS-DYNAMICS FINDINGS:\n\n");

        for (ProcessDynamicsFinding finding : findings) {
            sb.append(finding.toPromptText());
            sb.append("\n---\n");
        }

        return sb.toString();
    }
}