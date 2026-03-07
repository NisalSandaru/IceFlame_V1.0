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
public class CartDto {
    private Long id;
    private Long userId;
    private List<CartItemDto> items;
    private String createdAt;
    private String updatedAt;
}
