# 🧩 VETC Rule Engine Plugin

> ⚙️ **Lightweight, pluggable, and extensible rule engine**  
> for Java & Spring Boot projects — using **SpEL**, **DSL**, and **JSON rule format**.

---

## 🚀 Giới thiệu

**VETC Rule Engine** là một thư viện (plugin) Java  
cho phép bạn **định nghĩa, validate, và thực thi các rule nghiệp vụ**  
theo cách **linh hoạt, mở rộng và dễ tích hợp** vào bất kỳ project nào.

Engine hỗ trợ:
- ✅ **SpEL (Spring Expression Language)**
- ✅ **JSON rule tree** (cấu trúc logic dạng cây)
- ✅ **DSL (Domain Specific Language)** — cú pháp “IF ... THEN ...” thân thiện
- ✅ **Custom function**: `#contains()`, `#round()`, `#daysBetween()`, ...
- ✅ **Dễ nhúng vào microservice** (plugin .jar, không cần service riêng)
- ✅ **Tương thích Spring Boot auto-configuration**, có sẵn **entry API `RuleEngine`**

---

## 📦 Cài đặt

### 1️⃣ Thêm dependency (Spring Boot project)
```xml
<dependency>
  <groupId>com.vetc</groupId>
  <artifactId>rule-engine-springboot-starter</artifactId>
  <version>1.0.0</version>
</dependency>
```

### 2️⃣ Hoặc trong ứng dụng Java thuần
```xml
<dependency>
  <groupId>com.vetc</groupId>
  <artifactId>rule-engine-core</artifactId>
  <version>1.0.0</version>
</dependency>
```

---

## ⚙️ Cấu trúc chính
```
com.vetc.rule.core
├── builder/          → SpelExpressionBuilder.java
├── dsl/              → RuleDslParser.java
├── engine/           → RuleEngineService.java
├── validation/       → RuleValidationService.java
├── function/         → RuleFunctionRegistry.java
├── model/            → ConditionNode.java
└── RuleEngine.java   → Entry point tiện dụng
```

---

## 🧠 Nguyên lý hoạt động

1. Người dùng định nghĩa rule bằng DSL hoặc JSON.
2. `RuleValidationService` → chuyển DSL/JSON thành **SpEL expression**.
3. `RuleEngineService` → evaluate SpEL expression với **context đầu vào**.
4. Kết quả trả về: `true` / `false` (rule match).

---

## 💡 Cách sử dụng

### ✅ 1️⃣ Validate & Build Rule
```java
@Autowired
private RuleValidationService ruleValidationService;

String dsl = "IF (amount + fee) * rate > 100000 AND #contains(region, 'HCM') THEN APPROVE";
String spel = ruleValidationService.validateAndBuild(dsl);
System.out.println("Built SpEL: " + spel);
```

### ✅ 2️⃣ Evaluate Rule
```java
@Autowired
private RuleEngineService ruleEngineService;

Map<String, Object> ctx = Map.of(
    "amount", 95000,
    "fee", 10000,
    "rate", 1.05,
    "region", "HCM"
);

String spel = "(amount + fee) * rate > 100000 and #contains(region, 'HCM')";
boolean match = ruleEngineService.evaluateRule(spel, ctx);
System.out.println("Rule matched = " + match);
```

### ✅ 3️⃣ Dùng JSON rule
```java
String json = """
{
  "logic": "AND",
  "conditions": [
    {"field": "amount", "op": ">", "value": 100000},
    {"field": "region", "op": "=", "value": "HCM"}
  ]
}
""";
String spel = ruleValidationService.validateAndBuild(json);
boolean result = ruleEngineService.evaluateRule(spel, Map.of("amount", 150000, "region", "HCM"));
```

### ✅ 4️⃣ Java thuần
```java
RuleFunctionRegistry registry = new RuleFunctionRegistry();
RuleDslParser parser = new RuleDslParser();
SpelExpressionBuilder builder = new SpelExpressionBuilder();
RuleValidationService validator = new RuleValidationService(parser, builder, registry);
RuleEngineService engine = new RuleEngineService(registry);
RuleEngine ruleEngine = new RuleEngine(engine, validator);

String spel = ruleEngine.buildExpression("IF amount > 200000 THEN APPROVE");
boolean match = ruleEngine.evaluate(spel, Map.of("amount", 250000));
```

---

## 🧩 Entry API: `RuleEngine`

Khi sử dụng **Spring Boot Starter**, bạn không cần inject từng service riêng lẻ.  
Starter sẽ tự động cấu hình toàn bộ core service và cung cấp bean **`RuleEngine`** sẵn dùng.

### Ví dụ:
```java
import com.vetc.rule.core.RuleEngine;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class TransactionRuleEvaluator {

    private final RuleEngine ruleEngine;

    public boolean checkRule() {
        String dsl = "IF (amount + fee) * rate > 100000 AND #contains(region, 'HCM') THEN APPROVE";
        String spel = ruleEngine.buildExpression(dsl);

        return ruleEngine.evaluate(spel, Map.of(
                "amount", 95000,
                "fee", 10000,
                "rate", 1.05,
                "region", "HCM"
        ));
    }
}
```

✅ Khi import starter, Spring Boot tự động tạo các bean:
| Bean | Type | Mô tả |
|------|------|--------|
| `ruleEngine` | `RuleEngine` | Entry point chính (gọi evaluate & build) |
| `ruleEngineService` | `RuleEngineService` | Core evaluator |
| `ruleValidationService` | `RuleValidationService` | Validate DSL/JSON |
| `ruleFunctionRegistry` | `RuleFunctionRegistry` | Đăng ký hàm tùy chỉnh |
| `ruleDslParser` | `RuleDslParser` | Parse DSL `IF...THEN...` |
| `spelExpressionBuilder` | `SpelExpressionBuilder` | Build SpEL từ JSON |

---

## 🧮 Cú pháp DSL
| Nhóm | Cú pháp | Ví dụ |
|------|----------|--------|
| So sánh | `>`, `<`, `>=`, `<=`, `=`, `!=` | `amount > 100000` |
| Logic | `AND`, `OR`, `NOT` | `(A AND B) OR C` |
| Toán học | `+`, `-`, `*`, `/`, `%` | `(amount + fee) * rate` |
| Hàm | `#contains(a,b)` | `#contains(region, 'HCM')` |
| Gán | `THEN result = 'APPROVE'` | Gán biến khi match |

---

## 🧩 Custom Functions
| Hàm | Mô tả | Ví dụ |
|------|--------|--------|
| `#contains(a,b)` | Kiểm tra chuỗi con | `#contains(productCode, 'ACB')` |
| `#equalsIgnoreCase(a,b)` | So sánh không phân biệt hoa thường | `#equalsIgnoreCase(region, 'hcm')` |
| `#daysBetween(d1,d2)` | Số ngày giữa hai ngày | `#daysBetween(txnDate, today)` |
| `#isWeekend(d)` | Kiểm tra cuối tuần | `#isWeekend(today)` |
| `#round(x)` | Làm tròn số | `#round(amount * 1.1)` |

---

## ⚙️ Tích hợp với Spring Boot
Starter tự động cấu hình qua:
```
com.vetc.rule.starter.RuleEngineAutoConfiguration
```

Các bean được tạo tự động, trong đó `RuleEngine` là entry API tiện dụng để gọi:
```java
ruleEngine.buildExpression(dsl);
ruleEngine.evaluate(spel, context);
```

---

## 🧱 Build & Publish
```bash
mvn clean install
```
Triển khai lên Nexus nội bộ:
```bash
mvn deploy -DaltDeploymentRepository=vetc-nexus::default::http://nexus.vetc.com.vn/repository/maven-releases/
```

---

## 🧭 Roadmap
| Phiên bản | Tính năng |
|------------|------------|
| 1.1.0 | Caffeine cache cho rule |
| 1.2.0 | Rule chaining (Rule Flow) |
| 1.3.0 | Decision Table |
| 1.4.0 | AI-assisted rule generation |
| 2.0.0 | Visual Rule Designer |

---

## ✨ Ví dụ thực tế
```
IF (amount + fee) * rate > 100000 AND #contains(region, 'HCM')
THEN APPROVE
```
Context:
```json
{
  "amount": 95000,
  "fee": 10000,
  "rate": 1.05,
  "region": "HCM"
}
```
**Kết quả:**
```json
{
  "matched": true,
  "action": "APPROVE"
}
```

---

> 🧩 **VETC Rule Engine Plugin**  
> Smart · Lightweight · Extensible  
> Made for microservices, by engineers 🚀

