package com.vetc.rule.core;

import com.vetc.rule.core.engine.RuleEngineService;
import com.vetc.rule.core.function.RuleFunctionRegistry;
import com.vetc.rule.core.validation.RuleValidationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.expression.spel.SpelEvaluationException;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class RuleEngineService2Test {

    private RuleValidationService validationService;
    private RuleFunctionRegistry functionRegistry;
    private RuleEngineService ruleEngineService;

    @BeforeEach
    void setup() {
        validationService = Mockito.mock(RuleValidationService.class);
        functionRegistry = Mockito.mock(RuleFunctionRegistry.class);
        ruleEngineService = new RuleEngineService(validationService, functionRegistry);
    }

    @Test
    void shouldReturnTrueWhenExpressionMatches() {
        // given
        String jsonRule = "{\"logic\":\"AND\",\"conditions\":[{\"field\":\"amount\",\"op\":\">\",\"value\":100}]}";
        Map<String, Object> ctx = new HashMap<>();
        ctx.put("amount", 200);

        when(validationService.validateAndBuild(jsonRule)).thenReturn("amount > 100");

        // when
        boolean result = ruleEngineService.evaluateJsonOrDslCondition(jsonRule, ctx, "APPROVE");

        // then
        assertThat(result).isTrue();
        verify(validationService, times(1)).validateAndBuild(jsonRule);
        verify(functionRegistry, times(1)).registerAll(any());
    }

    @Test
    void shouldReturnFalseWhenExpressionDoesNotMatch() {
        // given
        String jsonRule = "{\"logic\":\"AND\",\"conditions\":[{\"field\":\"amount\",\"op\":\">\",\"value\":500}]}";
        Map<String, Object> ctx = new HashMap<>();
        ctx.put("amount", 100);

        when(validationService.validateAndBuild(jsonRule)).thenReturn("#amount > 500");

        // when
        boolean result = ruleEngineService.evaluateJsonOrDslCondition(jsonRule, ctx, "REJECT");

        // then
        assertThat(result).isFalse();
    }

    @Test
    void shouldReturnFalseWhenValidationThrowsException() {
        // given
        String invalidRule = "invalid json";
        when(validationService.validateAndBuild(invalidRule)).thenThrow(new RuntimeException("Invalid JSON"));

        // when
        boolean result = ruleEngineService.evaluateJsonOrDslCondition(invalidRule, Map.of("amount", 100), "SKIP");

        // then
        assertThat(result).isFalse();
        verify(validationService, times(1)).validateAndBuild(invalidRule);
    }

    @Test
    void shouldAssignVariableWhenActionIsAssignment() {
        // given
        String jsonRule = "{\"logic\":\"AND\",\"conditions\":[{\"field\":\"amount\",\"op\":\">\",\"value\":50}]}";
        Map<String, Object> ctx = new HashMap<>();
        ctx.put("amount", 200);

        when(validationService.validateAndBuild(jsonRule)).thenReturn("amount > 50");

        // when
        boolean result = ruleEngineService.evaluateJsonOrDslCondition(jsonRule, ctx, "status='APPROVED'");

        // then
        assertThat(result).isTrue();
        assertThat(ctx).containsEntry("status", "APPROVED");
    }

    @Test
    void shouldHandleInvalidSpelGracefully() {
        // given
        String jsonRule = "{\"invalid\":true}";
        Map<String, Object> ctx = new HashMap<>();

        when(validationService.validateAndBuild(jsonRule)).thenReturn("#invalid#syntax");

        // when
        boolean result = ruleEngineService.evaluateJsonOrDslCondition(jsonRule, ctx, "ANY");

        // then
        assertThat(result).isFalse(); // vì SpEL bị lỗi cú pháp
    }
}
