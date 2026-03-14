package com.nisal.iceflame.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderItemDto {
    private Long id;

    private Long productId;

    private String productName;

    private Integer quantity;

    private Double price;
}
