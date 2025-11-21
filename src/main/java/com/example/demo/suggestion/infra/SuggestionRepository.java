package com.example.demo.suggestion.infra;

import com.example.demo.suggestion.domain.CategoryType;
import com.example.demo.suggestion.domain.StatusType;
import com.example.demo.suggestion.domain.Suggestion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 건의사항 Repository
 */
public interface SuggestionRepository extends JpaRepository<Suggestion, Long> {

    /**
     * 상태별 조회
     */
    Page<Suggestion> findByStatus(StatusType status, Pageable pageable);

    /**
     * 카테고리별 조회
     */
    Page<Suggestion> findByCategory(CategoryType category, Pageable pageable);

    /**
     * 상태 및 카테고리별 조회
     */
    Page<Suggestion> findByStatusAndCategory(StatusType status, CategoryType category, Pageable pageable);
}
