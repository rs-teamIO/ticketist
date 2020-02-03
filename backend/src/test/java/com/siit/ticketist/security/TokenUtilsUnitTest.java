package com.siit.ticketist.security;

import com.siit.ticketist.model.Role;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * This class contains unit test methods for {@link TokenUtils}.
 */
public class TokenUtilsUnitTest {

    private static final String VALID_TEST_TOKEN = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJmaXZrb3ZpYyIsImF1ZGllbmNlIjoid2ViIiwiY3JlYXRlZCI6MTkwMDAwMDAwMCwiZXhwIjoxOTAwMTAwMDAwLCJhdXRob3JpdGllcyI6IlJFR0lTVEVSRURfVVNFUiJ9.LG11_InitpGOYICy2CZgirlMFEPp8YoVa5DKTo2NrdLVAspGRYC4m2tVeGLH8j3G2DYdcnbR_f1wxZgEmj-3Qw";
    private static final String VALID_TEST_TOKEN_USERNAME = "fivkovic";
    private static final String TEST_TOKEN_NO_SUBJECT = "eyJhbGciOiJIUzUxMiJ9.eyJhdWRpZW5jZSI6IndlYiIsImNyZWF0ZWQiOjE5MDAwMDAwMDAsImV4cCI6MTkwMDEwMDAwMCwiYXV0aG9yaXRpZXMiOiJSRUdJU1RFUkVEX1VTRVIifQ.YUnjO8pYjsTEmCHp_WgCIp12CrbUTTY1KPaF1xc8Ai7JTk6x93yyPvcRTKWpOU8YDY30k2Am4qoU6Llkw893iA";
    private static final String TEST_TOKEN_EXPIRED = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJmaXZrb3ZpYyIsImF1ZGllbmNlIjoid2ViIiwiY3JlYXRlZCI6MTU3NzQ0NDA4MSwiZXhwIjoxNTc3NDQ1MDgxLCJhdXRob3JpdGllcyI6IlJFR0lTVEVSRURfVVNFUiJ9.EiZfsD7zAxjDuo5IGow-GDdHdJz353Aa042Vt-3_9uhJStpPe8T3pW33Q96JFDDNK1sZA-MYtLc65EElWS3_yg";
    private static final Long ID = 1L;
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String EMAIL = "test@ticketist.com";

    private static final String SECRET = "\"Authorization\"";
    private static final Long EXPIRATION = 86400L;

    @InjectMocks
    private TokenUtils tokenUtils;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(tokenUtils, "secret", SECRET);
        ReflectionTestUtils.setField(tokenUtils, "expiration", EXPIRATION);
    }

    /**
     * Tests generateToken from {@link TokenUtils} when given {@link UserDetails} instance is valid.
     */
    @Test
    public void generateToken_shouldAlwaysReturnToken() {

        // Arrange
        final ArrayList<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(Role.REGISTERED_USER.toString()));
        final UserDetails userDetails = new SpringSecurityUser(ID, USERNAME, PASSWORD, EMAIL, authorities);

        // Act
        final String token = this.tokenUtils.generateToken(userDetails);

        // Assert
        assertNotNull("Token is not null", token);
    }

    /**
     * Tests getUsernameFromToken from {@link TokenUtils} when subject is in token.
     */
    @Test
    public void getUsernameFromToken_shouldReturnUsername_whenSubjectInToken() {
        // Act
        final String username = this.tokenUtils.getUsernameFromToken(VALID_TEST_TOKEN);

        // Assert
        assertNotNull("Username is not null", username);
        assertEquals("Username is correct", VALID_TEST_TOKEN_USERNAME, username);
    }

    /**
     * Tests getUsernameFromToken from {@link TokenUtils} when subject not present in token.
     */
    @Test
    public void getUsernameFromToken_shouldReturnNull_whenSubjectNotInToken() {
        // Act
        final String username = this.tokenUtils.getUsernameFromToken(TEST_TOKEN_NO_SUBJECT);

        // Assert
        assertNull("Username is null", username);
    }

    /**
     * Tests validateToken from {@link TokenUtils} when token credentials match given user details.
     */
    @Test
    public void validateToken_shouldReturnTrue_whenTokenCredentialsMatchUserDetails() {

        // Arrange
        final ArrayList<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(Role.REGISTERED_USER.toString()));
        final UserDetails userDetails = new SpringSecurityUser(ID, VALID_TEST_TOKEN_USERNAME, PASSWORD, EMAIL, authorities);

        // Act
        final boolean value = this.tokenUtils.validateToken(VALID_TEST_TOKEN, userDetails);

        // Assert
        assertTrue("Token is valid", value);
    }

    /**
     * Tests validateToken from {@link TokenUtils} when token credentials don't match given user details.
     */
    @Test
    public void validateToken_shouldReturnFalse_whenTokenCredentialsDoNotMatchUserDetails() {

        // Arrange
        final ArrayList<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(Role.REGISTERED_USER.toString()));
        final UserDetails userDetails = new SpringSecurityUser(ID, USERNAME, PASSWORD, EMAIL, authorities);

        // Act
        final boolean value = this.tokenUtils.validateToken(VALID_TEST_TOKEN, userDetails);

        // Assert
        assertFalse("Token is not valid", value);
    }

    /**
     * Tests validateToken from {@link TokenUtils} when token expiration date has passed.
     */
    @Test
    public void validateToken_shouldReturnFalse_whenTokenExpired() {

        // Arrange
        final ArrayList<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(Role.REGISTERED_USER.toString()));
        final UserDetails userDetails = new SpringSecurityUser(ID, VALID_TEST_TOKEN_USERNAME, PASSWORD, EMAIL, authorities);

        // Act
        final boolean value = this.tokenUtils.validateToken(TEST_TOKEN_EXPIRED, userDetails);

        // Assert
        assertFalse("Token is not valid", value);
    }
}
