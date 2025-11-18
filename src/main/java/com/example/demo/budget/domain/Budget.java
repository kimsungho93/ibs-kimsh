package com.example.demo.budget.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 예산 엔티티
 */
@Entity
@Table(name = "budget")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "budget_id")
    private Long id;

    @Column(nullable = false)
    private Integer year;

    @Column(nullable = false)
    private Integer month;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal totalAmount;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal remainingAmount;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /**
     * 예산 차감
     * - 차감 금액 유효성 검증
     * - 잔여 예산 부족 시 예외 발생
     */
    public void deduct(BigDecimal amount) {
        validateDeductAmount(amount);
        validateSufficientBudget(amount);
        this.remainingAmount = this.remainingAmount.subtract(amount);
    }

    /**
     * 차감 금액 유효성 검증
     */
    private void validateDeductAmount(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("차감 금액은 0보다 커야 합니다.");
        }
    }

    /**
     * 잔여 예산 충분성 검증
     */
    private void validateSufficientBudget(BigDecimal amount) {
        if (remainingAmount.compareTo(amount) < 0) {
            throw new IllegalArgumentException("남은 예산이 부족합니다.");
        }
    }

    /**
     * 예산 생성 팩토리 메서드 (활성 회원 수 기반)
     * - 1인당 50,000원 기준으로 총 예산 계산
     */
    public static Budget create(int year, int month, int activeUserCount) {
        validateYearMonth(year, month);
        validateActiveUserCount(activeUserCount);

        BigDecimal totalAmount = calculateTotalAmount(activeUserCount);

        return Budget.builder()
                .year(year)
                .month(month)
                .totalAmount(totalAmount)
                .remainingAmount(totalAmount)
                .build();
    }

    /**
     * 년월 유효성 검증
     */
    private static void validateYearMonth(int year, int month) {
        if (year < 2000 || year > 2100) {
            throw new IllegalArgumentException("유효하지 않은 년도입니다.");
        }
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("유효하지 않은 월입니다.");
        }
    }

    /**
     * 활성 회원 수 유효성 검증
     */
    private static void validateActiveUserCount(int activeUserCount) {
        if (activeUserCount < 0) {
            throw new IllegalArgumentException("활성 회원 수는 0 이상이어야 합니다.");
        }
    }

    /**
     * 총 예산 계산 (1인당 50,000원)
     */
    private static BigDecimal calculateTotalAmount(int activeUserCount) {
        return BigDecimal.valueOf((long) activeUserCount * 50_000);
    }
}
