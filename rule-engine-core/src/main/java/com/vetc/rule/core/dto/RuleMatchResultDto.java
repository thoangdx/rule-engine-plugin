package com.vetc.rule.core.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RuleMatchResultDto {
    @JsonProperty("rule_code")
    private String ruleCode;
    private boolean matched;
    @JsonProperty("spel_expression")
    private String spelExpression;
}
