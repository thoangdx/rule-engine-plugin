package com.vetc.rule.core.dsl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Simple DSL parser that converts "IF ... THEN ..." to SpEL expression
 */

@Slf4j
public class RuleDslParser {

    private static final Pattern IF_PATTERN = Pattern.compile("^IF\\s+(.+)\\s+THEN\\s+(.*)$", Pattern.CASE_INSENSITIVE);

    public DslParseResult parse(String dslText) {
        if (dslText == null || dslText.isBlank()) {
            throw new IllegalArgumentException("DSL text cannot be empty");
        }

        Matcher matcher = IF_PATTERN.matcher(dslText.trim());
        if (!matcher.find()) {
            throw new IllegalArgumentException("Invalid DSL format. Expected: IF <condition> THEN <action>");
        }

        String conditionPart = matcher.group(1).trim();
        String actionPart = matcher.group(2).trim();

        // Chuyển cú pháp DSL sang SpEL hợp lệ
        String spelExpr = translateToSpel(conditionPart);

        log.info("✅ Parsed DSL to SpEL: {}", spelExpr);
        return new DslParseResult(spelExpr, actionPart);
    }

    /**
     * Chuyển một phần điều kiện DSL sang SpEL (rule logic)
     */
    private String translateToSpel(String condition) {
        // Chuẩn hóa toán tử
        if(condition.contains("=") && !condition.contains("==") && !condition.contains("!=")) {
            condition = condition.replaceAll("=", "==");
        }
        return condition
            .replaceAll("(?i)\\bAND\\b", "and")
            .replaceAll("(?i)\\bOR\\b", "or")
            .replaceAll("(?i)\\bNOT\\b", "not")
            .replaceAll("!=", "!=")
            .replaceAll("(?i)\\bIS NULL\\b", "== null")
            .replaceAll("(?i)\\bIS NOT NULL\\b", "!= null")
            .trim();
    }

    public record DslParseResult(String spelExpression, String action) {}
}
