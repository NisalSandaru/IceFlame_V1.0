package com.nisal.iceflame.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductDto {
    private Long id;
    private String name;
    private String description;
    private Double price;
    private List<String> images;
    private Boolean isActive;
    private Long categoryId;
    private Double rating;
    private String portionSize;
    private String createdAt;
    private String updatedAt;
    private Integer kcal;
}
