package com.siit.ticketist.security;

import com.siit.ticketist.model.User;
import com.siit.ticketist.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * This class contains unit test methods for {@link UserDetailsServiceImpl}.
 */
public class UserDetailsServiceImplUnitTest {

    public static final String EXISTING_USERNAME = "EXISTING_USERNAME";
    public static final String NON_EXISTING_USERNAME = "NON_EXISTING_USERNAME";
    public static final String PASSWORD = "password";
    public static final String FIRST_NAME = "First name";
    public static final String LAST_NAME = "Last name";
    public static final String EMAIL = "test@ticketist.com";

    @Mock
    private UserRepository userRepositoryMock;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    /**
     * Initializes mocks
     */
    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    /**
     * Tests loadUserByUsername from {@link UserDetailsServiceImpl} when user exists.
     * Should return {@link UserDetails} instance.
     */
    @Test
    public void loadUserByUsername_shouldReturnUserDetails_whenUserExists() {

        // Arrange
        final User existingUser = new User(EXISTING_USERNAME, PASSWORD, EMAIL, FIRST_NAME, LAST_NAME);
        when(this.userRepositoryMock.findByUsernameIgnoreCase(EXISTING_USERNAME))
                .thenReturn(Optional.of(existingUser));

        // Act
        final UserDetails userDetails = this.userDetailsService.loadUserByUsername(EXISTING_USERNAME);

        // Assert
        verify(this.userRepositoryMock).findByUsernameIgnoreCase(EXISTING_USERNAME);
        assertNotNull("User details loaded", userDetails);
        assertEquals("Username correct", EXISTING_USERNAME, userDetails.getUsername());
    }

    /**
     * Tests loadUserByUsername from {@link UserDetailsServiceImpl} when user doesn't exist.
     * Should throw {@link UsernameNotFoundException}.
     */
    @Test(expected = UsernameNotFoundException.class)
    public void loadUserByUsername_shouldThrowUsernameNotFoundException_whenUserDoesNotExist() {

        // Arrange
        when(this.userRepositoryMock.findByUsernameIgnoreCase(NON_EXISTING_USERNAME))
                .thenReturn(Optional.empty());

        // Act
        this.userDetailsService.loadUserByUsername(NON_EXISTING_USERNAME);

        // Assert
        verify(this.userRepositoryMock).findByUsernameIgnoreCase(NON_EXISTING_USERNAME);
    }
}
