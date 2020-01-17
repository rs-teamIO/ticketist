package com.siit.ticketist.service;

import com.siit.ticketist.exceptions.BadRequestException;
import com.siit.ticketist.model.RegisteredUser;
import com.siit.ticketist.model.Ticket;
import com.siit.ticketist.model.TicketStatus;
import com.siit.ticketist.repository.TicketRepository;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.event.annotation.BeforeTestExecution;
import org.springframework.test.context.event.annotation.BeforeTestMethod;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql("/data.sql")
public class TicketServiceUnitTest {

    @Autowired
    private TicketService ticketService;

    @MockBean
    private UserService userService;

    @MockBean
    private TicketRepository ticketRepository;

//    @Test(expected = BadRequestException.class)
//    public void acceptOrCancelReservations_ShouldThrowException_whenSomeTicketsAreSoldorNotFoundUnit(){
//        List<Long> tickets = new ArrayList();
//        tickets.add(2l);
//        tickets.add(100l);
//
//        RegisteredUser registeredUser = new RegisteredUser();
//        registeredUser.setId(1L);
//
//        Ticket ticket = new Ticket();
//        List<Ticket> ticketList = new ArrayList<>();
//        ticketList.add(ticket);
//
//        Mockito.when(userService.findCurrentUser()).thenReturn(registeredUser);
//        Mockito.when(ticketRepository.findTicketsByIdGroup(tickets,registeredUser.getId())).thenReturn(ticketList);
//
//        ticketService.acceptOrCancelReservations(tickets,TicketStatus.PAID);
//    }

//    @Test
//    public void acceptOrCancelReservations_ShouldReturnTrue_whenTicketsAndStatusAreValid(){
//        List<Long> tickets = new ArrayList();
//        tickets.add(2l);
//        tickets.add(5l);
//
//        RegisteredUser registeredUser = new RegisteredUser();
//        registeredUser.setId(1L);
//
//        Ticket ticket = new Ticket();
//        ticket.setId(2l);
//        Ticket ticket2 = new Ticket();
//        ticket2.setId(5l);
//        List<Ticket> ticketList = new ArrayList<>();
//        ticketList.add(ticket);
//        ticketList.add(ticket2);
//
//        Mockito.when(userService.findCurrentUser()).thenReturn(registeredUser);
//        Mockito.when(ticketRepository.findTicketsByIdGroup(tickets,registeredUser.getId())).thenReturn(ticketList);
//        Mockito.when(ticketRepository.findOneById(ticket.getId())).thenReturn(Optional.of(ticket));
//        Mockito.when(ticketRepository.findOneById(ticket2.getId())).thenReturn(Optional.of(ticket2));
//
//        Boolean rez = ticketService.acceptOrCancelReservations(tickets, TicketStatus.PAID);
//        assertTrue(rez);
//    }
}
