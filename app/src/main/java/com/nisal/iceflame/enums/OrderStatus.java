package com.nisal.iceflame.enums;

public enum OrderStatus {
    PENDING,        // order created
    CONFIRMED,      // payment confirmed
    PROCESSING,     // preparing order
    SHIPPED,        // sent to delivery
    DELIVERED,
    CANCELLED
}
