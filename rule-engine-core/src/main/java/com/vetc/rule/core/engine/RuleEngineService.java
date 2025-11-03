package com.vetc.rule.core.engine;

import com.vetc.rule.core.function.RuleFunctionRegistry;
import com.vetc.rule.core.validation.RuleValidationService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.expression.MapAccessor;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;


@RequiredArgsConstructor
@Slf4j
public class RuleEngineService {

    private final RuleValidationService validationService;
    private final ExpressionParser parser = new SpelExpressionParser();
    private final RuleFunctionRegistry functionRegistry;

    /**
     * Evaluate một rule duy nhất
     */
    public boolean evaluateJsonOrDslCondition(String jsonOrDsl, Map<String, Object> contextData, String action) {
        try {
            String spelExpression = validationService.validateAndBuild(jsonOrDsl);
            StandardEvaluationContext context = new StandardEvaluationContext(contextData);
            context.addPropertyAccessor(new MapAccessor());
            // 🔥 inject custom functions
            functionRegistry.registerAll(context);
            Expression expr = parser.parseExpression(spelExpression);
            Boolean result = expr.getValue(context, Boolean.class);
            if (Boolean.TRUE.equals(result)) {
                executeAction(action, contextData);
            }
            return Boolean.TRUE.equals(result);
        } catch (Exception e) {
            log.error("❌ Evaluate rule error: {}", e.getMessage());
            return false;
        }
    }
    /**
     * Evaluate một rule duy nhất
     */
    public boolean evaluateSpelExpression(String spelExpression, Map<String, Object> contextData, String action) {
        try {
            StandardEvaluationContext context = new StandardEvaluationContext(contextData);
            context.addPropertyAccessor(new MapAccessor());
            // 🔥 inject custom functions
            functionRegistry.registerAll(context);
            Expression expr = parser.parseExpression(spelExpression);
            Boolean result = expr.getValue(context, Boolean.class);
            if (Boolean.TRUE.equals(result)) {
                executeAction(action, contextData);
            }
            return Boolean.TRUE.equals(result);
        } catch (Exception e) {
            log.error("❌ Evaluate rule error: {}", e);
            return false;
        }
    }

    private void executeAction(String action, Map<String, Object> context) {
        if (action == null) {
            return;
        }

        // Trường hợp gán biến
        if (action.contains("=")) {
            String[] parts = action.split("=", 2);
            String var = parts[0].trim();
            String val = parts[1].trim().replaceAll("'", "");
            context.put(var, val);
            log.info("💡 Set variable [{}] = {}", var, val);
        }
        // Trường hợp gọi hàm (#functionName)
        else if (action.startsWith("#")) {
            Expression expr = parser.parseExpression(action);
            StandardEvaluationContext ctx = new StandardEvaluationContext();
            functionRegistry.registerAll(ctx);
            expr.getValue(ctx);
            log.info("⚙️ Executed function action: {}", action);
        }
        // Trường hợp action text
        else {
            log.info("✅ Rule Action Executed: {}", action);
        }
    }
}
