package com.siit.ticketist.integration.service;

import com.siit.ticketist.exceptions.AuthorizationException;
import com.siit.ticketist.exceptions.BadRequestException;
import com.siit.ticketist.model.Admin;
import com.siit.ticketist.model.RegisteredUser;
import com.siit.ticketist.model.Role;
import com.siit.ticketist.model.User;
import com.siit.ticketist.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * This class contains integration test methods for {@link UserService}.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql("/authentication.sql")
public class UserServiceTest {

    private static final String TAKEN_EMAIL = "verified_user@ticketist.com";
    private static final String FREE_EMAIL = "free@ticketist.com";
    private static final String TAKEN_USERNAME = "verified_user";
    private static final String FREE_USERNAME = "free";
    private static final String ADMIN_USERNAME = "admin";
    private static final String NON_EXISTING_USERNAME = "greenenvy123";

    @Autowired
    private UserService userService;

    /**
     * Tests findByUsername from {@link UserService} when user exists.
     * Should return {@link User} instance.
     */
    @Test
    public void findByUsername_shouldReturnUser_whenUserExists() {
        // Act
        final User user = this.userService.findByUsername(TAKEN_USERNAME);

        // Assert
        assertNotNull("User loaded", user);
        assertEquals("Username correct", TAKEN_USERNAME, user.getUsername());
    }

    /**
     * Tests findByUsername from {@link UserService} when user doesn't exist.
     * Should throw {@link AuthorizationException}.
     */
    @Test(expected = AuthorizationException.class)
    public void findByUsername_shouldThrowAuthorizationException_whenUserDoesNotExist() {
        // Act
        this.userService.findByUsername(NON_EXISTING_USERNAME);
    }

    /**
     * Tests findAdminByUsername from {@link UserService} when admin exists.
     * Should return {@link Admin} instance.
     */
    @Test
    public void findAdminByUsername_shouldReturnAdmin_whenAdminExists() {

        // Act
        final Admin admin = this.userService.findAdminByUsername(ADMIN_USERNAME);

        // Assert
        assertNotNull("Admin loaded", admin);
        assertEquals("Username correct", ADMIN_USERNAME, admin.getUsername());
        assertThat(admin.getAuthorities(), hasSize(1));
        assertThat(admin.getAuthorities(), hasItem(Role.ADMIN));
    }

    /**
     * Tests findAdminByUsername from {@link UserService} when admin doesn't exist.
     * Should throw {@link AuthorizationException}.
     */
    @Test(expected = AuthorizationException.class)
    public void findAdminByUsername_shouldThrowAuthorizationException_whenAdminDoesNotExist() {
        // Act
        this.userService.findAdminByUsername(NON_EXISTING_USERNAME);
    }

    /**
     * Tests findAdminByUsername from {@link UserService} when an user with given
     * username exists but the user is not an admin but a registered user.
     * Should throw {@link ClassCastException}.
     */
    @Test(expected = ClassCastException.class)
    public void findAdminByUsername_shouldThrowClassCastException_whenUserThatIsNotAdminExists() {
        // Act
        final Admin admin = this.userService.findAdminByUsername(TAKEN_USERNAME);
    }

    /**
     * Tests findRegisteredUserByUsername from {@link UserService} when registered user exists.
     * Should return {@link RegisteredUser} instance.
     */
    @Test
    public void findRegisteredUserByUsername_shouldReturnRegisteredUser_whenRegisteredUserExists() {
        // Act
        final RegisteredUser registeredUser = this.userService.findRegisteredUserByUsername(TAKEN_USERNAME);

        // Assert
        assertNotNull("Registered user loaded", registeredUser);
        assertEquals("Username correct", TAKEN_USERNAME, registeredUser.getUsername());
    }

    /**
     * Tests findRegisteredUserByUsername from {@link UserService} when registered user doesn't exist.
     * Should throw {@link AuthorizationException}.
     */
    @Test(expected = AuthorizationException.class)
    public void findRegisteredUserByUsername_shouldThrowAuthorizationException_whenRegisteredUserDoesNotExist() {
        // Act
        this.userService.findRegisteredUserByUsername(NON_EXISTING_USERNAME);
    }

    /**
     * Tests findRegisteredUserByUsername from {@link UserService} when an user with given
     * username exists but the user is not an registered user but an admin.
     * Should throw {@link ClassCastException}.
     */
    @Test(expected = ClassCastException.class)
    public void findRegisteredUserByUsername_shouldThrowClassCastException_whenUserThatIsNotRegisteredUserExists() {
        // Act
        this.userService.findRegisteredUserByUsername(ADMIN_USERNAME);
    }

    /**
     * Tests checkIfUsernameTaken from {@link UserService} when username is not taken.
     * Shouldn't throw any exception.
     */
    @Test
    public void checkIfUsernameTaken_shouldNotThrowAnyException_whenUsernameIsNotTaken() {
        // Act
        this.userService.checkIfUsernameTaken(FREE_USERNAME);
    }

    /**
     * Tests checkIfUsernameTaken from {@link UserService} when username is taken.
     * Should throw {@link BadRequestException}.
     */
    @Test(expected = BadRequestException.class)
    public void checkIfUsernameTaken_shouldThrowBadRequestException_whenUsernameIsTaken() {
        // Act
        this.userService.checkIfUsernameTaken(TAKEN_USERNAME);
    }


    /**
     * Tests checkIfEmailTaken from {@link UserService} when email is not taken.
     * Shouldn't throw any exception.
     */
    @Test
    public void checkIfEmailTaken_shouldNotThrowAnyException_whenEmailIsNotTaken() {
        // Act
        this.userService.checkIfEmailTaken(FREE_EMAIL);
    }

    /**
     * Tests checkIfEmailTaken from {@link UserService} when email is taken.
     * Should throw {@link BadRequestException}.
     */
    @Test(expected = BadRequestException.class)
    public void checkIfEmailTaken_shouldThrowBadRequestException_whenEmailIsTaken() {
        // Act
        this.userService.checkIfEmailTaken(TAKEN_EMAIL);
    }
}
