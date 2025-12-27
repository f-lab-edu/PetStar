package com.petstarproject.petstar.controller;

import com.petstarproject.petstar.dto.MessageResponse;
import com.petstarproject.petstar.dto.VideoInfoRequest;
import com.petstarproject.petstar.dto.VideoResponse;
import com.petstarproject.petstar.entity.Video;
import com.petstarproject.petstar.service.VideoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/videos")
public class VideoController {

    private final VideoService videoService;

    @Autowired
    public VideoController(VideoService videoService) {
        this.videoService = videoService;
    }

    @GetMapping("/{videoId}")
    public ResponseEntity<?> getVideo(@PathVariable String videoId,
                                      @RequestHeader(value = "X-REQUESTER-ID", required = false) String requesterId) {
        Video video = videoService.getVideo(videoId, requesterId);
        return ResponseEntity.ok(VideoResponse.from(video));
    } // todo: 추후 Spring Security로 requesterId 추출 방식 교체


    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createVideo(@RequestPart("info") @Valid VideoInfoRequest info,
                                         @RequestPart("videoSource") MultipartFile videoSource,
                                         @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnail,
                                         @RequestParam("petId") String petId,
                                         @RequestHeader(value = "X-REQUESTER-ID", required = false) String requesterId
    ) {

        if (requesterId == null || requesterId.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String videoId = videoService.createVideo(info, videoSource, thumbnail, petId, requesterId);
        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageResponse(videoId));
    } // todo: 추후 Spring Security로 requesterId 추출 방식 교체


    @PatchMapping(value = "/{videoId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateVideo(
            @PathVariable String videoId,
            @RequestPart("info") @Valid VideoInfoRequest info,
            @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnail,
            @RequestHeader(value = "X-REQUESTER-ID", required = false) String requesterId
    ) {
        if (requesterId == null || requesterId.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        videoService.updateVideo(videoId, info, thumbnail, requesterId);
        return ResponseEntity.noContent().build();
    } // todo: 추후 Spring Security로 requesterId 추출 방식 교체


    @DeleteMapping("/{videoId}")
    public ResponseEntity<?> deleteVideo(@PathVariable String videoId,
                                         @RequestHeader(value = "X-REQUESTER-ID", required = false) String requesterId) {
        if (requesterId == null || requesterId.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        videoService.deleteVideo(videoId, requesterId);
        return ResponseEntity.noContent().build();
    }

}
