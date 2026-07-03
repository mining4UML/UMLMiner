package com.uniba.mining.umlprocess;

import java.util.ArrayList;
import java.util.List;

import com.uniba.mining.processfeedback.core.DomainViolationRule;

public class UmlViolationRulesFactory {

    public static List<DomainViolationRule> createRules() {
        List<DomainViolationRule> rules = new ArrayList<>();

        rules.add(new UmlAttributeTypeRule());
        rules.add(new UmlClassPlacementRule());
        rules.add(new UmlGenericProcessRule());

        return rules;
    }

    private UmlViolationRulesFactory() {
    }
}