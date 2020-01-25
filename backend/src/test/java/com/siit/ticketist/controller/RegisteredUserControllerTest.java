package com.siit.ticketist.controller;

import static org.junit.Assert.*;

import com.siit.ticketist.dto.RegisteredUserDTO;
import com.siit.ticketist.dto.UpdateUserDTO;
import com.siit.ticketist.exceptions.AuthorizationException;
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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Sql("/data.sql")
public class RegisteredUserControllerTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    private HttpHeaders headers;

    @Autowired
    private TokenUtils tokenUtils;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Before
    public void setUp() {
        final UserDetails userDetails = userDetailsService.loadUserByUsername("user2020");
        final String token = tokenUtils.generateToken(userDetails);

        headers = new HttpHeaders();
        headers.set("Authorization", token);
    }

    @Test
    public void updateUser_ShouldThrowAuthorizationException_whenUsersDontMatch(){

        UpdateUserDTO update = new UpdateUserDTO();
        update.setUsername("asdf");

        HttpEntity<UpdateUserDTO> request = new HttpEntity<>(update, headers);

        ResponseEntity<RegisteredUserDTO> result = testRestTemplate.exchange("/api/users", HttpMethod.PUT, request, RegisteredUserDTO.class);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNull(result.getBody().getUsername());
    }

    @Test
    public void updateUser_ShouldThrowAuthorizationException_whenPasswordIsIncorrect(){

        UpdateUserDTO update = new UpdateUserDTO();
        update.setUsername("user2020");
        update.setOldPassword("123");

        HttpEntity<UpdateUserDTO> request = new HttpEntity<>(update, headers);

        ResponseEntity<RegisteredUserDTO> result = testRestTemplate.exchange("/api/users", HttpMethod.PUT, request, RegisteredUserDTO.class);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNull(result.getBody().getUsername());
    }

    @Test
    public void updateUser_ShouldThrowAuthorizationException_whenNewPasswordsDontMatch(){

        UpdateUserDTO update = new UpdateUserDTO();
        update.setUsername("user2020");
        update.setOldPassword("123456");
        update.setNewPassword("123");
        update.setNewPasswordRepeat("1234");

        HttpEntity<UpdateUserDTO> request = new HttpEntity<>(update, headers);

        ResponseEntity<RegisteredUserDTO> result = testRestTemplate.exchange("/api/users", HttpMethod.PUT, request, RegisteredUserDTO.class);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNull(result.getBody().getUsername());
    }

    @Test
    public void updateUser_ShouldThrowBadRequestException_whenEmailIsTaken(){

        UpdateUserDTO update = new UpdateUserDTO();
        update.setUsername("user2020");
        update.setOldPassword("123456");
        update.setEmail("f.ivkovic16+1@gmail.com");

        HttpEntity<UpdateUserDTO> request = new HttpEntity<>(update, headers);

        ResponseEntity<RegisteredUserDTO> result = testRestTemplate.exchange("/api/users", HttpMethod.PUT, request, RegisteredUserDTO.class);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNull(result.getBody().getUsername());
    }

    @Test
    public void updateUser_ShouldPass_whenUserUpdateIsValid(){

        UpdateUserDTO update = new UpdateUserDTO();
        update.setUsername("user2020");
        update.setOldPassword("123456");
        update.setEmail("test@gmail.com");
        update.setFirstName("Name");
        update.setLastName("LastName");
        update.setPhone("");

        HttpEntity<UpdateUserDTO> request = new HttpEntity<>(update, headers);

        ResponseEntity<RegisteredUserDTO> result = testRestTemplate.exchange("/api/users", HttpMethod.PUT, request, RegisteredUserDTO.class);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(update.getUsername(), result.getBody().getUsername());
    }
}
