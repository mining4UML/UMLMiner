package com.uniba.mining.processfeedback.core;

import java.util.ArrayList;
import java.util.List;

public class ViolationInterpretationEngine {

	private final List<DomainViolationRule> rules;

	public ViolationInterpretationEngine(List<DomainViolationRule> rules) {
		this.rules = rules;
	}

	public List<InterpretedViolation> interpret(List<ProcessViolation> violations) {

		List<InterpretedViolation> interpreted = new ArrayList<>();

		for (ProcessViolation violation : violations) {

			if (violation == null || !violation.isViolation()) {
				continue;
			}

			boolean matched = false;

			for (DomainViolationRule rule : rules) {

				if (rule.matches(violation)) {

					System.out
							.println("[ViolationInterpretationEngine] Matched rule: " + rule.getClass().getSimpleName()
									+ " | Activity=" + violation.getActivityName() + " | ElementType="
									+ violation.getElementType() + " | ElementName=" + violation.getElementName());

					InterpretedViolation result = rule.interpret(violation);

					if (result != null) {
						interpreted.add(result);
					}

					matched = true;
					break;
				}
			}

			if (!matched) {
				System.out.println("[ViolationInterpretationEngine] No rule matched | Activity="
						+ violation.getActivityName() + " | ElementType=" + violation.getElementType()
						+ " | ElementName=" + violation.getElementName());
			}
		}

		return interpreted;
	}
}