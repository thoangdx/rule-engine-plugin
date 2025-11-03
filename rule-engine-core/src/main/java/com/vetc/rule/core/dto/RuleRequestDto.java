package com.vetc.rule.core.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RuleRequestDto {
    @JsonProperty("rule_code")
    private String ruleCode;
    @JsonProperty("rule_name")
    private String ruleName;
    private String domain;
    private Object conditions; // JSON object
    private Integer priority;
    private String description;
    private String action;
}
