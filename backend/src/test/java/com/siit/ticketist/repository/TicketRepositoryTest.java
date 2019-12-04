package com.siit.ticketist.repository;

import com.siit.ticketist.model.Ticket;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.*;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TicketRepositoryTest {

    @Autowired
    private TicketRepository ticketRepository;

    @Test
    public void findTicketsByEventId_ShouldReturnEmptyList_whenEventIDisInvalid() {
        List<Ticket> foundTickets = ticketRepository.findTicketsByEventId(88L);
        assertEquals("ticket list is empty (0 elements)", 0, foundTickets.size());
    }

    @Test
    public void findTicketsByEventId_ShouldReturnListWithTickets_whenEventIDisValid() {
        List<Ticket> foundTickets = ticketRepository.findTicketsByEventId((12L));
        assertEquals("ticket list is not empty (16 elements)", 8, foundTickets.size());
    }

    @Test
    public void findTicketsByEventSectorId_ShouldReturnEmptyList_whenEventSectorIDisInvalid() {
        List<Ticket> foundTickets = ticketRepository.findTicketsByEventSectorId(100L);
        assertEquals("ticket list is empty (0 elements)", 0, foundTickets.size());
    }

    @Test
    public void findTicketsByEventSectorId_ShouldReturnListWithTickets_whenEventSectorIDisValid() {
        List<Ticket> foundTickets = ticketRepository.findTicketsByEventSectorId((1L));
        assertEquals("ticket list is not empty (8 elements)", 8, foundTickets.size());
    }

    @Test
    public void findAllReservationsByUser_ShouldReturnEmptyList_whenUserIsNotRegistered() {
        List<Ticket> foundTickets = ticketRepository.findAllReservationsByUser(100L);
        assertEquals("ticket list is empty (0 elements)", 0, foundTickets.size());
    }

    @Test
    public void findAllReservationsByUser_ShouldReturnEmptyList_whenUserIsRegisteredButDontHaveAnyTicketReserved() {
        List<Ticket> foundTickets = ticketRepository.findAllReservationsByUser(3L);
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
    public void findAllReservationsByUserAndEvent_ShouldReturnEmptyList_whenUserIsRegisteredAndCorrectEventId() {
        List<Ticket> foundTickets = ticketRepository.findUsersReservationsByEvent(1L,12L);
        assertEquals("ticket list is empty (2 elements)", 2, foundTickets.size());
    }

}
