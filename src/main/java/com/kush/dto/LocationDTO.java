package com.kush.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocationDTO {
    private Long locationId;
    private String locationName;
    private String address;
    private String city;
    private String postalCode;
    private String phoneNumber;
    private String status;
    private LocalDateTime createdAt;
}
