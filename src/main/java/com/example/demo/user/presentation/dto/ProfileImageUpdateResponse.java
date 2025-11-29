package com.example.demo.user.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 프로필 이미지 변경 응답 DTO
 */
@Getter
@AllArgsConstructor
@Builder
public class ProfileImageUpdateResponse {
    private Long userId;
    private String profileImageUrl;
    private LocalDateTime updatedAt;

    public static ProfileImageUpdateResponse of(Long userId, String profileImageUrl, LocalDateTime updatedAt) {
        return ProfileImageUpdateResponse.builder()
                .userId(userId)
                .profileImageUrl(profileImageUrl)
                .updatedAt(updatedAt)
                .build();
    }
}
