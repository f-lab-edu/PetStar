package com.petstarproject.petstar.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class S3Config {
    /**
     * Todo
     * 1. S3Service 만들기
     * 2. S3Service를 활용해서 PetService로직 수정
     */

    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .region(Region.AP_NORTHEAST_2)
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }
}
