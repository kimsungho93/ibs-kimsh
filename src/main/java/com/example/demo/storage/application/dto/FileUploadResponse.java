package com.example.demo.storage.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * 파일 업로드 응답 DTO
 */
@Getter
@AllArgsConstructor
@Builder
public class FileUploadResponse {
    private String originalFileName;  // 원본 파일명
    private String storedFileName;     // 저장된 파일명 (UUID)
    private String fileUrl;            // 파일 접근 URL
    private Long fileSize;             // 파일 크기 (bytes)
}
