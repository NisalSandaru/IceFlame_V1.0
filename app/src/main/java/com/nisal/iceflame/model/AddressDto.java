package com.nisal.iceflame.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddressDto {
    private Long id;
    private String street;
    private String city;
    private String postalCode;
//    private String type;
private String title;
    private Boolean isDefault;
    private Long userId;
}
