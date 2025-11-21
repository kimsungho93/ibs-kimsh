package com.example.demo.suggestion.application.dto;

import com.example.demo.suggestion.domain.StatusType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 건의사항 수정 요청 DTO (관리자용)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSuggestionRequest {

    @NotNull(message = "상태를 선택해주세요")
    private StatusType status;

    private String adminComment;
}
