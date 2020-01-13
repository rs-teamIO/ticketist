package com.siit.ticketist.service;

import com.siit.ticketist.exceptions.BadRequestException;
import com.siit.ticketist.model.TicketStatus;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.event.annotation.BeforeTestExecution;
import org.springframework.test.context.event.annotation.BeforeTestMethod;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql("/data.sql")
public class TicketServiceTest {

    @Autowired
    private TicketService ticketService;

    @Test(expected = BadRequestException.class)
    public void checkDuplicates_ShouldThrowBadRequestException_whenThereAreDuplicates(){
        List<Long> tickets = new ArrayList<>();
        tickets.add(1L);
        tickets.add(1L);
        ticketService.checkTicketDuplicates(tickets);
    }

    @Test
    public void checkDuplicates_ShouldPass_whenThereAreNoDuplicates(){
        List<Long> tickets = new ArrayList<>();
        tickets.add(1L);
        tickets.add(2L);
        ticketService.checkTicketDuplicates(tickets);
    }

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
        ticketService.checkStatusIsValid(TicketStatus.FREE);
        ticketService.checkStatusIsValid(TicketStatus.PAID);
    }


    @Test(expected = BadRequestException.class)
    public void checkStatusIsValid_ShouldThrowException_whenStatusIsInvalid(){
        ticketService.checkStatusIsValid(TicketStatus.USED);
    }

    @Test(expected = BadRequestException.class)
    public void acceptOrCancelReservations_ShouldThrowException_whenSomeTicketsAreNotFound(){
        List<Long> tickets = new ArrayList();
        tickets.add(2l);
        tickets.add(100l);
        ticketService.acceptOrCancelReservations(tickets,TicketStatus.PAID);
    }

    @Test(expected = BadRequestException.class)
    public void acceptOrCancelReservations_ShouldThrowException_whenSomeTicketsAreSold(){
        List<Long> tickets = new ArrayList();
        tickets.add(1l);
        tickets.add(2l);
        ticketService.acceptOrCancelReservations(tickets, TicketStatus.PAID);
    }

    @Test
    public void acceptOrCancelReservations_ShouldReturnTrue_whenTicketsAndStatusAreValid(){
        List<Long> tickets = new ArrayList();
        tickets.add(2l);
        tickets.add(5l);
        Boolean rez = ticketService.acceptOrCancelReservations(tickets, TicketStatus.PAID);
        assertTrue(rez);
    }
}
