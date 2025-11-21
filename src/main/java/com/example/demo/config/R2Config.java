package com.example.demo.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;

/**
 * Cloudflare R2 스토리지 설정 클래스
 * S3 호환 API를 사용하여 R2와 통신
 */
@Getter
@Configuration
public class R2Config {

    @Value("${r2.access-key-id}")
    private String accessKeyId;

    @Value("${r2.secret-access-key}")
    private String secretAccessKey;

    @Value("${r2.endpoint}")
    private String endpoint;

    @Value("${r2.bucket-name}")
    private String bucketName;

    @Value("${r2.public-url}")
    private String publicUrl;

    /**
     * R2용 S3 클라이언트 생성
     * @return S3Client R2와 통신하기 위한 S3 클라이언트
     */
    @Bean
    public S3Client s3Client() {
        // AWS 자격 증명 생성
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKeyId, secretAccessKey);

        // S3 클라이언트 빌드 (R2 엔드포인트 사용)
        return S3Client.builder()
                .endpointOverride(URI.create(endpoint))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .region(Region.US_EAST_1) // R2는 리전이 없지만 SDK 요구사항으로 임의 설정
                .build();
    }
}
