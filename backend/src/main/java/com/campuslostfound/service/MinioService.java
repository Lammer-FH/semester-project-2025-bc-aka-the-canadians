package com.campuslostfound.service;

import io.minio.*;
import io.minio.messages.DeleteObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Profile("!dev")
public class MinioService {

    private final MinioClient minioClient;
    private final String bucketName;
    private final Boolean initializedMinio;

    @Autowired
    public MinioService(
            MinioClient minioClient,
            String bucketName,
            @Qualifier("initMinioClient") Boolean initializedMinio) {
        this.minioClient = minioClient;
        this.bucketName = bucketName;
        this.initializedMinio = initializedMinio;
    }

    public String uploadFile(MultipartFile file, String category) {
        try {
            String fileName = generateUniqueFileName(file.getOriginalFilename());
            Map<String, String> metadata = new HashMap<>();
            metadata.put("originalName", file.getOriginalFilename());
            metadata.put("contentType", file.getContentType());
            metadata.put("size", String.valueOf(file.getSize()));
            if (category != null && !category.isEmpty()) {
                metadata.put("category", category);
            }

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .userMetadata(metadata)
                            .build());
            return fileName;
        } catch (Exception e) {
            throw new RuntimeException("Error uploading file to MinIO", e);
        }
    }

    public InputStream getFile(String fileName) {
        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .build());
        } catch (Exception e) {
            throw new RuntimeException("Error getting file from MinIO", e);
        }
    }

    public void deleteFile(String fileName) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .build());
        } catch (Exception e) {
            throw new RuntimeException("Error deleting file from MinIO", e);
        }
    }

    public void deleteMultipleFiles(List<String> fileNames) {
        try {
            List<DeleteObject> objects = new ArrayList<>();
            for (String fileName : fileNames) {
                objects.add(new DeleteObject(fileName));
            }
            minioClient.removeObjects(
                    RemoveObjectsArgs.builder()
                            .bucket(bucketName)
                            .objects(objects)
                            .build());
        } catch (Exception e) {
            throw new RuntimeException("Error deleting multiple files from MinIO", e);
        }
    }

    public Map<String, String> getFileMetadata(String fileName) {
        try {
            StatObjectResponse stat = minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .build());
            return stat.userMetadata();
        } catch (Exception e) {
            throw new RuntimeException("Error getting file metadata from MinIO", e);
        }
    }

    private String generateUniqueFileName(String originalFileName) {
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        return UUID.randomUUID().toString() + extension;
    }
}
