package com.example.demo.global.exception;

import lombok.Getter;

/**
 * 커스텀 예외 클래스
 */
@Getter
public class CustomException extends RuntimeException {
    private final ErrorCode errorCode;

    public CustomException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
