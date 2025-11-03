package com.vetc.rule.core;

import com.vetc.rule.core.builder.SpelExpressionBuilder;
import com.vetc.rule.core.dsl.RuleDslParser;
import com.vetc.rule.core.function.RuleFunctionRegistry;
import com.vetc.rule.core.validation.RuleValidationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RuleValidationServiceTest {

    private RuleValidationService validationService;

    @BeforeEach
    void setup() {
        validationService = new RuleValidationService(
            new SpelExpressionBuilder(),
            new RuleFunctionRegistry(),
            new RuleDslParser()
        );
    }

    @Test
    void shouldValidateAndBuildFromJson() {
        String json = """
            {
              "logic": "AND",
              "conditions": [
                {"field": "amount", "op": ">", "value": 100000},
                {"field": "region", "op": "==", "value": "HCM"}
              ]
            }
            """;
        String spel = validationService.validateAndBuild(json);
        assertThat(spel).contains("amount > 100000");
        assertThat(spel).contains("region == 'HCM'");
    }

    @Test
    void shouldValidateAndBuildFromDsl() {
        String dsl = "IF amount > 100000 AND region == 'HCM' THEN APPROVE";
        String spel = validationService.validateAndBuild(dsl);
        assertThat(spel).contains("amount > 100000");
    }
    @Test
    void shouldValidateAndBuildFromDirectSpElExpress() {
        String json = """
            {
              "spElExpression": "amount > 100000 AND region == 'HCM'"
            }
            """;
        String spel = validationService.validateAndBuild(json);
        assertThat(spel).contains("amount > 100000");
        assertThat(spel).contains("region == 'HCM'");
    }
}
