package com.siit.ticketist.service;

import com.siit.ticketist.controller.exceptions.BadRequestException;
import com.siit.ticketist.model.RegisteredUser;
import com.siit.ticketist.model.Role;
import com.siit.ticketist.repository.RegisteredUserRepository;
import com.siit.ticketist.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.util.UUID;

/**
 * Registered User service layer.
 */
@Service
public class RegisteredUserService {

    private final UserRepository userRepository;
    private final RegisteredUserRepository registeredUserRepository;
    private final EmailService emailService;

    @Autowired
    public RegisteredUserService(UserRepository userRepository, RegisteredUserRepository registeredUserRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.registeredUserRepository = registeredUserRepository;
        this.emailService = emailService;
    }

    /**
     * Creates a new Registered User and sends verification email
     *
     * @param registeredUser RegisteredUser instance to be created
     * @return created RegisteredUser
     */
    public RegisteredUser create(RegisteredUser registeredUser) throws MessagingException {
        registeredUser.setIsVerified(false);
        registeredUser.setVerificationCode(UUID.randomUUID().toString());

        userRepository.save(registeredUser);
        emailService.sendVerificationEmail(registeredUser);

        return registeredUser;
    }

    public RegisteredUser verify(String verificationCode) {

        final RegisteredUser registeredUser = registeredUserRepository.findByVerificationCode(verificationCode)
                .orElseThrow(() -> new BadRequestException("No user found with specified verification code."));

        registeredUser.setIsVerified(true);
        registeredUser.getAuthorities().add(Role.REGISTERED_USER);
        registeredUser.setVerificationCode(null);
        userRepository.save(registeredUser);

        return registeredUser;
    }
}
