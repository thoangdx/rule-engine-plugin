package com.vetc.rule.core.builder;

import com.vetc.rule.core.exception.RuleValidationException;

import com.vetc.rule.core.model.ConditionNode;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;


public class SpelExpressionBuilder {

    public String buildExpression(ConditionNode node) {
        if (node == null) {
            throw new IllegalArgumentException("Condition node cannot be null");
        }
        // Nếu node có biểu thức tùy chỉnh, ưu tiên dùng luôn
        if (node.getSpElExpression() != null && !node.getSpElExpression().isBlank()) {
            return "(" + node.getSpElExpression() + ")";
        }
        // Nếu có sub-condition (nhóm điều kiện)
        if (!CollectionUtils.isEmpty(node.getConditions())) {
            return buildGroup(node);
        }

        // Nếu là điều kiện đơn
        return buildSingle(node);
    }

    private String buildGroup(ConditionNode node) {
        String logic = node.getLogic() != null ? node.getLogic().toUpperCase() : "AND";
        List<ConditionNode> children = node.getConditions();
        StringBuilder expr = new StringBuilder("(");
        for (int i = 0; i < children.size(); i++) {
            expr.append(buildExpression(children.get(i)));
            if (i < children.size() - 1) expr.append(" ").append(logic).append(" ");
        }
        expr.append(")");
        return expr.toString();
    }

    private String buildSingle(ConditionNode node) {
        String field = node.getField();
        String op = node.getOp();

        if (field == null || op == null)
            throw new IllegalArgumentException("Invalid condition: missing field or operator");

        return switch (op.toLowerCase()) {
            // So sánh logic cơ bảnx
            case "==" -> String.format("(%s == '%s')", field, node.getValue());
            case "=" -> String.format("(%s == '%s')", field, node.getValue());
            case "!=" -> String.format("(%s != '%s')", field, node.getValue());
            case ">" -> String.format("(%s > %s)", field, node.getValue());
            case "<" -> String.format("(%s < %s)", field, node.getValue());
            case ">=" -> String.format("(%s >= %s)", field, node.getValue());
            case "<=" -> String.format("(%s <= %s)", field, node.getValue());
            // Toán học (có thể kết hợp trong điều kiện phức tạp)
            case "+" -> String.format("(%s + %s)", field, node.getValue());
            case "-" -> String.format("(%s - %s)", field, node.getValue());
            case "*" -> String.format("(%s * %s)", field, node.getValue());
            case "/" -> String.format("(%s / %s)", field, node.getValue());
            case "%" -> String.format("(%s %% %s)", field, node.getValue());
            // Mở rộng khác
            case "in" -> String.format("(%s in %s)", field, node.getValue());
            case "between" -> String.format("(%s >= %s and %s <= %s)", field, node.getFrom(), field, node.getTo());
            default -> throw new RuleValidationException("Unsupported operator: " + op);
        };
    }
}
