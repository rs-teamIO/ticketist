package com.siit.ticketist.integration.controller;

import com.siit.ticketist.dto.AuthenticationRequest;
import com.siit.ticketist.dto.AuthenticationResponse;
import com.siit.ticketist.dto.ErrorResponse;
import com.siit.ticketist.security.TokenUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql("/authentication.sql")
@Transactional
public class AuthenticationControllerTest {

    private static final String AUTHENTICATION_URL = "/api/auth";

    private static final String VERIFIED_USER_USERNAME = "verified_user";
    private static final String VERIFIED_USER_PASSWORD = "verified_user";

    private static final String UNVERIFIED_USER_USERNAME = "unverified_user";
    private static final String UNVERIFIED_USER_PASSWORD = "unverified_user";

    @Autowired
    private TokenUtils tokenUtils;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    public void authenticate_shouldReturnToken_whenAuthenticationRequestIsValidAndUserIsVerified() {
        // Arrange
        final AuthenticationRequest authenticationRequest = new AuthenticationRequest(VERIFIED_USER_USERNAME, VERIFIED_USER_PASSWORD);

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<AuthenticationRequest> request = new HttpEntity<>(authenticationRequest, headers);

        // Act
        ResponseEntity<AuthenticationResponse> response = this.testRestTemplate
                .exchange(AUTHENTICATION_URL, HttpMethod.POST, request, AuthenticationResponse.class);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody(), notNullValue());
        assertEquals(VERIFIED_USER_USERNAME, this.tokenUtils.getUsernameFromToken(response.getBody().getToken()));
    }

    @Test
    public void authenticate_shouldThrowAuthorizationException_whenAuthenticationRequestIsValidAndUserIsNotVerified() {
        // Arrange
        final AuthenticationRequest authenticationRequest = new AuthenticationRequest(UNVERIFIED_USER_USERNAME, UNVERIFIED_USER_PASSWORD);

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<AuthenticationRequest> request = new HttpEntity<>(authenticationRequest, headers);

        // Act
        ResponseEntity<ErrorResponse> response = this.testRestTemplate
                .exchange(AUTHENTICATION_URL, HttpMethod.POST, request, ErrorResponse.class);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertThat(response.getBody(), notNullValue());
        assertEquals("User not verified.", response.getBody().getMessage());
    }

    @Test
    public void authenticate_shouldThrowAuthorizationException_whenAuthenticationRequestIsValidAndUserIsNotRegistered() {
        // Arrange
        final AuthenticationRequest authenticationRequest = new AuthenticationRequest(UNVERIFIED_USER_USERNAME, VERIFIED_USER_PASSWORD);

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<AuthenticationRequest> request = new HttpEntity<>(authenticationRequest, headers);

        // Act
        ResponseEntity<ErrorResponse> response = this.testRestTemplate
                .exchange(AUTHENTICATION_URL, HttpMethod.POST, request, ErrorResponse.class);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertThat(response.getBody(), notNullValue());
        assertEquals("User credentials invalid.", response.getBody().getMessage());
    }

    @Test
    public void authenticate_shouldThrowBadRequestException_whenAuthenticationRequestIsInvalid() {
        // Arrange
        final AuthenticationRequest authenticationRequest = new AuthenticationRequest(null, VERIFIED_USER_PASSWORD);

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<AuthenticationRequest> request = new HttpEntity<>(authenticationRequest, headers);

        // Act
        ResponseEntity<Object> response = this.testRestTemplate
                .exchange(AUTHENTICATION_URL, HttpMethod.POST, request, Object.class);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}
