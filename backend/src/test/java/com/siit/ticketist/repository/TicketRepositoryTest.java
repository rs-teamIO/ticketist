package com.siit.ticketist.repository;

import com.siit.ticketist.model.Ticket;
import com.siit.ticketist.model.TicketStatus;
import org.hamcrest.collection.IsEmptyCollection;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql("/email-notif.sql")
public class TicketRepositoryTest {

    @Autowired
    private TicketRepository ticketRepository;

    private static final Long EVENT_1 = Long.valueOf(1);
    private static final Long EVENT_2 = Long.valueOf(2);
    private static final Long EVENT_3 = Long.valueOf(3);

    @Test
    public void findEmailsToBeNotifiedReturnsUserEmailsWhenThereAreReservations_ForReservationDeadline() {
        Set<String> emails = ticketRepository.findEmailsToBeNotified(EVENT_1, TicketStatus.RESERVED.ordinal());
        assertThat(emails, hasSize(1));
        assertThat("Email of user who reserved the event should be returned", emails, hasItems("user1@gmail.com"));
    }

    @Test
    public void findEmailsToBeNotifiedReturnsEmptySetWhenThereAreTicketsButNoReservations_ForReservationDeadline() {
        Set<String> emails = ticketRepository.findEmailsToBeNotified(EVENT_2, TicketStatus.RESERVED.ordinal());
        assertThat("No emails are returned when there are no reservations for the event", emails, IsEmptyCollection.empty());
    }

    @Test
    public void findEmailsToBeNotifiedReturnsAllUserEmailsWhenThereAreReservationsAndTickets_ForEventCancellation() {
        TicketStatus a = TicketStatus.PAID;
        Set<String> emails = ticketRepository.findEmailsToBeNotified(EVENT_1, TicketStatus.PAID.ordinal());
        assertThat(emails, hasSize(2));
        assertThat("Emails of the user who bought the ticket, and the user who reserved should be returned",
                emails, containsInAnyOrder("user1@gmail.com", "user2@gmail.com"));
    }

    @Test
    public void findEmailsToBeNotifiedReturnsEmptySetWhenThereAreNoTicketsAndNoReservationsForGivenEvent() {
        Set<String> emails = ticketRepository.findEmailsToBeNotified(EVENT_3, TicketStatus.RESERVED.ordinal());
        assertThat("No emails are returned when there are no reservations and no tickets for the event", emails, IsEmptyCollection.empty());
    }

    @Test
    @Transactional
    public void deactivateTicketsSetsTicketStatusToCancelledWhenEventWithGivenIdExists() {
        ticketRepository.deactivateTickets(EVENT_1);
        List<TicketStatus> ticketStatuses = ticketRepository.findByEventId(EVENT_1).stream()
                .map(ticket -> ticket.getStatus()).collect(Collectors.toList());
        assertThat(ticketStatuses, everyItem(equalTo(TicketStatus.EVENT_CANCELLED)));
    }

}
