package com.example.demo.budget.presentation.dto;

import com.example.demo.budget.domain.BudgetUsage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 예산 사용 이력 응답 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BudgetUsageResponse {

    private Long usageId;
    private Long budgetId;
    private BigDecimal amount;
    private String reason;
    private LocalDate usageDate;
    private LocalDateTime createdAt;

    public static BudgetUsageResponse from(BudgetUsage budgetUsage) {
        return BudgetUsageResponse.builder()
                .usageId(budgetUsage.getId())
                .budgetId(budgetUsage.getBudget().getId())
                .amount(budgetUsage.getAmount())
                .reason(budgetUsage.getReason())
                .usageDate(budgetUsage.getUsageDate())
                .createdAt(budgetUsage.getCreatedAt())
                .build();
    }
}
