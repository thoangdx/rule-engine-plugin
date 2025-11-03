//package com.vetc.rule.core.cache;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.cache.annotation.CacheEvict;
//import org.springframework.cache.annotation.Cacheable;
//import org.springframework.stereotype.Service;
//
//import java.util.Optional;
//
//@Service
//@RequiredArgsConstructor
//@Slf4j
//public class RuleCacheService {
//
//    private final RuleRepository ruleRepository;
//
//    /**
//     * Lấy rule theo ruleCode, cache kết quả.
//     */
//    @Cacheable(value = "rules", key = "#ruleCode")
//    public Rule getRule(String ruleCode) {
//        log.info("🧠 Loading rule {} from DB...", ruleCode);
//        Optional<Rule> rule = ruleRepository.findByRuleCode(ruleCode);
//        return rule.orElse(null);
//    }
//
//    /**
//     * Xóa cache theo ruleCode
//     */
//    @CacheEvict(value = "rules", key = "#ruleCode")
//    public void evictRule(String ruleCode) {
//        log.info("🧹 Evict rule {} from cache", ruleCode);
//    }
//
//    /**
//     * Xóa toàn bộ cache
//     */
//    @CacheEvict(value = "rules", allEntries = true)
//    public void clearAll() {
//        log.info("🧹 Cleared all rule cache");
//    }
//}
