package com.petstarproject.petstar.dto;

import com.petstarproject.petstar.enums.Visibility;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VideoInfoRequest {

    @NotBlank
    private String title;

    private String description;

    private Visibility visibility;  // 디폴트: PUBLIC

    private List<String> tags;

}
