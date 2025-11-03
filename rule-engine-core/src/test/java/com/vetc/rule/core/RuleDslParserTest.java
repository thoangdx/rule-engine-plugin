package com.vetc.rule.core;

import com.vetc.rule.core.dsl.RuleDslParser;
import com.vetc.rule.core.dsl.RuleDslParser.DslParseResult;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class RuleDslParserTest {

    @Test
    void shouldParseDslToJson() {
        RuleDslParser parser = new RuleDslParser();
        DslParseResult result = parser.parse("IF amount > 100000 THEN APPROVE");
        assertThat(result.spelExpression()).contains("amount");
        assertThat(result.spelExpression()).contains("100000");
    }
}
