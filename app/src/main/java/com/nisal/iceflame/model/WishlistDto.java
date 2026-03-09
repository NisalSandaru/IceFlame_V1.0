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
public class WishlistDto {
    private Long id;
    private Long userId;
    private List<WishItemDto> items;
    private String createdAt;
    private String updatedAt;
}
