package com.kush.config.jwt;

import com.kush.entity.SecurityUser;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
public class JwtTokenAuthenticationFilter extends GenericFilterBean {

    public static final String HEADER_PREFIX = "Bearer ";

    private final JwtTokenProvider jwtTokenProvider;

    private final UserDetailsService userDetailsService;


    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain filterChain) throws IOException, ServletException {

        String token = resolveToken((HttpServletRequest) req);
        if (token != null && jwtTokenProvider.validateToken(token,true)) {
            Pair<String, String> data = jwtTokenProvider.getAuthentication(token);
            SecurityUser user = (SecurityUser) userDetailsService.loadUserByUsername(data.getFirst());
            if (user != null) {
                Authentication auth = new UsernamePasswordAuthenticationToken(user, data.getSecond(), user.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }
        filterChain.doFilter(req, res);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(HEADER_PREFIX)) {
            return bearerToken.substring(7);
        }
        return null;
    }

}
