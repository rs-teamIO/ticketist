package com.siit.ticketist.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Custom implementation of {@link UserDetails}.
 */
@Getter @Setter
public class SpringSecurityUser implements UserDetails {

    private Long id;
    private String username;
    private String password;
    private String email;
    private Collection<? extends GrantedAuthority> authorities;
    private Boolean accountNonExpired = true;
    private Boolean accountNonLocked = true;
    private Boolean credentialsNonExpired = true;
    private Boolean enabled = true;

    public SpringSecurityUser() {
        super();
    }

    public SpringSecurityUser(Long id, String username, String password, String email,
                              Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.authorities = authorities;
    }

    @Override
    public boolean isAccountNonExpired() { return this.getAccountNonExpired(); }

    @Override
    public boolean isAccountNonLocked() { return this.getAccountNonLocked(); }

    @Override
    public boolean isCredentialsNonExpired() { return this.getCredentialsNonExpired(); }

    @Override
    public boolean isEnabled() {
        return this.getEnabled();
    }
}
