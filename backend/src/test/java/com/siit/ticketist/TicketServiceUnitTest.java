package com.siit.ticketist;

import com.siit.ticketist.exceptions.BadRequestException;
import com.siit.ticketist.exceptions.ForbiddenException;
import com.siit.ticketist.model.RegisteredUser;
import com.siit.ticketist.model.Reservation;
import com.siit.ticketist.model.Ticket;
import com.siit.ticketist.model.TicketStatus;
import com.siit.ticketist.repository.ReservationRepository;
import com.siit.ticketist.repository.TicketRepository;
import com.siit.ticketist.service.TicketService;
import com.siit.ticketist.service.UserService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TicketServiceUnitTest {

    @InjectMocks
    private TicketService ticketService;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Mock
    private UserService userService;

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void acceptOrCancelReservations_ShouldThrowBadRequestException_whenReservationIsInvalid(){
        exceptionRule.expect(BadRequestException.class);
        exceptionRule.expectMessage("Reservation does not exist");
        RegisteredUser registeredUser = new RegisteredUser();
        registeredUser.setId(1L);

        Reservation reservation = new Reservation();
        reservation.setId(10l);

        Mockito.when(userService.findCurrentUser()).thenReturn(registeredUser);
        Mockito.when(reservationRepository.findById(reservation.getId())).thenReturn(Optional.empty());

        ticketService.acceptOrCancelReservations(reservation.getId(),TicketStatus.PAID);
    }

    @Test
    public void acceptOrCancelReservations_ShouldThrowBadRequestException_whenItDoesntBelongToThatUser(){
        exceptionRule.expect(BadRequestException.class);
        exceptionRule.expectMessage("Reservation does not belong to that user!");
        RegisteredUser registeredUser = new RegisteredUser();
        registeredUser.setId(1L);

        Reservation reservation = new Reservation();
        reservation.setId(3l);
        RegisteredUser reg = new RegisteredUser();
        reg.setId(3l);
        reservation.setUser(reg);


        Mockito.when(userService.findCurrentUser()).thenReturn(registeredUser);
        Mockito.when(reservationRepository.findById(reservation.getId())).thenReturn(Optional.of(reservation));

        ticketService.acceptOrCancelReservations(reservation.getId(),TicketStatus.PAID);
    }


    @Test
    public void acceptOrCancelReservations_ShouldPass_whenReservationIsCorrect(){
        RegisteredUser registeredUser = new RegisteredUser();
        registeredUser.setId(1L);

        Ticket ticket = new Ticket();
        ticket.setId(2l);
        Ticket ticket2 = new Ticket();
        ticket2.setId(5l);
        Set<Ticket> ticketList = new HashSet<>();
        ticketList.add(ticket);
        ticketList.add(ticket2);

        Reservation reservation = new Reservation();
        reservation.setId(1l);
        RegisteredUser reg = new RegisteredUser();
        reg.setId(1l);
        reservation.setUser(reg);
        reservation.setTickets(ticketList);


        Mockito.when(userService.findCurrentUser()).thenReturn(registeredUser);
        Mockito.when(reservationRepository.findById(reservation.getId())).thenReturn(Optional.of(reservation));
        Boolean rez = ticketService.acceptOrCancelReservations(reservation.getId(), TicketStatus.PAID);
        assertTrue(rez);
    }

    @Test
    public void getUsersBoughtTickets_ShouldPass_whenUserIsValid(){
        RegisteredUser registeredUser = new RegisteredUser();
        registeredUser.setId(1l);

        List<Ticket> ticketList = new ArrayList<>();
        Ticket ticket = new Ticket();
        Ticket ticket2 = new Ticket();
        ticketList.add(ticket);
        ticketList.add(ticket2);


        Mockito.when(userService.findCurrentUser()).thenReturn(registeredUser);
        Mockito.when(ticketRepository.findAllByUserIdAndStatus(registeredUser.getId(), TicketStatus.PAID)).thenReturn(ticketList);
        List<Ticket> tickets = this.ticketService.getUsersBoughtTickets();
        assertEquals(2,tickets.size());

    }

    @Test
    public void getUsersBoughtTickets_ShouldThrowForbiddenException_whenUserIsNotAllowedAccess(){
        exceptionRule.expect(ForbiddenException.class);
        exceptionRule.expectMessage("You don't have permission to access this method on the server.");

        Mockito.when(userService.findCurrentUser()).thenThrow(new ForbiddenException("You don't have permission to access this method on the server."));
        this.ticketService.getUsersBoughtTickets();

    }

    @Test
    public void getTotalNumberOfUsersReservations_ShouldThrowForbiddenException_whenUserIsNotAllowedAccess(){
        exceptionRule.expect(ForbiddenException.class);
        exceptionRule.expectMessage("You don't have permission to access this method on the server.");

        Mockito.when(userService.findCurrentUser()).thenThrow(new ForbiddenException("You don't have permission to access this method on the server."));
        this.ticketService.getTotalNumberOfUsersReservations();

    }


    @Test
    public void getTotalNumberOfUsersReservations_ShouldPass_whenUserIsValid(){
        RegisteredUser registeredUser = new RegisteredUser();
        registeredUser.setId(1l);

        Mockito.when(userService.findCurrentUser()).thenReturn(registeredUser);
        Mockito.when(reservationRepository.countByUserId(registeredUser.getId())).thenReturn(3l);
        Long ticketSize = this.ticketService.getTotalNumberOfUsersReservations();
        assertTrue(ticketSize.equals(3l));

    }


}
