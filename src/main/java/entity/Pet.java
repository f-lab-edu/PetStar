package entity;


import com.petstarproject.petstar.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Pet {
    private String id;
    private String ownerId;
    private String name;
    private Integer age;
    private String species;
    private Gender gender;
    private String bio; // 소개글
    private String profileImageKey; // s3 프로필 이미지 key
    private Integer subscriptionCount; // 구독자 수
}
