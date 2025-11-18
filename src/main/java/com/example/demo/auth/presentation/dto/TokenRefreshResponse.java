package com.example.demo.auth.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * 토큰 갱신 응답 DTO
 */
@Getter
@AllArgsConstructor
@Builder
public class TokenRefreshResponse {
    private String accessToken;
}
