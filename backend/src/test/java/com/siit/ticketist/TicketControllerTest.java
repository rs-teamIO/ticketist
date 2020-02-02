package com.siit.ticketist;

import com.siit.ticketist.dto.RegisteredUserDTO;
import com.siit.ticketist.dto.TicketDTO;
import com.siit.ticketist.dto.UpdateUserDTO;
import com.siit.ticketist.security.TokenUtils;
import com.siit.ticketist.security.UserDetailsServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Sql("/data.sql")
public class TicketControllerTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    private HttpHeaders headers;

    @Autowired
    private TokenUtils tokenUtils;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Before
    public void setUp() {
        final UserDetails userDetails = userDetailsService.loadUserByUsername("filip");
        final String token = tokenUtils.generateToken(userDetails);

        headers = new HttpHeaders();
        headers.set("Authorization", token);
    }

    @Test
    public void ticketAccept_ShouldThrowBadRequestException_whenReservationIsInvalid(){
        HttpEntity request = new HttpEntity<>(headers);

//        ResponseEntity<Boolean> result = testRestTemplate.exchange("/api/tickets/reservations/accept/100", HttpMethod.POST, request, Boolean.class);
            ResponseEntity<Boolean> result = testRestTemplate.postForEntity("/api/tickets/reservations/accept/100/",request,Boolean.class);
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNull(result.getBody().booleanValue());
    }

    @Test
    public void ticketCancel_ShouldThrowBadRequestException_whenReservationIsInvalid(){
        HttpEntity request = new HttpEntity<>(headers);

        ResponseEntity<Boolean> result = testRestTemplate.exchange("/api/tickets/reservations/cancel/100", HttpMethod.POST, request, Boolean.class);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNull(result.getBody().booleanValue());

    }

    @Test
    public void ticketAccept_ShouldThrowBadRequestException_whenReservationDoesntBelongToUser(){
        HttpEntity request = new HttpEntity<>(headers);

        ResponseEntity<Boolean> result = testRestTemplate.exchange("/api/tickets/reservations/accept/3", HttpMethod.POST, request, Boolean.class);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNull(result.getBody().booleanValue());

    }


    @Test
    public void ticketCancel_ShouldThrowBadRequestException_whenReservationDoesntBelongToUser(){
        HttpEntity<Long> request = new HttpEntity<>(3l, headers);

        ResponseEntity<Boolean> result = testRestTemplate.exchange("/api/tickets/reservations/cancel/3", HttpMethod.POST, request, Boolean.class);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNull(result.getBody().booleanValue());

    }


    @Test
    public void ticketAccept_ShouldPass_whenItsValid(){
        HttpEntity request = new HttpEntity<>(null, headers);

        ResponseEntity<Boolean> result = testRestTemplate.exchange("/api/tickets/reservations/accept/1", HttpMethod.POST, request, Boolean.class);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNull(result.getBody().booleanValue());

    }


    @Test
    public void ticketCancel_ShouldPass_whenItsValid(){

        HttpEntity<Long> request = new HttpEntity<>(1l, headers);

        ResponseEntity<Boolean> result = testRestTemplate.exchange("/api/tickets/reservations/accept/1", HttpMethod.POST, request, Boolean.class);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNull(result.getBody().booleanValue());
    }


    @Test
    public void findUsersTickets_ShouldReturnTickets_whenUserIsValid(){

        HttpEntity<Long> request = new HttpEntity<>(1l, headers);

        ResponseEntity<List<TicketDTO>> result = testRestTemplate.exchange("/api/tickets", HttpMethod.GET, request, new ParameterizedTypeReference<List<TicketDTO>>() {

        });

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(4, result.getBody().size());
    }
}
