package com.petstarproject.petstar.entity;


import com.petstarproject.petstar.enums.Gender;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
    private String id; // UUID

    @Column(nullable = false)
    private String ownerId;

    @Column(nullable = false)
    private String name;

    private Integer age;
    private String species;
    private Gender gender;
    private String bio; // 소개글

    private String profileImageKey; // s3 프로필 이미지 key
    private Integer subscriptionCount; // 구독자 수
}
