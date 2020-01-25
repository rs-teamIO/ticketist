package com.siit.ticketist.unit.service;

import com.siit.ticketist.dto.PdfTicket;
import com.siit.ticketist.exceptions.BadRequestException;
import com.siit.ticketist.exceptions.NotFoundException;
import com.siit.ticketist.model.*;
import com.siit.ticketist.repository.ReservationRepository;
import com.siit.ticketist.repository.TicketRepository;
import com.siit.ticketist.service.EmailService;
import com.siit.ticketist.service.TicketService;
import com.siit.ticketist.service.UserService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import javax.mail.MessagingException;
import java.math.BigDecimal;
import java.util.*;

import static org.junit.Assert.*;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql("/tickets.sql")
public class TicketServiceTest {

    @Autowired
    private TicketService ticketService;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @MockBean
    private TicketRepository ticketRepositoryMock;

    @MockBean
    private UserService userServiceMock;

    @MockBean
    private EmailService emailServiceMock;

    @MockBean
    private ReservationRepository reservationRepositoryMock;

    RegisteredUser registeredUser;

    @Before
    public void before(){
        registeredUser = new RegisteredUser("kaca", "123456", "kaca@gmail.com", "Katarina", "Tukelic");
        registeredUser.setReservations(new HashSet<>());
        registeredUser.setTickets(new HashSet<>());
        registeredUser.setIsVerified(true);
        registeredUser.setId(1L);

        when(userServiceMock.findCurrentUser())
                .thenReturn(registeredUser);
    }

    @Test
    public void findOne_throwsNotFoundException_whenWantedTicketDoesNotExist() {
        exceptionRule.expect(NotFoundException.class);
        exceptionRule.expectMessage("Ticket not found.");
        Long wantedID = 13L;
        when(ticketRepositoryMock.findById(wantedID))
                .thenReturn(Optional.empty());

        ticketService.findOne(wantedID);
    }

    @Test
    public void findOne_successfullyGetTicket() {
        Long wantedID = 1L;
        int numRow = 3;
        int numCol = 5;
        BigDecimal price = new BigDecimal(555.55);
        TicketStatus ticketStatus = TicketStatus.FREE;
        Long version = 0L;

        Ticket ticket = new Ticket(wantedID, numRow, numCol, price, ticketStatus, version, new EventSector(), new Event(), null, null);

        when(ticketRepositoryMock.findById(wantedID))
                .thenReturn(Optional.of(ticket));

        Ticket foundTicket = ticketService.findOne(wantedID);
        assertEquals("Ticket's id equals to 1", wantedID, foundTicket.getId());
        assertSame("Ticket has number row equals to 3",3, foundTicket.getNumberRow());
        assertSame("Ticket has number column equals to 5",5, foundTicket.getNumberColumn());
        assertEquals("Ticket has price equals to 555.55", new Double("555.55"), new Double(foundTicket.getPrice().doubleValue()));
        assertSame("Ticket has status equals to FREE", TicketStatus.FREE, foundTicket.getStatus());
    }

    @Test
    public void findAllByEventId_successfullyGetTickets() {
        Long eventID = 1L;
        Event event = new Event();
        event.setId(eventID);
        Ticket ticket1 = new Ticket(1L, 1, 3, new BigDecimal(55.5), TicketStatus.PAID, 1L, new EventSector(), event, null, null);
        Ticket ticket2 = new Ticket(2L, 2, 4, new BigDecimal(66.6), TicketStatus.PAID, 1L, new EventSector(), event, null, null);
        Ticket ticket3 = new Ticket(3L, 5, 5, new BigDecimal(66.6), TicketStatus.PAID, 1L, new EventSector(), event, null, null);

        ArrayList<Ticket> tickets = new ArrayList<>();
        tickets.add(ticket1);
        tickets.add(ticket2);
        tickets.add(ticket3);

        when(ticketRepositoryMock.findByEventId(eventID))
                .thenReturn(tickets);

        List<Ticket> foundTickets = ticketService.findAllByEventId(eventID);
        assertSame("Number of tickets equals to 3", 3, tickets.size());
    }

    @Test
    public void findAllByEventSectorId_successfullyGetTickets() {
        Long eventID = 1L;
        EventSector eventSector = new EventSector();
        eventSector.setId(eventID);
        Ticket ticket1 = new Ticket(1L, 1, 3, new BigDecimal(55.5), TicketStatus.PAID, 1L, eventSector, new Event(), null, null);
        Ticket ticket2 = new Ticket(2L, 2, 4, new BigDecimal(66.6), TicketStatus.PAID, 1L, eventSector, new Event(), null, null);
        ArrayList<Ticket> tickets = new ArrayList<>();
        tickets.add(ticket1);
        tickets.add(ticket2);

        when(ticketRepositoryMock.findByEventSectorId(eventID))
                .thenReturn(tickets);

        List<Ticket> foundTickets = ticketService.findAllByEventSectorId(eventID);
        assertSame("Number of tickets equals to 2", 2, tickets.size());
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
        when(ticketRepositoryMock.findById(99L))
                .thenReturn(Optional.empty());
        ticketService.buyTickets(tickets);
    }

    @Test
    public void buyTickets_throwsBadRequestException_whenTicketArrayHasTicketWhichExistButIsNotFree() throws MessagingException {
        exceptionRule.expect(BadRequestException.class);
        exceptionRule.expectMessage("Tickets are already taken");
        Ticket ticket = new Ticket(2L, 1, 3, new BigDecimal(55.5), TicketStatus.PAID, 1L, new EventSector(), new Event(), null, null);

        when(ticketRepositoryMock.findById(2L))
                .thenReturn(Optional.of(ticket));

        List<Long> tickets = new ArrayList<>();
        tickets.add(2L);
        ticketService.buyTickets(tickets);
    }

    @Test
    public void buyTickets_throwsBadRequestException_whenTicketArrayHasTicketsRelatedToOtherEvents() throws MessagingException {
        exceptionRule.expect(BadRequestException.class);
        exceptionRule.expectMessage("Every ticket should be for same event!");
        Event event1 = new Event();
        event1.setId(1L);
        Event event2 = new Event();
        event2.setId(2L);
        Ticket ticket1 = new Ticket(1L, 1, 3, new BigDecimal(55.5), TicketStatus.FREE, 1L, new EventSector(), event1, null, null);
        Ticket ticket2 = new Ticket(2L, 1, 3, new BigDecimal(55.5), TicketStatus.FREE, 1L, new EventSector(), event2, null, null);

        when(ticketRepositoryMock.findById(1L))
                .thenReturn(Optional.of(ticket1));
        when(ticketRepositoryMock.findById((2L)))
                .thenReturn(Optional.of(ticket2));

        List<Long> tickets = new ArrayList<>();
        tickets.add(1L);
        tickets.add(2L);
        ticketService.buyTickets(tickets);
    }

    @Test
    public void buyTickets_throwsBadRequestException_whenTicketArrayRelatesToEventInPast() throws MessagingException {
        exceptionRule.expect(BadRequestException.class);
        exceptionRule.expectMessage("Event already started!");
        Event event = new Event();
        event.setId(1L);
        event.setStartDate(new Date(new Date().getTime() - 50000));
        Ticket ticket = new Ticket(1L, 1, 3, new BigDecimal(55.5), TicketStatus.FREE, 1L, new EventSector(), event, null, null);

        when(ticketRepositoryMock.findById(1L))
                .thenReturn(Optional.of(ticket));

        List<Long> tickets = new ArrayList<>();
        tickets.add(1L);
        ticketService.buyTickets(tickets);
    }

    @Test
    public void buyTickets_successfully() throws MessagingException {
        Sector sector = new Sector();
        sector.setName("Sever");

        EventSector eventSector = new EventSector();
        eventSector.setSector(sector);

        Venue venue = new Venue();
        venue.setName("Emirates Stadium");

        Event event = new Event();
        event.setId(1L);
        event.setName("EXIT");
        event.setVenue(venue);
        event.setStartDate(new Date(new Date().getTime() + 5000000));

        Ticket ticket = new Ticket(1L, 1, 3, new BigDecimal(55.5), TicketStatus.FREE, 1L, eventSector, event, null, null);

        when(ticketRepositoryMock.findById(1L))
                .thenReturn(Optional.of(ticket));

        when(ticketRepositoryMock.findOneById(1L))
                .thenReturn(Optional.of(ticket));

        List<PdfTicket> pdfTickets = new ArrayList<>();
        pdfTickets.add(new PdfTicket(ticket));

        doNothing().when(emailServiceMock).sendTicketsPurchaseEmail(registeredUser, pdfTickets);

        List<Long> tickets = new ArrayList<>();
        tickets.add(1L);

        ticketService.buyTickets(tickets);

        assertSame("Ticket status is PAID", TicketStatus.PAID, ticket.getStatus());
        assertNotNull("Ticket is related to user", ticket.getUser());
        assertSame("Ticket is related to user 1", 1L, ticket.getUser().getId());
    }

    @Test
    public void buyTickets_throwsBadCredentialsException_whenUserHasInvalidAuthInfo() throws  MessagingException {
        exceptionRule.expect(BadCredentialsException.class);
        Event event = new Event();
        event.setId(1L);
        event.setStartDate(new Date(new Date().getTime() + 500000));
        Ticket ticket = new Ticket(1L, 1, 3, new BigDecimal(55.5), TicketStatus.FREE, 1L, new EventSector(), event, null, null);

        when(ticketRepositoryMock.findById(1L))
                .thenReturn(Optional.of(ticket));

        when(userServiceMock.findCurrentUser())
                .thenThrow(BadCredentialsException.class);

        List<Long> tickets = new ArrayList<>();
        tickets.add(1L);
        ticketService.buyTickets(tickets);
    }

    @Test
    public void buyTickets_throwsClassCastException_whenUserHasWrongRole() throws  MessagingException {
        exceptionRule.expect(ClassCastException.class);
        Event event = new Event();
        event.setId(1L);
        event.setStartDate(new Date(new Date().getTime() + 500000));
        Ticket ticket = new Ticket(1L, 1, 3, new BigDecimal(55.5), TicketStatus.FREE, 1L, new EventSector(), event, null, null);

        when(ticketRepositoryMock.findById(1L))
                .thenReturn(Optional.of(ticket));

        when(userServiceMock.findCurrentUser())
                .thenThrow(ClassCastException.class);

        List<Long> tickets = new ArrayList<>();
        tickets.add(1L);
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
        when(ticketRepositoryMock.findById(99L))
                .thenReturn(Optional.empty());
        ticketService.reserveTickets(tickets);
    }

    @Test
    public void reserveTickets_throwsBadRequestException_whenTicketArrayHasTicketWhichExistButIsNotFreeToReserve() throws MessagingException {
        exceptionRule.expect(BadRequestException.class);
        exceptionRule.expectMessage("Tickets are already taken");
        Ticket ticket = new Ticket(2L, 1, 3, new BigDecimal(55.5), TicketStatus.PAID, 1L, new EventSector(), new Event(), null, null);

        when(ticketRepositoryMock.findById(2L))
                .thenReturn(Optional.of(ticket));

        List<Long> tickets = new ArrayList<>();
        tickets.add(2L);
        ticketService.reserveTickets(tickets);
    }

    @Test
    public void reserveTickets_throwsBadRequestException_whenTicketArrayHasTicketsRelatedToOtherEvents() throws MessagingException {
        exceptionRule.expect(BadRequestException.class);
        exceptionRule.expectMessage("Every ticket should be for same event!");
        Event event1 = new Event();
        event1.setId(1L);
        Event event2 = new Event();
        event2.setId(2L);
        Ticket ticket1 = new Ticket(1L, 1, 3, new BigDecimal(55.5), TicketStatus.FREE, 1L, new EventSector(), event1, null, null);
        Ticket ticket2 = new Ticket(2L, 1, 3, new BigDecimal(55.5), TicketStatus.FREE, 1L, new EventSector(), event2, null, null);

        when(ticketRepositoryMock.findById(1L))
                .thenReturn(Optional.of(ticket1));
        when(ticketRepositoryMock.findById((2L)))
                .thenReturn(Optional.of(ticket2));

        List<Long> tickets = new ArrayList<>();
        tickets.add(1L);
        tickets.add(2L);
        ticketService.reserveTickets(tickets);
    }

    @Test
    public void reserveTickets_throwsBadRequestException_whenDeadlineOfWantedEventPassed() throws MessagingException {
        exceptionRule.expect(BadRequestException.class);
        exceptionRule.expectMessage("Reservation deadline passed! Now you can only buy a ticket");
        Event event = new Event();
        event.setId(1L);
        event.setReservationDeadline(new Date(new Date().getTime() - 500000));
        Ticket ticket = new Ticket(1L, 1, 3, new BigDecimal(55.5), TicketStatus.FREE, 1L, new EventSector(), event, null, null);

        when(ticketRepositoryMock.findById(1L))
                .thenReturn(Optional.of(ticket));

        List<Long> tickets = new ArrayList<>();
        tickets.add(1L);
        ticketService.reserveTickets(tickets);
    }

    @Test
    public void reserveTickets_throwsBadRequestException_whenUserReserveMoreTicketsThanEventProvidesForReservation() throws MessagingException {
        exceptionRule.expect(BadRequestException.class);
        exceptionRule.expectMessage("Event limit of reservations is 0");
        Event event = new Event();
        event.setId(1L);
        event.setReservationDeadline(new Date(new Date().getTime() + 500000));
        event.setReservationLimit(0);
        Ticket ticket = new Ticket(1L, 1, 3, new BigDecimal(55.5), TicketStatus.FREE, 1L, new EventSector(), event, null, null);

        when(ticketRepositoryMock.findById(1L))
                .thenReturn(Optional.of(ticket));

        when(ticketRepositoryMock.findAllByUserIdAndStatusAndEventId(1L, TicketStatus.RESERVED, 1L))
                .thenReturn(new ArrayList<>());

        List<Long> tickets = new ArrayList<>();
        tickets.add(1L);
        ticketService.reserveTickets(tickets);
    }

    @Test
    public void reserveTickets_successfully() throws MessagingException {
        Sector sector = new Sector();
        sector.setName("Sever");

        EventSector eventSector = new EventSector();
        eventSector.setSector(sector);

        Venue venue = new Venue();
        venue.setName("Emirates Stadium");

        Event event = new Event();
        event.setId(1L);
        event.setName("EXIT");
        event.setVenue(venue);
        event.setReservationDeadline(new Date(new Date().getTime() + 500000));
        event.setStartDate(new Date(new Date().getTime() + 5000000));
        event.setReservationLimit(4);

        Ticket ticket = new Ticket(1L, 1, 3, new BigDecimal(55.5), TicketStatus.FREE, 1L, eventSector, event, null, null);

        when(ticketRepositoryMock.findById(1L))
                .thenReturn(Optional.of(ticket));

        when(ticketRepositoryMock.findAllByUserIdAndStatusAndEventId(1L, TicketStatus.RESERVED, 1L))
                .thenReturn(new ArrayList<>());

        when(ticketRepositoryMock.findOneById(1L))
                .thenReturn(Optional.of(ticket));

        List<PdfTicket> pdfTickets = new ArrayList<>();
        pdfTickets.add(new PdfTicket(ticket));

        doNothing().when(emailServiceMock).sendTicketsPurchaseEmail(registeredUser, pdfTickets);
        when(reservationRepositoryMock.save(any(Reservation.class)))
                .thenReturn(null);

        List<Long> tickets = new ArrayList<>();
        tickets.add(1L);

        ticketService.reserveTickets(tickets);

        assertSame("Ticket status is RESERVED", TicketStatus.RESERVED, ticket.getStatus());
        assertNotNull("Ticket is related to user", ticket.getUser());
        assertSame("Ticket is related to user 1", 1L, ticket.getUser().getId());
    }

    @Test
    public void reserveTickets_throwsBadCredentialsException_whenUserHasInvalidAuthInfo() throws  MessagingException {
        exceptionRule.expect(BadCredentialsException.class);
        Event event = new Event();
        event.setId(1L);
        event.setReservationDeadline(new Date(new Date().getTime() + 500000));
        event.setReservationLimit(0);
        Ticket ticket = new Ticket(1L, 1, 3, new BigDecimal(55.5), TicketStatus.FREE, 1L, new EventSector(), event, null, null);

        when(ticketRepositoryMock.findById(1L))
                .thenReturn(Optional.of(ticket));

        when(ticketRepositoryMock.findAllByUserIdAndStatusAndEventId(1L, TicketStatus.RESERVED, 1L))
                .thenReturn(new ArrayList<>());

        when(userServiceMock.findCurrentUser())
                .thenThrow(BadCredentialsException.class);

        List<Long> tickets = new ArrayList<>();
        tickets.add(1L);
        ticketService.reserveTickets(tickets);
    }

    @Test
    public void reserveTickets_throwsClassCastException_whenUserHasWrongRole() throws  MessagingException {
        exceptionRule.expect(ClassCastException.class);
        Event event = new Event();
        event.setId(1L);
        event.setReservationDeadline(new Date(new Date().getTime() + 500000));
        event.setReservationLimit(0);
        Ticket ticket = new Ticket(1L, 1, 3, new BigDecimal(55.5), TicketStatus.FREE, 1L, new EventSector(), event, null, null);

        when(ticketRepositoryMock.findById(1L))
                .thenReturn(Optional.of(ticket));

        when(ticketRepositoryMock.findAllByUserIdAndStatusAndEventId(1L, TicketStatus.RESERVED, 1L))
                .thenReturn(new ArrayList<>());

        when(userServiceMock.findCurrentUser())
                .thenThrow(ClassCastException.class);

        List<Long> tickets = new ArrayList<>();
        tickets.add(1L);

        ticketService.reserveTickets(tickets);
    }


}
