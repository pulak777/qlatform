package com.qlatform.quant.model.adapter;

import com.qlatform.quant.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public record CustomUserDetails(User user) implements UserDetails {
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Convert the role into a GrantedAuthority
        return List.of(new SimpleGrantedAuthority(user.getRole().name()));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Customize as needed
    }

    @Override
    public boolean isAccountNonLocked() {
        return !user.isBlocked(); // Use the `blocked` field
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Customize as needed
    }

    @Override
    public boolean isEnabled() {
        return user.isEnabled(); // Use the `enabled` field
    }
}
