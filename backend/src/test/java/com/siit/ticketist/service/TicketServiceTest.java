package com.siit.ticketist.service;

import com.siit.ticketist.controller.exceptions.BadRequestException;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TicketServiceTest {

    @Autowired
    private TicketService ticketService;

    @Test
    public void checkNumberOfTickets_ShouldPass_whenNumberOfTicketsIsGreaterThanZero(){
        List<Long> tickets = new ArrayList();
        tickets.add(1l);
        ticketService.checkNumberOfTickets(tickets);
    }


    @Test(expected = BadRequestException.class)
    public void checkNumberOfTickets_ShouldThrowException_whenNumberOfTicketsIsZero(){
        List<Long> tickets = new ArrayList();
        ticketService.checkNumberOfTickets(tickets);
    }

    @Test
    public void checkStatusIsValid_ShouldPass_whenStatusIsValid(){
        ticketService.checkStatusIsValid(-1);
        ticketService.checkStatusIsValid(1);
    }


    @Test(expected = BadRequestException.class)
    public void checkStatusIsValid_ShouldThrowException_whenStatusIsInvalid(){
        ticketService.checkStatusIsValid(10);
    }

    @Test(expected = BadRequestException.class)
    public void acceptOrCancelReservations_ShouldThrowException_whenSomeTicketsAreNotFound(){
        List<Long> tickets = new ArrayList();
        tickets.add(2l);
        tickets.add(100l);
        ticketService.acceptOrCancelReservations(tickets,1);
    }

    @Test(expected = BadRequestException.class)
    public void acceptOrCancelReservations_ShouldThrowException_whenSomeTicketsAreSold(){
        List<Long> tickets = new ArrayList();
        tickets.add(1l);
        tickets.add(2l);
        ticketService.acceptOrCancelReservations(tickets,1);
    }

    @Test
    public void acceptOrCancelReservations_ShouldReturnTrue_whenTicketsAndStatusAreValid(){
        List<Long> tickets = new ArrayList();
        tickets.add(2l);
        tickets.add(5l);
        Boolean rez = ticketService.acceptOrCancelReservations(tickets,1);
        assertTrue(rez);
    }
}
