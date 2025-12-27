package com.petstarproject.petstar.service;

import com.petstarproject.petstar.dto.VideoInfoRequest;
import com.petstarproject.petstar.entity.Video;
import org.springframework.web.multipart.MultipartFile;


public interface VideoService {

    /**
     * @return 생성된 vidoeId
     */
    String createVideo(VideoInfoRequest request, MultipartFile videoSource, MultipartFile thumbnail, String petId, String userId);

    Video getVideo(String videoId, String requesterId);

    void updateVideo(String videoId, VideoInfoRequest request, MultipartFile thumbnail, String requesterId);

    void deleteVideo(String videoId, String requesterId);
}
