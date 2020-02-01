package com.siit.ticketist.integration.controller;

import com.siit.ticketist.dto.TicketDTO;
import com.siit.ticketist.model.TicketStatus;
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
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment =  SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql("/tickets.sql")
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
        final UserDetails userDetails = userDetailsService.loadUserByUsername("kaca");
        final String token = tokenUtils.generateToken(userDetails);
        headers = new HttpHeaders();
        headers.set("Authorization", token);
    }

    @Test
    public void findAllByEventId_successfullyGetTickets() {
        HttpEntity request = new HttpEntity(headers);
        ResponseEntity<TicketDTO[]> response = testRestTemplate.exchange("/api/tickets/event/1", HttpMethod.GET, request, TicketDTO[].class);
        assertEquals("Expected status OK", HttpStatus.OK, response.getStatusCode());
        assertSame("body contains 8 tickets", 8, response.getBody().length);
    }

    @Test
    public void findAllByEventId_getEmptyArrayForUnknownEvent() {
        HttpEntity request = new HttpEntity(headers);
        ResponseEntity<TicketDTO[]> response = testRestTemplate.exchange("/api/tickets/event/111", HttpMethod.GET, request, TicketDTO[].class);
        assertEquals("Expected status OK", HttpStatus.OK, response.getStatusCode());
        assertSame("body contains 0 tickets", 0, response.getBody().length);
    }

    @Test
    public void findAllByEventSectorId_successfullyGetTickets() {
        HttpEntity request = new HttpEntity(headers);
        ResponseEntity<TicketDTO[]> response = testRestTemplate.exchange("/api/tickets/event/sector/1", HttpMethod.GET, request, TicketDTO[].class);
        assertEquals("Expected status OK", HttpStatus.OK, response.getStatusCode());
        assertSame("body contains 4 tickets", 4, response.getBody().length);
    }

    @Test
    public void findAllByEventSectorId_getEmptyArrayForUnknownSector() {
        HttpEntity request = new HttpEntity(headers);
        ResponseEntity<TicketDTO[]> response = testRestTemplate.exchange("/api/tickets/event/sector/111", HttpMethod.GET, request, TicketDTO[].class);
        assertEquals("Expected status OK", HttpStatus.OK, response.getStatusCode());
        assertSame("body contains 0 tickets", 0, response.getBody().length);
    }

    @Test
    public void buyTickets_throwsBadRequest_whenTicketArrayIsEmpty() {
        Long[] ticketList = {};
        HttpEntity<Long[]> request = new HttpEntity<>(ticketList, headers);
        ResponseEntity<Object> response = testRestTemplate.exchange("/api/tickets", HttpMethod.POST, request, Object.class);
        assertEquals("Expected status BAD_REQUEST", HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void buyTickets_throwsBadRequest_whenTicketArrayHasDuplicates() {
        Long[] ticketList = {3L, 3L};
        HttpEntity<Long[]> request = new HttpEntity<>(ticketList, headers);
        ResponseEntity<Object> response = testRestTemplate.exchange("/api/tickets", HttpMethod.POST, request, Object.class);
        assertEquals("Expected status BAD_REQUEST", HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void buyTickets_throwsBadRequest_whenTicketArrayHasTicketIdWhichDoesNotExist() {
        Long[] ticketList = {99L};
        HttpEntity<Long[]> request = new HttpEntity<>(ticketList, headers);
        ResponseEntity<Object> response = testRestTemplate.exchange("/api/tickets", HttpMethod.POST, request, Object.class);
        assertEquals("Expected status NOT_FOUND", HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void buyTickets_throwsBadRequest_whenTicketArrayHasTicketWhichExistButIsNotFree() {
        Long[] ticketList = {2L};
        HttpEntity<Long[]> request = new HttpEntity<>(ticketList, headers);
        ResponseEntity<Object> response = testRestTemplate.exchange("/api/tickets", HttpMethod.POST, request, Object.class);
        assertEquals("Expected status BAD_REQUEST", HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void buyTickets_throwsBadRequest_whenTicketArrayHasTicketsRelatedToOtherEvents() {
        Long[] ticketList = {3L, 11L};
        HttpEntity<Long[]> request = new HttpEntity<>(ticketList, headers);
        ResponseEntity<Object> response = testRestTemplate.exchange("/api/tickets", HttpMethod.POST, request, Object.class);
        assertEquals("Expected status BAD_REQUEST", HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void buyTickets_throwsBadRequest_whenTicketArrayRelatesToEventInPast() {
        Long[] ticketList = {11L, 12L};
        HttpEntity<Long[]> request = new HttpEntity<>(ticketList, headers);
        ResponseEntity<Object> response = testRestTemplate.exchange("/api/tickets", HttpMethod.POST, request, Object.class);
        assertEquals("Expected status BAD_REQUEST", HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void buyTickets_successfully() {
        Long[] ticketList = {3L, 4L};
        HttpEntity<Long[]> request = new HttpEntity<>(ticketList, headers);
        ResponseEntity<TicketDTO[]> response = testRestTemplate.exchange("/api/tickets", HttpMethod.POST, request, TicketDTO[].class);
        assertEquals("Expected status OK", HttpStatus.OK, response.getStatusCode());
        assertSame("Array of two tickets", 2, response.getBody().length);
        for(int i=0; i < response.getBody().length; i++) {
            assertSame("Ticket status is PAID", TicketStatus.PAID, response.getBody()[i].getStatus());
            assertSame("User who bought ticket has id 1L", 1L, response.getBody()[i].getUserId());
        }
    }

    @Test
    public void buyTickets_throwsUnauthorized_whenUserIsNotLogged() {
        Long[] ticketList = {3L, 4L};
        HttpHeaders httpHeaders = new HttpHeaders();
        HttpEntity<Long[]> request = new HttpEntity<>(ticketList, httpHeaders);
        ResponseEntity<Object> response = testRestTemplate.exchange("/api/tickets", HttpMethod.POST, request, Object.class);
        assertEquals("Expected status Unauthorized", HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void buyTickets_throwsForbidden_whenUserIsAdmin() {
        Long[] ticketList = {3L, 4L};
        UserDetails userDetails = userDetailsService.loadUserByUsername("filip");
        String token = tokenUtils.generateToken(userDetails);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", token);
        HttpEntity<Long[]> request = new HttpEntity<>(ticketList, httpHeaders);
        ResponseEntity<Object> response = testRestTemplate.exchange("/api/tickets", HttpMethod.POST, request, Object.class);
        assertEquals("Expected status FORBIDDEN", HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    public void reserveTickets_throwsBadRequest_whenTicketArrayIsEmpty() {
        Long[] ticketList = {};
        HttpEntity<Long[]> request = new HttpEntity<>(ticketList, headers);
        ResponseEntity<Object> response = testRestTemplate.exchange("/api/tickets/reservations", HttpMethod.POST, request, Object.class);
        assertEquals("Expected status BAD_REQUEST", HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void reserveTickets_throwsBadRequest_whenTicketArrayHasDuplicates() {
        Long[] ticketList = {3L, 3L};
        HttpEntity<Long[]> request = new HttpEntity<>(ticketList, headers);
        ResponseEntity<Object> response = testRestTemplate.exchange("/api/tickets/reservations", HttpMethod.POST, request, Object.class);
        assertEquals("Expected status BAD_REQUEST", HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void reserveTickets_throwsBadRequest_whenTicketArrayHasTicketIdWhichDoesNotExist() {
        Long[] ticketList = {99L};
        HttpEntity<Long[]> request = new HttpEntity<>(ticketList, headers);
        ResponseEntity<Object> response = testRestTemplate.exchange("/api/tickets/reservations", HttpMethod.POST, request, Object.class);
        assertEquals("Expected status NOT_FOUND", HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void reserveTickets_throwsBadRequest_whenTicketArrayHasTicketWhichExistButIsNotFreeToReserve() {
        Long[] ticketList = {2L};
        HttpEntity<Long[]> request = new HttpEntity<>(ticketList, headers);
        ResponseEntity<Object> response = testRestTemplate.exchange("/api/tickets/reservations", HttpMethod.POST, request, Object.class);
        assertEquals("Expected status BAD_REQUEST", HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void reserveTickets_throwsBadRequest_whenTicketArrayHasTicketsRelatedToOtherEvents() {
        Long[] ticketList = {3L, 11L};
        HttpEntity<Long[]> request = new HttpEntity<>(ticketList, headers);
        ResponseEntity<Object> response = testRestTemplate.exchange("/api/tickets/reservations", HttpMethod.POST, request, Object.class);
        assertEquals("Expected status BAD_REQUEST", HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void reserveTickets_throwsBadRequest_whenDeadlineOfWantedEventPassed() {
        Long[] ticketList = {11L, 12L};
        HttpEntity<Long[]> request = new HttpEntity<>(ticketList, headers);
        ResponseEntity<Object> response = testRestTemplate.exchange("/api/tickets/reservations", HttpMethod.POST, request, Object.class);
        assertEquals("Expected status BAD_REQUEST", HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void reserveTickets_throwsBadRequest_whenUserReserveMoreTicketsThanEventProvidesForReservation() {
        Long[] ticketList = {3L, 4L};
        HttpEntity<Long[]> request = new HttpEntity<>(ticketList, headers);
        ResponseEntity<Object> response = testRestTemplate.exchange("/api/tickets/reservations", HttpMethod.POST, request, Object.class);
        assertEquals("Expected status BAD_REQUEST", HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void reserveTickets_successfully() {
        Long[] ticketList = {3L};
        HttpEntity<Long[]> request = new HttpEntity<>(ticketList, headers);
        ResponseEntity<TicketDTO[]> response = testRestTemplate.exchange("/api/tickets/reservations", HttpMethod.POST, request, TicketDTO[].class);
        assertEquals("Expected status OK", HttpStatus.OK, response.getStatusCode());
        assertSame("Array of two tickets", 1, response.getBody().length);
        assertSame("Ticket status is PAID", TicketStatus.RESERVED, response.getBody()[0].getStatus());
        assertSame("User who bought ticket has id 1L", 1L, response.getBody()[0].getUserId());
    }

    @Test
    public void reserveTickets_throwsUnauthorized_whenUserIsNotLogged() {
        Long[] ticketList = {3L};
        HttpHeaders httpHeaders = new HttpHeaders();
        HttpEntity<Long[]> request = new HttpEntity<>(ticketList, httpHeaders);
        ResponseEntity<Object> response = testRestTemplate.exchange("/api/tickets/reservations", HttpMethod.POST, request, Object.class);
        assertEquals("Expected status Unauthorized", HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void reserveTickets_throwsForbidden_whenUserIsAdmin() {
        Long[] ticketList = {3L};
        UserDetails userDetails = userDetailsService.loadUserByUsername("filip");
        String token = tokenUtils.generateToken(userDetails);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", token);
        HttpEntity<Long[]> request = new HttpEntity<>(ticketList, httpHeaders);
        ResponseEntity<Object> response = testRestTemplate.exchange("/api/tickets/reservations", HttpMethod.POST, request, Object.class);
        assertEquals("Expected status FORBIDDEN", HttpStatus.FORBIDDEN, response.getStatusCode());
    }

}
