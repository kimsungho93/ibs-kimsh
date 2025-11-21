package com.example.demo.suggestion.application.dto;

import com.example.demo.suggestion.domain.FileMetadata;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * 파일 메타데이터 응답 DTO
 */
@Getter
@AllArgsConstructor
@Builder
public class FileMetadataResponse {
    private String originalFileName;
    private String storedFileName;
    private String fileUrl;
    private Long fileSize;

    /**
     * Entity to DTO
     */
    public static FileMetadataResponse from(FileMetadata fileMetadata) {
        return FileMetadataResponse.builder()
                .originalFileName(fileMetadata.getOriginalFileName())
                .storedFileName(fileMetadata.getStoredFileName())
                .fileUrl(fileMetadata.getFileUrl())
                .fileSize(fileMetadata.getFileSize())
                .build();
    }
}
