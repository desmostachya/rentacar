package com.kush.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    private Long userId;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String address;
    private String city;
    private String postalCode;
    private String role;
    private Boolean isActive;
    private LocalDateTime createdAt;
}
