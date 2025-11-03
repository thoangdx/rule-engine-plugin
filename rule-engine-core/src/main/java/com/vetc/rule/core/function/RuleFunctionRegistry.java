package com.vetc.rule.core.function;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;


@Slf4j
public class RuleFunctionRegistry {

    public void registerAll(StandardEvaluationContext context) {
        try {
            // Math functions
            context.registerFunction("round", Math.class.getDeclaredMethod("round", double.class));
            context.registerFunction("abs", Math.class.getDeclaredMethod("abs", double.class));
            context.registerFunction("max", Math.class.getDeclaredMethod("max", double.class, double.class));
            context.registerFunction("min", Math.class.getDeclaredMethod("min", double.class, double.class));

            // String helper
            context.registerFunction("contains", RuleFunctionRegistry.class.getDeclaredMethod("contains", String.class, String.class));
            context.registerFunction("equalsIgnoreCase", RuleFunctionRegistry.class.getDeclaredMethod("equalsIgnoreCase", String.class, String.class));

            // Utility date helper
            context.registerFunction("daysBetween", RuleFunctionRegistry.class.getDeclaredMethod("daysBetween", LocalDate.class, LocalDate.class));
            context.registerFunction("isWeekend", RuleFunctionRegistry.class.getDeclaredMethod("isWeekend", LocalDate.class));

            log.info("✅ Rule functions registered successfully.");
        } catch (Exception e) {
            log.error("❌ Error registering functions: {}", e);
        }
    }

    // ========== Utility Implementations ==========

    public static boolean contains(String str, String sub) {
        return str != null && sub != null && str.contains(sub);
    }

    public static boolean equalsIgnoreCase(String s1, String s2) {
        return s1 != null && s2 != null && s1.equalsIgnoreCase(s2);
    }

    public static long daysBetween(LocalDate from, LocalDate to) {
        return ChronoUnit.DAYS.between(from, to);
    }

    public static boolean isWeekend(LocalDate date) {
        return date != null &&
            (date.getDayOfWeek().getValue() == 6 || date.getDayOfWeek().getValue() == 7);
    }
}
