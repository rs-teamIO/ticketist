package com.siit.ticketist.security;

import com.siit.ticketist.model.RegisteredUser;
import com.siit.ticketist.model.User;
import com.siit.ticketist.repository.UserRepository;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * This class contains integration test methods for {@link UserDetailsServiceImpl}.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@Sql(scripts = "classpath:users.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Transactional
public class UserDetailsServiceImplIntegrationTest {

    public static final String USERNAME = "test";
    public static final String PASSWORD = "password";
    public static final String EMAIL = "test@ticketist.com";
    public static final String FIRST_NAME = "First name";
    public static final String LAST_NAME = "Last name";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    /**
     * Inserts a {@link RegisteredUser} into the DB
     */
    @Before
    public void setUp() {
        this.userRepository.save(new RegisteredUser(USERNAME, PASSWORD, EMAIL, FIRST_NAME, LAST_NAME));
    }

    /**
     * Tests loadUserByUsername from {@link UserDetailsServiceImpl} when user exists.
     * Should return {@link UserDetails} instance.
     */
    @Test
    public void loadUserByUsername_shouldReturnUserDetails_whenUserExists() {

        // Arrange
        final User existingUser = this.userRepository.findByUsernameIgnoreCase(USERNAME).orElseGet(null);

        // Act
        final UserDetails userDetails = this.userDetailsService.loadUserByUsername(existingUser.getUsername());

        // Assert
        assertNotNull("User details loaded", userDetails);
        assertEquals("Username correct", existingUser.getUsername(), userDetails.getUsername());
    }

    /**
     * Tests loadUserByUsername from {@link UserDetailsServiceImpl} when user doesn't exist.
     * Should throw {@link UsernameNotFoundException}.
     */
    @Test(expected = UsernameNotFoundException.class)
    public void loadUserByUsername_shouldThrowUsernameNotFoundException_whenUserDoesNotExist() {

        // Arrange
        final User existingUser = this.userRepository.findByUsernameIgnoreCase(USERNAME).orElseGet(null);
        this.userRepository.delete(existingUser);

        // Act
        this.userDetailsService.loadUserByUsername(USERNAME);
    }
}
