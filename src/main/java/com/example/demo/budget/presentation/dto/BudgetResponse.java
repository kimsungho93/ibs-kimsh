package com.example.demo.budget.presentation.dto;

import com.example.demo.budget.domain.Budget;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 예산 응답 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BudgetResponse {

    private Long budgetId;
    private Integer year;
    private Integer month;
    private BigDecimal totalAmount;
    private BigDecimal remainingAmount;

    public static BudgetResponse from(Budget budget) {
        return BudgetResponse.builder()
                .budgetId(budget.getId())
                .year(budget.getYear())
                .month(budget.getMonth())
                .totalAmount(budget.getTotalAmount())
                .remainingAmount(budget.getRemainingAmount())
                .build();
    }
}
