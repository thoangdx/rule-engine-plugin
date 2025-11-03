package com.vetc.rule.core;

import com.vetc.rule.core.function.RuleFunctionRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import static org.assertj.core.api.Assertions.assertThat;

class RuleFunctionRegistryTest {

    @Test
    void shouldRegisterContainsFunction() throws Exception {
        RuleFunctionRegistry registry = new RuleFunctionRegistry();
        StandardEvaluationContext ctx = new StandardEvaluationContext();
        registry.registerAll(ctx);

        Object containsFn = ctx.lookupVariable("contains");
        assertThat(containsFn).isNotNull();
    }
}
