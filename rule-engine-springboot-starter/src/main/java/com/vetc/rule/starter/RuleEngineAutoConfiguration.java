package com.vetc.rule.starter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vetc.rule.core.RuleEngine;
import com.vetc.rule.core.builder.SpelExpressionBuilder;
import com.vetc.rule.core.dsl.RuleDslParser;
import com.vetc.rule.core.engine.RuleEngineService;
import com.vetc.rule.core.function.RuleFunctionRegistry;
import com.vetc.rule.core.validation.RuleValidationService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
/**
 * Auto-configuration cho VETC Rule Engine.
 * Khi thêm dependency starter, Spring Boot sẽ tự động khởi tạo các bean core:
 *  - RuleEngineService
 *  - RuleValidationService
 *  - RuleFunctionRegistry
 *  - RuleDslParser
 *  - SpelExpressionBuilder
 *  - RuleEngine (entry API chính)
 */
@Configuration
public class RuleEngineAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public RuleFunctionRegistry ruleFunctionRegistry() {
        return new RuleFunctionRegistry();
    }

    @Bean
    @ConditionalOnMissingBean
    public RuleDslParser ruleDslParser() {
        return new RuleDslParser();
    }

    @Bean
    @ConditionalOnMissingBean
    public SpelExpressionBuilder spelExpressionBuilder() {
        return new SpelExpressionBuilder();
    }

    @Bean
    @ConditionalOnMissingBean
    public RuleValidationService ruleValidationService(RuleDslParser dslParser, SpelExpressionBuilder spelBuilder,
        RuleFunctionRegistry registry) {
        return new RuleValidationService( spelBuilder, registry, dslParser);
    }

    @Bean
    @ConditionalOnMissingBean
    public RuleEngineService ruleEngineService(RuleValidationService validationService, RuleFunctionRegistry registry) {
        return new RuleEngineService(validationService, registry);
    }
    /**
     * 🔥 Entry point cho toàn bộ Rule Engine
     * Tự động có sẵn khi import starter
     */
    @Bean
    @ConditionalOnMissingBean
    public RuleEngine ruleEngine(RuleEngineService ruleEngineService,
        RuleValidationService ruleValidationService) {
        return new RuleEngine(ruleEngineService, ruleValidationService);
    }
}
