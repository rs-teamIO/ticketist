package com.siit.ticketist.security;

import com.siit.ticketist.model.User;
import com.siit.ticketist.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

/**
 * Implementation of {@link UserDetailsService}.
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Generates a {@link UserDetails} instance for given username
     * In case an user with given username is not found, {@link UsernameNotFoundException} is thrown.
     *
     * @param username User's username
     * @return {@link UserDetails} instance
     */
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) {
        final User user = this.userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("No user found with username '%s'.", username)));
        return UserDetailsFactory.create(user);
    }
}

