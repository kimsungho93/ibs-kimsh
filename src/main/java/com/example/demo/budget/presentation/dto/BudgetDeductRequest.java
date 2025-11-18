package com.example.demo.budget.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 예산 차감 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BudgetDeductRequest {

    @NotNull(message = "차감 금액은 필수입니다.")
    @Positive(message = "차감 금액은 0보다 커야 합니다.")
    private BigDecimal amount;

    @NotBlank(message = "사유는 필수입니다.")
    private String reason;

    @NotNull(message = "사용 날짜는 필수입니다.")
    private LocalDate usageDate;
}
