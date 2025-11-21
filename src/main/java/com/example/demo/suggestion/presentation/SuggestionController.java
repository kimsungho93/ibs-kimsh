package com.example.demo.suggestion.presentation;

import com.example.demo.suggestion.application.SuggestionService;
import com.example.demo.suggestion.application.dto.CreateSuggestionRequest;
import com.example.demo.suggestion.application.dto.SuggestionResponse;
import com.example.demo.suggestion.application.dto.UpdateSuggestionRequest;
import com.example.demo.suggestion.domain.CategoryType;
import com.example.demo.suggestion.domain.StatusType;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * 건의사항 컨트롤러
 */
@RestController
@RequestMapping("/api/suggestions")
@RequiredArgsConstructor
public class SuggestionController {

    private final SuggestionService suggestionService;

    /**
     * 건의사항 생성
     */
    @PostMapping
    public ResponseEntity<SuggestionResponse> createSuggestion(
            @Valid @ModelAttribute CreateSuggestionRequest request,
            Authentication authentication) {

        String email = authentication.getName();
        SuggestionResponse response = suggestionService.create(request, email);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 건의사항 목록 조회
     */
    @GetMapping
    public ResponseEntity<Page<SuggestionResponse>> getSuggestions(
            @RequestParam(required = false) StatusType status,
            @RequestParam(required = false) CategoryType category,
            Pageable pageable) {

        Page<SuggestionResponse> suggestions = suggestionService.findAll(status, category, pageable);
        return ResponseEntity.ok(suggestions);
    }

    /**
     * 건의사항 상세 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<SuggestionResponse> getSuggestion(@PathVariable Long id) {
        SuggestionResponse suggestion = suggestionService.findById(id);
        return ResponseEntity.ok(suggestion);
    }

    /**
     * 건의사항 수정 (관리자 전용)
     */
    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SuggestionResponse> updateSuggestion(
            @PathVariable Long id,
            @Valid @RequestBody UpdateSuggestionRequest request) {

        SuggestionResponse response = suggestionService.update(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * 건의사항 삭제 (작성자 또는 관리자)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSuggestion(
            @PathVariable Long id,
            Authentication authentication) {

        String email = authentication.getName();
        suggestionService.delete(id, email);
        return ResponseEntity.noContent().build();
    }
}
