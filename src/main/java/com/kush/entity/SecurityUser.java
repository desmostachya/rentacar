package com.kush.entity;


import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@Data
public class SecurityUser implements UserDetails {

    private final Collection<? extends GrantedAuthority> authorities;
    private Long id;
    private String username;
    private String password;
    private boolean accountNonExpired;
    private boolean accountNonLocked;
    private boolean credentialsNonExpired;
    private boolean enabled;
    private User user;

    public SecurityUser(User user) {
        Set<String> actions = new HashSet<>();
        actions.add(user.getRole().name());
        Collection<SimpleGrantedAuthority> directAuthorities = actions.stream()
                .map(SimpleGrantedAuthority::new).toList();
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>(directAuthorities);
        this.id = user.getUserId();
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.accountNonExpired = true;
        this.accountNonLocked = true;
        this.credentialsNonExpired = true;
        this.enabled = user.isEnabled();
        this.authorities = authorities;
        this.user = user;
    }


}