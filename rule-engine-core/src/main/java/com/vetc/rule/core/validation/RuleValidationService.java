package com.vetc.rule.core.validation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vetc.rule.core.builder.SpelExpressionBuilder;
import com.vetc.rule.core.dsl.RuleDslParser;
import com.vetc.rule.core.exception.RuleValidationException;
import com.vetc.rule.core.function.RuleFunctionRegistry;
import com.vetc.rule.core.model.ConditionNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Slf4j
public class RuleValidationService {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final SpelExpressionBuilder spelBuilder;
    private final ExpressionParser parser = new SpelExpressionParser();
    private final RuleFunctionRegistry functionRegistry;
    private final RuleDslParser dslParser;
    /**
     * Validate JSON và build SpEL
     */
    public String validateAndBuild(String jsonOrDsl) {
        try {
            log.info("🔍 Validating rule jsonOrDsl: {}", jsonOrDsl);

            // ✅ Nhận biết DSL (bắt đầu bằng "IF")
            if (jsonOrDsl.trim().toUpperCase().startsWith("IF ")) {
                var parsed = dslParser.parse(jsonOrDsl);
                parser.parseExpression(parsed.spelExpression()); // check syntax
                log.info("✅ Valid DSL rule -> SpEL: {}", parsed.spelExpression());
                return parsed.spelExpression();
            }

            // ✅ Nếu không phải DSL → coi là JSON

            ConditionNode node = objectMapper.readValue(jsonOrDsl, ConditionNode.class);

            validateNode(node);

            String spelExpr = spelBuilder.buildExpression(node);
            // ✅ Create context with functions for validation
            StandardEvaluationContext ctx = new StandardEvaluationContext();
            functionRegistry.registerAll(ctx);
            // Thử parse SpEL để chắc chắn hợp lệ
            parser.parseExpression(spelExpr);

            log.info("✅ Valid rule, built SpEL = {}", spelExpr);
            return spelExpr;

        } catch (Exception e) {
            log.error("❌ Invalid rule JSON or SpEL: {}", e);
            throw new RuleValidationException("Invalid rule: " + e.getMessage());
        }
    }

    /**
     * Validate logic điều kiện (field, operator)
     */
    private void validateNode(ConditionNode node) {
        if (node == null) {
            throw new RuleValidationException("Condition node cannot be null");
        }

        // Nếu là group condition
        if (node.getConditions() != null && !node.getConditions().isEmpty()) {
            for (ConditionNode child : node.getConditions()) {
                validateNode(child);
            }
        } else {
            if(node.getSpElExpression() != null && !node.getSpElExpression().isEmpty()) {
                // bỏ qua validate các field khác nếu dùng spElExpression
                return;
            }
            if (node.getField() == null || node.getOp() == null) {
                throw new RuleValidationException("Missing field or operator");
            }
            if (node.getOp().equalsIgnoreCase("between")) {
                if (node.getFrom() == null || node.getTo() == null) {
                    throw new RuleValidationException("Operator 'between' requires both from and to");
                }
            }
        }
    }
    public String validateSpElExpression(String expression) {
        try {
            parser.parseExpression(expression);
            log.info("✅ Valid SpEL expression: {}", expression);
            return expression;
        } catch (Exception e) {
            log.error("❌ Invalid SpEL expression: {}", e);
            throw new RuleValidationException("Invalid SpEL expression: " + e.getMessage());
        }
    }
}
