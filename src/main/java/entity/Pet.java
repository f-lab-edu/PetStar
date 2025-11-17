package entity;


import com.petstarproject.petstar.enums.Gender;
import lombok.Data;

@Data
public class Pet {
    private String id;
    private String name;
    private Integer age;
    private String species;
    private Gender gender;
    private String bio; // 소개글
}
