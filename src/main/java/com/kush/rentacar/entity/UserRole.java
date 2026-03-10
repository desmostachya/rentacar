package com.kush.rentacar.entity;

import org.springframework.security.core.GrantedAuthority;

public enum UserRole implements GrantedAuthority {
    ADMIN("Admin - Full system access"),
    MANAGER("Manager - Reporting Access"),
    AGENT("Agent - Reservation and check-in access"),
    CUSTOMER("Customer - Browse and book vehicles");

    private final String description;

    UserRole(String description) {
        this.description = description;
    }

    @Override
    public String getAuthority() {
        return "ROLE_" + this.name();
    }

    public String getDescription() {
        return description;
    }
}
