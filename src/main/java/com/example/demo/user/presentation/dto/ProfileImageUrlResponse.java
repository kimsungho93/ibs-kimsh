package com.example.demo.user.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * 프로필 이미지 Presigned URL 응답 DTO
 */
@Getter
@AllArgsConstructor
@Builder
public class ProfileImageUrlResponse {
    private String presignedUrl;
    private long expiresIn;

    public static ProfileImageUrlResponse of(String presignedUrl, long expiresInSeconds) {
        return ProfileImageUrlResponse.builder()
                .presignedUrl(presignedUrl)
                .expiresIn(expiresInSeconds)
                .build();
    }
}
