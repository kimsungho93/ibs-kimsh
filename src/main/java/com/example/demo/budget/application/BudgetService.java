package com.example.demo.budget.application;

import com.example.demo.budget.domain.Budget;
import com.example.demo.budget.domain.BudgetUsage;
import com.example.demo.budget.infra.BudgetRepository;
import com.example.demo.budget.infra.BudgetUsageRepository;
import com.example.demo.budget.presentation.dto.BudgetDeductRequest;
import com.example.demo.budget.presentation.dto.BudgetResponse;
import com.example.demo.budget.presentation.dto.BudgetUsageResponse;
import com.example.demo.user.infra.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 예산 서비스
 * - 예산 생성, 조회, 차감 등의 비즈니스 로직 처리
 * - 트랜잭션 관리 및 도메인 객체 간 협력 조율
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final BudgetUsageRepository budgetUsageRepository;
    private final UserRepository userRepository;

    /**
     * 특정 년월 예산 조회
     * - 존재하지 않을 경우 예외 발생
     */
    public BudgetResponse getBudget(int year, int month) {
        return budgetRepository.findByYearAndMonth(year, month)
                .map(BudgetResponse::from)
                .orElseThrow(() -> new IllegalArgumentException("해당 년월의 예산이 존재하지 않습니다."));
    }

    /**
     * 특정 년월 예산 생성
     * - 활성 회원 수 기반으로 예산 계산 (1인당 50,000원)
     * - 중복 생성 방지
     */
    @Transactional
    public BudgetResponse createBudget(int year, int month) {
        validateBudgetNotExists(year, month);

        int activeUserCount = getActiveUserCount();
        Budget budget = Budget.create(year, month, activeUserCount);

        return saveBudget(budget);
    }

    /**
     * 예산 차감 및 사용 이력 기록
     * - 원자적 트랜잭션으로 예산 차감과 이력 생성 동시 처리
     */
    @Transactional
    public BudgetUsageResponse deductBudget(Long budgetId, BudgetDeductRequest request) {
        Budget budget = findBudgetById(budgetId);

        budget.deduct(request.getAmount());

        BudgetUsage budgetUsage = createBudgetUsage(budget, request);

        return BudgetUsageResponse.from(budgetUsage);
    }

    /**
     * 특정 예산의 사용 이력 조회
     * - 사용 날짜 역순으로 정렬
     */
    public List<BudgetUsageResponse> getBudgetUsageHistory(Long budgetId) {
        return budgetUsageRepository.findByBudgetIdOrderByUsageDateDesc(budgetId)
                .stream()
                .map(BudgetUsageResponse::from)
                .toList();
    }

    /**
     * 예산 중복 생성 검증
     */
    private void validateBudgetNotExists(int year, int month) {
        if (budgetRepository.existsByYearAndMonth(year, month)) {
            throw new IllegalArgumentException("이미 해당 년월의 예산이 존재합니다.");
        }
    }

    /**
     * 활성 회원 수 조회
     */
    private int getActiveUserCount() {
        return (int) userRepository.countByActiveTrue();
    }

    /**
     * 예산 저장 및 응답 변환
     */
    private BudgetResponse saveBudget(Budget budget) {
        Budget savedBudget = budgetRepository.save(budget);
        return BudgetResponse.from(savedBudget);
    }

    /**
     * ID로 예산 조회
     */
    private Budget findBudgetById(Long budgetId) {
        return budgetRepository.findById(budgetId)
                .orElseThrow(() -> new IllegalArgumentException("예산을 찾을 수 없습니다."));
    }

    /**
     * 예산 사용 이력 생성 및 저장
     */
    private BudgetUsage createBudgetUsage(Budget budget, BudgetDeductRequest request) {
        BudgetUsage budgetUsage = BudgetUsage.create(
                budget,
                request.getAmount(),
                request.getReason(),
                request.getUsageDate()
        );
        return budgetUsageRepository.save(budgetUsage);
    }
}
