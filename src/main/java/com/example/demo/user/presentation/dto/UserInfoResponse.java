package com.example.demo.user.presentation.dto;

import com.example.demo.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * 사용자 정보 응답 DTO
 */
@Getter
@AllArgsConstructor
@Builder
public class UserInfoResponse {
    private Long id;
    private String email;
    private String name;
    private String role;
    private String position;
    private String positionDisplayName;
    private String profileImageUrl;

    /**
     * Entity to DTO 변환
     */
    public static UserInfoResponse from(User user) {
        return UserInfoResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole().name())
                .position(user.getPosition().name())
                .positionDisplayName(user.getPosition().getDisplayName())
                .profileImageUrl(user.getProfileImageUrl())
                .build();
    }
}
