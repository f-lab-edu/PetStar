package com.petstarproject.petstar.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileStorageService {
    String upload(MultipartFile file, String key);

    void delete(String key);

    void deleteAll(List<String> keys);
}
