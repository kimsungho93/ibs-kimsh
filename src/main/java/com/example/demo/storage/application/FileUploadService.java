package com.example.demo.storage.application;

import com.example.demo.config.R2Config;
import com.example.demo.global.exception.CustomException;
import com.example.demo.global.exception.ErrorCode;
import com.example.demo.storage.application.dto.FileUploadResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 파일 업로드 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileUploadService {

    private final S3Client s3Client;
    private final R2Config r2Config;

    // 허용되는 파일 확장자
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(
            // 이미지
            "jpg", "jpeg", "png", "gif", "bmp", "webp", "svg",
            // PDF
            "pdf",
            // Office 문서
            "doc", "docx", "xls", "xlsx", "ppt", "pptx"
    );

    // 최대 파일 크기 (10MB)
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    /**
     * 여러 파일 업로드
     *
     * @param files 업로드할 파일 리스트
     * @return List<FileUploadResponse> 파일명과 URL 리스트
     */
    public List<FileUploadResponse> uploadFiles(List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_FILE);
        }

        return files.stream()
                .map(this::uploadFile)
                .collect(Collectors.toList());
    }

    /**
     * 단일 파일 업로드
     *
     * @param file 업로드할 파일
     * @return FileUploadResponse 파일명과 URL
     */
    private FileUploadResponse uploadFile(MultipartFile file) {
        // 파일 검증
        validateFile(file);

        // 고유한 파일명 생성
        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);
        String storedFileName = UUID.randomUUID() + "." + extension;

        try {
            // R2에 파일 업로드
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(r2Config.getBucketName())
                    .key(storedFileName)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            // 파일 URL 생성
            String fileUrl = r2Config.getPublicUrl() + "/" + storedFileName;

            log.info("파일 업로드 성공: 원본={}, 저장={}", originalFilename, storedFileName);

            return FileUploadResponse.builder()
                    .originalFileName(originalFilename)
                    .storedFileName(storedFileName)
                    .fileUrl(fileUrl)
                    .fileSize(file.getSize())
                    .build();

        } catch (IOException e) {
            log.error("파일 업로드 실패: {}", e.getMessage());
            throw new CustomException(ErrorCode.FILE_UPLOAD_FAILED);
        }
    }

    /**
     * 파일 검증
     *
     * @param file 검증할 파일
     */
    private void validateFile(MultipartFile file) {
        // 파일이 비어있는지 확인
        if (file.isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_FILE);
        }

        // 파일 크기 확인
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new CustomException(ErrorCode.FILE_SIZE_EXCEEDED);
        }

        // 파일 확장자 확인
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_FILE);
        }

        String extension = getFileExtension(originalFilename).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new CustomException(ErrorCode.INVALID_FILE_EXTENSION);
        }
    }

    /**
     * 파일 확장자 추출
     *
     * @param filename 파일명
     * @return 확장자
     */
    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == filename.length() - 1) {
            throw new CustomException(ErrorCode.INVALID_FILE);
        }
        return filename.substring(lastDotIndex + 1);
    }

    /**
     * 파일 다운로드
     *
     * @param storedFileName 저장된 파일명 (UUID)
     * @return Resource 파일 리소스
     */
    public Resource downloadFile(String storedFileName) {
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(r2Config.getBucketName())
                    .key(storedFileName)
                    .build();

            ResponseInputStream<GetObjectResponse> s3Object = s3Client.getObject(getObjectRequest);

            log.info("파일 다운로드 성공: {}", storedFileName);
            return new InputStreamResource(s3Object);

        } catch (NoSuchKeyException e) {
            log.error("파일을 찾을 수 없음: {}", storedFileName);
            throw new CustomException(ErrorCode.FILE_NOT_FOUND);
        } catch (Exception e) {
            log.error("파일 다운로드 실패: {}, error={}", storedFileName, e.getMessage());
            throw new CustomException(ErrorCode.FILE_DOWNLOAD_FAILED);
        }
    }

    /**
     * 파일 메타데이터 조회
     *
     * @param storedFileName 저장된 파일명
     * @return 파일 Content-Type
     */
    public String getContentType(String storedFileName) {
        String extension = getFileExtension(storedFileName).toLowerCase();

        return switch (extension) {
            case "jpg", "jpeg" -> "image/jpeg";
            case "png" -> "image/png";
            case "gif" -> "image/gif";
            case "bmp" -> "image/bmp";
            case "webp" -> "image/webp";
            case "svg" -> "image/svg+xml";
            case "pdf" -> "application/pdf";
            case "doc" -> "application/msword";
            case "docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case "xls" -> "application/vnd.ms-excel";
            case "xlsx" -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            case "ppt" -> "application/vnd.ms-powerpoint";
            case "pptx" -> "application/vnd.openxmlformats-officedocument.presentationml.presentation";
            default -> "application/octet-stream";
        };
    }
}
