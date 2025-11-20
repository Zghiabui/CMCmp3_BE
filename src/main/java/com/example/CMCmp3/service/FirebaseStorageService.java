package com.example.CMCmp3.service;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.Acl;
import com.google.firebase.cloud.StorageClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class FirebaseStorageService {

    @Value("${firebase.bucket.name}")
    private String bucketName;


    public String uploadFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File không được để trống");
        }

        Bucket bucket = StorageClient.getInstance().bucket(bucketName);

        // 1. Tạo tên file tránh trùng lặp
        String originalFileName = file.getOriginalFilename();
        String extension = "";
        if (originalFileName != null) {
            int dotIndex = originalFileName.lastIndexOf('.');
            if (dotIndex > 0) {
                extension = originalFileName.substring(dotIndex);
            }
        }
        String newFileName = UUID.randomUUID().toString() + extension;

        //  Upload file
        Blob blob = bucket.create(newFileName, file.getBytes(), file.getContentType());

        // Set file ở chế độ public read
        blob.createAcl(Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER));

        // Trả về URL
        return String.format("https://storage.googleapis.com/%s/%s", bucketName, newFileName);
    }
}
