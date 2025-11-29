package com.example.demo.user.application;

import com.example.demo.config.R2Config;
import com.example.demo.global.exception.CustomException;
import com.example.demo.global.exception.ErrorCode;
import com.example.demo.user.domain.User;
import com.example.demo.user.infra.UserRepository;
import com.example.demo.user.presentation.dto.ActiveUserCountResponse;
import com.example.demo.user.presentation.dto.ActiveUserNamesResponse;
import com.example.demo.user.presentation.dto.ProfileImageUpdateResponse;
import com.example.demo.user.presentation.dto.ProfileImageUrlResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.IOException;
import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final R2Config r2Config;

    private static final long MAX_PROFILE_IMAGE_SIZE = 5 * 1024 * 1024;
    private static final Set<String> ALLOWED_IMAGE_EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp");
    private static final String PROFILE_IMAGE_PATH_PREFIX = "profile/";
    private static final long PRESIGNED_URL_EXPIRATION_SECONDS = 3600;

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    public long countActiveUsers() {
        return userRepository.countByActiveTrue();
    }

    public ActiveUserCountResponse getActiveUserCount() {
        long count = userRepository.countByActiveTrue();
        return ActiveUserCountResponse.of(count);
    }

    public ActiveUserNamesResponse getActiveUserNames() {
        List<String> names = userRepository.findByActiveTrue()
                .stream()
                .map(User::getName)
                .toList();

        return ActiveUserNamesResponse.of(names);
    }

    @Transactional
    public ProfileImageUpdateResponse updateProfileImage(String email, MultipartFile profileImage, String currentPassword) {
        User user = findByEmail(email);

        validateCurrentPassword(currentPassword, user.getPassword());
        validateProfileImage(profileImage);

        String oldProfileImageUrl = user.getProfileImageUrl();
        String newProfileImageUrl = uploadProfileImage(user.getId(), profileImage);

        user.updateProfileImageUrl(newProfileImageUrl);

        deleteOldProfileImage(oldProfileImageUrl);

        log.info("프로필 이미지 변경 완료 - userId: {}", user.getId());

        return ProfileImageUpdateResponse.of(user.getId(), newProfileImageUrl, user.getUpdatedAt());
    }

    private void validateCurrentPassword(String rawPassword, String encodedPassword) {
        if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
            throw new CustomException(ErrorCode.CURRENT_PASSWORD_MISMATCH);
        }
    }

    private void validateProfileImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new CustomException(ErrorCode.PROFILE_IMAGE_REQUIRED);
        }

        if (file.getSize() > MAX_PROFILE_IMAGE_SIZE) {
            throw new CustomException(ErrorCode.PROFILE_IMAGE_SIZE_EXCEEDED);
        }

        String extension = extractExtension(file.getOriginalFilename());
        if (!ALLOWED_IMAGE_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new CustomException(ErrorCode.PROFILE_IMAGE_INVALID_FORMAT);
        }
    }

    private String extractExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            throw new CustomException(ErrorCode.PROFILE_IMAGE_INVALID_FORMAT);
        }
        return filename.substring(filename.lastIndexOf('.') + 1);
    }

    private String uploadProfileImage(Long userId, MultipartFile file) {
        String extension = extractExtension(file.getOriginalFilename());
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        String fileName = "profile_image_" + timestamp + "." + extension;
        String key = PROFILE_IMAGE_PATH_PREFIX + userId + "/" + fileName;

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(r2Config.getBucketName())
                    .key(key)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            String profileImageUrl = r2Config.getPublicUrl() + "/" + key;
            log.info("프로필 이미지 업로드 성공 - key: {}", key);

            return profileImageUrl;
        } catch (IOException e) {
            log.error("프로필 이미지 업로드 실패 - userId: {}, error: {}", userId, e.getMessage());
            throw new CustomException(ErrorCode.PROFILE_IMAGE_UPLOAD_FAILED);
        }
    }

    private void deleteOldProfileImage(String oldProfileImageUrl) {
        if (oldProfileImageUrl == null || oldProfileImageUrl.isBlank()) {
            return;
        }

        try {
            String oldKey = extractKeyFromUrl(oldProfileImageUrl);
            if (oldKey == null) {
                return;
            }

            DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                    .bucket(r2Config.getBucketName())
                    .key(oldKey)
                    .build();

            s3Client.deleteObject(deleteRequest);
            log.info("기존 프로필 이미지 삭제 성공 - key: {}", oldKey);
        } catch (Exception e) {
            log.warn("기존 프로필 이미지 삭제 실패 - url: {}, error: {}", oldProfileImageUrl, e.getMessage());
        }
    }

    private String extractKeyFromUrl(String url) {
        String publicUrl = r2Config.getPublicUrl();
        if (url.startsWith(publicUrl)) {
            return url.substring(publicUrl.length() + 1);
        }
        return null;
    }

    public ProfileImageUrlResponse getProfileImageUrl(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        String profileImageUrl = user.getProfileImageUrl();
        if (profileImageUrl == null || profileImageUrl.isBlank()) {
            throw new CustomException(ErrorCode.PROFILE_IMAGE_NOT_FOUND);
        }

        String key = extractKeyFromUrl(profileImageUrl);
        if (key == null) {
            throw new CustomException(ErrorCode.PROFILE_IMAGE_NOT_FOUND);
        }

        String presignedUrl = generatePresignedUrl(key);
        return ProfileImageUrlResponse.of(presignedUrl, PRESIGNED_URL_EXPIRATION_SECONDS);
    }

    /**
     * 프로필 이미지 URL을 Presigned URL로 변환
     * DB에서 최신 profileImageUrl을 조회하여 캐시 문제 방지
     * 프로필 이미지가 없으면 null 반환
     */
    public String getPresignedProfileImageUrl(User user) {
        if (user == null) {
            return null;
        }
        return getPresignedProfileImageUrl(user.getId());
    }

    /**
     * User ID로 프로필 이미지 Presigned URL 조회
     */
    public String getPresignedProfileImageUrl(Long userId) {
        if (userId == null) {
            return null;
        }

        String profileImageUrl = userRepository.findProfileImageUrlById(userId);
        return convertToPresignedUrl(userId, profileImageUrl);
    }

    /**
     * 여러 사용자의 프로필 이미지 Presigned URL 벌크 조회
     * N+1 문제 방지를 위한 배치 처리
     */
    public Map<Long, String> getPresignedProfileImageUrls(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return Collections.emptyMap();
        }

        List<Object[]> results = userRepository.findProfileImageUrlsByIds(userIds);

        Map<Long, String> resultMap = new HashMap<>();
        for (Object[] row : results) {
            Long userId = (Long) row[0];
            String profileImageUrl = (String) row[1];
            String presignedUrl = convertToPresignedUrl(userId, profileImageUrl);
            resultMap.put(userId, presignedUrl);
        }
        return resultMap;
    }

    private String convertToPresignedUrl(Long userId, String profileImageUrl) {
        if (profileImageUrl == null || profileImageUrl.isBlank()) {
            return null;
        }

        String key = extractKeyFromUrl(profileImageUrl);
        if (key == null) {
            return null;
        }

        try {
            return generatePresignedUrl(key);
        } catch (Exception e) {
            log.warn("Presigned URL 생성 실패 - userId: {}, error: {}", userId, e.getMessage());
            return null;
        }
    }

    private String generatePresignedUrl(String key) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(r2Config.getBucketName())
                .key(key)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofSeconds(PRESIGNED_URL_EXPIRATION_SECONDS))
                .getObjectRequest(getObjectRequest)
                .build();

        PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);
        return presignedRequest.url().toString();
    }
}
