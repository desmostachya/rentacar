package com.kush.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateUserRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phoneNumber;
    private String address;
    private String city;
    private String postalCode;
    private String role;
}
