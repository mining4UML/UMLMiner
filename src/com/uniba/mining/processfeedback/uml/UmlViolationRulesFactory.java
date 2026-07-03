package com.uniba.mining.processfeedback.uml;

import java.util.ArrayList;
import java.util.List;

import com.uniba.mining.processfeedback.core.DomainViolationRule;

public class UmlViolationRulesFactory {

	public static List<DomainViolationRule> createRules() {

		List<DomainViolationRule> rules = new ArrayList<>();

		/*
		 * Rule order is important.
		 *
		 * The interpretation engine applies the FIRST matching rule. Therefore, the
		 * most specific UML rules must appear before more general ones.
		 */

		// Class-related process deviations
		rules.add(new UmlClassRefinementRule());

		// Attribute-related process deviations
		rules.add(new UmlAttributeRefinementRule());

		// Relationship-related process deviations
		rules.add(new UmlAssociationRule());
		rules.add(new UmlGeneralizationRule());
		rules.add(new UmlAggregationRule());

		// Diagram-level process deviations
		rules.add(new UmlDiagramPlacementRule());

		// Final fallback
		rules.add(new UmlGenericProcessRule());

		return rules;
	}

	private UmlViolationRulesFactory() {
	}
}