package com.petstarproject.petstar.dto;

import com.petstarproject.petstar.entity.Posting;
import com.petstarproject.petstar.enums.Visibility;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class PostingResponse {
    private String id;
    private String petId;
    private String ownerId;
    private String title;
    private String content;
    private Visibility visibility;
    private int likeCount;
    private int commentCount;
    private List<String> imageKeys;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime publishedAt;

    public static PostingResponse from(Posting posting) {
        return PostingResponse.builder()
                .id(posting.getId())
                .petId(posting.getPetId())
                .ownerId(posting.getOwnerId())
                .title(posting.getTitle())
                .content(posting.getContent())
                .visibility(posting.getVisibility())
                .likeCount(posting.getLikeCount())
                .commentCount(posting.getCommentCount())
                .imageKeys(posting.getImageKeys())
                .createdAt(posting.getCreatedAt())
                .updatedAt(posting.getUpdatedAt())
                .publishedAt(posting.getPublishedAt())
                .build();
    }

}
