package com.example.demo.suggestion.application;

import com.example.demo.global.exception.CustomException;
import com.example.demo.global.exception.ErrorCode;
import com.example.demo.storage.application.FileUploadService;
import com.example.demo.storage.application.dto.FileUploadResponse;
import com.example.demo.suggestion.application.dto.CreateSuggestionRequest;
import com.example.demo.suggestion.application.dto.SuggestionResponse;
import com.example.demo.suggestion.application.dto.UpdateSuggestionRequest;
import com.example.demo.suggestion.domain.CategoryType;
import com.example.demo.suggestion.domain.FileMetadata;
import com.example.demo.suggestion.domain.StatusType;
import com.example.demo.suggestion.domain.Suggestion;
import com.example.demo.suggestion.infra.SuggestionRepository;
import com.example.demo.user.domain.User;
import com.example.demo.user.infra.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 건의사항 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SuggestionService {

    private final SuggestionRepository suggestionRepository;
    private final UserRepository userRepository;
    private final FileUploadService fileUploadService;

    /**
     * 건의사항 생성
     */
    @Transactional
    public SuggestionResponse create(CreateSuggestionRequest request, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 첨부파일 업로드 처리 (동기)
        List<FileMetadata> fileMetadataList = new ArrayList<>();
        if (request.getFiles() != null && !request.getFiles().isEmpty()) {
            List<FileUploadResponse> uploadResponses = fileUploadService.uploadFiles(request.getFiles());
            fileMetadataList = uploadResponses.stream()
                    .map(response -> FileMetadata.builder()
                            .originalFileName(response.getOriginalFileName())
                            .storedFileName(response.getStoredFileName())
                            .fileUrl(response.getFileUrl())
                            .fileSize(response.getFileSize())
                            .build())
                    .collect(Collectors.toList());
            log.info("첨부파일 업로드 완료: 파일 개수={}", fileMetadataList.size());
        }

        Suggestion suggestion = Suggestion.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .category(request.getCategory())
                .name(request.getName())
                .attachments(fileMetadataList)
                .user(user)
                .build();

        Suggestion saved = suggestionRepository.save(suggestion);
        log.info("건의사항 생성 완료: id={}, user={}, 첨부파일 개수={}", saved.getId(), email, fileMetadataList.size());

        return SuggestionResponse.from(saved);
    }

    /**
     * 건의사항 목록 조회 (필터링)
     */
    public Page<SuggestionResponse> findAll(StatusType status, CategoryType category, Pageable pageable) {
        Page<Suggestion> suggestions;

        if (status != null && category != null) {
            suggestions = suggestionRepository.findByStatusAndCategory(status, category, pageable);
        } else if (status != null) {
            suggestions = suggestionRepository.findByStatus(status, pageable);
        } else if (category != null) {
            suggestions = suggestionRepository.findByCategory(category, pageable);
        } else {
            suggestions = suggestionRepository.findAll(pageable);
        }

        return suggestions.map(SuggestionResponse::from);
    }

    /**
     * 건의사항 상세 조회
     */
    public SuggestionResponse findById(Long id) {
        Suggestion suggestion = suggestionRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.SUGGESTION_NOT_FOUND));

        return SuggestionResponse.from(suggestion);
    }

    /**
     * 건의사항 수정 (관리자)
     */
    @Transactional
    public SuggestionResponse update(Long id, UpdateSuggestionRequest request) {
        Suggestion suggestion = suggestionRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.SUGGESTION_NOT_FOUND));

        suggestion.updateStatusAndComment(request.getStatus(), request.getAdminComment());
        log.info("건의사항 수정 완료: id={}, status={}", id, request.getStatus());

        return SuggestionResponse.from(suggestion);
    }

    /**
     * 건의사항 삭제 (작성자 또는 관리자)
     */
    @Transactional
    public void delete(Long id, String email) {
        Suggestion suggestion = suggestionRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.SUGGESTION_NOT_FOUND));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 작성자 본인이거나 관리자만 삭제 가능
        if (!suggestion.getUser().getId().equals(user.getId()) && user.getRole() != User.Role.ADMIN) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        suggestionRepository.delete(suggestion);
        log.info("건의사항 삭제 완료: id={}, user={}", id, email);
    }
}
