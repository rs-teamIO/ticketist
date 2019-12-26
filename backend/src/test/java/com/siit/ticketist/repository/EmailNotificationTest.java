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

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@Sql("/email-notif.sql")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EmailNotificationTest {

    @Autowired
    private TicketRepository ticketRepository;

    @Test
    public void findEmailsToBeNotifiedReturnsUserEmailsWhenThereAreReservationsForGivenEvent() {
        Set<String> emails = ticketRepository.findEmailsToBeNotified(Long.valueOf(1), TicketStatus.RESERVED);
        assertThat(emails, hasSize(1));
        assertEquals("Email of user who reserved wanted event should be returned","user1@gmail.com", emails.stream().findFirst().get());
    }

    @Test
    public void findEmailsToBeNotifiedReturnsEmptySetWhenThereAreTicketsButNoReservationsForGivenEvent() {
        Set<String> emails = ticketRepository.findEmailsToBeNotified(Long.valueOf(2), TicketStatus.RESERVED);
        assertThat("No emails are returned when there are no reservations for the event", emails, IsEmptyCollection.empty());
    }

    @Test
    public void findEmailsToBeNotifiedReturnsEmptySetWhenThereAreNoTicketsAndNoReservationsForGivenEvent() {
        Set<String> emails = ticketRepository.findEmailsToBeNotified(Long.valueOf(3), TicketStatus.RESERVED);
        assertThat("No emails are returned when there are no reservations and no tickets for the event", emails, IsEmptyCollection.empty());
    }

    //TODO Metoda izmenjena, dodati testove za TicketStatus.PAID


}
