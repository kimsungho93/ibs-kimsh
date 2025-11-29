package com.example.demo.user.domain;

import com.example.demo.global.converter.BooleanToYNConverter;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * 사용자 엔티티
 */
@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Position position;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /**
     * 활성화 여부 (DB에는 Y/N 저장)
     */
    @Column(nullable = false, length = 1)
    @Convert(converter = BooleanToYNConverter.class)
    private boolean active = true;

    /**
     * 사용자 역할
     */
    public enum Role {
        USER, ADMIN
    }

    /**
     * 직급
     */
    public enum Position {
        TRAINEE("수습사원"),
        STAFF("사원"),
        ASSISTANT("주임"),
        ASSOCIATE("전임"),
        SENIOR("선임"),
        LEAD("책임"),
        PRINCIPAL("수석"),
        SENIOR_PRINCIPAL("선임수석"),
        DIRECTOR("이사"),
        EXECUTIVE("전무");

        private final String displayName;

        Position(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    /**
     * 비밀번호 업데이트
     */
    public void updatePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    /**
     * 계정 비활성화
     */
    public void deactivate() {
        this.active = false;
    }

    /**
     * 계정 활성화
     */
    public void activate() {
        this.active = true;
    }

    /**
     * 직급 업데이트
     */
    public void updatePosition(Position position) {
        this.position = position;
    }

}
