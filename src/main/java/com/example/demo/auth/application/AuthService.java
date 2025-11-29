package com.example.demo.auth.application;

import com.example.demo.auth.domain.RefreshToken;
import com.example.demo.auth.infra.RefreshTokenRepository;
import com.example.demo.auth.presentation.dto.LoginRequest;
import com.example.demo.auth.presentation.dto.LoginResponse;
import com.example.demo.auth.presentation.dto.TokenRefreshResponse;
import com.example.demo.global.exception.CustomException;
import com.example.demo.global.exception.ErrorCode;
import com.example.demo.global.security.JwtTokenProvider;
import com.example.demo.user.domain.User;
import com.example.demo.user.infra.UserRepository;
import com.example.demo.user.presentation.dto.UserInfoResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 인증 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 로그인
     */
    @Transactional
    public LoginResponse login(LoginRequest request) {
        // 사용자 조회
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 비밀번호 검증
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.PASSWORD_MISMATCH);
        }

        // Access Token 생성
        String accessToken = jwtTokenProvider.createAccessToken(
                user.getEmail(),
                user.getRole().name()
        );

        // Refresh Token 생성 및 저장 (RTR 방식)
        String refreshTokenValue = jwtTokenProvider.createRefreshToken(user.getEmail());
        saveOrUpdateRefreshToken(user, refreshTokenValue);

        // 응답 생성
        return LoginResponse.builder()
                .accessToken(accessToken)
                .user(LoginResponse.UserInfo.builder()
                        .id(user.getId())
                        .email(user.getEmail())
                        .name(user.getName())
                        .role(user.getRole().name())
                        .position(user.getPosition().name())
                        .positionDisplayName(user.getPosition().getDisplayName())
                        .build())
                .build();
    }

    /**
     * 토큰 갱신 (RTR 방식)
     */
    @Transactional
    public TokenRefreshResponse refreshToken(String refreshTokenValue) {
        // Refresh Token 검증
        if (!jwtTokenProvider.validateToken(refreshTokenValue)) {
            throw new IllegalArgumentException("유효하지 않은 Refresh Token입니다.");
        }

        // Refresh Token 조회
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenValue)
                .orElseThrow(() -> new IllegalArgumentException("Refresh Token을 찾을 수 없습니다."));

        // 만료 확인
        if (refreshToken.isExpired()) {
            refreshTokenRepository.delete(refreshToken);
            throw new IllegalArgumentException("만료된 Refresh Token입니다.");
        }

        User user = refreshToken.getUser();

        // 새로운 Access Token 생성
        String newAccessToken = jwtTokenProvider.createAccessToken(
                user.getEmail(),
                user.getRole().name()
        );

        // 새로운 Refresh Token 생성 및 업데이트 (RTR)
        String newRefreshTokenValue = jwtTokenProvider.createRefreshToken(user.getEmail());
        refreshToken.updateToken(
                newRefreshTokenValue,
                jwtTokenProvider.getRefreshTokenExpiryDate()
        );

        return TokenRefreshResponse.builder()
                .accessToken(newAccessToken)
                .build();
    }

    /**
     * 로그아웃
     */
    @Transactional
    public void logout(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        // Refresh Token 삭제
        refreshTokenRepository.deleteByUser(user);
    }

    /**
     * 현재 사용자 정보 조회
     */
    public UserInfoResponse getCurrentUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        return UserInfoResponse.from(user);
    }

    /**
     * Refresh Token 저장 또는 업데이트
     */
    private void saveOrUpdateRefreshToken(User user, String tokenValue) {
        refreshTokenRepository.findByUser(user)
                .ifPresentOrElse(
                        // 기존 토큰이 있으면 업데이트
                        existingToken -> existingToken.updateToken(
                                tokenValue,
                                jwtTokenProvider.getRefreshTokenExpiryDate()
                        ),
                        // 없으면 새로 생성
                        () -> refreshTokenRepository.save(
                                RefreshToken.builder()
                                        .token(tokenValue)
                                        .user(user)
                                        .expiryDate(jwtTokenProvider.getRefreshTokenExpiryDate())
                                        .build()
                        )
                );
    }

}
