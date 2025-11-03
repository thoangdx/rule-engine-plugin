package com.vetc.rule.core.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RuleMatchRequestDto {
    @JsonProperty("rule_codes")
    private List<String> ruleCodes; // danh sách rule cần check
    private Map<String, Object> context; // dữ liệu đầu vào
}
