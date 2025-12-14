package com.petstarproject.petstar.dto;

import com.petstarproject.petstar.enums.Visibility;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VideoUpdateRequest {

    private String title;

    private String description;

    private Visibility visibility;

    private List<String> tags;
}
