package com.vetc.rule.core;

import com.vetc.rule.core.engine.RuleEngineService;
import com.vetc.rule.core.function.RuleFunctionRegistry;
import com.vetc.rule.core.validation.RuleValidationService;
import com.vetc.rule.core.builder.SpelExpressionBuilder;
import com.vetc.rule.core.dsl.RuleDslParser;
import java.util.HashMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;

class RuleEngineServiceTest {

    private RuleEngineService ruleEngineService;

    @BeforeEach
    void setup() {
        RuleValidationService validation = new RuleValidationService(
            new SpelExpressionBuilder(),
            new RuleFunctionRegistry(),
            new RuleDslParser()
        );
        ruleEngineService = new RuleEngineService(validation, new RuleFunctionRegistry());
    }

    @Test
    void shouldEvaluateTrueRule() {
        boolean result = ruleEngineService.evaluateSpelExpression(
            "amount > 100000 and region == 'HCM'",
            Map.of("amount", 200000, "region", "HCM"),
            "APPROVE"
        );
        assertThat(result).isTrue();
    }

    @Test
    void shouldEvaluateFalseRule() {
        boolean result = ruleEngineService.evaluateSpelExpression(
            "#amount > 100000 and #region == 'HCM'",
            Map.of("amount", 50000, "region", "HN"),
            "APPROVE"
        );
        assertThat(result).isFalse();
    }

    @Test
    void shouldExecuteVariableAssignmentAction() {
        Map<String, Object> ctx = new HashMap<>();
        ctx.put("amount", 200000);
        boolean matched = ruleEngineService.evaluateSpelExpression(
            "(amount > 100000)",
            ctx,
            "status = 'APPROVED'"
        );
        assertThat(matched).isTrue();
        assertThat(ctx).containsEntry("status", "APPROVED");
    }
}
