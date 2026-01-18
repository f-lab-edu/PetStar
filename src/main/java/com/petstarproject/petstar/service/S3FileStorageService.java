package com.petstarproject.petstar.service;

import com.petstarproject.petstar.exception.FileStorageException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;

/**
 * AWS S3에 파일을 업로드하는 {@link FileStorageService} 구현체입니다.
 *
 * <p>전달받은 {@link MultipartFile}과 지정된 S3 key를 기반으로
 * S3 버킷에 객체(파일)를 저장합니다. 업로드가 성공하면 해당 key를 반환하며,
 * 업로드 과정에서 IOException이 발생할 경우 {@link FileStorageException}을 발생시킵니다.</p>
 */
@Service
public class S3FileStorageService implements FileStorageService{

    private final S3Client s3Client;

    @Value("${aws.s3.bucket}")
    private String bucket;

    public S3FileStorageService(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    /**
     * 지정된 key로 S3 버킷에 파일을 업로드합니다.
     *
     * @param file 업로드할 파일
     * @param key  S3 객체 key (저장될 경로)
     * @return 업로드된 객체의 key
     * @throws FileStorageException 업로드 중 I/O 오류가 발생한 경우
     */
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
            throw new FileStorageException("파일 업로드 중 오류가 발생했습니다.", e);
        }

    }
}
