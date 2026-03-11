package com.kush.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {
    private String token;
    private String type = "Bearer";
    private Long userId;
    private String email;
    private String fullName;
    private String role;

    private String username;
    private String accessToken;
    private String refreshToken;
    private int accessTokenExpiry;
    private int refreshTokenExpiry;
}
