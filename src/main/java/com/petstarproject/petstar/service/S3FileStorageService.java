package com.petstarproject.petstar.service;

import com.petstarproject.petstar.exception.FileStorageException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;

@Service
public class S3FileStorageService implements FileStorageService{

    private final S3Client s3Client;

    @Value("${aws.s3.bucket}")
    private String bucket;

    public S3FileStorageService(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    @Override
    public String upload(MultipartFile file, String key) {
        try {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(
                    request,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize())
            );

            return key;

        } catch (IOException e) {
            throw new FileStorageException("파일 업로드 중 오류가 발생했습니다.");
        }

    }
}
