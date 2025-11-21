package com.example.demo.suggestion.domain;

/**
 * 건의사항 상태 타입
 */
public enum StatusType {
    PENDING,      // 대기중
    IN_PROGRESS,  // 처리중
    COMPLETED,    // 완료
    REJECTED      // 반려
}
