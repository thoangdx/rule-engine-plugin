package com.vetc.rule.core;

import com.vetc.rule.core.builder.SpelExpressionBuilder;
import com.vetc.rule.core.model.ConditionNode;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

class SpelExpressionBuilderTest {

    private final SpelExpressionBuilder builder = new SpelExpressionBuilder();

    @Test
    void shouldBuildSimpleExpression() {
        ConditionNode node = ConditionNode.builder()
            .field("amount")
            .op(">")
            .value(100000)
            .build();

        String expr = builder.buildExpression(node);
        assertThat(expr).isEqualTo("(amount > 100000)");
    }

    @Test
    void shouldBuildComplexNestedExpression() {
        ConditionNode node = ConditionNode.builder()
            .logic("AND")
            .conditions(List.of(
                ConditionNode.builder().field("amount").op(">").value(100000).build(),
                ConditionNode.builder().field("region").op("==").value("HCM").build()
            ))
            .build();

        String expr = builder.buildExpression(node);
        assertThat(expr).isEqualTo("((amount > 100000) AND (region == 'HCM'))");
    }
}
