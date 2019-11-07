package com.siit.ticketist.service;

import com.siit.ticketist.controller.exceptions.AuthorizationException;
import com.siit.ticketist.controller.exceptions.BadRequestException;
import com.siit.ticketist.controller.exceptions.ForbiddenException;
import com.siit.ticketist.model.Admin;
import com.siit.ticketist.model.RegisteredUser;
import com.siit.ticketist.model.User;
import com.siit.ticketist.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Optional;

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
     * @return User instance
     */
    public User findByUsername(String username) {
        return userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(AuthorizationException::new);
    }

    /**
     * Finds an Admin with given username.
     *
     * @param username Username
     * @return Admin instance
     */
    public Admin findAdminByUsername(String username) {
        return (Admin) findByUsername(username);
    }

    /**
     * Finds a Registered user with given username.
     *
     * @param username Username
     * @return RegisteredUser instance
     */
    public RegisteredUser findRegisteredUserByUsername(String username) {
        return (RegisteredUser) findByUsername(username);
    }

    /**
     * Finds the currently active user.
     * If no user is logged in, the method throws a {@link ForbiddenException}.
     *
     * @return Currently active User instance
     */
    public User findCurrentUser() {
        final UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null)
            throw new ForbiddenException("You don't have permission to access this method on the server.");

        final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return findByUsername(userDetails.getUsername());
    }

    /**
     * Checks if username is already taken.
     * In case an user with the same username exists {@link BadRequestException} is thrown.
     *
     * @param username username
     */
    public void checkIfUsernameTaken(String username) {
        userRepository.findByUsernameIgnoreCase(username)
                .ifPresent(u -> {throw new BadRequestException(String.format("Username '%s' is already taken", username));});
    }
}
