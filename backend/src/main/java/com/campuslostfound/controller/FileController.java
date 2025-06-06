package com.campuslostfound.controller;

import com.campuslostfound.service.MinioService;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Profile;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
@Profile("!dev")
public class FileController {

    private final MinioService minioService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "category", required = false) String category) {
        try {
            String fileName = minioService.uploadFile(file, category);
            return ResponseEntity.ok(fileName);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{fileName}")
    public ResponseEntity<InputStreamResource> getFile(@PathVariable String fileName) {
        try {
            InputStream fileStream = minioService.getFile(fileName);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(new InputStreamResource(fileStream));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{fileName}")
    public ResponseEntity<Void> deleteFile(@PathVariable String fileName) {
        try {
            minioService.deleteFile(fileName);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/batch")
    public ResponseEntity<Void> deleteMultipleFiles(@RequestBody List<String> fileNames) {
        try {
            minioService.deleteMultipleFiles(fileNames);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/metadata/{fileName}")
    public ResponseEntity<Map<String, String>> getFileMetadata(@PathVariable String fileName) {
        try {
            Map<String, String> metadata = minioService.getFileMetadata(fileName);
            return ResponseEntity.ok(metadata);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
