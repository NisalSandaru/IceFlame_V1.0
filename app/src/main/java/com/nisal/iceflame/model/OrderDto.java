package com.nisal.iceflame.model;

import java.time.LocalDateTime;
import java.util.List;

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
