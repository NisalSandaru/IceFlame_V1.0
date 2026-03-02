package com.nisal.iceflame.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryDto {
    private Long id;
    private String name;
    private String description;
    private Boolean isActive;
    private String catImageUrl;
    private String createdAt;
    private String updatedAt;
}
