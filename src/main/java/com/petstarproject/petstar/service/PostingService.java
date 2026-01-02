package com.petstarproject.petstar.service;

import com.petstarproject.petstar.dto.PostingRequest;
import com.petstarproject.petstar.entity.Posting;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PostingService {

    String createPosting(PostingRequest request, List<MultipartFile> images, String requesterId);

    Posting getPosting(String postingId, String requesterId);

    Posting updatePosting(String postingId, PostingRequest request, String requesterId);

    void deletePosting(String postingId, String requesterId);
}
