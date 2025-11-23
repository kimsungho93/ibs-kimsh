package com.example.demo.chat.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 채팅방 엔티티
 */
@Entity
@Table(name = "chat_rooms", indexes = {
    @Index(name = "idx_created_at", columnList = "created_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoom {

    /**
     * 채팅방 ID (UUID)
     */
    @Id
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false, length = 36)
    private String id;

    /**
     * 채팅방 이름 (최대 30자)
     */
    @Column(name = "name", nullable = false, length = 30)
    private String name;

    /**
     * 비밀번호 (BCrypt 해시, nullable)
     * - null이면 공개 채팅방
     * - 값이 있으면 비밀번호 보호 채팅방
     */
    @Column(name = "password", length = 60)
    private String password;

    /**
     * 채팅방 생성자 ID
     */
    @Column(name = "creator_id", nullable = false)
    private Long creatorId;

    /**
     * 채팅방 생성자 이름 (비정규화 - 조회 성능 향상)
     */
    @Column(name = "creator_name", nullable = false, length = 50)
    private String creatorName;

    /**
     * 최대 참여 인원 수 (2~15명)
     */
    @Column(name = "max_participants", nullable = false)
    private Integer maxParticipants;

    /**
     * 현재 참여 인원 수
     * - 사용자 입장/퇴장 시 증감
     * - 0이 되면 채팅방 자동 삭제 가능
     */
    @Column(name = "current_participants", nullable = false)
    @Builder.Default
    private Integer currentParticipants = 0;

    /**
     * 채팅방 생성 일시
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 채팅방 삭제 여부
     * - soft delete 지원
     */
    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private Boolean isDeleted = false;

    /**
     * 채팅방 삭제 시간
     */
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // ===== 연관 관계 =====

    /**
     * 채팅 메시지 목록 (양방향)
     * - CASCADE: 채팅방 삭제 시 메시지도 삭제
     * - orphanRemoval: 연관 관계 제거 시 메시지 삭제
     */
    @OneToMany(
        mappedBy = "chatRoom",
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    @Builder.Default
    private List<ChatMessage> messages = new ArrayList<>();

    /**
     * 현재 참여 중인 사용자 ID 목록
     * - ElementCollection: 별도 테이블로 관리
     * - 입장/퇴장 시 실시간 업데이트
     */
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
        name = "chat_room_participants",
        joinColumns = @JoinColumn(name = "room_id")
    )
    @Column(name = "user_id")
    @Builder.Default
    private Set<Long> participantIds = new HashSet<>();

    // ===== 비즈니스 메서드 =====

    /**
     * 비밀번호 설정 여부 확인
     */
    public boolean hasPassword() {
        return this.password != null && !this.password.isEmpty();
    }

    /**
     * 채팅방이 꽉 찼는지 확인
     */
    public boolean isFull() {
        return this.currentParticipants >= this.maxParticipants;
    }

    /**
     * 사용자 입장 처리
     */
    public void addParticipant(Long userId) {
        if (this.participantIds.add(userId)) {
            this.currentParticipants++;
        }
    }

    /**
     * 사용자 퇴장 처리
     */
    public void removeParticipant(Long userId) {
        if (this.participantIds.remove(userId)) {
            this.currentParticipants--;
        }
    }

    /**
     * 메시지 추가 (양방향 연관관계 편의 메서드)
     */
    public void addMessage(ChatMessage message) {
        this.messages.add(message);
        message.setChatRoom(this);
    }

    /**
     * 채팅방 삭제 (soft delete)
     */
    public void delete() {
        this.isDeleted = true;
        this.deletedAt = LocalDateTime.now();
    }
}
