package com.siit.ticketist.security;

import com.siit.ticketist.model.User;
import com.siit.ticketist.security.SpringSecurityUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * UserDetails Factory class used for creating SpringSecurityUser objects
 */
public class UserDetailsFactory {

    /**
     * Creates SpringSecurityUser from a User instance.
     *
     * @param user User instance
     * @return {@link SpringSecurityUser} Instance
     */
    public static SpringSecurityUser create(User user) {
        Collection<? extends GrantedAuthority> authorities;
        try {
            authorities = user.getAuthorities().stream().map(a -> new SimpleGrantedAuthority(a.toString())).collect(Collectors.toList());
        } catch (Exception e) {
            authorities = null;
        }

        return new SpringSecurityUser(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                user.getEmail(),
                authorities
        );
    }
}
