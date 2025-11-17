package com.example.CMCmp3.controller;

import com.example.CMCmp3.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/files") // Changed to /files for more generic file operations
@RequiredArgsConstructor
public class FileController {

    private final FileStorageService fileStorageService;

    @Value("${app.base-url}")
    private String baseUrl;

    @GetMapping("/images/{fileName:.+}") // Path adjusted for images subfolder
    public ResponseEntity<Resource> getFile(@PathVariable String fileName) {
        // Reconstruct the path within the 'images' subfolder
        String filePath = "images/" + fileName;
        Resource resource = fileStorageService.loadFileAsResource(filePath);

        // Determine content type
        String contentType = getContentType(fileName);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @PostMapping("/upload/image")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        String storedFileName = fileStorageService.storeFile(file, "images");
        String imageUrl = baseUrl + "/files/images/" + storedFileName; // Construct full URL
        return ResponseEntity.ok(Map.of("imageUrl", imageUrl));
    }

    private String getContentType(String fileName) {
        String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        switch (fileExtension) {
            case "png":
                return "image/png";
            case "gif":
                return "image/gif";
            case "webp":
                return "image/webp";
            case "jpeg":
            case "jpg":
            default:
                return "image/jpeg";
        }
    }
}
