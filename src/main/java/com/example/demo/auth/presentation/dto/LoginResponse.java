package com.example.demo.auth.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * 로그인 응답 DTO
 */
@Getter
@AllArgsConstructor
@Builder
public class LoginResponse {
    private String accessToken;
    private UserInfo user;

    /**
     * 사용자 정보 DTO
     */
    @Getter
    @AllArgsConstructor
    @Builder
    public static class UserInfo {
        private Long id;
        private String email;
        private String name;
        private String role;
        private String position;
        private String positionDisplayName;
    }
}
