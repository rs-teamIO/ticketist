package com.siit.ticketist.security;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * This class contains unit test methods for {@link SpringSecurityUser}.
 */
public class SpringSecurityUserUnitTest {

    private static final Long ID = 1L;
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String EMAIL = "test@ticketist.com";

    /**
     * Tests empty constructor from {@link SpringSecurityUser}.
     */
    @Test
    public void emptyConstructor_shouldReturnSpringSecurityUser() {

        // Act
        final SpringSecurityUser springSecurityUser = new SpringSecurityUser();

        // Assert
        assertNotNull("SpringSecurityUser instance created", springSecurityUser);
    }

    /**
     * Tests constructor with parameters from {@link SpringSecurityUser}.
     */
    @Test
    public void constructorWithParameters_shouldReturnSpringSecurityUser() {

        // Act
        final SpringSecurityUser springSecurityUser = new SpringSecurityUser(ID, USERNAME, PASSWORD, EMAIL, null);

        // Assert
        assertNotNull("SpringSecurityUser instance created", springSecurityUser);
        assertEquals("ID correct", ID, springSecurityUser.getId());
        assertEquals("Username correct", USERNAME, springSecurityUser.getUsername());
        assertEquals("Password correct", PASSWORD, springSecurityUser.getPassword());
        assertEquals("Email correct", EMAIL, springSecurityUser.getEmail());
        assertNull("SpringSecurityUser authorities is null", springSecurityUser.getAuthorities());
    }

    /**
     * Tests isAccountNonExpired from {@link SpringSecurityUser}.
     */
    @Test
    public void isAccountNonExpired_shouldReturnTrue() {
        // Arrange
        final SpringSecurityUser springSecurityUser = new SpringSecurityUser(ID, USERNAME, PASSWORD, EMAIL, null);
        // Act
        boolean value = springSecurityUser.isAccountNonExpired();
        // Assert
        assertTrue("Account has not expired", value);
    }

    /**
     * Tests isAccountNonLocked from {@link SpringSecurityUser}.
     */
    @Test
    public void isAccountNonLocked_shouldReturnTrue() {
        // Arrange
        final SpringSecurityUser springSecurityUser = new SpringSecurityUser(ID, USERNAME, PASSWORD, EMAIL, null);
        // Act
        boolean value = springSecurityUser.isAccountNonLocked();
        // Assert
        assertTrue("Account is not locked",value);
    }

    /**
     * Tests isCredentialsNonExpired from {@link SpringSecurityUser}.
     */
    @Test
    public void isCredentialsNonExpired_shouldReturnTrue() {
        // Arrange
        final SpringSecurityUser springSecurityUser = new SpringSecurityUser(ID, USERNAME, PASSWORD, EMAIL, null);
        // Act
        boolean value = springSecurityUser.isCredentialsNonExpired();
        // Assert
        assertTrue("Account credentials have not expired", value);
    }

    /**
     * Tests isEnabled from {@link SpringSecurityUser}.
     */
    @Test
    public void isEnabled_shouldReturnTrue() {
        // Arrange
        final SpringSecurityUser springSecurityUser = new SpringSecurityUser(ID, USERNAME, PASSWORD, EMAIL, null);
        // Act
        boolean value = springSecurityUser.isEnabled();
        // Assert
        assertTrue("Account is enabled", value);
    }
}