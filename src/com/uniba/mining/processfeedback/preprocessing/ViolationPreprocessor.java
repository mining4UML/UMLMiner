package com.uniba.mining.processfeedback.preprocessing;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.uniba.mining.processfeedback.core.ProcessViolation;

public class ViolationPreprocessor {

	private static final int MAX_VIOLATIONS_FOR_LLM = 15;

	public List<ProcessViolation> preprocess(List<ProcessViolation> violations) {

		Map<String, ProcessViolation> uniqueViolations = new LinkedHashMap<>();

		for (ProcessViolation violation : violations) {

			if (isNoise(violation)) {
				continue;
			}

			String key = buildGroupingKey(violation);

			if (!uniqueViolations.containsKey(key)) {
				uniqueViolations.put(key, violation);
			}
		}

		List<ProcessViolation> result = new ArrayList<>(uniqueViolations.values());

		result.sort(Comparator.comparingInt(this::priority).reversed().thenComparing(v -> safe(v.getActivityIndex())));

		result = limitOnePerElement(result);

		if (result.size() > MAX_VIOLATIONS_FOR_LLM) {
			return new ArrayList<>(result.subList(0, MAX_VIOLATIONS_FOR_LLM));
		}

		return result;
	}

	private boolean isNoise(ProcessViolation v) {

		if (v == null) {
			return true;
		}

		if ("init".equalsIgnoreCase(v.getConstraint())) {
			return true;
		}

		if ("Project".equalsIgnoreCase(v.getElementType())) {
			return true;
		}

		if ("Project opened".equalsIgnoreCase(v.getActivityName())) {
			return true;
		}

		if ("name property updated for Project".equalsIgnoreCase(v.getActivityName())) {
			return true;
		}

		return false;
	}

	private String buildGroupingKey(ProcessViolation v) {

		return String.join("|", safe(v.getConstraint()), safe(v.getActivityName()), safe(v.getElementType()),
				safe(v.getElementName()), safe(v.getPropertyName()), safe(v.getRelationshipFrom()),
				safe(v.getRelationshipTo()));
	}

	private int priority(ProcessViolation v) {

		String elementType = safe(v.getElementType());
		String activity = safe(v.getActivityName());
		String elementName = safe(v.getElementName());

		/*
		 * Important: Priority must be based on the observed event, not on the full
		 * Declare activity pair contained in Activities.
		 *
		 * Activities is useful as evidence, but it may mention a second activity that
		 * is not the actual violated event.
		 */

		if (contains(elementType, "Attribute") || contains(activity, "Attribute")) {
			return 100;
		}

		if (contains(elementType, "Association") || contains(activity, "Association")) {

			if ("unknown".equalsIgnoreCase(elementName) || elementName.isEmpty()) {
				return 50;
			}

			return 90;
		}

		if (contains(elementType, "Generalization") || contains(activity, "Generalization")) {

			if ("unknown".equalsIgnoreCase(elementName) || elementName.isEmpty()) {
				return 55;
			}

			return 85;
		}

		if (contains(elementType, "Aggregation") || contains(activity, "Aggregation")) {

			if ("unknown".equalsIgnoreCase(elementName) || elementName.isEmpty()) {
				return 50;
			}

			return 80;
		}

		if (contains(elementType, "Class") || contains(activity, "Class added")) {

			if ("Class".equalsIgnoreCase(elementName) || elementName.isEmpty()) {
				return 45;
			}

			return 70;
		}

		/*
		 * ClassDiagram events are often structural or view-level events. They should
		 * not dominate the feedback unless no more specific UML element violation is
		 * available.
		 */
		if (contains(elementType, "ClassDiagram")) {
			return 15;
		}

		if (contains(elementType, "Package")) {
			return 10;
		}

		return 5;
	}

	private List<ProcessViolation> limitOnePerElement(List<ProcessViolation> violations) {

		Map<String, ProcessViolation> selected = new LinkedHashMap<>();

		for (ProcessViolation v : violations) {
			String key = safe(v.getElementId());

			if (key.isEmpty()) {
				key = safe(v.getElementType()) + "|" + safe(v.getElementName());
			}

			if (!selected.containsKey(key)) {
				selected.put(key, v);
			}
		}

		return new ArrayList<>(selected.values());
	}

	private boolean contains(String value, String token) {

		return value != null && token != null && value.toLowerCase().contains(token.toLowerCase());
	}

	private String safe(String value) {

		return value == null ? "" : value.trim();
	}
}