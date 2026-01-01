package com.petstarproject.petstar.entity;

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
@Table(name = "postings")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Posting {

    @Id
    @Column(length = 36)
    private String id; // UUID(PK)

    @Column(name = "pet_id", nullable = false, length = 36)
    private String petId; // FK

    @Column(name = "owner_id", nullable = false, length = 36)
    private String ownerId;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Visibility visibility; // PUBLIC, PRIVATE

    @Column(name = "like_count", nullable = false)
    private int likeCount;

    @Column(name = "comment_count", nullable = false)
    private int commentCount;

    @ElementCollection
    @CollectionTable(
            name = "posting_images",
            joinColumns = @JoinColumn(name = "posting_id")
    )
    @OrderColumn(name = "sort_order")
    @Column(name = "image_key", length = 500, nullable = false)
    private List<String> imageKeys = new ArrayList<>(); // 최대 5개

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // 도메인 메서드

    public static Posting create(
            String id,
            String petId,
            String ownerId,
            String title,
            String content,
            Visibility visibility,
            List<String> imageKeys
    ) {
        Posting posting = new Posting();
        posting.id = id;
        posting.petId = petId;
        posting.ownerId = ownerId;
        posting.title = title;
        posting.content = content;
        posting.visibility = (visibility != null) ? visibility : Visibility.PUBLIC;

        posting.likeCount = 0;
        posting.commentCount = 0;

        posting.imageKeys.addAll(imageKeys);

        LocalDateTime now = LocalDateTime.now();
        posting.createdAt = now;
        posting.updatedAt = now;
        posting.publishedAt = null;

        return posting;
    }


    public void updateMeta(String title,
                           String content,
                           Visibility visibility) {
        if (title != null) this.title = title;
        if (content != null) this.content = content;
        if (visibility != null) this.visibility = visibility;
        this.updatedAt = LocalDateTime.now();
    }
}
