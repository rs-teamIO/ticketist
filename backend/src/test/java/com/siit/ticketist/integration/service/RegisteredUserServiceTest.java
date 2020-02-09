package com.siit.ticketist.integration.service;

import com.siit.ticketist.exceptions.AuthorizationException;
import com.siit.ticketist.exceptions.BadRequestException;
import com.siit.ticketist.model.RegisteredUser;
import com.siit.ticketist.model.Role;
import com.siit.ticketist.security.UserDetailsServiceImpl;
import com.siit.ticketist.service.RegisteredUserService;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import javax.mail.MessagingException;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/**
 * This class contains integration test methods for {@link RegisteredUserService}.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql("/authentication.sql")
public class RegisteredUserServiceTest {

    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String FIRST_NAME = "First name";
    private static final String LAST_NAME = "Last name";
    private static final String EMAIL = "test123@ticketist.com";
    private static final String VERIFICATION_CODE = "15f1b452-a23c-4f2b-9b23-2746f73d5b2b";
    private static final String TAKEN_EMAIL = "verified_user@ticketist.com";
    private static final String UNVERIFIED_TAKEN_EMAIL = "unverified_user@ticketist.com";
    private static final String TAKEN_USERNAME = "verified_user";

    private static final String USERNAME_TAKEN_EXCEPTION_MESSAGE = "Username '%s' is already taken";
    private static final String EMAIL_TAKEN_EXCEPTION_MESSAGE = "User with e-mail '%s' already registered";
    private static final String VERIFICATION_CODE_EXCEPTION_MESSAGE = "No user found with specified verification code.";

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RegisteredUserService registeredUserService;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    /**
     * Tests create method in {@link RegisteredUserService} when {@link RegisteredUser} instance is not null
     * and the username and e-mail are not taken.
     */
    @Test
    public void create_shouldReturnCreatedRegisteredUserAndSendVerificationEmail_whenUsernameAndEmailAreNotTaken() throws MessagingException {
        // Arrange
        final RegisteredUser registeredUser = new RegisteredUser(USERNAME, PASSWORD, EMAIL, FIRST_NAME, LAST_NAME);
        assertFalse(registeredUser.getIsVerified());
        assertNull(registeredUser.getVerificationCode());

        // Act
        final RegisteredUser savedRegisteredUser = this.registeredUserService.create(registeredUser);

        // Assert
        assertNotNull(savedRegisteredUser);
        assertNotNull(savedRegisteredUser.getId());
        assertEquals(USERNAME, savedRegisteredUser.getUsername());
        assertEquals(PASSWORD, savedRegisteredUser.getPassword());
        assertEquals(EMAIL, savedRegisteredUser.getEmail());
        assertEquals(FIRST_NAME, savedRegisteredUser.getFirstName());
        assertEquals(LAST_NAME, savedRegisteredUser.getLastName());
        assertNull(savedRegisteredUser.getPhone());
        assertNotNull(savedRegisteredUser.getVerificationCode());
        assertEquals(36, savedRegisteredUser.getVerificationCode().length());
        assertFalse(savedRegisteredUser.getIsVerified());
        assertThat(savedRegisteredUser.getAuthorities(), empty());
    }

    /**
     * Tests create method in {@link RegisteredUserService} when {@link RegisteredUser} contains
     * an username that is already taken.
     */
    @Test
    public void create_shouldThrowBadRequestException_whenUsernameIsTaken() throws MessagingException {
        // Arrange
        this.exceptionRule.expect(BadRequestException.class);
        this.exceptionRule.expectMessage(String.format(USERNAME_TAKEN_EXCEPTION_MESSAGE, TAKEN_USERNAME));
        final RegisteredUser registeredUser = new RegisteredUser(TAKEN_USERNAME, PASSWORD, EMAIL, FIRST_NAME, LAST_NAME);
        assertFalse(registeredUser.getIsVerified());
        assertNull(registeredUser.getVerificationCode());

        // Act
        this.registeredUserService.create(registeredUser);
    }

    /**
     * Tests create method in {@link RegisteredUserService} when {@link RegisteredUser} contains
     * an username that is free and an e-mail that is already taken.
     */
    @Test
    public void create_shouldThrowBadRequestException_whenUsernameIsNotTakenButEmailIsTaken() throws MessagingException {
        // Arrange
        this.exceptionRule.expect(BadRequestException.class);
        this.exceptionRule.expectMessage(String.format(EMAIL_TAKEN_EXCEPTION_MESSAGE, TAKEN_EMAIL));
        final RegisteredUser registeredUser = new RegisteredUser(USERNAME, PASSWORD, TAKEN_EMAIL, FIRST_NAME, LAST_NAME);
        assertFalse(registeredUser.getIsVerified());
        assertNull(registeredUser.getVerificationCode());

        // Act
        this.registeredUserService.create(registeredUser);
    }

    /**
     * Tests update method in {@link RegisteredUserService} when {@link RegisteredUser} updated e-mail is not taken.
     */
    @Test
    public void update_shouldReturnUpdatedRegisteredUser_whenUpdatedEmailIsNotTaken() {
        // Arrange
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(TAKEN_USERNAME, TAKEN_USERNAME));
        final UserDetails userDetails = this.userDetailsService.loadUserByUsername(TAKEN_USERNAME);
        final RegisteredUser updatedUser = new RegisteredUser(TAKEN_USERNAME, TAKEN_USERNAME, EMAIL, FIRST_NAME, LAST_NAME);

        // Act
        final RegisteredUser updatedAndPersistedRegisteredUser = this.registeredUserService.update(updatedUser, null);

        // Assert
        assertNotNull(updatedAndPersistedRegisteredUser);
        assertNotNull(updatedAndPersistedRegisteredUser.getId());
        assertEquals(TAKEN_USERNAME, updatedAndPersistedRegisteredUser.getUsername());
        assertTrue(this.passwordEncoder.matches(TAKEN_USERNAME,  updatedAndPersistedRegisteredUser.getPassword()));
        assertEquals(EMAIL, updatedAndPersistedRegisteredUser.getEmail());
        assertEquals(FIRST_NAME, updatedAndPersistedRegisteredUser.getFirstName());
        assertEquals(LAST_NAME, updatedAndPersistedRegisteredUser.getLastName());
        assertEquals(updatedUser.getPhone(), updatedAndPersistedRegisteredUser.getPhone());
        assertEquals(updatedUser.getVerificationCode(), updatedAndPersistedRegisteredUser.getVerificationCode());
        assertTrue(updatedAndPersistedRegisteredUser.getIsVerified());
        assertEquals(1, updatedAndPersistedRegisteredUser.getAuthorities().size());
    }

    /**
     * Tests update method in {@link RegisteredUserService} when {@link RegisteredUser} updated e-mail is not taken.
     */
    @Test
    public void update_shouldThrowBadRequestException_whenUpdatedEmailIsTaken() {
        // Arrange
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(TAKEN_USERNAME, TAKEN_USERNAME));
        this.exceptionRule.expect(BadRequestException.class);
        this.exceptionRule.expectMessage(String.format(EMAIL_TAKEN_EXCEPTION_MESSAGE, UNVERIFIED_TAKEN_EMAIL));

        final RegisteredUser updatedUser = new RegisteredUser(TAKEN_USERNAME, TAKEN_USERNAME, UNVERIFIED_TAKEN_EMAIL, FIRST_NAME, LAST_NAME);

        // Act
        this.registeredUserService.update(updatedUser, null);
    }

    /**
     * Tests update method in {@link RegisteredUserService} when {@link RegisteredUser} only password is updated.
     */
    @Test
    public void update_shouldReturnUpdatedRegisteredUser_whenOnlyPasswordIsUpdated() {
        // Arrange
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(TAKEN_USERNAME, TAKEN_USERNAME));
        final RegisteredUser updatedUser = new RegisteredUser(TAKEN_USERNAME, TAKEN_USERNAME, TAKEN_EMAIL, FIRST_NAME, LAST_NAME);

        // Act
        final RegisteredUser updatedAndPersistedRegisteredUser = this.registeredUserService.update(updatedUser, this.passwordEncoder.encode(PASSWORD));

        // Assert
        assertNotNull(updatedAndPersistedRegisteredUser);
        assertNotNull(updatedAndPersistedRegisteredUser.getId());
        assertEquals(TAKEN_USERNAME, updatedAndPersistedRegisteredUser.getUsername());
        assertTrue(this.passwordEncoder.matches(PASSWORD, updatedAndPersistedRegisteredUser.getPassword()));
        assertEquals(TAKEN_EMAIL, updatedAndPersistedRegisteredUser.getEmail());
        assertEquals(FIRST_NAME, updatedAndPersistedRegisteredUser.getFirstName());
        assertEquals(LAST_NAME, updatedAndPersistedRegisteredUser.getLastName());
        assertEquals(updatedUser.getPhone(), updatedAndPersistedRegisteredUser.getPhone());
        assertEquals(updatedUser.getVerificationCode(), updatedAndPersistedRegisteredUser.getVerificationCode());
        assertEquals(updatedAndPersistedRegisteredUser.getIsVerified(), updatedAndPersistedRegisteredUser.getIsVerified());
        assertEquals(1, updatedAndPersistedRegisteredUser.getAuthorities().size());
    }

    /**
     * Tests update method in {@link RegisteredUserService} when {@link RegisteredUser} with given username is not found.
     * Should throw {@link AuthorizationException}.
     */
    @Test(expected = AuthorizationException.class)
    public void update_shouldThrowAuthorizationException_ifRegisteredUserWithGivenUsernameIsNotFound() {
        // Arrange
        final RegisteredUser registeredUser = new RegisteredUser(TAKEN_USERNAME.concat(":)"), PASSWORD, EMAIL, FIRST_NAME, LAST_NAME);

        // Act
        this.registeredUserService.update(registeredUser, null);
    }

    /**
     * Tests verify method in {@link RegisteredUserService} when given verification code is assigned to an user.
     */
    @Test
    public void verify_shouldReturnRegisteredUser_whenVerificationCodeIsAssignedToUser() {
        // Act
        final RegisteredUser verifiedRegisteredUser = this.registeredUserService.verify(VERIFICATION_CODE);

        // Assert
        assertNotNull(verifiedRegisteredUser);
        assertTrue(verifiedRegisteredUser.getIsVerified());
        assertThat(verifiedRegisteredUser.getAuthorities(), hasSize(1));
        assertThat(verifiedRegisteredUser.getAuthorities(), hasItem(Role.REGISTERED_USER));
        assertNull(verifiedRegisteredUser.getVerificationCode());
    }

    /**
     * Tests verify method in {@link RegisteredUserService} when given verification code is not assigned to any user.
     * Should throw {@link BadRequestException}.
     */
    @Test
    public void verify_shouldThrowBadRequestException_whenVerificationCodeIsNotAssignedToAnyUser() {
        // Arrange
        this.exceptionRule.expect(BadRequestException.class);
        this.exceptionRule.expectMessage(VERIFICATION_CODE_EXCEPTION_MESSAGE);

        // Act
        this.registeredUserService.verify(VERIFICATION_CODE.concat(":)"));
    }
}
