package com.petstarproject.petstar.dto;

import com.petstarproject.petstar.enums.Visibility;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import software.amazon.awssdk.annotations.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostingRequest {

    @NotNull
    private String petId;

    @NotBlank
    private String title;

    String content;

    private Visibility visibility; // null 이면 PUBLIC
}
