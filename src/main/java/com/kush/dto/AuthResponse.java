package com.kush.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {

    private String username;
    private String accessToken;
    private String refreshToken;
    private int accessTokenExpiry;
    private int refreshTokenExpiry;
}
