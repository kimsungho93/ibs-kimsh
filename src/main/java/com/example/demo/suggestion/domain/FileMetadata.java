package com.example.demo.suggestion.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

/**
 * 파일 메타데이터 (Embeddable)
 */
@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class FileMetadata {

    @Column(name = "original_file_name", nullable = false)
    private String originalFileName;  // 원본 파일명

    @Column(name = "stored_file_name", nullable = false)
    private String storedFileName;     // 저장된 파일명 (UUID)

    @Column(name = "file_url", nullable = false)
    private String fileUrl;            // 파일 접근 URL

    @Column(name = "file_size")
    private Long fileSize;             // 파일 크기 (bytes)
}
