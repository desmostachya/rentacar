package com.kush.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateLocationRequest {
    private String locationName;
    private String address;
    private String city;
    private String postalCode;
    private String phoneNumber;
}
