package com.siit.ticketist.service;

import com.siit.ticketist.model.RegisteredUser;
import com.siit.ticketist.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Registered User service layer.
 */
@Service
public class RegisteredUserService {

    private final UserRepository userRepository;

    @Autowired
    public RegisteredUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Creates a new Registered User
     *
     * @param registeredUser RegisteredUser instance to be created
     * @return created RegisteredUser
     */
    public RegisteredUser create(RegisteredUser registeredUser) {
        registeredUser.setIsVerified(false);
        registeredUser.setVerificationCode(UUID.randomUUID().toString());

        return userRepository.save(registeredUser);
    }
}
