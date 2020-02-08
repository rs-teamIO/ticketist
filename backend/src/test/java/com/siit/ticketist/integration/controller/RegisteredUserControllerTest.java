package com.siit.ticketist.integration.controller;

import com.siit.ticketist.dto.ErrorResponse;
import com.siit.ticketist.dto.RegisteredUserDTO;
import com.siit.ticketist.dto.SuccessResponse;
import com.siit.ticketist.dto.UpdateUserDTO;
import com.siit.ticketist.security.TokenUtils;
import com.siit.ticketist.security.UserDetailsServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Sql("/registered-user.sql")
@Transactional
public class RegisteredUserControllerTest {

    private static final String BASE_URL = "/api/users/";
    private static final String VERIFICATION_URL = BASE_URL + "verify/%s";
    private static final String CURRENT_USER_URL = BASE_URL + "me";

    private static final Long NEXT_USER_ID = 4L;
    private static final String USERNAME = "free_username";
    private static final String PASSWORD = "free_username";
    private static final String FIRST_NAME = "TEST";
    private static final String LAST_NAME = "TEST";
    private static final String EMAIL = "free_email@ticketist.com";
    private static final String PHONE = "06912341234";

    private static final String EXISTING_USERNAME_EXCEPTION_MESSAGE = "Username '%s' is already taken";
    private static final String EXISTING_EMAIL_EXCEPTION_MESSAGE = "User with e-mail '%s' already registered";
    private static final String VERIFICATION_SUCCESSFUL_MESSAGE = "User verified successfully.";

    private static final String EXISTING_USERNAME = "verified_user";
    private static final String EXISTING_EMAIL = "verified_user@ticketist.com";
    private static final String EXISTING_EMAIL_OTHER = "unverified_user@ticketist.com";
    private static final String VERIFICATION_CODE = "15f1b452-a23c-4f2b-9b23-2746f73d5b2b";

    @Autowired
    private TokenUtils tokenUtils;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private TestRestTemplate testRestTemplate;

    private HttpHeaders headers;

    @Before
    public void setUp() {
        final UserDetails userDetails = this.userDetailsService.loadUserByUsername(EXISTING_USERNAME);
        final String token = this.tokenUtils.generateToken(userDetails);

        headers = new HttpHeaders();
        headers.set("Authorization", token);
    }

    @Test
    public void updateUser_ShouldThrowAuthorizationException_whenUserDoesNotExist() {
        UpdateUserDTO update = new UpdateUserDTO();
        update.setUsername("asdf");

        HttpEntity<UpdateUserDTO> request = new HttpEntity<>(update, headers);

        ResponseEntity<ErrorResponse> response = this.testRestTemplate
                .exchange(BASE_URL, HttpMethod.PUT, request, ErrorResponse.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    public void updateUser_ShouldThrowAuthorizationException_whenUsernamesDontMatch() {
        UpdateUserDTO dto = new UpdateUserDTO();
        dto.setUsername("unverified_user");
        dto.setOldPassword("unverified_user");
        dto.setEmail("NON_EXISTING@ticketist.com");
        dto.setFirstName("NewFirstName");
        dto.setLastName("NewLastName");
        dto.setPhone("");

        HttpEntity<UpdateUserDTO> request = new HttpEntity<>(dto, headers);

        ResponseEntity<ErrorResponse> response = this.testRestTemplate
                .exchange(BASE_URL, HttpMethod.PUT, request, ErrorResponse.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Usernames don't match.", response.getBody().getMessage());
    }

    @Test
    public void updateUser_ShouldThrowAuthorizationException_whenPasswordIsIncorrect() {

        UpdateUserDTO dto = new UpdateUserDTO();
        dto.setUsername(EXISTING_USERNAME);
        dto.setOldPassword("123");
        dto.setNewPassword(EXISTING_USERNAME.concat("new"));
        dto.setNewPasswordRepeat(EXISTING_USERNAME);
        dto.setEmail("NON_EXISTING@ticketist.com");
        dto.setFirstName("NewFirstName");
        dto.setLastName("NewLastName");
        dto.setPhone("");

        HttpEntity<UpdateUserDTO> request = new HttpEntity<>(dto, headers);

        ResponseEntity<ErrorResponse> response = this.testRestTemplate
                .exchange(BASE_URL, HttpMethod.PUT, request, ErrorResponse.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Incorrect password.", response.getBody().getMessage());
    }

    @Test
    public void updateUser_ShouldThrowAuthorizationException_whenNewPasswordsDontMatch() {

        UpdateUserDTO dto = new UpdateUserDTO();
        dto.setUsername(EXISTING_USERNAME);
        dto.setOldPassword(EXISTING_USERNAME);
        dto.setNewPassword(EXISTING_USERNAME.concat("new"));
        dto.setNewPasswordRepeat(EXISTING_USERNAME);
        dto.setEmail("NON_EXISTING@ticketist.com");
        dto.setFirstName("NewFirstName");
        dto.setLastName("NewLastName");
        dto.setPhone("");

        HttpEntity<UpdateUserDTO> request = new HttpEntity<>(dto, headers);

        ResponseEntity<ErrorResponse> response = this.testRestTemplate
                .exchange(BASE_URL, HttpMethod.PUT, request, ErrorResponse.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("New passwords don't match.", response.getBody().getMessage());
    }

    @Test
    public void updateUser_ShouldThrowBadRequestException_whenEmailIsTaken() {

        UpdateUserDTO dto = new UpdateUserDTO();
        dto.setUsername(EXISTING_USERNAME);
        dto.setOldPassword(EXISTING_USERNAME);
        dto.setEmail(EXISTING_EMAIL_OTHER);
        dto.setFirstName("NewFirstName");
        dto.setLastName("NewLastName");
        dto.setPhone("");

        HttpEntity<UpdateUserDTO> request = new HttpEntity<>(dto, headers);

        ResponseEntity<ErrorResponse> response = this.testRestTemplate
                .exchange(BASE_URL, HttpMethod.PUT, request, ErrorResponse.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertThat(response.getBody(), notNullValue());
        assertEquals(String.format(EXISTING_EMAIL_EXCEPTION_MESSAGE, EXISTING_EMAIL_OTHER), response.getBody().getMessage());
    }

    @Test
    public void updateUser_shouldPass_whenUserUpdateIsValid() {

        UpdateUserDTO dto = new UpdateUserDTO();
        dto.setUsername(EXISTING_USERNAME);
        dto.setOldPassword(EXISTING_USERNAME);
        dto.setEmail("NON_EXISTING@ticketist.com");
        dto.setFirstName("NewFirstName");
        dto.setLastName("NewLastName");
        dto.setPhone("");

        HttpEntity<UpdateUserDTO> request = new HttpEntity<>(dto, this.headers);

        // Act
        ResponseEntity<RegisteredUserDTO> response = this.testRestTemplate
                .exchange(BASE_URL, HttpMethod.PUT, request, RegisteredUserDTO.class);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dto.getUsername(), response.getBody().getUsername());
        // TODO: Assert other stuff
    }

    @Test
    public void handleCreate_shouldCreateRegisteredUser_whenRequestIsValid_andUsernameAndEmailAreNotTaken() {
        // Arrange
        final RegisteredUserDTO dto = new RegisteredUserDTO(USERNAME, PASSWORD, EMAIL, FIRST_NAME, LAST_NAME, PHONE);

        HttpHeaders customHeaders = new HttpHeaders();
        HttpEntity<RegisteredUserDTO> request = new HttpEntity<>(dto, customHeaders);

        // Act
        ResponseEntity<Object> response = this.testRestTemplate
                .exchange(BASE_URL, HttpMethod.POST, request, Object.class);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertThat(response.getBody(), notNullValue());
        assertEquals(NEXT_USER_ID.toString(), response.getBody().toString());
    }

    @Test
    public void handleCreate_shouldThrowBadRequestException_whenRequestIsValid_andUsernameIsTaken() {
        // Arrange
        final RegisteredUserDTO dto = new RegisteredUserDTO(EXISTING_USERNAME, PASSWORD, EMAIL, FIRST_NAME, LAST_NAME, PHONE);

        HttpHeaders customHeaders = new HttpHeaders();
        HttpEntity<RegisteredUserDTO> request = new HttpEntity<>(dto, customHeaders);

        // Act
        ResponseEntity<ErrorResponse> response = this.testRestTemplate
                .exchange(BASE_URL, HttpMethod.POST, request, ErrorResponse.class);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertThat(response.getBody(), notNullValue());
        assertEquals(String.format(EXISTING_USERNAME_EXCEPTION_MESSAGE, EXISTING_USERNAME), response.getBody().getMessage());
    }

    @Test
    public void handleCreate_shouldThrowBadRequestException_whenRequestIsValid_andUsernameIsFreeButEmailIsTaken() {
        // Arrange
        final RegisteredUserDTO dto = new RegisteredUserDTO(USERNAME, PASSWORD, EXISTING_EMAIL, FIRST_NAME, LAST_NAME, PHONE);

        HttpHeaders customHeaders = new HttpHeaders();
        HttpEntity<RegisteredUserDTO> request = new HttpEntity<>(dto, customHeaders);

        // Act
        ResponseEntity<ErrorResponse> response = this.testRestTemplate
                .exchange(BASE_URL, HttpMethod.POST, request, ErrorResponse.class);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertThat(response.getBody(), notNullValue());
        assertEquals(String.format(EXISTING_EMAIL_EXCEPTION_MESSAGE, EXISTING_EMAIL), response.getBody().getMessage());
    }

    @Test
    public void handleCreate_shouldThrowBadRequestException_whenRequestIsInvalid() {
        // Arrange
        final RegisteredUserDTO dto = new RegisteredUserDTO(null, PASSWORD, EXISTING_EMAIL, FIRST_NAME, LAST_NAME, PHONE);

        HttpHeaders customHeaders = new HttpHeaders();
        HttpEntity<RegisteredUserDTO> request = new HttpEntity<>(dto, customHeaders);

        // Act
        ResponseEntity<ErrorResponse> response = this.testRestTemplate
                .exchange(BASE_URL, HttpMethod.POST, request, ErrorResponse.class);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void handleVerify_shouldVerifyUser_whenRequestIsValid_andUserWithGivenVerificationCodeExists() {
        // Arrange
        String url = String.format(VERIFICATION_URL, VERIFICATION_CODE);

        HttpHeaders customHeaders = new HttpHeaders();
        HttpEntity<Object> request = new HttpEntity<>(customHeaders);

        // Act
        ResponseEntity<SuccessResponse> response = this.testRestTemplate
                .exchange(url, HttpMethod.GET, request, SuccessResponse.class);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody(), notNullValue());
        assertEquals(VERIFICATION_SUCCESSFUL_MESSAGE, response.getBody().getMessage());
    }

    @Test
    public void handleVerify_shouldThrowBadRequestException_whenRequestIsValid_andUserWithGivenVerificationCodeDoesNotExist() {
        // Arrange
        String url = String.format(VERIFICATION_URL, "invalid-verification-code");

        HttpHeaders customHeaders = new HttpHeaders();
        HttpEntity<Object> request = new HttpEntity<>(customHeaders);

        // Act
        ResponseEntity<ErrorResponse> response = this.testRestTemplate
                .exchange(url, HttpMethod.GET, request, ErrorResponse.class);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertThat(response.getBody(), notNullValue());
        assertEquals("No user found with specified verification code.", response.getBody().getMessage());
    }

    @Test
    public void getCurrentUser_shouldReturnCurrentUser_whenRequestContainsAuthToken() {
        // Arrange
        HttpEntity<Object> request = new HttpEntity<>(this.headers);

        // Act
        ResponseEntity<RegisteredUserDTO> response = this.testRestTemplate
                .exchange(CURRENT_USER_URL, HttpMethod.GET, request, RegisteredUserDTO.class);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody(), notNullValue());
        assertEquals(EXISTING_USERNAME, response.getBody().getUsername());
    }

    @Test
    public void getCurrentUser_shouldThrowForbiddenException_whenRequestDoesNotContainAuthToken() {
        // Arrange
        HttpHeaders customHeaders = new HttpHeaders();
        HttpEntity<Object> request = new HttpEntity<>(customHeaders);

        // Act
        ResponseEntity<ErrorResponse> response = this.testRestTemplate
                .exchange(CURRENT_USER_URL, HttpMethod.GET, request, ErrorResponse.class);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertThat(response.getBody(), notNullValue());
    }
}
