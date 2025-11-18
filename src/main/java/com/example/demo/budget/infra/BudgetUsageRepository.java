package com.example.demo.budget.infra;

import com.example.demo.budget.domain.BudgetUsage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 예산 사용 이력 리포지토리
 */
public interface BudgetUsageRepository extends JpaRepository<BudgetUsage, Long> {

    /**
     * 특정 예산의 모든 사용 이력 조회
     */
    List<BudgetUsage> findByBudgetIdOrderByUsageDateDesc(Long budgetId);
}
