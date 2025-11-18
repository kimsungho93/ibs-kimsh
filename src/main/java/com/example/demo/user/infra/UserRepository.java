package com.example.demo.user.infra;

import com.example.demo.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * 사용자 리포지토리
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 이메일로 사용자 조회
     */
    Optional<User> findByEmail(String email);

    /**
     * 이메일 존재 여부 확인
     */
    boolean existsByEmail(String email);

    /**
     * 활성 회원 수 조회 (active = true)
     */
    long countByActiveTrue();

    /**
     * 활성 회원 전체 조회 (active = true)
     */
    List<User> findByActiveTrue();
}
