package com.siit.ticketist.repository;

import com.siit.ticketist.model.Ticket;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TicketRepositoryTest {

    @Autowired
    private TicketRepository ticketRepository;

    @Test
    public void findTicketsByEventId_ShouldReturnEmptyList_whenEventIDisInvalid() {
        List<Ticket> foundTickets = ticketRepository.findByEventId(88L);
        assertEquals("ticket list is empty (0 elements)", 0, foundTickets.size());
    }

    @Test
    public void findTicketsByEventId_ShouldReturnListWithTickets_whenEventIDisValid() {
        List<Ticket> foundTickets = ticketRepository.findByEventId((12L));
        assertEquals("ticket list is not empty (8 elements)", 8, foundTickets.size());
    }

    @Test
    public void findTicketsByEventSectorId_ShouldReturnEmptyList_whenEventSectorIDisInvalid() {
        List<Ticket> foundTickets = ticketRepository.findTicketsByEventSectorId(100L);
        assertEquals("ticket list is empty (0 elements)", 0, foundTickets.size());
    }

    @Test
    public void findTicketsByEventSectorId_ShouldReturnListWithTickets_whenEventSectorIDisValid() {
        List<Ticket> foundTickets = ticketRepository.findTicketsByEventSectorId((1L));
        assertEquals("ticket list is not empty (12 elements)", 12, foundTickets.size());
    }

    @Test
    public void findAllReservationsByUser_ShouldReturnEmptyList_whenUserIsNotRegistered() {
        List<Ticket> foundTickets = ticketRepository.findAllReservationsByUser(100L);
        assertEquals("ticket list is empty (0 elements)", 0, foundTickets.size());
    }

    @Test
    public void findAllReservationsByUser_ShouldReturnEmptyList_whenUserIsRegisteredButDontHaveAnyTicketReserved() {
        List<Ticket> foundTickets = ticketRepository.findAllReservationsByUser(4L);
        assertEquals("ticket list is empty (0 elements)", 0, foundTickets.size());
    }

    @Test
    public void findAllReservationsByUser_ShouldReturnListWithTickets_whenUserIsRegisteredAndHasReservedTickets() {
        List<Ticket> foundTickets = ticketRepository.findAllReservationsByUser(1L);
        assertEquals("ticket list is empty (4 elements)", 4, foundTickets.size());
    }

    @Test
    public void findAllReservationsByUserAndEvent_ShouldReturnEmptyList_whenUserIsRegisteredButWrongEventId() {
        List<Ticket> foundTickets = ticketRepository.findUsersReservationsByEvent(1L,40L);
        assertEquals("ticket list is empty (0 elements)", 0, foundTickets.size());
    }


    @Test
    public void findAllReservationsByUserAndEvent_ShouldReturnEmptyList_whenEventIsCorrectButWrongUserId() {
        List<Ticket> foundTickets = ticketRepository.findUsersReservationsByEvent(6L,12L);
        assertEquals("ticket list is empty (0 elements)", 0, foundTickets.size());
    }


    @Test
    public void findAllReservationsByUserAndEvent_ShouldReturnList_whenUserIsRegisteredAndCorrectEventId() {
        List<Ticket> foundTickets = ticketRepository.findUsersReservationsByEvent(1L,12L);
        assertEquals("ticket list is not empty (2 elements)", 2, foundTickets.size());
    }

    @Test
    public void findUserTickets_ShouldReturnEmptyList_whenUserIdIsWrong(){
        List<Ticket> foundTickets = ticketRepository.findUsersTickets(10L);
        assertEquals("ticket list is empty (0 elements)", 0, foundTickets.size());
    }


    @Test
    public void findUserTickets_ShouldReturnList_whenUserIdIsCorrect(){
        List<Ticket> foundTickets = ticketRepository.findUsersTickets(1L);
        assertEquals("ticket list is not empty (4 elements)", 4, foundTickets.size());
    }

    @Test
    public void findTicketsByIdGroup_ShouldReturnEmptyList_whenUserIdIsWrong(){
        List<Long> temp = new ArrayList<>();
        temp.add(2l);
        temp.add(5l);
        List<Ticket> foundTickets = ticketRepository.findTicketsByIdGroup(temp,11l);
        assertEquals("ticket list is empty (0 elements)", 0, foundTickets.size());
    }

    @Test
    public void findTicketsByIdGroup_ShouldReturnWrongList_whenSendindTicketsThatAreBought(){
        List<Long> temp = new ArrayList<>();
        temp.add(1l);
        temp.add(2l);
        List<Ticket> foundTickets = ticketRepository.findTicketsByIdGroup(temp,1l);
        assertEquals("ticket list size is wronge (1 elements)", 1, foundTickets.size());
    }

    @Test
    public void findTicketsByIdGroup_ShouldReturnList_whenTicketsAndUserIdIsCorrect(){
        List<Long> temp = new ArrayList<>();
        temp.add(2l);
        temp.add(5l);
        List<Ticket> foundTickets = ticketRepository.findTicketsByIdGroup(temp,1l);
        assertEquals("ticket list is not empty (2 elements)", 2, foundTickets.size());
    }
}
