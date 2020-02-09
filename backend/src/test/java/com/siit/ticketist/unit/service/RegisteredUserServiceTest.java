package com.siit.ticketist.unit.service;

import com.siit.ticketist.exceptions.AuthorizationException;
import com.siit.ticketist.exceptions.BadRequestException;
import com.siit.ticketist.model.RegisteredUser;
import com.siit.ticketist.model.Role;
import com.siit.ticketist.repository.RegisteredUserRepository;
import com.siit.ticketist.service.EmailService;
import com.siit.ticketist.service.RegisteredUserService;
import com.siit.ticketist.service.UserService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.mail.MessagingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * This class contains unit test methods for {@link RegisteredUserService}.
 */
public class RegisteredUserServiceTest {

    private static final Long ID = 1L;
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String FIRST_NAME = "First name";
    private static final String LAST_NAME = "Last name";
    private static final String EMAIL = "test@ticketist.com";
    private static final String VERIFICATION_CODE = "verification_code";
    private static final String UPDATED_EMAIL = "test_updated@ticketist.com";
    private static final String UPDATED_PASSWORD = "updated_password";

    private static final String USERNAME_TAKEN_EXCEPTION_MESSAGE = "Username '%s' is already taken";
    private static final String EMAIL_TAKEN_EXCEPTION_MESSAGE = "User with e-mail '%s' already registered";
    private static final String VERIFICATION_CODE_EXCEPTION_MESSAGE = "No user found with specified verification code.";

    @Mock
    private UserService userServiceMock;

    @Mock
    private RegisteredUserRepository registeredUserRepositoryMock;

    @Mock
    private EmailService emailServiceMock;

    @InjectMocks
    private RegisteredUserService registeredUserService;

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
     * Tests create method in {@link RegisteredUserService} when {@link RegisteredUser} instance is not null
     * and the username and e-mail are not taken.
     */
    @Test
    public void create_shouldReturnCreatedRegisteredUserAndSendVerificationEmail_whenUsernameAndEmailAreNotTaken() throws MessagingException {
        // Arrange
        final RegisteredUser registeredUser = new RegisteredUser(USERNAME, PASSWORD, EMAIL, FIRST_NAME, LAST_NAME);
        assertFalse(registeredUser.getIsVerified());
        assertNull(registeredUser.getVerificationCode());
        final RegisteredUser registeredUserToReturn = new RegisteredUser(USERNAME, PASSWORD, EMAIL, FIRST_NAME, LAST_NAME);
        registeredUserToReturn.setId(ID);
        registeredUserToReturn.setVerificationCode(VERIFICATION_CODE);

        doAnswer(invocationOnMock -> {
            Object arg0 = invocationOnMock.getArgument(0);
            assertEquals(USERNAME, arg0);
            return null;
        }).when(this.userServiceMock).checkIfUsernameTaken(USERNAME);

        doAnswer(invocationOnMock -> {
            Object arg0 = invocationOnMock.getArgument(0);
            assertEquals(EMAIL, arg0);
            return null;
        }).when(this.userServiceMock).checkIfEmailTaken(EMAIL);

        when(this.userServiceMock.save(registeredUser))
                .thenReturn(registeredUserToReturn);
        // Act
        final RegisteredUser savedRegisteredUser = this.registeredUserService.create(registeredUser);

        // Assert
        verify(this.userServiceMock).checkIfUsernameTaken(USERNAME);
        verify(this.userServiceMock).checkIfEmailTaken(EMAIL);
        verify(this.userServiceMock).save(registeredUser);
        verify(this.emailServiceMock).sendVerificationEmail(registeredUser);
        assertNotNull(savedRegisteredUser);
        assertSame(registeredUserToReturn, savedRegisteredUser);
        assertEquals(ID, savedRegisteredUser.getId());
        assertEquals(USERNAME, savedRegisteredUser.getUsername());
        assertEquals(PASSWORD, savedRegisteredUser.getPassword());
        assertEquals(EMAIL, savedRegisteredUser.getEmail());
        assertEquals(FIRST_NAME, savedRegisteredUser.getFirstName());
        assertEquals(LAST_NAME, savedRegisteredUser.getLastName());
        assertNull(savedRegisteredUser.getPhone());
        assertEquals(VERIFICATION_CODE, savedRegisteredUser.getVerificationCode());
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
        this.exceptionRule.expectMessage(String.format(USERNAME_TAKEN_EXCEPTION_MESSAGE, USERNAME));
        final RegisteredUser registeredUser = new RegisteredUser(USERNAME, PASSWORD, EMAIL, FIRST_NAME, LAST_NAME);
        assertFalse(registeredUser.getIsVerified());
        assertNull(registeredUser.getVerificationCode());

        doAnswer(invocationOnMock -> {
            Object arg0 = invocationOnMock.getArgument(0);
            assertEquals(USERNAME, arg0);
            throw new BadRequestException(String.format(USERNAME_TAKEN_EXCEPTION_MESSAGE, USERNAME));
        }).when(this.userServiceMock).checkIfUsernameTaken(USERNAME);

        // Act
        this.registeredUserService.create(registeredUser);

        // Assert
        verify(this.userServiceMock).checkIfUsernameTaken(USERNAME);
        verify(this.userServiceMock, never()).checkIfEmailTaken(EMAIL);
        verify(this.emailServiceMock, never()).sendVerificationEmail(ArgumentMatchers.any(RegisteredUser.class));
        verify(this.userServiceMock, never()).save(ArgumentMatchers.any(RegisteredUser.class));
    }

    /**
     * Tests create method in {@link RegisteredUserService} when {@link RegisteredUser} contains
     * an username that is free and an e-mail that is already taken.
     */
    @Test
    public void create_shouldThrowBadRequestException_whenUsernameIsNotTakenButEmailIsTaken() throws MessagingException {
        // Arrange
        this.exceptionRule.expect(BadRequestException.class);
        this.exceptionRule.expectMessage(String.format(EMAIL_TAKEN_EXCEPTION_MESSAGE, EMAIL));
        final RegisteredUser registeredUser = new RegisteredUser(USERNAME, PASSWORD, EMAIL, FIRST_NAME, LAST_NAME);
        assertFalse(registeredUser.getIsVerified());
        assertNull(registeredUser.getVerificationCode());

        doAnswer(invocationOnMock -> {
            Object arg0 = invocationOnMock.getArgument(0);
            assertEquals(USERNAME, arg0);
            return null;
        }).when(this.userServiceMock).checkIfUsernameTaken(USERNAME);

        doAnswer(invocationOnMock -> {
            Object arg0 = invocationOnMock.getArgument(0);
            assertEquals(EMAIL, arg0);
            throw new BadRequestException(String.format(EMAIL_TAKEN_EXCEPTION_MESSAGE, EMAIL));
        }).when(this.userServiceMock).checkIfEmailTaken(EMAIL);

        // Act
        this.registeredUserService.create(registeredUser);

        // Assert
        verify(this.userServiceMock).checkIfUsernameTaken(USERNAME);
        verify(this.userServiceMock).checkIfEmailTaken(EMAIL);
        verify(this.emailServiceMock, never()).sendVerificationEmail(ArgumentMatchers.any(RegisteredUser.class));
        verify(this.userServiceMock, never()).save(ArgumentMatchers.any(RegisteredUser.class));
    }

    /**
     * Tests update method in {@link RegisteredUserService} when {@link RegisteredUser} updated e-mail is not taken.
     */
    @Test
    public void update_shouldReturnUpdatedRegisteredUser_whenUpdatedEmailIsNotTaken() {
        // Arrange
        final RegisteredUser registeredUser = new RegisteredUser(USERNAME, PASSWORD, EMAIL, FIRST_NAME, LAST_NAME);
        registeredUser.setId(ID);
        registeredUser.setAuthorities(new ArrayList<>(Collections.singletonList(Role.REGISTERED_USER)));
        registeredUser.setVerificationCode(null);
        registeredUser.setIsVerified(true);
        when(this.userServiceMock.findRegisteredUserByUsername(USERNAME))
                .thenReturn(registeredUser);

        doAnswer(invocationOnMock -> {
            Object arg0 = invocationOnMock.getArgument(0);
            assertEquals(UPDATED_EMAIL, arg0);
            return null;
        }).when(this.userServiceMock).checkIfEmailTaken(UPDATED_EMAIL);

        final RegisteredUser updatedUser = new RegisteredUser(USERNAME, PASSWORD, UPDATED_EMAIL, FIRST_NAME, LAST_NAME);
        updatedUser.setId(ID);
        final RegisteredUser registeredUserToReturn = new RegisteredUser(USERNAME, PASSWORD, UPDATED_EMAIL, FIRST_NAME, LAST_NAME);
        registeredUserToReturn.setId(ID);
        registeredUserToReturn.setAuthorities(new ArrayList<>(Collections.singletonList(Role.REGISTERED_USER)));
        registeredUserToReturn.setVerificationCode(null);
        registeredUserToReturn.setIsVerified(true);
        when(this.userServiceMock.save(registeredUser))
                .thenReturn(registeredUserToReturn);

        // Act
        final RegisteredUser updatedAndPersistedRegisteredUser = this.registeredUserService.update(updatedUser, null);

        // Assert
        verify(this.userServiceMock).checkIfEmailTaken(UPDATED_EMAIL);
        verify(this.userServiceMock).save(registeredUser);
        assertNotNull(updatedAndPersistedRegisteredUser);
        assertSame(registeredUserToReturn, updatedAndPersistedRegisteredUser);
        assertEquals(registeredUser.getId(), updatedAndPersistedRegisteredUser.getId());
        assertEquals(registeredUser.getUsername(), updatedAndPersistedRegisteredUser.getUsername());
        assertEquals(registeredUser.getPassword(), updatedAndPersistedRegisteredUser.getPassword());
        assertEquals(registeredUser.getEmail(), updatedAndPersistedRegisteredUser.getEmail());
        assertEquals(registeredUser.getFirstName(), updatedAndPersistedRegisteredUser.getFirstName());
        assertEquals(registeredUser.getLastName(), updatedAndPersistedRegisteredUser.getLastName());
        assertEquals(registeredUser.getPhone(), updatedAndPersistedRegisteredUser.getPhone());
        assertEquals(registeredUser.getVerificationCode(), updatedAndPersistedRegisteredUser.getVerificationCode());
        assertEquals(registeredUser.getIsVerified(), updatedAndPersistedRegisteredUser.getIsVerified());
        assertEquals(registeredUser.getAuthorities().size(), updatedAndPersistedRegisteredUser.getAuthorities().size());
    }

    /**
     * Tests update method in {@link RegisteredUserService} when {@link RegisteredUser} updated e-mail is not taken.
     */
    @Test
    public void update_shouldThrowBadRequestException_whenUpdatedEmailIsTaken() {
        // Arrange
        this.exceptionRule.expect(BadRequestException.class);
        this.exceptionRule.expectMessage(String.format(EMAIL_TAKEN_EXCEPTION_MESSAGE, UPDATED_EMAIL));
        final RegisteredUser registeredUser = new RegisteredUser(USERNAME, PASSWORD, EMAIL, FIRST_NAME, LAST_NAME);
        registeredUser.setId(ID);
        registeredUser.setAuthorities(new ArrayList<>(Collections.singletonList(Role.REGISTERED_USER)));
        registeredUser.setVerificationCode(null);
        registeredUser.setIsVerified(true);
        when(this.userServiceMock.findRegisteredUserByUsername(USERNAME))
                .thenReturn(registeredUser);

        doAnswer(invocationOnMock -> {
            Object arg0 = invocationOnMock.getArgument(0);
            assertEquals(UPDATED_EMAIL, arg0);
            throw new BadRequestException(String.format(EMAIL_TAKEN_EXCEPTION_MESSAGE, UPDATED_EMAIL));
        }).when(this.userServiceMock).checkIfEmailTaken(UPDATED_EMAIL);

        final RegisteredUser updatedUser = new RegisteredUser(USERNAME, PASSWORD, UPDATED_EMAIL, FIRST_NAME, LAST_NAME);
        updatedUser.setId(ID);

        // Act
        this.registeredUserService.update(updatedUser, null);

        // Assert
        verify(this.userServiceMock).checkIfEmailTaken(UPDATED_EMAIL);
        verify(this.userServiceMock, never()).save(registeredUser);
    }

    /**
     * Tests update method in {@link RegisteredUserService} when {@link RegisteredUser} only password is updated.
     */
    @Test
    public void update_shouldReturnUpdatedRegisteredUser_whenOnlyPasswordIsUpdated() {
        // Arrange
        final RegisteredUser registeredUser = new RegisteredUser(USERNAME, PASSWORD, EMAIL, FIRST_NAME, LAST_NAME);
        registeredUser.setId(ID);
        registeredUser.setAuthorities(new ArrayList<>(Collections.singletonList(Role.REGISTERED_USER)));
        registeredUser.setVerificationCode(null);
        registeredUser.setIsVerified(true);
        when(this.userServiceMock.findRegisteredUserByUsername(USERNAME))
                .thenReturn(registeredUser);

        final RegisteredUser updatedUser = new RegisteredUser(USERNAME, UPDATED_PASSWORD, EMAIL, FIRST_NAME, LAST_NAME);
        updatedUser.setId(ID);
        final RegisteredUser registeredUserToReturn = new RegisteredUser(USERNAME, UPDATED_PASSWORD, EMAIL, FIRST_NAME, LAST_NAME);
        registeredUserToReturn.setId(ID);
        registeredUserToReturn.setAuthorities(new ArrayList<>(Collections.singletonList(Role.REGISTERED_USER)));
        registeredUserToReturn.setVerificationCode(null);
        registeredUserToReturn.setIsVerified(true);
        when(this.userServiceMock.save(registeredUser))
                .thenReturn(registeredUserToReturn);

        // Act
        final RegisteredUser updatedAndPersistedRegisteredUser = this.registeredUserService.update(updatedUser, UPDATED_PASSWORD);

        // Assert
        verify(this.userServiceMock, never()).checkIfEmailTaken(EMAIL);
        verify(this.userServiceMock).save(registeredUser);
        assertNotNull(updatedAndPersistedRegisteredUser);
        assertSame(registeredUserToReturn, updatedAndPersistedRegisteredUser);
        assertEquals(registeredUser.getId(), updatedAndPersistedRegisteredUser.getId());
        assertEquals(registeredUser.getUsername(), updatedAndPersistedRegisteredUser.getUsername());
        assertEquals(registeredUser.getPassword(), updatedAndPersistedRegisteredUser.getPassword());
        assertEquals(registeredUser.getEmail(), updatedAndPersistedRegisteredUser.getEmail());
        assertEquals(registeredUser.getFirstName(), updatedAndPersistedRegisteredUser.getFirstName());
        assertEquals(registeredUser.getLastName(), updatedAndPersistedRegisteredUser.getLastName());
        assertEquals(registeredUser.getPhone(), updatedAndPersistedRegisteredUser.getPhone());
        assertEquals(registeredUser.getVerificationCode(), updatedAndPersistedRegisteredUser.getVerificationCode());
        assertEquals(registeredUser.getIsVerified(), updatedAndPersistedRegisteredUser.getIsVerified());
        assertEquals(registeredUser.getAuthorities().size(), updatedAndPersistedRegisteredUser.getAuthorities().size());
    }

    /**
     * Tests update method in {@link RegisteredUserService} when {@link RegisteredUser} with given username is not found.
     * Should throw {@link AuthorizationException}.
     */
    @Test
    public void update_shouldThrowAuthorizationException_ifRegisteredUserWithGivenUsernameIsNotFound() {
        // Arrange
        exceptionRule.expect(AuthorizationException.class);
        final RegisteredUser registeredUser = new RegisteredUser(USERNAME, PASSWORD, EMAIL, FIRST_NAME, LAST_NAME);
        when(this.userServiceMock.findRegisteredUserByUsername(USERNAME))
                .thenThrow(AuthorizationException.class);

        // Act
        this.registeredUserService.update(registeredUser, null);

        // Assert
        verify(this.userServiceMock, never()).checkIfEmailTaken(EMAIL);
        verify(this.userServiceMock, never()).save(registeredUser);
    }

    /**
     * Tests verify method in {@link RegisteredUserService} when given verification code is assigned to an user.
     */
    @Test
    public void verify_shouldReturnRegisteredUser_whenVerificationCodeIsAssignedToUser() {
        // Arrange
        final RegisteredUser registeredUser = new RegisteredUser(USERNAME, PASSWORD, EMAIL, FIRST_NAME, LAST_NAME);
        registeredUser.setVerificationCode(VERIFICATION_CODE);
        assertFalse(registeredUser.getIsVerified());
        final RegisteredUser registeredUserToReturn = new RegisteredUser(USERNAME, PASSWORD, EMAIL, FIRST_NAME, LAST_NAME);
        registeredUserToReturn.setId(ID);
        registeredUserToReturn.setIsVerified(true);
        registeredUserToReturn.setAuthorities(new ArrayList<>(Collections.singletonList(Role.REGISTERED_USER)));
        registeredUserToReturn.setVerificationCode(null);

        when(this.registeredUserRepositoryMock.findByVerificationCode(VERIFICATION_CODE))
                .thenReturn(Optional.of(registeredUser));

        when(this.userServiceMock.save(registeredUser))
                .thenReturn(registeredUserToReturn);
        // Act
        final RegisteredUser verifiedRegisteredUser = this.registeredUserService.verify(VERIFICATION_CODE);

        // Assert
        verify(this.registeredUserRepositoryMock).findByVerificationCode(VERIFICATION_CODE);
        verify(this.userServiceMock).save(registeredUser);
        assertNotNull(verifiedRegisteredUser);
        assertSame(registeredUserToReturn, verifiedRegisteredUser);
        assertTrue(registeredUser.getIsVerified());
        assertThat(registeredUser.getAuthorities(), hasSize(1));
        assertThat(registeredUser.getAuthorities(), hasItem(Role.REGISTERED_USER));
        assertNull(registeredUser.getVerificationCode());
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
        when(this.registeredUserRepositoryMock.findByVerificationCode(VERIFICATION_CODE))
                .thenReturn(Optional.empty());

        // Act
        this.registeredUserService.verify(VERIFICATION_CODE);

        // Assert
        verify(this.registeredUserRepositoryMock).findByVerificationCode(VERIFICATION_CODE);
        verify(this.userServiceMock, never()).save(ArgumentMatchers.any(RegisteredUser.class));
    }
}
