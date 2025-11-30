package com.example.demo.user.presentation;

import com.example.demo.user.application.UserService;
import com.example.demo.user.presentation.dto.ActiveUserCountResponse;
import com.example.demo.user.presentation.dto.ActiveUserNamesResponse;
import com.example.demo.user.presentation.dto.ProfileImageUpdateResponse;
import com.example.demo.user.presentation.dto.ProfileImageUrlResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 사용자 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 활성 회원 수 조회
     * @return 활성 회원 수
     */
    @GetMapping("/active/count")
    public ResponseEntity<ActiveUserCountResponse> getActiveUserCount() {
        return ResponseEntity.ok(userService.getActiveUserCount());
    }

    /**
     * 활성 회원 이름 목록 조회
     * @return 활성 회원 이름 목록
     */
    @GetMapping("/active/names")
    public ResponseEntity<ActiveUserNamesResponse> getActiveUserNames() {
        return ResponseEntity.ok(userService.getActiveUserNames());
    }

    /**
     * 프로필 이미지 변경
     */
    @PatchMapping("/profile/image")
    public ResponseEntity<ProfileImageUpdateResponse> updateProfileImage(
            @AuthenticationPrincipal String email,
            @RequestParam("profileImage") MultipartFile profileImage,
            @RequestParam("currentPassword") String currentPassword
    ) {
        ProfileImageUpdateResponse response = userService.updateProfileImage(email, profileImage, currentPassword);
        log.info("프로필 이미지 변경 - email: {}", email);
        return ResponseEntity.ok(response);
    }

    /**
     * 프로필 이미지 Presigned URL 조회
     */
    @GetMapping("/{userId}/profile/image-url")
    public ResponseEntity<ProfileImageUrlResponse> getProfileImageUrl(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getProfileImageUrl(userId));
    }
}
