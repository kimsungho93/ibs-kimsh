package com.example.demo.user.presentation.dto;

/**
 * 활성 회원 수 응답 DTO
 */
public record ActiveUserCountResponse(
    long activeCount
) {
    public static ActiveUserCountResponse of(long count) {
        return new ActiveUserCountResponse(count);
    }
}