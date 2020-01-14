package com.siit.ticketist.repository;

import com.siit.ticketist.model.TicketStatus;
import org.hamcrest.collection.IsEmptyCollection;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@Sql("/email-notif.sql")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EmailNotificationTest {

    @Autowired
    private TicketRepository ticketRepository;

    @Test
    public void findEmailsToBeNotifiedReturnsUserEmailsWhenThereAreReservations_ForReservationDeadline() {
        Set<String> emails = ticketRepository.findEmailsToBeNotified(Long.valueOf(1), TicketStatus.RESERVED.ordinal());
        assertThat(emails, hasSize(1));
        assertThat("Email of user who reserved the event should be returned", emails, hasItems("user1@gmail.com"));
    }

    @Test
    public void findEmailsToBeNotifiedReturnsEmptySetWhenThereAreTicketsButNoReservations_ForReservationDeadline() {
        Set<String> emails = ticketRepository.findEmailsToBeNotified(Long.valueOf(2), TicketStatus.RESERVED.ordinal());
        assertThat("No emails are returned when there are no reservations for the event", emails, IsEmptyCollection.empty());
    }

    @Test
    public void findEmailsToBeNotifiedReturnsAllUserEmailsWhenThereAreReservationsAndTickets_ForEventCancellation() {
        TicketStatus a = TicketStatus.PAID;
        Set<String> emails = ticketRepository.findEmailsToBeNotified(Long.valueOf(1), TicketStatus.PAID.ordinal());
        assertThat(emails, hasSize(2));
        assertThat("Emails of the user who bought the ticket, and the user who reserved should be returned",
                emails, containsInAnyOrder("user1@gmail.com", "user2@gmail.com"));
    }

    @Test
    public void findEmailsToBeNotifiedReturnsEmptySetWhenThereAreNoTicketsAndNoReservationsForGivenEvent() {
        Set<String> emails = ticketRepository.findEmailsToBeNotified(Long.valueOf(3), TicketStatus.RESERVED.ordinal());
        assertThat("No emails are returned when there are no reservations and no tickets for the event", emails, IsEmptyCollection.empty());
    }

}
