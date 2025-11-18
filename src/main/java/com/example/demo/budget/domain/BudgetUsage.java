package com.example.demo.budget.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 예산 사용 이력 엔티티
 */
@Entity
@Table(name = "budget_usage")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class BudgetUsage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "usage_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "budget_id", nullable = false)
    private Budget budget;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 500)
    private String reason;

    @Column(nullable = false)
    private LocalDate usageDate;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 예산 사용 이력 생성 팩토리 메서드
     * - 도메인 규칙 검증 후 객체 생성
     */
    public static BudgetUsage create(Budget budget, BigDecimal amount, String reason, LocalDate usageDate) {
        validateBudget(budget);
        validateAmount(amount);
        validateReason(reason);
        validateUsageDate(usageDate);

        return BudgetUsage.builder()
                .budget(budget)
                .amount(amount)
                .reason(reason)
                .usageDate(usageDate)
                .build();
    }

    /**
     * 예산 객체 유효성 검증
     */
    private static void validateBudget(Budget budget) {
        if (budget == null) {
            throw new IllegalArgumentException("예산 정보는 필수입니다.");
        }
    }

    /**
     * 사용 금액 유효성 검증
     */
    private static void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("사용 금액은 0보다 커야 합니다.");
        }
    }

    /**
     * 사유 유효성 검증
     */
    private static void validateReason(String reason) {
        if (reason == null || reason.trim().isEmpty()) {
            throw new IllegalArgumentException("사용 사유는 필수입니다.");
        }
        if (reason.length() > 500) {
            throw new IllegalArgumentException("사용 사유는 500자를 초과할 수 없습니다.");
        }
    }

    /**
     * 사용 날짜 유효성 검증
     */
    private static void validateUsageDate(LocalDate usageDate) {
        if (usageDate == null) {
            throw new IllegalArgumentException("사용 날짜는 필수입니다.");
        }
        if (usageDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("사용 날짜는 미래일 수 없습니다.");
        }
    }
}
