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
public class User {
    private Long id;
    private String fullName;
    private String email;
    private String password;        // usually optional for frontend
    private String profileImageUrl;
    private String role;
    private Boolean isActive;
    private String createdAt;
    private String updatedAt;
}
