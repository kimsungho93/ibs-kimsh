package com.example.demo.storage.presentation;

import com.example.demo.storage.application.FileUploadService;
import com.example.demo.storage.application.dto.FileUploadResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 파일 업로드/다운로드 컨트롤러
 */
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileUploadController {

    private final FileUploadService fileUploadService;

    /**
     * 파일 업로드
     *
     * @param files 업로드할 파일 리스트
     * @return List<FileUploadResponse> 파일명과 URL 리스트
     */
    @PostMapping
    public ResponseEntity<List<FileUploadResponse>> uploadFiles(
            @RequestParam("files") List<MultipartFile> files
    ) {
        List<FileUploadResponse> responses = fileUploadService.uploadFiles(files);
        return ResponseEntity.ok(responses);
    }

    /**
     * 파일 다운로드
     *
     * @param fileName 저장된 파일명 (UUID)
     * @return 파일 리소스
     */
    @GetMapping("/download/{fileName}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName) {
        Resource resource = fileUploadService.downloadFile(fileName);
        String contentType = fileUploadService.getContentType(fileName);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .body(resource);
    }
}
