package com.kush.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateUserRequest {
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String address;
    private String city;
    private String postalCode;
}
