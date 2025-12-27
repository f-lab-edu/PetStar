package com.petstarproject.petstar.dto;

import com.petstarproject.petstar.entity.Video;
import com.petstarproject.petstar.enums.Visibility;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class VideoResponse {
    private String id;
    private String petId;
    private String ownerId;
    private String title;
    private String description;
    private Visibility visibility;
    private int durationSec;
    private String sourceKey;
    private String thumbnailKey;
    private int viewCount;
    private int likeCount;
    private int commentCount;
    private List<String> tags;
    private LocalDateTime publishedAt;

    public static VideoResponse from(Video video) {
        return VideoResponse.builder().
                id(video.getId())
                .petId(video.getPetId())
                .ownerId(video.getOwnerId())
                .title(video.getTitle())
                .description(video.getDescription())
                .visibility(video.getVisibility())
                .durationSec(video.getDurationSec())
                .sourceKey(video.getSourceKey())
                .thumbnailKey(video.getThumbnailKey())
                .viewCount(video.getViewCount())
                .likeCount(video.getLikeCount())
                .tags(video.getTags())
                .publishedAt(video.getPublishedAt())
                .build();
    }
}
