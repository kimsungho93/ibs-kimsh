package com.example.demo.chat.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;

/**
 * 채팅 메시지 엔티티
 */
@Entity
@Table(name = "chat_messages", indexes = {
    @Index(name = "idx_room_created", columnList = "room_id, created_at"),
    @Index(name = "idx_sender_created", columnList = "sender_id, created_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessage {

    /**
     * 메시지 ID (UUID)
     */
    @Id
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false, length = 36)
    private String id;

    /**
     * 채팅방 (다대일 관계)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private ChatRoom chatRoom;

    /**
     * 발신자 ID
     * - 시스템 메시지(입장/퇴장)의 경우에도 발신자 ID 필요
     */
    @Column(name = "sender_id", nullable = false)
    private Long senderId;

    /**
     * 발신자 이름 (비정규화 - 조회 성능 향상)
     */
    @Column(name = "sender_name", nullable = false, length = 50)
    private String senderName;

    /**
     * 메시지 내용
     * - TEXT: 일반 텍스트
     * - ENTER/LEAVE: "{사용자}님이 입장/퇴장하셨습니다"
     * - IMAGE/FILE: 파일 URL
     */
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    /**
     * 메시지 타입
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 10)
    private MessageType type;

    /**
     * 메시지 생성 시간
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 메시지 삭제 여부 (soft delete)
     */
    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private Boolean isDeleted = false;

    /**
     * 읽음 여부 (추후 확장)
     */
    @Column(name = "is_read", nullable = false)
    @Builder.Default
    private Boolean isRead = false;

    // ===== 비즈니스 메서드 =====

    /**
     * 시스템 메시지 여부 확인
     */
    public boolean isSystemMessage() {
        return this.type == MessageType.ENTER || this.type == MessageType.LEAVE;
    }

    /**
     * 메시지 삭제
     */
    public void delete() {
        this.isDeleted = true;
    }
}
