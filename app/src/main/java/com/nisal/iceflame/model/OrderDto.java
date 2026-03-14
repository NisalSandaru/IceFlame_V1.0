package com.nisal.iceflame.model;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDto {
    private Long id;
    private Long userId;
    private Long addressId;
    private Double totalAmount;

    private String status;
    private String paymentStatus;
    private String paymentMethod;
    private String createdAt;

    private List<OrderItemDto> items;
}
