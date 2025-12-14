package com.petstarproject.petstar.entity;

import com.petstarproject.petstar.enums.VideoStatus;
import com.petstarproject.petstar.enums.Visibility;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "videos")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Video {
    @Id
    @Column(length = 36)
    private String id;  // UUID(PK)

    @Column(name = "pet_id", nullable = false, length = 36)
    private String petId;   // FK

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private VideoStatus status; // UPLOADING, TRANSCODING, READY, FAILED

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Visibility visibility;  // PUBLIC, PRIVATE

    @Column(name = "source_key", nullable = false, length = 500)
    private String sourceKey;   // S3 원본 파일 key

    @Column(name = "thumbnail_key", length = 500)
    private String thumbnailKey;    // 썸네일 S3 key

    @Column(name = "duration_sec", nullable = false)
    private int durationSec;    // 영상 길이(초)

    @Column(name = "view_count", nullable = false)
    private int viewCount;

    @Column(name = "like_count", nullable = false)
    private int likeCount;

    @Column(name = "comment_count", nullable = false)
    private int commentCount;

    @ElementCollection
    @CollectionTable(
            name = "video_tags",
            joinColumns = @JoinColumn(name = "video_id")
    )
    @Column(name = "tag", length = 50)
    private List<String> tags = new ArrayList<>();

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    // 도메인 메서드

    public static Video create(
            String id,
            String petId,
            String title,
            String description,
            String sourceKey,
            String thumbnailKey,
            int durationSec,
            List<String> tags
    ) {
        Video video = new Video();
        video.id = id;
        video.petId = petId;
        video.title = title;
        video.description = description;
        video.status = VideoStatus.UPLOADING; // todo: Uploading -> Transcoding -> Ready 기능 추가
        video.sourceKey = sourceKey;
        video.thumbnailKey = thumbnailKey;
        video.durationSec = durationSec;
        video.viewCount = 0;
        video.likeCount = 0;
        video.commentCount = 0;
        if (tags != null) {
            video.tags.addAll(tags);
        }
        video.createdAt = LocalDateTime.now();
        video.publishedAt = null;

        return video;
    }

    public void updateMeta(String title,
                           String description,
                           Visibility visibility,
                           List<String> tags) {
        if (title != null) this.title = title;
        if (description != null) this.description = description;
        if(visibility != null) this.visibility = visibility;
        if (tags != null) {
            this.tags.clear();
            this.tags.addAll(tags);
        }
    }

    public void updateThumbnail(String thumbnailKey) {
        this.thumbnailKey = thumbnailKey;
    }
}
