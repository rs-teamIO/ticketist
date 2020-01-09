package com.siit.ticketist.service;

import com.siit.ticketist.exceptions.BadRequestException;
import com.siit.ticketist.model.RegisteredUser;
import com.siit.ticketist.model.Role;
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

        this.userService.checkIfUsernameTaken(registeredUser.getUsername());
        this.userService.checkIfEmailTaken(registeredUser.getEmail());
        final RegisteredUser savedUser = this.userService.save(registeredUser);
        this.emailService.sendVerificationEmail(registeredUser);

        return savedUser;
    }

    /**
     * Updates the {@link RegisteredUser} data
     *
     * @param updatedUser {@link RegisteredUser} instance containing updated data
     * @param newPassword new password to replace the old one
     * @return updated {@link RegisteredUser} instance
     */
    public RegisteredUser update(RegisteredUser updatedUser, String newPassword) {
        final RegisteredUser registeredUser = this.userService.findRegisteredUserByUsername(updatedUser.getUsername());

        registeredUser.setFirstName(updatedUser.getFirstName());
        registeredUser.setLastName(updatedUser.getLastName());
        registeredUser.setEmail(updatedUser.getEmail());
        registeredUser.setPhone(updatedUser.getPhone());

        if(newPassword != null)
            registeredUser.setPassword(newPassword);

       // this.userService.checkIfUsernameTaken(registeredUser.getUsername());
        this.userService.checkIfEmailTaken(registeredUser.getEmail());

        return this.userService.save(registeredUser);
    }

    /**
     * Verifies the user's account and removes previously generated verification code.
     *
     * @param verificationCode verification code of the user to be verified
     * @return verified {@link RegisteredUser}
     */
    public RegisteredUser verify(String verificationCode) {

        final RegisteredUser registeredUser = this.registeredUserRepository.findByVerificationCode(verificationCode)
                .orElseThrow(() -> new BadRequestException("No user found with specified verification code."));

        registeredUser.setIsVerified(true);
        registeredUser.getAuthorities().add(Role.REGISTERED_USER);
        registeredUser.setVerificationCode(null);

        return this.userService.save(registeredUser);
    }
}
