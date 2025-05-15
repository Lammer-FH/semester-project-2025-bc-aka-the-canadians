package com.campuslostfound.config;

import io.minio.MinioClient;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class MinioConfig {

    @Value("${minio.endpoint}")
    private String endpoint;

    @Value("${minio.accessKey}")
    private String accessKey;

    @Value("${minio.secretKey}")
    private String secretKey;

    @Value("${minio.bucketName}")
    private String bucketName;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }

    @Bean
    public String bucketName() {
        return bucketName;
    }

    @Bean
    public Boolean initMinioClient(MinioClient minioClient) {
        try {
            boolean bucketExists = minioClient.bucketExists(BucketExistsArgs.builder()
                    .bucket(bucketName)
                    .build());
            
            if (!bucketExists) {
                minioClient.makeBucket(MakeBucketArgs.builder()
                        .bucket(bucketName)
                        .build());
                log.info("Created MinIO bucket: {}", bucketName);
            } else {
                log.info("MinIO bucket already exists: {}", bucketName);
            }
            return true;
        } catch (Exception e) {
            log.error("Error initializing MinIO bucket: {}", e.getMessage());
            throw new RuntimeException("Could not initialize MinIO bucket", e);
        }
    }
} 