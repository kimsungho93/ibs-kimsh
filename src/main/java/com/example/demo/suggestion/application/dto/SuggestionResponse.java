package com.example.demo.suggestion.application.dto;

import com.example.demo.suggestion.domain.CategoryType;
import com.example.demo.suggestion.domain.StatusType;
import com.example.demo.suggestion.domain.Suggestion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 건의사항 응답 DTO
 */
@Getter
@AllArgsConstructor
@Builder
public class SuggestionResponse {

    private Long id;
    private String title;
    private String content;
    private CategoryType category;
    private String name;
    private List<FileMetadataResponse> attachments;
    private StatusType status;
    private String adminComment;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Entity to DTO
     */
    public static SuggestionResponse from(Suggestion suggestion) {
        return SuggestionResponse.builder()
                .id(suggestion.getId())
                .title(suggestion.getTitle())
                .content(suggestion.getContent())
                .category(suggestion.getCategory())
                .name(suggestion.getName())
                .attachments(suggestion.getAttachments().stream()
                        .map(FileMetadataResponse::from)
                        .collect(Collectors.toList()))
                .status(suggestion.getStatus())
                .adminComment(suggestion.getAdminComment())
                .createdAt(suggestion.getCreatedAt())
                .updatedAt(suggestion.getUpdatedAt())
                .build();
    }
}
