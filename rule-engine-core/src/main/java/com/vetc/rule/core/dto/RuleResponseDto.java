package com.vetc.rule.core.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RuleResponseDto {
    @JsonProperty("rule_code")
    private String ruleCode;
    @JsonProperty("rule_name")
    private String ruleName;
    @JsonProperty("spel_expression")
    private String spelExpression;
    private Integer priority;
    private String domain;
    private String status;
    private String description;
    private String action; // 🔥 thêm action
}
