package com.petstarproject.petstar.service;

import com.petstarproject.petstar.dto.VideoCreateRequest;
import com.petstarproject.petstar.dto.VideoUpdateRequest;
import com.petstarproject.petstar.entity.Video;
import org.springframework.web.multipart.MultipartFile;


public interface VideoService {

    /**
     * @return 생성된 vidoeId
     */
    String createVideo(VideoCreateRequest request, MultipartFile videoSource, MultipartFile thumbnail, String petId);

    Video getVideo(String videoId);

    void updateVideo(String videoId, VideoUpdateRequest request, MultipartFile thumbnail);

    void deleteVideo(String videoId);
}
