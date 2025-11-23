package com.example.demo.chat.domain;

/**
 * 채팅 메시지 타입
 */
public enum MessageType {
    /**
     * 일반 텍스트 메시지
     */
    TEXT,

    /**
     * 사용자 입장 알림
     */
    ENTER,

    /**
     * 사용자 퇴장 알림
     */
    LEAVE,

    /**
     * 이미지 메시지 (추후 확장)
     */
    IMAGE,

    /**
     * 파일 메시지 (추후 확장)
     */
    FILE
}
