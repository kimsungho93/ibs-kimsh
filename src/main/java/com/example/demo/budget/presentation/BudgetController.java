package com.example.demo.budget.presentation;

import com.example.demo.budget.application.BudgetService;
import com.example.demo.budget.presentation.dto.BudgetDeductRequest;
import com.example.demo.budget.presentation.dto.BudgetResponse;
import com.example.demo.budget.presentation.dto.BudgetUsageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 예산 컨트롤러
 * - 예산 관련 HTTP 요청 처리
 * - RESTful API 제공
 */
@Slf4j
@RestController
@RequestMapping("/api/budget")
@RequiredArgsConstructor
public class BudgetController {

    private final BudgetService budgetService;

    /**
     * 특정 년월 예산 조회
     * @param year 조회할 년도
     * @param month 조회할 월
     * @return 예산 정보
     */
    @GetMapping("/{year}/{month}")
    public ResponseEntity<BudgetResponse> getBudget(
            @PathVariable Integer year,
            @PathVariable Integer month
    ) {
        return ResponseEntity.ok(budgetService.getBudget(year, month));
    }

    /**
     * 특정 년월 예산 생성
     * - 활성 회원 수 기반으로 자동 계산
     * @param year 생성할 년도
     * @param month 생성할 월
     * @return 생성된 예산 정보
     */
    @PostMapping("/{year}/{month}")
    public ResponseEntity<BudgetResponse> createBudget(
            @PathVariable Integer year,
            @PathVariable Integer month
    ) {
        BudgetResponse budget = budgetService.createBudget(year, month);
        log.info("예산 생성 완료: {}년 {}월, 총액: {}", year, month, budget.getTotalAmount());
        return ResponseEntity.ok(budget);
    }

    /**
     * 예산 차감
     * - 예산 차감과 동시에 사용 이력 기록
     * @param budgetId 차감할 예산 ID
     * @param request 차감 요청 정보 (금액, 사유, 사용일자)
     * @return 생성된 사용 이력 정보
     */
    @PostMapping("/{budgetId}/deduct")
    public ResponseEntity<BudgetUsageResponse> deductBudget(
            @PathVariable Long budgetId,
            @Valid @RequestBody BudgetDeductRequest request
    ) {
        BudgetUsageResponse usage = budgetService.deductBudget(budgetId, request);
        log.info("예산 차감 완료: budgetId={}, 차감액={}, 사유={}", budgetId, request.getAmount(), request.getReason());
        return ResponseEntity.ok(usage);
    }

    /**
     * 특정 예산의 사용 이력 조회
     * - 사용 날짜 역순 정렬
     * @param budgetId 조회할 예산 ID
     * @return 예산 사용 이력 목록
     */
    @GetMapping("/{budgetId}/usage-history")
    public ResponseEntity<List<BudgetUsageResponse>> getBudgetUsageHistory(
            @PathVariable Long budgetId
    ) {
        return ResponseEntity.ok(budgetService.getBudgetUsageHistory(budgetId));
    }
}
