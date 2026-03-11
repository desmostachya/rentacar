package com.kush.controller;

import com.kush.dto.ApiResponse;
import com.kush.dto.AuthResponse;
import com.kush.dto.LoginRequest;
import com.kush.dto.RegisterRequest;
import com.kush.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/auth")
//@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8080"})
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        log.info("Login request for email: {}", request.getEmail());
        
        AuthResponse authResponse = authenticationService.login(request);
        
        ApiResponse<AuthResponse> response = ApiResponse.<AuthResponse>builder()
                .success(true)
                .message("Login successful")
                .data(authResponse)
                .timestamp(LocalDateTime.now())
                .build();
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Registration request for email: {}", request.getEmail());
        
        AuthResponse authResponse = authenticationService.register(request);
        
        ApiResponse<AuthResponse> response = ApiResponse.<AuthResponse>builder()
                .success(true)
                .message("Registration successful")
                .data(authResponse)
                .timestamp(LocalDateTime.now())
                .build();
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/validate-token")
    public ResponseEntity<ApiResponse<Object>> validateToken(@RequestHeader("Authorization") String token) {
        // Token validation is handled by Spring Security
        ApiResponse<Object> response = ApiResponse.builder()
                .success(true)
                .message("Token is valid")
                .timestamp(LocalDateTime.now())
                .build();
        
        return ResponseEntity.ok(response);
    }
}
