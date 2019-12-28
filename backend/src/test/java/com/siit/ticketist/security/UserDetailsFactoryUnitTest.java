package com.siit.ticketist.security;

import com.siit.ticketist.model.Role;
import com.siit.ticketist.model.User;
import org.junit.Test;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.*;

/**
 * This class contains unit test methods for {@link UserDetailsFactory}.
 */
public class UserDetailsFactoryUnitTest {

    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String FIRST_NAME = "First name";
    public static final String LAST_NAME = "Last name";
    public static final String EMAIL = "test@ticketist.com";

    /**
     * Tests the package private constructor behavior from {@link UserDetailsFactory}.
     */
    @Test(expected = IllegalStateException.class)
    public void shouldNotBeAbleToInstantiateUserDetailsFactory() {
        final UserDetailsFactory userDetailsFactory = new UserDetailsFactory();
    }

    /**
     * Tests create method from {@link UserDetailsFactory} when user has authorities.
     */
    @Test
    public void create_shouldReturnSpringSecurityUserWithAuthorities_whenUserHasAuthorities() {
        // Arrange
        final User userWithAuthorities = new User(USERNAME, PASSWORD, EMAIL, FIRST_NAME, LAST_NAME);
        userWithAuthorities.getAuthorities().add(Role.REGISTERED_USER);

        // Act
        final SpringSecurityUser springSecurityUser = UserDetailsFactory.create(userWithAuthorities);

        // Assert
        assertNotNull("SpringSecurityUser instance created", springSecurityUser);
        assertEquals("Username correct", USERNAME, springSecurityUser.getUsername());
        assertEquals("Password correct", PASSWORD, springSecurityUser.getPassword());
        assertEquals("Email correct", EMAIL, springSecurityUser.getEmail());
        assertNotNull("SpringSecurityUser Authorities not null", springSecurityUser.getAuthorities());
        assertThat("SpringSecurityUser has exactly 1 authority", springSecurityUser.getAuthorities(), hasSize(1));
    }

    /**
     * Tests create method from {@link UserDetailsFactory} when user does not have any authorities.
     */
    @Test
    public void create_shouldReturnSpringSecurityUserWithEmptyAuthorities_whenUserDoesNotHaveAnyAuthorities() {
        // Arrange
        final User userWithEmptyAuthorities = new User(USERNAME, PASSWORD, EMAIL, FIRST_NAME, LAST_NAME);

        // Act
        final SpringSecurityUser springSecurityUser = UserDetailsFactory.create(userWithEmptyAuthorities);

        // Assert
        assertNotNull("SpringSecurityUser instance created", springSecurityUser);
        assertEquals("Username correct", USERNAME, springSecurityUser.getUsername());
        assertEquals("Password correct", PASSWORD, springSecurityUser.getPassword());
        assertEquals("Email correct", EMAIL, springSecurityUser.getEmail());
        assertNotNull("SpringSecurityUser Authorities not null", springSecurityUser.getAuthorities());
        assertThat("SpringSecurityUser has 0 authorities", springSecurityUser.getAuthorities(), empty());
    }

    /**
     * Tests create method from {@link UserDetailsFactory} when user authorities are invalid.
     */
    @Test
    public void create_shouldReturnSpringSecurityUserWithAuthoritiesSetToNull_whenUserAuthoritiesAreInvalid() {
        // Arrange
        final User userWithInvalidAuthorities = new User(USERNAME, PASSWORD, EMAIL, FIRST_NAME, LAST_NAME);
        userWithInvalidAuthorities.setAuthorities(null);

        // Act
        final SpringSecurityUser springSecurityUser = UserDetailsFactory.create(userWithInvalidAuthorities);

        // Assert
        assertNotNull("SpringSecurityUser instance created", springSecurityUser);
        assertEquals("Username correct", USERNAME, springSecurityUser.getUsername());
        assertEquals("Password correct", PASSWORD, springSecurityUser.getPassword());
        assertEquals("Email correct", EMAIL, springSecurityUser.getEmail());
        assertNull("SpringSecurityUser Authorities is null", springSecurityUser.getAuthorities());
    }
}
