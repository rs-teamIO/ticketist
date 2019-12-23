package com.siit.ticketist.repository;

import com.siit.ticketist.model.RegisteredUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RegisteredUserRepository extends JpaRepository<RegisteredUser, Long> {

    Optional<RegisteredUser> findByVerificationCode(String verificationCode);
}