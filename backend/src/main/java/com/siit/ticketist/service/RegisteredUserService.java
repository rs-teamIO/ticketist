package com.siit.ticketist.service;

import com.siit.ticketist.exceptions.AuthorizationException;
import com.siit.ticketist.exceptions.BadRequestException;
import com.siit.ticketist.model.RegisteredUser;
import com.siit.ticketist.model.Role;
import com.siit.ticketist.model.User;
import com.siit.ticketist.repository.RegisteredUserRepository;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.util.UUID;

/**
 * Registered User service layer.
 */
@Service
public class RegisteredUserService {

    private final UserService userService;
    private final RegisteredUserRepository registeredUserRepository;
    private final EmailService emailService;

    public RegisteredUserService(UserService userService, RegisteredUserRepository registeredUserRepository, EmailService emailService) {
        this.userService = userService;
        this.registeredUserRepository = registeredUserRepository;
        this.emailService = emailService;
    }

    /**
     * Creates a new Registered User and sends verification email
     *
     * @param registeredUser {@link RegisteredUser} instance to be created
     * @return created {@link RegisteredUser} instance
     */
    public RegisteredUser create(RegisteredUser registeredUser) throws MessagingException {
        registeredUser.setIsVerified(false);
        registeredUser.setVerificationCode(UUID.randomUUID().toString());

        this.userService.save(registeredUser);
        this.emailService.sendVerificationEmail(registeredUser);

        return registeredUser;
    }

    public RegisteredUser update(RegisteredUser updatedUser, String newPassword) {
        User currentUser = this.userService.findCurrentUser();
        RegisteredUser registeredUser = this.userService.findRegisteredUserByUsername(updatedUser.getUsername());
        if(!currentUser.getUsername().equalsIgnoreCase(registeredUser.getUsername()))
            throw new AuthorizationException("Usernames don't match.");

        Boolean oldPasswordCorrect = registeredUser.getPassword().equals(updatedUser.getPassword());
        if(!oldPasswordCorrect.booleanValue())
            throw new AuthorizationException("Incorrect password.");

        registeredUser.setFirstName(updatedUser.getFirstName());
        registeredUser.setLastName(updatedUser.getLastName());
        registeredUser.setEmail(updatedUser.getEmail());

        if(newPassword != null)
            registeredUser.setPassword(updatedUser.getPassword());

        return this.userService.save(registeredUser);
    }

    /**
     * Verifies the user's account and removes previously generated verification code.
     *
     * @param verificationCode verification code of the user to be verified
     * @return verified {@link RegisteredUser}
     */
    public RegisteredUser verify(String verificationCode) {

        final RegisteredUser registeredUser = registeredUserRepository.findByVerificationCode(verificationCode)
                .orElseThrow(() -> new BadRequestException("No user found with specified verification code."));

        registeredUser.setIsVerified(true);
        registeredUser.getAuthorities().add(Role.REGISTERED_USER);
        registeredUser.setVerificationCode(null);
        this.userService.save(registeredUser);

        return registeredUser;
    }
}
