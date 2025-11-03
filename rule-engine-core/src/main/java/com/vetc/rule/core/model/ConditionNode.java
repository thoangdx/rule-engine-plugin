package com.vetc.rule.core.model;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConditionNode {
    private String field;           // ví dụ: "amount"
    private String op;              // ví dụ: ">", "=", "between"
    private Object value;           // giá trị cho các phép so sánh
    private Object from;            // dùng cho between
    private Object to;              // dùng cho between
    private String logic;           // "AND" / "OR"
    private List<ConditionNode> conditions; // các điều kiện con (nhóm)
    // 🔥 NEW: cho phép biểu thức tùy chỉnh
    private String spElExpression;      // ví dụ: "(amount + fee - discount) * rate > limit"
}
