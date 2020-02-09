package com.siit.ticketist.integration.controller;

import com.siit.ticketist.dto.ErrorResponse;
import com.siit.ticketist.dto.SuccessResponse;
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
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.hamcrest.Matchers.notNullValue;
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

    // ----------------------------------------------

    @Test
    public void ticketAccept_ShouldThrowBadRequestException_whenReservationIsInvalid(){
        HttpEntity request = new HttpEntity<>(headers);

//        ResponseEntity<Boolean> result = testRestTemplate.exchange("/api/tickets/reservations/accept/100", HttpMethod.POST, request, Boolean.class);
        ResponseEntity<Object> result = testRestTemplate.postForEntity("/api/tickets/reservations/accept/100/",request,Object.class);
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNull(request.getBody());
    }

    @Test
    public void ticketCancel_ShouldThrowBadRequestException_whenReservationIsInvalid(){
        HttpEntity request = new HttpEntity<>(headers);

        ResponseEntity<Object> result = testRestTemplate.exchange("/api/tickets/reservations/cancel/100", HttpMethod.POST, request, Object.class);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNull(request.getBody());
    }

    @Test
    public void ticketAccept_ShouldThrowBadRequestException_whenReservationDoesntBelongToUser(){
        HttpEntity request = new HttpEntity<>(headers);

        ResponseEntity<Object> result = testRestTemplate.exchange("/api/tickets/reservations/accept/3", HttpMethod.POST, request, Object.class);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNull(request.getBody());

    }


    @Test
    public void ticketCancel_ShouldThrowBadRequestException_whenReservationDoesntBelongToUser(){
        HttpEntity request = new HttpEntity<>(headers);

        ResponseEntity<Object> result = testRestTemplate.exchange("/api/tickets/reservations/cancel/3", HttpMethod.POST, request, Object.class);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNull(request.getBody());
    }


    @Test
    public void ticketAccept_ShouldPass_whenItsValid(){
        HttpEntity request = new HttpEntity<>(null, headers);

        ResponseEntity<Boolean> result = testRestTemplate.exchange("/api/tickets/reservations/accept/1", HttpMethod.POST, request, Boolean.class);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(result.getBody().booleanValue());

    }


    @Test
    public void ticketCancel_ShouldPass_whenItsValid(){

        HttpEntity<Long> request = new HttpEntity<>(1l, headers);

        ResponseEntity<Boolean> result = testRestTemplate.exchange("/api/tickets/reservations/accept/1", HttpMethod.POST, request, Boolean.class);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(result.getBody().booleanValue());
    }


    @Test
    public void findUsersTickets_ShouldReturnTickets_whenUserIsValid(){

        HttpEntity<Long> request = new HttpEntity<>(1l, headers);

        ResponseEntity<List<TicketDTO>> result = testRestTemplate.exchange("/api/tickets", HttpMethod.GET, request, new ParameterizedTypeReference<List<TicketDTO>>() {

        });

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(3, result.getBody().size());
    }



    private static final String SCAN_TICKET_URL = "/api/tickets/scan?ticketId=";

    private static final Long PAID_TICKET_ID = 10L;
    private static final Long RESERVED_TICKET_ID = 1L;
    private static final Long NON_EXISTING_TICKET_ID = 666L;

    private static final String TICKET_SCANNED_SUCCESSFULLY_MESSAGE = "Ticket scanned successfully.";
    private static final String UNABLE_TO_SCAN_TICKET_MESSAGE = "Unable to scan ticket with requested ID.";
    private static final String NO_TICKET_FOUND_MESSAGE = "No ticket found with specified ID.";

    @Test
    public void scanTicket_shouldSuccessfullyScanAndInvalidateTicket_whenTicketWithGivenIdExists() {
        // Arrange
        HttpHeaders customHeaders = new HttpHeaders();
        HttpEntity<Object> request = new HttpEntity<>(customHeaders);

        // Act
        ResponseEntity<SuccessResponse> response = this.testRestTemplate
                .exchange(SCAN_TICKET_URL.concat(PAID_TICKET_ID.toString()), HttpMethod.GET, request, SuccessResponse.class);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody(), notNullValue());
        assertEquals(TICKET_SCANNED_SUCCESSFULLY_MESSAGE, response.getBody().getMessage());
    }

    @Test
    public void scanTicket_shouldThrowBadRequestException_whenTicketWithGivenIdIsNotPaid() {
        // Arrange
        HttpHeaders customHeaders = new HttpHeaders();
        HttpEntity<Object> request = new HttpEntity<>(customHeaders);

        // Act
        ResponseEntity<ErrorResponse> response = this.testRestTemplate
                .exchange(SCAN_TICKET_URL.concat(RESERVED_TICKET_ID.toString()), HttpMethod.GET, request, ErrorResponse.class);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertThat(response.getBody(), notNullValue());
        assertEquals(UNABLE_TO_SCAN_TICKET_MESSAGE, response.getBody().getMessage());
    }

    @Test
    public void scanTicket_shouldThrowBadRequestException_whenTicketWithGivenIdDoesNotExist() {
        // Arrange
        HttpHeaders customHeaders = new HttpHeaders();
        HttpEntity<Object> request = new HttpEntity<>(customHeaders);

        // Act
        ResponseEntity<ErrorResponse> response = this.testRestTemplate
                .exchange(SCAN_TICKET_URL.concat(NON_EXISTING_TICKET_ID.toString()), HttpMethod.GET, request, ErrorResponse.class);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertThat(response.getBody(), notNullValue());
        assertEquals(NO_TICKET_FOUND_MESSAGE, response.getBody().getMessage());
    }

    private static final Long VALID_RESERVATION_ID = 1L;
    private static final Long INVALID_RESERVATION_ID = 666L;

    private static final String FIND_RESERVATION_TICKETS_URL = "/api/tickets/reservations/";

    @Test
    public void findReservationTickets_shouldReturnListOfTicketsForGivenReservationId_whenReservationWithGivenIdExists() {
        // Arrange
        HttpEntity<Object> request = new HttpEntity<>(this.headers);

        // Act
        ResponseEntity<List> response = this.testRestTemplate
                .exchange(FIND_RESERVATION_TICKETS_URL.concat(VALID_RESERVATION_ID.toString()), HttpMethod.GET, request, List.class);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody(), notNullValue());
        assertEquals(2, response.getBody().size());
    }

    @Test
    public void findReservationTickets_shouldThrowNotFoundException_whenReservationWithGivenIdDoesNotExist() {
        // Arrange
        HttpEntity<Object> request = new HttpEntity<>(this.headers);

        // Act
        ResponseEntity<ErrorResponse> response = this.testRestTemplate
                .exchange(FIND_RESERVATION_TICKETS_URL.concat(INVALID_RESERVATION_ID.toString()), HttpMethod.GET, request, ErrorResponse.class);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertThat(response.getBody(), notNullValue());
        assertEquals("Reservation not found", response.getBody().getMessage());
    }
}
