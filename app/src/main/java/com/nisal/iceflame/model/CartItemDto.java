package com.nisal.iceflame.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartItemDto {
    private Long id;
    private Long productId;
    private String productName;
    private String imageUrl;
    private double rating;
    private double price;
    private int quantity;
}
