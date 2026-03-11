package com.kush.service;


import com.kush.config.jwt.JwtTokenProvider;
import com.kush.dto.AuthResponse;
import com.kush.dto.LoginRequest;
import com.kush.dto.RefreshTokenRequest;
import com.kush.dto.RegisterRequest;
import com.kush.entity.SecurityUser;
import com.kush.entity.TokenType;
import com.kush.entity.User;
import com.kush.entity.UserRole;
import com.kush.exception.BadRequestException;
import com.kush.exception.DuplicateResourceException;
import com.kush.exception.UnauthorizedException;
import com.kush.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthenticationService implements AuthService {

    @Value("${jwt_configs.access_token_validity_s}")
    private long validity;

    @Value("${jwt_configs.refresh_token_validity_s}")
    private long refreshTokenValidity;

    private final AuthenticationManager authenticationManager;

    private final JwtTokenProvider jwtTokenProvider;

    private final UserDetailsService userDetailsService;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;


    public AuthResponse generateTokens(Authentication authentication) {
        SecurityUser user = (SecurityUser) authentication.getPrincipal();
        String accessToken = jwtTokenProvider.createToken(authentication, TokenType.ACCESS, true);
        String refreshToken = jwtTokenProvider.createToken(authentication, TokenType.REFRESH, false);
        assert user != null;
        return AuthResponse.builder()
                .username(user.getUsername())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .accessTokenExpiry((int) validity)
                .refreshTokenExpiry((int) refreshTokenValidity)
                .token(accessToken)
                .email(user.getUsername())
                .build();
    }

    public AuthResponse authenticate(Authentication auth) {
            Authentication authentication = authenticationManager.authenticate(auth);
            return generateTokens(authentication);

    }

    public AuthResponse authenticateRefreshToken(RefreshTokenRequest refreshTokenRequest) {
            Pair<String, String> data = jwtTokenProvider.getAuthentication(refreshTokenRequest.getRefreshToken());
            SecurityUser user = (SecurityUser) userDetailsService.loadUserByUsername(data.getFirst());
            if (user != null) {
                if (!user.isEnabled()) {
                    throw new DisabledException("account.not.active");
                }

                Authentication auth = new UsernamePasswordAuthenticationToken(user, data.getSecond(), user.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(auth);
                return generateTokens(auth);
            } else {
                log.error("User associated with token not found");
                throw new UnauthorizedException("Invalid Token");
            }

    }

    public AuthResponse login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            SecurityUser user = (SecurityUser) authentication.getPrincipal();
            String accessToken = jwtTokenProvider.createToken(authentication, TokenType.ACCESS, true);
            String refreshToken = jwtTokenProvider.createToken(authentication, TokenType.REFRESH, false);


            log.info("User logged in successfully: {}", request.getEmail());

            return AuthResponse.builder()
                    .username(user.getUsername())
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .accessTokenExpiry((int) validity)
                    .refreshTokenExpiry((int) refreshTokenValidity)
                    .token(accessToken)
                    .email(user.getUsername())
                    .build();
        } catch (AuthenticationException e) {
            log.warn("Login failed for user: {}", request.getEmail());
            throw new UnauthorizedException("Invalid email or password");
        }
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new DuplicateResourceException("Email already registered");
        }

        if (request.getPassword().length() < 6) {
            throw new BadRequestException("Password must be at least 6 characters");
        }

        User newUser = User.builder()
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phoneNumber(request.getPhoneNumber())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(UserRole.CUSTOMER)
                .isActive(true)
                .build();

        User savedUser = userRepository.save(newUser);

        log.info("New user registered: {}", request.getEmail());
        SecurityUser user = (SecurityUser) userDetailsService.loadUserByUsername(savedUser.getEmail());

        Authentication authentication = new UsernamePasswordAuthenticationToken(request.getEmail(), null, user.getAuthorities());

        String accessToken = jwtTokenProvider.createToken(authentication, TokenType.ACCESS, true);
        String refreshToken = jwtTokenProvider.createToken(authentication, TokenType.REFRESH, false);


        log.info("User logged in successfully: {}", request.getEmail());

        return AuthResponse.builder()
                .username(user.getUsername())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .accessTokenExpiry((int) validity)
                .refreshTokenExpiry((int) refreshTokenValidity)
                .token(accessToken)
                .email(user.getUsername())
                .build();
    }


}
