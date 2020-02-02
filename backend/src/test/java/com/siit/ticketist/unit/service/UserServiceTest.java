package com.siit.ticketist.unit.service;

import com.siit.ticketist.exceptions.AuthorizationException;
import com.siit.ticketist.exceptions.BadRequestException;
import com.siit.ticketist.exceptions.ForbiddenException;
import com.siit.ticketist.model.Admin;
import com.siit.ticketist.model.RegisteredUser;
import com.siit.ticketist.model.Role;
import com.siit.ticketist.model.User;
import com.siit.ticketist.repository.UserRepository;
import com.siit.ticketist.security.SpringSecurityUser;
import com.siit.ticketist.service.UserService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * This class contains unit test methods for {@link UserService}.
 */
public class UserServiceTest {

    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String FIRST_NAME = "First name";
    public static final String LAST_NAME = "Last name";
    public static final String EMAIL = "test@ticketist.com";

    public static final String FORBIDDEN_EXCEPTION_MESSAGE = "You don't have permission to access this method on the server.";

    @Mock
    private UserRepository userRepositoryMock;

    @InjectMocks
    private UserService userService;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    /**
     * Initializes mocks
     */
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    /**
     * Tests findByUsername from {@link UserService} when user exists.
     * Should return {@link User} instance.
     */
    @Test
    public void findByUsername_shouldReturnUser_whenUserExists() {

        // Arrange
        final User existingUser = new User(USERNAME, PASSWORD, EMAIL, FIRST_NAME, LAST_NAME);
        when(this.userRepositoryMock.findByUsernameIgnoreCase(USERNAME))
                .thenReturn(Optional.of(existingUser));

        // Act
        final User user = this.userService.findByUsername(USERNAME);

        // Assert
        verify(this.userRepositoryMock).findByUsernameIgnoreCase(USERNAME);
        assertNotNull("User loaded", user);
        assertEquals("Username correct", USERNAME, user.getUsername());
        assertThat(user.getAuthorities(), empty());
    }

    /**
     * Tests findByUsername from {@link UserService} when user doesn't exist.
     * Should throw {@link AuthorizationException}.
     */
    @Test(expected = AuthorizationException.class)
    public void findByUsername_shouldThrowAuthorizationException_whenUserDoesNotExist() {

        // Arrange
        when(this.userRepositoryMock.findByUsernameIgnoreCase(USERNAME))
                .thenReturn(Optional.empty());

        // Act
        this.userService.findByUsername(USERNAME);

        // Assert
        verify(this.userRepositoryMock).findByUsernameIgnoreCase(USERNAME);
    }

    /**
     * Tests findAdminByUsername from {@link UserService} when admin exists.
     * Should return {@link Admin} instance.
     */
    @Test
    public void findAdminByUsername_shouldReturnAdmin_whenAdminExists() {

        // Arrange
        final Admin existingAdmin = new Admin(USERNAME, PASSWORD, EMAIL, FIRST_NAME, LAST_NAME);
        when(this.userRepositoryMock.findByUsernameIgnoreCase(USERNAME))
                .thenReturn(Optional.of(existingAdmin));

        // Act
        final Admin admin = this.userService.findAdminByUsername(USERNAME);

        // Assert
        verify(this.userRepositoryMock).findByUsernameIgnoreCase(USERNAME);
        assertNotNull("Admin loaded", admin);
        assertEquals("Username correct", USERNAME, admin.getUsername());
        assertThat(admin.getAuthorities(), hasSize(1));
        assertThat(admin.getAuthorities(), hasItem(Role.ADMIN));
    }

    /**
     * Tests findAdminByUsername from {@link UserService} when admin doesn't exist.
     * Should throw {@link AuthorizationException}.
     */
    @Test(expected = AuthorizationException.class)
    public void findAdminByUsername_shouldThrowAuthorizationException_whenAdminDoesNotExist() {

        // Arrange
        when(this.userRepositoryMock.findByUsernameIgnoreCase(USERNAME))
                .thenReturn(Optional.empty());

        // Act
        this.userService.findAdminByUsername(USERNAME);

        // Assert
        verify(this.userRepositoryMock).findByUsernameIgnoreCase(USERNAME);
    }

    /**
     * Tests findAdminByUsername from {@link UserService} when an user with given
     * username exists but the user is not an admin but a registered user.
     * Should throw {@link ClassCastException}.
     */
    @Test(expected = ClassCastException.class)
    public void findAdminByUsername_shouldThrowClassCastException_whenUserThatIsNotAdminExists() {

        // Arrange
        final RegisteredUser existingRegisteredUser = new RegisteredUser(USERNAME, PASSWORD, EMAIL, FIRST_NAME, LAST_NAME);
        when(this.userRepositoryMock.findByUsernameIgnoreCase(USERNAME))
                .thenReturn(Optional.of(existingRegisteredUser));

        // Act
        final Admin admin = this.userService.findAdminByUsername(USERNAME);

        // Assert
        assertNull(admin);
        verify(this.userRepositoryMock).findByUsernameIgnoreCase(USERNAME);
    }

    /**
     * Tests findRegisteredUserByUsername from {@link UserService} when registered user exists.
     * Should return {@link RegisteredUser} instance.
     */
    @Test
    public void findRegisteredUserByUsername_shouldReturnRegisteredUser_whenRegisteredUserExists() {

        // Arrange
        final RegisteredUser existingRegisteredUser = new RegisteredUser(USERNAME, PASSWORD, EMAIL, FIRST_NAME, LAST_NAME);
        when(this.userRepositoryMock.findByUsernameIgnoreCase(USERNAME))
                .thenReturn(Optional.of(existingRegisteredUser));

        // Act
        final RegisteredUser registeredUser = this.userService.findRegisteredUserByUsername(USERNAME);

        // Assert
        verify(this.userRepositoryMock).findByUsernameIgnoreCase(USERNAME);
        assertNotNull("Registered user loaded", registeredUser);
        assertEquals("Username correct", USERNAME, registeredUser.getUsername());
        assertThat(registeredUser.getAuthorities(), empty());
    }

    /**
     * Tests findRegisteredUserByUsername from {@link UserService} when registered user doesn't exist.
     * Should throw {@link AuthorizationException}.
     */
    @Test(expected = AuthorizationException.class)
    public void findRegisteredUserByUsername_shouldThrowAuthorizationException_whenRegisteredUserDoesNotExist() {

        // Arrange
        when(this.userRepositoryMock.findByUsernameIgnoreCase(USERNAME))
                .thenReturn(Optional.empty());

        // Act
        this.userService.findRegisteredUserByUsername(USERNAME);

        // Assert
        verify(this.userRepositoryMock).findByUsernameIgnoreCase(USERNAME);
    }

    /**
     * Tests findRegisteredUserByUsername from {@link UserService} when an user with given
     * username exists but the user is not an registered user but an admin.
     * Should throw {@link ClassCastException}.
     */
    @Test(expected = ClassCastException.class)
    public void findRegisteredUserByUsername_shouldThrowClassCastException_whenUserThatIsNotRegisteredUserExists() {

        // Arrange
        final Admin existingAdmin = new Admin(USERNAME, PASSWORD, EMAIL, FIRST_NAME, LAST_NAME);
        when(this.userRepositoryMock.findByUsernameIgnoreCase(USERNAME))
                .thenReturn(Optional.of(existingAdmin));

        // Act
        final RegisteredUser registeredUser = this.userService.findRegisteredUserByUsername(USERNAME);

        // Assert
        assertNull(registeredUser);
        verify(this.userRepositoryMock).findByUsernameIgnoreCase(USERNAME);
    }

    /**
     * Tests findCurrentUser from {@link UserService} when the SecurityContextHolder contains a valid AuthenticationToken
     * and the authenticated user exists.
     * Should return {@link User} instance
     */
    @Test
    public void findCurrentUser_shouldReturnCurrentUser_whenSecurityContextHolderContainsAuthenticationToken_andUserExists() {
        // Arrange
        final UserDetails userDetails = new SpringSecurityUser(null, USERNAME, PASSWORD, EMAIL, null);
        final UsernamePasswordAuthenticationToken authentication = mock(UsernamePasswordAuthenticationToken.class);
        when(authentication.getPrincipal())
                .thenReturn(userDetails);
        final SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication())
                .thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        final RegisteredUser existingRegisteredUser = new RegisteredUser(USERNAME, PASSWORD, EMAIL, FIRST_NAME, LAST_NAME);
        when(this.userRepositoryMock.findByUsernameIgnoreCase(USERNAME))
                .thenReturn(Optional.of(existingRegisteredUser));
        // Act
        final User user = this.userService.findCurrentUser();

        // Assert
        assertNotNull("User loaded", user);
        assertEquals("Username correct", USERNAME, user.getUsername());
        verify(this.userRepositoryMock).findByUsernameIgnoreCase(USERNAME);
        verify(authentication).getPrincipal();
        verify(securityContext).getAuthentication();
    }


    /**
     * Tests findCurrentUser from {@link UserService} when the SecurityContextHolder contains an invalid AuthenticationToken.
     * Should throw {@link ForbiddenException}.
     */
    @Test
    public void findCurrentUser_shouldThrowForbiddenException_whenUsernamePasswordAuthenticationTokenIsNull() {
        // Arrange
        exceptionRule.expect(ForbiddenException.class);
        exceptionRule.expectMessage(FORBIDDEN_EXCEPTION_MESSAGE);

        final SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication())
                .thenReturn(null);
        SecurityContextHolder.setContext(securityContext);

        final RegisteredUser existingRegisteredUser = new RegisteredUser(USERNAME, PASSWORD, EMAIL, FIRST_NAME, LAST_NAME);
        when(this.userRepositoryMock.findByUsernameIgnoreCase(USERNAME))
                .thenReturn(Optional.of(existingRegisteredUser));
        // Act
        final User user = this.userService.findCurrentUser();

        // Assert
        assertNull(user);
        verify(securityContext).getAuthentication();
        verify(this.userRepositoryMock, times(0)).findByUsernameIgnoreCase(USERNAME);
    }

    /**
     * Tests findCurrentUser from {@link UserService} when authentication has no principal.
     * and the {@link UserDetails} instance is null.
     * Should throw {@link NullPointerException}
     */
    @Test(expected = NullPointerException.class)
    public void findCurrentUser_shouldThrowNullPointerException_whenAuthenticationHasNoPrincipal_andUserDetailsIsNull() {
        // Arrange
        final UsernamePasswordAuthenticationToken authentication = mock(UsernamePasswordAuthenticationToken.class);
        when(authentication.getPrincipal())
                .thenReturn(null);
        final SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication())
                .thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        final RegisteredUser existingRegisteredUser = new RegisteredUser(USERNAME, PASSWORD, EMAIL, FIRST_NAME, LAST_NAME);
        when(this.userRepositoryMock.findByUsernameIgnoreCase(USERNAME))
                .thenReturn(Optional.of(existingRegisteredUser));
        // Act
        final User user = this.userService.findCurrentUser();

        // Assert
        assertNull(user);
        verify(authentication).getPrincipal();
        verify(securityContext).getAuthentication();
        verify(this.userRepositoryMock, times(0)).findByUsernameIgnoreCase(USERNAME);
    }

    /**
     * Tests findCurrentUser from {@link UserService} when the SecurityContextHolder contains a valid AuthenticationToken
     * but the user with given credentials does not exist.
     * Should throw {@link AuthorizationException}.
     */
    @Test(expected = AuthorizationException.class)
    public void findCurrentUser_shouldThrowAuthorizationException_whenSecurityContextHolderContainsAuthenticationToken_andUserDoesNotExist() {
        // Arrange
        final UserDetails userDetails = new SpringSecurityUser(null, USERNAME, PASSWORD, EMAIL, null);
        final UsernamePasswordAuthenticationToken authentication = mock(UsernamePasswordAuthenticationToken.class);
        when(authentication.getPrincipal())
                .thenReturn(userDetails);
        final SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication())
                .thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(this.userRepositoryMock.findByUsernameIgnoreCase(USERNAME))
                .thenReturn(Optional.empty());
        // Act
        final User user = this.userService.findCurrentUser();

        // Assert
        assertNotNull("User loaded", user);
        assertEquals("Username correct", USERNAME, user.getUsername());
        verify(this.userRepositoryMock).findByUsernameIgnoreCase(USERNAME);
        verify(authentication).getPrincipal();
        verify(securityContext).getAuthentication();
    }

    /**
     * Tests checkIfUsernameTaken from {@link UserService} when username is not taken.
     * Shouldn't throw any exception.
     */
    @Test
    public void checkIfUsernameTaken_shouldNotThrowAnyException_whenUsernameIsNotTaken() {

        // Arrange
        when(this.userRepositoryMock.findByUsernameIgnoreCase(USERNAME))
                .thenReturn(Optional.empty());

        // Act
        this.userService.checkIfUsernameTaken(USERNAME);

        // Assert
        verify(this.userRepositoryMock).findByUsernameIgnoreCase(USERNAME);
    }

    /**
     * Tests checkIfUsernameTaken from {@link UserService} when username is taken.
     * Should throw {@link BadRequestException}.
     */
    @Test(expected = BadRequestException.class)
    public void checkIfUsernameTaken_shouldThrowBadRequestException_whenUsernameIsTaken() {

        // Arrange
        final RegisteredUser existingRegisteredUser = new RegisteredUser(USERNAME, PASSWORD, EMAIL, FIRST_NAME, LAST_NAME);
        when(this.userRepositoryMock.findByUsernameIgnoreCase(USERNAME))
                .thenReturn(Optional.of(existingRegisteredUser));

        // Act
        this.userService.checkIfUsernameTaken(USERNAME);

        // Assert
        verify(this.userRepositoryMock).findByUsernameIgnoreCase(USERNAME);
    }

    /**
     * Tests checkIfEmailTaken from {@link UserService} when email is not taken.
     * Shouldn't throw any exception.
     */
    @Test
    public void checkIfEmailTaken_shouldNotThrowAnyException_whenEmailIsNotTaken() {

        // Arrange
        when(this.userRepositoryMock.findByEmail(EMAIL))
                .thenReturn(Optional.empty());

        // Act
        this.userService.checkIfEmailTaken(EMAIL);

        // Assert
        verify(this.userRepositoryMock).findByEmail(EMAIL);
    }

    /**
     * Tests checkIfEmailTaken from {@link UserService} when email is taken.
     * Should throw {@link BadRequestException}.
     */
    @Test(expected = BadRequestException.class)
    public void checkIfEmailTaken_shouldThrowBadRequestException_whenEmailIsTaken() {

        // Arrange
        final RegisteredUser existingRegisteredUser = new RegisteredUser(USERNAME, PASSWORD, EMAIL, FIRST_NAME, LAST_NAME);
        when(this.userRepositoryMock.findByEmail(EMAIL))
                .thenReturn(Optional.of(existingRegisteredUser));

        // Act
        this.userService.checkIfEmailTaken(EMAIL);

        // Assert
        verify(this.userRepositoryMock).findByEmail(EMAIL);
    }

    @Test
    public void save_shouldAlwaysPersistEntitySuccessfully() {

        // Arrange
        final RegisteredUser existingRegisteredUser = new RegisteredUser(USERNAME, PASSWORD, EMAIL, FIRST_NAME, LAST_NAME);
        when(this.userRepositoryMock.save(existingRegisteredUser))
                .thenReturn(existingRegisteredUser);

        // Act
        this.userService.save(existingRegisteredUser);

        // Assert
        verify(this.userRepositoryMock).save(existingRegisteredUser);
    }
}
