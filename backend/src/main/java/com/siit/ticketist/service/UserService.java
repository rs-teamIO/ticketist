package com.siit.ticketist.service;

import com.siit.ticketist.exceptions.AuthorizationException;
import com.siit.ticketist.exceptions.BadRequestException;
import com.siit.ticketist.exceptions.ForbiddenException;
import com.siit.ticketist.model.Admin;
import com.siit.ticketist.model.RegisteredUser;
import com.siit.ticketist.model.User;
import com.siit.ticketist.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

/**
 * User service layer.
 */
@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Finds an User with given username.
     *
     * @param username Username
     * @return {@link User} instance
     */
    public User findByUsername(String username) {
        return this.userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(AuthorizationException::new);
    }

    /**
     * Finds an Admin with given username.
     *
     * @param username Username
     * @return {@link Admin} instance
     */
    public Admin findAdminByUsername(String username) {
        return (Admin) this.findByUsername(username);
    }

    /**
     * Finds a Registered user with given username.
     *
     * @param username Username
     * @return {@link RegisteredUser} instance
     */
    public RegisteredUser findRegisteredUserByUsername(String username) {
        return (RegisteredUser) this.findByUsername(username);
    }

    /**
     * Finds the currently active {@link User}.
     * If no user is logged in, the method throws a {@link ForbiddenException}.
     *
     * @return Currently active {@link User}
     * @throws ForbiddenException Exception thrown in case the authentication fails.
     */
    public User findCurrentUser() {
        final UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null)
            throw new ForbiddenException("You don't have permission to access this method on the server.");

        final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return this.findByUsername(userDetails.getUsername());
    }

    /**
     * Checks if username is already taken.
     * In case an user with the same username exists {@link BadRequestException} is thrown.
     *
     * @param username username
     */
    public void checkIfUsernameTaken(String username) {
        this.userRepository.findByUsernameIgnoreCase(username)
                .ifPresent(u -> {
                    throw new BadRequestException(String.format("Username '%s' is already taken", username));
                });
    }
}
