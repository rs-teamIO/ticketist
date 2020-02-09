package com.siit.ticketist;

import com.siit.ticketist.exceptions.BadRequestException;
import com.siit.ticketist.model.Reservation;
import com.siit.ticketist.model.Ticket;
import com.siit.ticketist.model.TicketStatus;
import com.siit.ticketist.repository.ReservationRepository;
import com.siit.ticketist.security.TokenUtils;
import com.siit.ticketist.security.UserDetailsServiceImpl;
import com.siit.ticketist.service.TicketService;
import com.siit.ticketist.service.UserService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql("/data.sql")
public class TicketServiceTest {

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Autowired
    private TicketService ticketService;

    @Autowired
    private TokenUtils tokenUtils;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private ReservationRepository reservationRepository;

    @Before
    public void before(){
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken("user2020", "123456"));

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

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

        Optional<Reservation> test = reservationRepository.findById(reservation.getId());
        if(test.isPresent()){
            for (Ticket ticket: test.get().getTickets()) {
                assertEquals(TicketStatus.RESERVED, ticket.getStatus());
                assertEquals(reservation.getId(), ticket.getReservation().getId());
            }
        }
        ticketService.acceptOrCancelReservations(reservation.getId(), TicketStatus.PAID);

        test = reservationRepository.findById(reservation.getId());
        if(test.isPresent()){
            for (Ticket ticket: test.get().getTickets()) {
                assertEquals(TicketStatus.PAID, ticket.getStatus());
                assertNull(ticket.getReservation());
            }
        }

    }

    @Test
    public void getUsersBoughtTickets_ShouldPass_whenUserIsValid(){
        List<Ticket> tickets = this.ticketService.getUsersBoughtTickets();
        assertEquals(2,tickets.size());

    }

    @Test
    public void getTotalNumberOfUsersReservations_ShouldPass_whenUserIsValid(){
        Long ticketSize = this.ticketService.getTotalNumberOfUsersReservations();
        assertTrue(ticketSize.equals(3l));

    }

}
