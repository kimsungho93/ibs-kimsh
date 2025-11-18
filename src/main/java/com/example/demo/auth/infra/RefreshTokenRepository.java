package com.example.demo.auth.infra;

import com.example.demo.auth.domain.RefreshToken;
import com.example.demo.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    /**
     * 토큰으로 조회
     */
    Optional<RefreshToken> findByToken(String token);

    /**
     * 사용자로 조회
     */
    Optional<RefreshToken> findByUser(User user);

    /**
     * 사용자의 토큰 삭제
     */
    void deleteByUser(User user);

    /**
     * 만료된 토큰 삭제
     */
    void deleteByExpiryDateBefore(LocalDateTime now);
}
