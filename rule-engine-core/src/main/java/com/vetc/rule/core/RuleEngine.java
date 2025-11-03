package com.vetc.rule.core;

import com.vetc.rule.core.engine.RuleEngineService;
import com.vetc.rule.core.validation.RuleValidationService;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public class RuleEngine {

    private final RuleEngineService ruleEngineService;
    private final RuleValidationService validationService;

    /**
     * Evaluate a rule SpEL or DSL string with context
     */
    public boolean evaluateJsonOrDslCondition(String jsonOrDsl, Map<String, Object> context,String action) {
        return ruleEngineService.evaluateJsonOrDslCondition(jsonOrDsl, context,action);
    }
    /**
     * Evaluate a rule SpEL or DSL string with context
     */
    public boolean evaluateSpelExpression(String ruleExpr, Map<String, Object> context,String action) {
        return ruleEngineService.evaluateSpelExpression(ruleExpr, context,action);
    }

    /**
     * Validate and build SpEL expression from JSON or DSL
     */
    public String buildSpELExpression(String jsonOrDsl) {
        return validationService.validateAndBuild(jsonOrDsl);
    }
}
