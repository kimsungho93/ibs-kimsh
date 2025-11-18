package com.example.demo.budget.infra;

import com.example.demo.budget.domain.Budget;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 예산 리포지토리
 */
public interface BudgetRepository extends JpaRepository<Budget, Long> {

    /**
     * 년월로 예산 조회
     */
    Optional<Budget> findByYearAndMonth(Integer year, Integer month);

    /**
     * 년월 예산 존재 여부 확인
     */
    boolean existsByYearAndMonth(Integer year, Integer month);
}
