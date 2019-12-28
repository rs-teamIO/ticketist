package com.siit.ticketist.security;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import javax.servlet.ServletException;
import java.io.IOException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * This class contains unit test methods for {@link AuthenticationTokenFilter}.
 */
public class AuthenticationTokenFilterUnitTest {

    private static final String VALID_TEST_TOKEN = "VALID_TEST_TOKEN";
    private static final String TEST_TOKEN_WITHOUT_SUBJECT = "TEST_TOKEN_WITHOUT_SUBJECT";
    private static final String TEST_URI = "/api";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final Long ID = 1L;
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String EMAIL = "test@ticketist.com";

    @Mock
    private TokenUtils tokenUtilsMock;

    @Mock
    private UserDetailsService userDetailsServiceMock;

    @InjectMocks
    private AuthenticationTokenFilter authenticationTokenFilter;

    /**
     * Initializes mocks and clears security context
     */
    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        SecurityContextHolder.clearContext();
    }

    /**
     * Tests doFilterInternal from {@link AuthenticationTokenFilter} when token is present and valid.
     */
    @Test
    public void doFilterInternal_positiveScenario_whenTokenIsPresentAndValid() throws IOException, ServletException {

        // Arrange
        final MockHttpServletRequest request = new MockHttpServletRequest(null, TEST_URI);
        request.addHeader(AUTHORIZATION_HEADER, VALID_TEST_TOKEN);
        final MockHttpServletResponse response = new MockHttpServletResponse();
        final MockFilterChain filterChain = new MockFilterChain();
        final UserDetails userDetails = new SpringSecurityUser(ID, USERNAME, PASSWORD, EMAIL, null);
        when(this.tokenUtilsMock.getUsernameFromToken(VALID_TEST_TOKEN)).thenReturn(USERNAME);
        when(this.userDetailsServiceMock.loadUserByUsername(USERNAME)).thenReturn(userDetails);
        when(this.tokenUtilsMock.validateToken(VALID_TEST_TOKEN, userDetails)).thenReturn(true);

        // Act
        this.authenticationTokenFilter.doFilter(request, response, filterChain);

        // Assert
        verify(this.tokenUtilsMock).getUsernameFromToken(VALID_TEST_TOKEN);
        verify(this.userDetailsServiceMock).loadUserByUsername(USERNAME);
        verify(this.tokenUtilsMock).validateToken(VALID_TEST_TOKEN, userDetails);
        assertNotNull("Authentication request token is present", SecurityContextHolder.getContext().getAuthentication());
        assertEquals("HTTP status is OK", HttpStatus.OK.value(), response.getStatus());
    }

    /**
     * Tests doFilterInternal from {@link AuthenticationTokenFilter} when token is not present in request header.
     */
    @Test
    public void doFilterInternal_negativeScenario_whenTokenIsNotPresentInHeader() throws IOException, ServletException {

        // Arrange
        final MockHttpServletRequest request = new MockHttpServletRequest(null, TEST_URI);
        final MockHttpServletResponse response = new MockHttpServletResponse();
        final MockFilterChain filterChain = new MockFilterChain();
        when(this.tokenUtilsMock.getUsernameFromToken(null)).thenReturn(null);

        // Act
        this.authenticationTokenFilter.doFilter(request, response, filterChain);

        // Assert
        verify(this.tokenUtilsMock).getUsernameFromToken(null);
        verifyNoMoreInteractions(this.tokenUtilsMock);
        verifyNoInteractions(this.userDetailsServiceMock);
        assertNull("Authentication request token is not present", SecurityContextHolder.getContext().getAuthentication());
        assertEquals("HTTP status is OK", HttpStatus.OK.value(), response.getStatus());
    }

    /**
     * Tests doFilterInternal from {@link AuthenticationTokenFilter} when token is present but does not contain subject.
     */
    @Test
    public void doFilterInternal_negativeScenario_whenTokenIsPresentButDoesNotContainSubject() throws IOException, ServletException {

        // Arrange
        final MockHttpServletRequest request = new MockHttpServletRequest(null, TEST_URI);
        request.addHeader(AUTHORIZATION_HEADER, TEST_TOKEN_WITHOUT_SUBJECT);
        final MockHttpServletResponse response = new MockHttpServletResponse();
        final MockFilterChain filterChain = new MockFilterChain();
        when(this.tokenUtilsMock.getUsernameFromToken(TEST_TOKEN_WITHOUT_SUBJECT)).thenReturn(null);

        // Act
        this.authenticationTokenFilter.doFilter(request, response, filterChain);

        // Assert
        verify(this.tokenUtilsMock).getUsernameFromToken(TEST_TOKEN_WITHOUT_SUBJECT);
        verifyNoMoreInteractions(this.tokenUtilsMock);
        verifyNoInteractions(this.userDetailsServiceMock);
        assertNull("Authentication request token is not present", SecurityContextHolder.getContext().getAuthentication());
        assertEquals("HTTP status is OK", HttpStatus.OK.value(), response.getStatus());
    }

    /**
     * Tests doFilterInternal from {@link AuthenticationTokenFilter} when token is present and valid but security context is already set.
     */
    @Test
    public void doFilterInternal_negativeScenario_whenTokenIsPresentAndValidButSecurityContextIsAlreadySet() throws IOException, ServletException {

        // Arrange
        final MockHttpServletRequest request = new MockHttpServletRequest(null, TEST_URI);
        request.addHeader(AUTHORIZATION_HEADER, VALID_TEST_TOKEN);
        final MockHttpServletResponse response = new MockHttpServletResponse();
        final MockFilterChain filterChain = new MockFilterChain();
        final UserDetails userDetails = new SpringSecurityUser(ID, USERNAME, PASSWORD, EMAIL, null);
        final UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(this.tokenUtilsMock.getUsernameFromToken(VALID_TEST_TOKEN)).thenReturn(USERNAME);

        // Act
        this.authenticationTokenFilter.doFilter(request, response, filterChain);

        // Assert
        verify(this.tokenUtilsMock).getUsernameFromToken(VALID_TEST_TOKEN);
        verifyNoMoreInteractions(this.tokenUtilsMock);
        verifyNoInteractions(this.userDetailsServiceMock);
        assertNotNull("Authentication request token is present", SecurityContextHolder.getContext().getAuthentication());
        assertEquals("HTTP status is OK", HttpStatus.OK.value(), response.getStatus());
    }

    /**
     * Tests doFilterInternal from {@link AuthenticationTokenFilter} when token is present but token validation fails.
     */
    @Test
    public void doFilterInternal_negativeScenario_whenTokenIsPresentButTokenValidationFails() throws IOException, ServletException {

        // Arrange
        final MockHttpServletRequest request = new MockHttpServletRequest(null, TEST_URI);
        request.addHeader(AUTHORIZATION_HEADER, VALID_TEST_TOKEN);
        final MockHttpServletResponse response = new MockHttpServletResponse();
        final MockFilterChain filterChain = new MockFilterChain();
        final UserDetails userDetails = new SpringSecurityUser(ID, USERNAME, PASSWORD, EMAIL, null);
        when(this.tokenUtilsMock.getUsernameFromToken(VALID_TEST_TOKEN)).thenReturn(USERNAME);
        when(this.userDetailsServiceMock.loadUserByUsername(USERNAME)).thenReturn(userDetails);
        when(this.tokenUtilsMock.validateToken(VALID_TEST_TOKEN, userDetails)).thenReturn(false);

        // Act
        this.authenticationTokenFilter.doFilter(request, response, filterChain);

        // Assert
        verify(this.tokenUtilsMock).getUsernameFromToken(VALID_TEST_TOKEN);
        verify(this.userDetailsServiceMock).loadUserByUsername(USERNAME);
        verify(this.tokenUtilsMock).validateToken(VALID_TEST_TOKEN, userDetails);
        assertNull("Authentication request token is not present", SecurityContextHolder.getContext().getAuthentication());
        assertEquals("HTTP status is OK", HttpStatus.OK.value(), response.getStatus());
    }
}
