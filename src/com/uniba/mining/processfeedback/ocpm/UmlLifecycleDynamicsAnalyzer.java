package com.uniba.mining.processfeedback.ocpm;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class UmlLifecycleDynamicsAnalyzer {

	private static final int LATE_REFINEMENT_THRESHOLD = 20;
	private static final int FRAGMENTED_CONSTRUCTION_THRESHOLD = 30;
	private static final int MAX_FINDINGS_FOR_LLM = 8;

	public List<ProcessDynamicsFinding> analyze(List<UmlObjectLifecycle> lifecycles) {

		List<ProcessDynamicsFinding> findings = new ArrayList<>();

		for (UmlObjectLifecycle lc : lifecycles) {

			if (isNoise(lc)) {
				continue;
			}

			detectRepeatedRenaming(lc, findings);
			detectRework(lc, findings);
			detectLateRefinement(lc, findings);
			detectFragmentedConstruction(lc, findings);
			detectRelationshipEndpointIssues(lc, findings);
			detectPossiblyAbandonedRelationship(lc, findings);

		}

		return findings.stream().sorted(Comparator.comparingInt(this::priority).reversed()).limit(MAX_FINDINGS_FOR_LLM)
				.collect(Collectors.toList());
	}

	private void detectRepeatedRenaming(UmlObjectLifecycle lc, List<ProcessDynamicsFinding> findings) {

		int renames = lc.countActivityContaining("name property updated");

		if (renames >= 2 && !isUnknown(lc.getObjectName())) {
			findings.add(finding("Repeated renaming", lc, "The object name was updated " + renames + " times.",
					"The same UML element was renamed several times during the modeling process. This suggests that its role or responsibility may not have been stable.",
					"Review whether the final name of this element clearly expresses its intended role in the model."));
		}
	}

	private void detectRework(UmlObjectLifecycle lc, List<ProcessDynamicsFinding> findings) {

		boolean added = lc.hasActivityContaining("added");
		boolean removed = lc.hasActivityContaining("removed");

		if (added && removed && !isUnknown(lc.getObjectName())) {
			findings.add(finding("Rework", lc, "The object lifecycle contains both creation and removal events.",
					"The element was introduced and later removed. This indicates a revision or correction during modeling, which may reflect uncertainty or redesign.",
					"Check whether the removal was intentional and whether the remaining model still preserves the intended concept."));
		}
	}

	private void detectLateRefinement(UmlObjectLifecycle lc, List<ProcessDynamicsFinding> findings) {

		if (!isRefinableType(lc.getObjectType())) {
			return;
		}

		int createdAt = lc.firstIndexOf("added");
		int renamedAt = lc.lastIndexOf("name property updated");
		int typedAt = lc.lastIndexOf("type property updated");

		int refinedAt = Math.max(renamedAt, typedAt);

		if (createdAt >= 0 && refinedAt >= 0) {
			int delay = refinedAt - createdAt;

			if (delay >= LATE_REFINEMENT_THRESHOLD) {
				findings.add(finding("Late refinement", lc,
						"The object was created at event " + createdAt + " and refined at event " + refinedAt
								+ " after " + delay + " events.",
						"The element remained under-specified for a long part of the modeling process.",
						"Consider refining important elements soon after creating them, so later modeling decisions are based on clearer concepts."));
			}
		}
	}

	private void detectFragmentedConstruction(UmlObjectLifecycle lc, List<ProcessDynamicsFinding> findings) {

		if (!isRefinableType(lc.getObjectType())) {
			return;
		}

		if (lc.getEvents().size() < 3) {
			return;
		}

		int first = lc.getEvents().get(0).getIndex();
		int last = lc.getEvents().get(lc.getEvents().size() - 1).getIndex();
		int span = last - first;

		if (span >= FRAGMENTED_CONSTRUCTION_THRESHOLD) {
			findings.add(finding("Fragmented construction", lc, "The object was modified across " + span + " events.",
					"The modeling of this element was spread across a long portion of the session, suggesting backtracking or delayed consolidation.",
					"Review whether the element’s role was clear from the beginning or evolved during modeling."));
		}
	}

	private void detectPossiblyAbandonedRelationship(UmlObjectLifecycle lc, List<ProcessDynamicsFinding> findings) {

		if (!isRelationshipType(lc.getObjectType())) {
			return;
		}

		if (isUnknown(lc.getObjectName())) {
			return;
		}

		boolean added = lc.hasActivityContaining("added");
		boolean refined = lc.hasActivityContaining("name property updated")
				|| lc.hasActivityContaining("property updated") || lc.hasActivityContaining("type property updated");
		boolean removed = lc.hasActivityContaining("removed");

		if (added && !refined && !removed && lc.getEvents().size() == 1) {
			findings.add(finding("Possibly abandoned relationship", lc,
					"The relationship was added once, but no later direct refinement or removal was observed.",
					"The relationship may have been introduced without further process-level consolidation.",
					"Check whether this relationship was intentionally integrated into the model or whether it should be refined or removed."));
		}
	}

	private int priority(ProcessDynamicsFinding finding) {

		String category = safe(finding.getCategory());

		if ("Rework".equalsIgnoreCase(category)) {
			return 100;
		}

		if ("Relationship endpoint issue".equalsIgnoreCase(category)) {
			return 95;
		}

		if ("Repeated renaming".equalsIgnoreCase(category)) {
			return 90;
		}

		if ("Late refinement".equalsIgnoreCase(category)) {
			return 80;
		}

		if ("Fragmented construction".equalsIgnoreCase(category)) {
			return 70;
		}

		if ("Possibly abandoned relationship".equalsIgnoreCase(category)) {
			return 50;
		}

		return 10;
	}

	private String printable(String value) {
		return isUnknown(value) ? "unknown" : value;
	}

	private ProcessDynamicsFinding finding(String category, UmlObjectLifecycle lc, String evidence,
			String interpretation, String suggestedAction) {

		ProcessDynamicsFinding f = new ProcessDynamicsFinding();

		f.setCategory(category);
		f.setObjectType(lc.getObjectType());
		f.setObjectName(lc.getObjectName());
		f.setEvidence(evidence);
		f.setInterpretation(interpretation);
		f.setSuggestedAction(suggestedAction);

		return f;
	}

	private boolean isNoise(UmlObjectLifecycle lc) {
		return lc == null || "Project".equalsIgnoreCase(lc.getObjectType())
				|| "ClassDiagram".equalsIgnoreCase(lc.getObjectType()) || "View".equalsIgnoreCase(lc.getObjectType())
				|| "Anchor".equalsIgnoreCase(lc.getObjectType()) || "Model".equalsIgnoreCase(lc.getObjectType());
	}

	private boolean isRelationshipType(String type) {
		return "Association".equalsIgnoreCase(type) || "Aggregation".equalsIgnoreCase(type)
				|| "Composition".equalsIgnoreCase(type) || "Generalization".equalsIgnoreCase(type)
				|| "AssociationClass".equalsIgnoreCase(type) || "GeneralizationSet".equalsIgnoreCase(type);
	}

	private boolean isRefinableType(String type) {
		return "Class".equalsIgnoreCase(type) || "Attribute".equalsIgnoreCase(type)
				|| "Operation".equalsIgnoreCase(type) || "Association".equalsIgnoreCase(type)
				|| "Aggregation".equalsIgnoreCase(type) || "Composition".equalsIgnoreCase(type)
				|| "Generalization".equalsIgnoreCase(type);
	}

	private boolean isUnknown(String value) {
		return value == null || value.trim().isEmpty() || "unknown".equalsIgnoreCase(value.trim())
				|| "not specified".equalsIgnoreCase(value.trim());
	}

	private String safe(String value) {
		return value == null ? "" : value.trim();
	}

	private void detectRelationshipEndpointIssues(UmlObjectLifecycle lc, List<ProcessDynamicsFinding> findings) {

		if (!isRelationshipType(lc.getObjectType())) {
			return;
		}

		if (isUnknown(lc.getObjectName())) {
			return;
		}

		for (UmlObjectEvent event : lc.getEvents()) {

			String from = safe(event.getRelationshipFrom());
			String to = safe(event.getRelationshipTo());

			boolean missingFrom = isUnknown(from);
			boolean missingTo = isUnknown(to);

			if (missingFrom || missingTo) {
				findings.add(finding("Relationship endpoint issue", lc,
						"The relationship '" + lc.getObjectName() + "' has incomplete endpoints at event "
								+ event.getIndex() + ": RelationshipFrom=" + printable(from) + ", RelationshipTo="
								+ printable(to) + ".",
						"The relationship was recorded before its endpoints were clearly available in the process log.",
						"Review whether this relationship was later connected to the intended UML elements, and consider improving the logging so endpoint updates are captured."));

				return;
			}
		}
	}

}