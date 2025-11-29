package com.example.demo.user.infra;

import com.example.demo.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    /**
     * 사용자 프로필 이미지 URL만 조회 (영속성 컨텍스트 캐시 우회)
     */
    @Query("SELECT u.profileImageUrl FROM User u WHERE u.id = :userId")
    String findProfileImageUrlById(@Param("userId") Long userId);

    /**
     * 여러 사용자의 프로필 이미지 URL 벌크 조회
     */
    @Query("SELECT u.id, u.profileImageUrl FROM User u WHERE u.id IN :userIds")
    List<Object[]> findProfileImageUrlsByIds(@Param("userIds") List<Long> userIds);
}
