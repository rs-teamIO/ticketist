package com.siit.ticketist.repository;

import com.siit.ticketist.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    <E extends User> Optional<E> findById(Long id);

    <E extends User> Optional<E> findByUsernameIgnoreCase(String username);

    <E extends User> Optional<E> findByEmail(String email);

    <E extends User> E save(E user);
}
