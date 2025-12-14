package com.petstarproject.petstar.entity;


import com.petstarproject.petstar.enums.Gender;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "pets")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Pet {
    @Id
    @Column(length = 36)
    private String id;  // UUID(PK)

    @Column(name = "owner_id", nullable = false, length = 36)
    private String ownerId; // FK

    @Column(nullable = false, length = 50)
    private String name;

    private Integer age;

    @Column(length = 100)
    private String species;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Gender gender;      // MALE, FEMALE

    @Column(columnDefinition = "TEXT")
    private String bio;     // 소개글

    @Column(name = "profile_image_key", length = 500)
    private String profileImageKey; // s3 프로필 이미지 key

    @Column(name = "subscription_count", nullable = false)
    private Integer subscriptionCount; // 구독자 수
}
