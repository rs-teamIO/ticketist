package com.siit.ticketist.integration.service;

import com.siit.ticketist.exceptions.BadRequestException;
import com.siit.ticketist.exceptions.NotFoundException;
import com.siit.ticketist.model.Event;
import com.siit.ticketist.model.Reservation;
import com.siit.ticketist.model.Ticket;
import com.siit.ticketist.model.TicketStatus;
import com.siit.ticketist.repository.ReservationRepository;
import com.siit.ticketist.service.TicketService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql("/tickets.sql")
public class TicketServiceTest {

    @Autowired
    private TicketService ticketService;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    private ReservationRepository reservationRepository;

    @Before
    public void before(){
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken("kaca", "123456"));

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    public void findOne_throwsNotFoundException_whenWantedTicketDoesNotExist() {
        exceptionRule.expect(NotFoundException.class);
        exceptionRule.expectMessage("Ticket not found.");
        Long wantedID = 13L;
        ticketService.findOne(wantedID);
    }

    @Test
    @Transactional
    @Rollback
    public void findOne_successfullyGetTicket() {
        Long wantedID = 1L;
        Ticket ticket = ticketService.findOne(wantedID);
        assertEquals("Ticket's id equals to 1", wantedID, ticket.getId());
        assertSame("Ticket has number row equals to 1",1, ticket.getNumberRow());
        assertSame("Ticket has number column equals to 1",1, ticket.getNumberColumn());
        assertEquals("Ticket has price equals to 35.0", new Double("35.0"), new Double(ticket.getPrice().doubleValue()));
        assertSame("Ticket has status equals to RESERVRED", TicketStatus.RESERVED, ticket.getStatus());

        Event event = ticket.getEvent();
        assertSame("Event has id equals to 1", 1L, event.getId());
        assertEquals("Event has name equals to EXIT Festival", "EXIT Festival", event.getName());
        assertEquals("Event has description equals to Cool music", "Cool music!", event.getDescription());
        assertSame("Event has reservation limit equals to 5", 5, event.getReservationLimit());
        assertSame("Event has venue id equals to 1", 1L, event.getVenue().getId());

        assertSame("Ticket's event sector id equals to 1", 1L, ticket.getEventSector().getId());
        assertSame("Ticket's user id equals to 1", 1L, ticket.getUser().getId());
        assertSame("Ticket's reservation id equals to 1", 1L, ticket.getReservation().getId());
    }

    @Test
    public void findAllByEventId_successfullyGetTickets() {
        Long eventID = 1L;
        List<Ticket> tickets = ticketService.findAllByEventId(eventID);
        assertSame("Number of tickets equals to 8", 8, tickets.size());
    }

    @Test
    public void findAllByEventSectorId_successfullyGetTickets() {
        Long eventSectorID = 1L;
        List<Ticket> tickets = ticketService.findAllByEventSectorId(eventSectorID);
        assertSame("Number of tickets equals to 4", 4, tickets.size());
    }

    @Test
    public void buyTickets_throwsBadRequestException_whenTicketArrayIsEmpty() throws MessagingException {
        exceptionRule.expect(BadRequestException.class);
        exceptionRule.expectMessage("Ticket array is empty!");
        List<Long> tickets = new ArrayList<>();
        ticketService.buyTickets(tickets);
    }

    @Test
    public void buyTickets_throwsBadRequestException_whenTicketArrayHasDuplicates() throws MessagingException {
        exceptionRule.expect(BadRequestException.class);
        exceptionRule.expectMessage("Same tickets detected!");
        List<Long> tickets = new ArrayList<>();
        tickets.add(3L);
        tickets.add(3L);
        ticketService.buyTickets(tickets);
    }

    @Test
    public void buyTickets_throwsBadRequestException_whenTicketArrayHasTicketIdWhichDoesNotExist() throws MessagingException {
        exceptionRule.expect(NotFoundException.class);
        exceptionRule.expectMessage("Ticket not found.");
        List<Long> tickets = new ArrayList<>();
        tickets.add(99L);
        ticketService.buyTickets(tickets);
    }

    @Test
    public void buyTickets_throwsBadRequestException_whenTicketArrayHasTicketWhichExistButIsNotFree() throws MessagingException {
        exceptionRule.expect(BadRequestException.class);
        exceptionRule.expectMessage("Tickets are already taken");
        List<Long> tickets = new ArrayList<>();
        tickets.add(2L);
        ticketService.buyTickets(tickets);
    }

    @Test
    public void buyTickets_throwsBadRequestException_whenTicketArrayHasTicketsRelatedToOtherEvents() throws MessagingException {
        exceptionRule.expect(BadRequestException.class);
        exceptionRule.expectMessage("Every ticket should be for same event!");
        List<Long> tickets = new ArrayList<>();
        tickets.add(3L);
        tickets.add(11L);
        ticketService.buyTickets(tickets);
    }

    @Test
    public void buyTickets_throwsBadRequestException_whenTicketArrayRelatesToEventInPast() throws MessagingException {
        exceptionRule.expect(BadRequestException.class);
        exceptionRule.expectMessage("Event already started!");
        List<Long> tickets = new ArrayList<>();
        tickets.add(11L);
        tickets.add(12L);
        ticketService.buyTickets(tickets);
    }

    @Test
    public void buyTickets_successfully() throws MessagingException {
        List<Long> tickets = new ArrayList<>();
        Long firstTicketID = 3L;
        Long secondTicketID = 4L;
        tickets.add(firstTicketID);
        tickets.add(secondTicketID);

        Ticket ticket3 = ticketService.findOne(firstTicketID);
        Ticket ticket4 = ticketService.findOne(secondTicketID);
        assertSame("Ticket id is 3", firstTicketID, ticket3.getId());
        assertSame("Ticket status is FREE", TicketStatus.FREE, ticket3.getStatus());
        assertNull("Ticket is not related to any user", ticket3.getUser());

        assertSame("Ticket id is 4", secondTicketID, ticket4.getId());
        assertSame("Ticket status is FREE", TicketStatus.FREE, ticket4.getStatus());
        assertNull("Ticket is not related to any user", ticket4.getUser());


        ticketService.buyTickets(tickets);

        ticket3 = ticketService.findOne(firstTicketID);
        ticket4 = ticketService.findOne(secondTicketID);

        assertSame("Ticket status is PAID", TicketStatus.PAID, ticket3.getStatus());
        assertNotNull("Ticket is related to user", ticket3.getUser());
        assertSame("Ticket is related to user 1", 1L, ticket3.getUser().getId());

        assertSame("Ticket status is PAID", TicketStatus.PAID, ticket4.getStatus());
        assertNotNull("Ticket is related to user", ticket4.getUser());
        assertSame("Ticket is related to user 1", 1L, ticket4.getUser().getId());
    }

    @Test
    public void buyTickets_throwsBadCredentialsException_whenUserHasInvalidAuthInfo() throws  MessagingException {
        exceptionRule.expect(BadCredentialsException.class);
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken("rocky", "123456"));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        List<Long> tickets = new ArrayList<>();
        tickets.add(3L);
        tickets.add(4L);
        ticketService.buyTickets(tickets);
    }

    @Test
    public void buyTickets_throwsClassCastException_whenUserHasWrongRole() throws  MessagingException {
        exceptionRule.expect(ClassCastException.class);
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken("filip", "123456"));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        List<Long> tickets = new ArrayList<>();
        tickets.add(3L);
        tickets.add(4L);
        ticketService.buyTickets(tickets);
    }

    @Test
    public void reserveTickets_throwsBadRequestException_whenTicketArrayIsEmpty() throws MessagingException {
        exceptionRule.expect(BadRequestException.class);
        exceptionRule.expectMessage("Ticket array is empty!");
        List<Long> tickets = new ArrayList<>();
        ticketService.reserveTickets(tickets);
    }

    @Test
    public void reserveTickets_throwsBadRequestException_whenTicketArrayHasDuplicates() throws MessagingException {
        exceptionRule.expect(BadRequestException.class);
        exceptionRule.expectMessage("Same tickets detected!");
        List<Long> tickets = new ArrayList<>();
        tickets.add(3L);
        tickets.add(3L);
        ticketService.reserveTickets(tickets);
    }

    @Test
    public void reserveTickets_throwsBadRequestException_whenTicketArrayHasTicketIdWhichDoesNotExist() throws MessagingException {
        exceptionRule.expect(NotFoundException.class);
        exceptionRule.expectMessage("Ticket not found.");
        List<Long> tickets = new ArrayList<>();
        tickets.add(99L);
        ticketService.reserveTickets(tickets);
    }

    @Test
    public void reserveTickets_throwsBadRequestException_whenTicketArrayHasTicketWhichExistButIsNotFreeToReserve() throws MessagingException {
        exceptionRule.expect(BadRequestException.class);
        exceptionRule.expectMessage("Tickets are already taken");
        List<Long> tickets = new ArrayList<>();
        tickets.add(2L);
        ticketService.reserveTickets(tickets);
    }

    @Test
    public void reserveTickets_throwsBadRequestException_whenTicketArrayHasTicketsRelatedToOtherEvents() throws MessagingException {
        exceptionRule.expect(BadRequestException.class);
        exceptionRule.expectMessage("Every ticket should be for same event!");
        List<Long> tickets = new ArrayList<>();
        tickets.add(3L);
        tickets.add(11L);
        ticketService.reserveTickets(tickets);
    }

    @Test
    public void reserveTickets_throwsBadRequestException_whenDeadlineOfWantedEventPassed() throws MessagingException {
        exceptionRule.expect(BadRequestException.class);
        exceptionRule.expectMessage("Reservation deadline passed! Now you can only buy a ticket");
        List<Long> tickets = new ArrayList<>();
        tickets.add(11L);
        tickets.add(12L);
        ticketService.reserveTickets(tickets);
    }

    @Test
    public void reserveTickets_throwsBadRequestException_whenUserReserveMoreTicketsThanEventProvidesForReservation() throws MessagingException {
        exceptionRule.expect(BadRequestException.class);
        exceptionRule.expectMessage("Event limit of reservations is 5");
        List<Long> tickets = new ArrayList<>();
        tickets.add(3L);
        tickets.add(4L);
        ticketService.reserveTickets(tickets);
    }

    @Test
    public void reserveTickets_successfully() throws MessagingException {
        List<Long> tickets = new ArrayList<>();
        Long ticketID = 3L;
        tickets.add(ticketID);

        Ticket ticket3 = ticketService.findOne(ticketID);
        assertSame("Ticket id is 3", ticketID, ticket3.getId());
        assertSame("Ticket status is FREE", TicketStatus.FREE, ticket3.getStatus());
        assertNull("Ticket is not related to any user", ticket3.getUser());

        ticketService.reserveTickets(tickets);

        ticket3 = ticketService.findOne(ticketID);

        assertSame("Ticket status is RESERVED", TicketStatus.RESERVED, ticket3.getStatus());
        assertNotNull("Ticket is related to user", ticket3.getUser());
        assertSame("Ticket is related to user 1", 1L, ticket3.getUser().getId());
        assertSame("Ticket reservation id is 4", 4L, ticket3.getReservation().getId());
    }

    @Test
    public void reserveTickets_throwsBadCredentialsException_whenUserHasInvalidAuthInfo() throws  MessagingException {
        exceptionRule.expect(BadCredentialsException.class);
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken("rocky", "123456"));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        List<Long> tickets = new ArrayList<>();
        tickets.add(3L);
        ticketService.reserveTickets(tickets);
    }

    @Test
    public void reserveTickets_throwsClassCastException_whenUserHasWrongRole() throws  MessagingException {
        exceptionRule.expect(ClassCastException.class);
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken("filip", "123456"));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        List<Long> tickets = new ArrayList<>();
        tickets.add(3L);
        ticketService.reserveTickets(tickets);
    }

    //---------------------------------------------
    @Test
    public void checkStatusIsValid_ShouldPass_whenStatusIsValid(){
        ticketService.checkStatusIsValid(TicketStatus.FREE);
        ticketService.checkStatusIsValid(TicketStatus.PAID);
    }

    @Test
    public void checkStatusIsValid_ShouldThrowException_whenStatusIsInvalid(){
        exceptionRule.expect(BadRequestException.class);
        exceptionRule.expectMessage("Status is not valid");
        ticketService.checkStatusIsValid(TicketStatus.USED);
    }

    @Test
    public void acceptOrCancelReservations_ShouldThrowBadRequestException_whenReservationIsInvalid(){
        exceptionRule.expect(BadRequestException.class);
        exceptionRule.expectMessage("Reservation does not exist");
        Reservation reservation = new Reservation();
        reservation.setId(10l);
        ticketService.acceptOrCancelReservations(reservation.getId(),TicketStatus.PAID);
    }

    @Test
    public void acceptOrCancelReservations_ShouldThrowBadRequestException_whenItDoesntBelongToThatUser(){
        exceptionRule.expect(BadRequestException.class);
        exceptionRule.expectMessage("Reservation does not belong to that user!");
        Reservation reservation = new Reservation();
        reservation.setId(3l);

        ticketService.acceptOrCancelReservations(reservation.getId(), TicketStatus.PAID);
    }

    @Test
    public void acceptOrCancelReservations_ShouldReturnTrue_whenTicketsAndStatusAreValid() {
        Reservation reservation = new Reservation();
        reservation.setId(1l);

        ticketService.acceptOrCancelReservations(reservation.getId(), TicketStatus.PAID);
    }

    @Test
    public void getUsersBoughtTickets_ShouldPass_whenUserIsValid(){
        List<Ticket> tickets = this.ticketService.getUsersBoughtTickets();
        assertEquals(3,tickets.size());

    }

    @Test
    public void getTotalNumberOfUsersReservations_ShouldPass_whenUserIsValid(){
        Long ticketSize = this.ticketService.getTotalNumberOfUsersReservations();
        assertTrue(ticketSize.equals(2l));

    }

}
