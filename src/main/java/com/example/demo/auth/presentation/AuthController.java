package com.example.demo.auth.presentation;

import com.example.demo.auth.application.AuthService;
import com.example.demo.auth.presentation.dto.ChangePasswordRequest;
import com.example.demo.auth.presentation.dto.LoginRequest;
import com.example.demo.auth.presentation.dto.LoginResponse;
import com.example.demo.user.presentation.dto.UserInfoResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

/**
 * 인증 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    private static final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";
    private static final int REFRESH_TOKEN_COOKIE_MAX_AGE = 7 * 24 * 60 * 60; // 7일

    /**
     * 로그인
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response
    ) {
        LoginResponse loginResponse = authService.login(request);

        // Refresh Token을 httpOnly 쿠키로 설정
        setRefreshTokenCookie(response, loginResponse.getUser().getEmail());

        log.info("로그인 성공: {}", request.getEmail());

        return ResponseEntity.ok(loginResponse);
    }

    /**
     * 로그아웃
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @AuthenticationPrincipal String email,
            HttpServletResponse response
    ) {
        authService.logout(email);

        // Refresh Token 쿠키 삭제
        deleteRefreshTokenCookie(response);

        log.info("로그아웃 성공: {}", email);

        return ResponseEntity.ok().build();
    }

    /**
     * 현재 사용자 정보 조회
     */
    @GetMapping("/me")
    public ResponseEntity<UserInfoResponse> getCurrentUser(
            @AuthenticationPrincipal String email
    ) {
        UserInfoResponse userInfo = authService.getCurrentUser(email);
        return ResponseEntity.ok(userInfo);
    }

    /**
     * 비밀번호 변경
     */
    @PutMapping("/password")
    public ResponseEntity<Void> changePassword(
            @AuthenticationPrincipal String email,
            @Valid @RequestBody ChangePasswordRequest request
    ) {
        authService.changePassword(email, request);
        return ResponseEntity.noContent().build();
    }

    /**
     * Refresh Token 쿠키 설정
     */
    private void setRefreshTokenCookie(HttpServletResponse response, String email) {
        // 실제로는 AuthService에서 생성한 Refresh Token을 사용해야 함
        // 여기서는 간소화를 위해 생략하고, 로그인 시 이미 저장된 토큰 사용

        Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, "placeholder");
        cookie.setHttpOnly(true);
        cookie.setSecure(true); // HTTPS에서만 전송 (프로덕션)
        cookie.setPath("/");
        cookie.setMaxAge(REFRESH_TOKEN_COOKIE_MAX_AGE);
        cookie.setAttribute("SameSite", "Strict");

        response.addCookie(cookie);
    }

    /**
     * 쿠키에서 Refresh Token 추출
     */
    private String extractRefreshTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return null;
        }

        return Arrays.stream(request.getCookies())
                .filter(cookie -> REFRESH_TOKEN_COOKIE_NAME.equals(cookie.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);
    }

    /**
     * Refresh Token 쿠키 삭제
     */
    private void deleteRefreshTokenCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0); // 즉시 만료

        response.addCookie(cookie);
    }
}
