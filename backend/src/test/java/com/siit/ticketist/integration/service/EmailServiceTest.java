package com.siit.ticketist.integration.service;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;
import com.siit.ticketist.dto.PdfTicket;
import com.siit.ticketist.model.Event;
import com.siit.ticketist.model.RegisteredUser;
import com.siit.ticketist.service.EmailService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * This class contains integration test methods for {@link EmailService}.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EmailServiceTest {

    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String FIRST_NAME = "First name";
    private static final String LAST_NAME = "Last name";
    private static final String RECIPIENT_EMAIL_1 = "test1@ticketist.org";

    private static final Long EVENT_ID = 1L;
    private static final String TICKET_ID = "1";
    private static final String EVENT_NAME = "event name";
    private static final String VENUE_NAME = "venue name";
    private static final String EVENT_SECTOR = "event sector";
    private static final String ROW = "row";
    private static final String COLUMN = "column";
    private static final String DATE = "date";
    private static final String PRICE = "price";
    private static final String IMAGE = "image";

    private static final String VERIFICATION_EMAIL_SUBJECT = "Please confirm your account";
    private static final String TICKETS_PURCHASE_EMAIL_SUBJECT = "Tickets purchase confirmation";
    private static final String EVENT_CANCELLED_EMAIL_SUBJECT = "Event %s has been cancelled.";

    private static final Integer TIMEOUT = 15000;

    @Autowired
    private EmailService emailService;

    private GreenMail greenMail;

    @Before
    public void setupSMTP() {
        this.greenMail = new GreenMail(new ServerSetup(2525, "localhost", "smtp"));
        this.greenMail.setUser("username", "secret");
        this.greenMail.start();
    }

    @Test
    public void sendVerificationEmail_shouldSendVerificationEmail_whenRegisteredUserIsNotNull() throws MessagingException, IOException {
        // Arrange
        final RegisteredUser registeredUser = new RegisteredUser(USERNAME, PASSWORD, RECIPIENT_EMAIL_1, FIRST_NAME, LAST_NAME);

        // Act
        this.emailService.sendVerificationEmail(registeredUser);

        // Assert
        assertTrue(this.greenMail.waitForIncomingEmail(TIMEOUT, 1));
        MimeMessage receivedMessage = this.greenMail.getReceivedMessages()[0];

        assertEquals(VERIFICATION_EMAIL_SUBJECT, receivedMessage.getSubject());
        assertEquals(registeredUser.getEmail(), receivedMessage.getAllRecipients()[0].toString());
        assertNotNull(receivedMessage.getContent());
    }

    @Test
    public void sendTicketsPurchaseEmail_shouldSendTicketsPurchaseEmail_whenTicketListIsEmpty() throws MessagingException, IOException {
        // Arrange
        ArrayList<PdfTicket> tickets = new ArrayList<>();

        final RegisteredUser registeredUser = new RegisteredUser(USERNAME, PASSWORD, RECIPIENT_EMAIL_1, FIRST_NAME, LAST_NAME);

        // Act
        this.emailService.sendTicketsPurchaseEmail(registeredUser, tickets);

        // Assert
        assertTrue(this.greenMail.waitForIncomingEmail(TIMEOUT, 1));
        MimeMessage receivedMessage = this.greenMail.getReceivedMessages()[0];

        assertEquals(TICKETS_PURCHASE_EMAIL_SUBJECT, receivedMessage.getSubject());
        assertEquals(registeredUser.getEmail(), receivedMessage.getAllRecipients()[0].toString());
        assertNotNull(receivedMessage.getContent());
    }

    @Test
    public void sendTicketsPurchaseEmail_shouldSendTicketsPurchaseEmail_whenTicketListContainsOneTicket() throws MessagingException, IOException {
        // Arrange
        final ArrayList<PdfTicket> tickets = new ArrayList<>();
        tickets.add(new PdfTicket(TICKET_ID, EVENT_NAME, VENUE_NAME, EVENT_SECTOR, ROW, COLUMN, DATE, PRICE, IMAGE));

        final RegisteredUser registeredUser = new RegisteredUser(USERNAME, PASSWORD, RECIPIENT_EMAIL_1, FIRST_NAME, LAST_NAME);

        // Act
        this.emailService.sendTicketsPurchaseEmail(registeredUser, tickets);

        // Assert
        assertTrue(this.greenMail.waitForIncomingEmail(TIMEOUT, 1));
        MimeMessage receivedMessage = this.greenMail.getReceivedMessages()[0];

        assertEquals(TICKETS_PURCHASE_EMAIL_SUBJECT, receivedMessage.getSubject());
        assertEquals(registeredUser.getEmail(), receivedMessage.getAllRecipients()[0].toString());
        assertNotNull(receivedMessage.getContent());
    }

    @Test
    public void sendTicketsPurchaseEmail_shouldSendTicketsPurchaseEmail_whenTicketListContainsMultipleTickets() throws MessagingException, IOException {
        // Arrange
        final ArrayList<PdfTicket> tickets = new ArrayList<>();
        tickets.add(new PdfTicket(TICKET_ID, EVENT_NAME, VENUE_NAME, EVENT_SECTOR, ROW, COLUMN, DATE, PRICE, IMAGE));
        tickets.add(new PdfTicket(TICKET_ID, EVENT_NAME, VENUE_NAME, EVENT_SECTOR, ROW, COLUMN, DATE, PRICE, IMAGE));
        tickets.add(new PdfTicket(TICKET_ID, EVENT_NAME, VENUE_NAME, EVENT_SECTOR, ROW, COLUMN, DATE, PRICE, IMAGE));

        final RegisteredUser registeredUser = new RegisteredUser(USERNAME, PASSWORD, RECIPIENT_EMAIL_1, FIRST_NAME, LAST_NAME);

        // Act
        this.emailService.sendTicketsPurchaseEmail(registeredUser, tickets);

        // Assert
        assertTrue(this.greenMail.waitForIncomingEmail(TIMEOUT, 1));
        MimeMessage receivedMessage = this.greenMail.getReceivedMessages()[0];

        assertEquals(TICKETS_PURCHASE_EMAIL_SUBJECT, receivedMessage.getSubject());
        assertEquals(registeredUser.getEmail(), receivedMessage.getAllRecipients()[0].toString());
        assertNotNull(receivedMessage.getContent());
    }

    @Test
    @Transactional
    @Sql("/email-notif.sql")
    public void sendEventCancelledEmails_shouldSendEventCancelledEmails_whenThereIsOnlyOneEmailToBeNotified() throws MessagingException, IOException {
        // Arrange
        final Event event = new Event();
        event.setId(EVENT_ID);
        event.setName("Danasnji event");

        // Act
        this.emailService.sendEventCancelledEmails(event);

        // Assert
        assertTrue(this.greenMail.waitForIncomingEmail(TIMEOUT, 1));
        MimeMessage receivedMessage = this.greenMail.getReceivedMessages()[0];

        assertEquals(String.format(EVENT_CANCELLED_EMAIL_SUBJECT, "Danasnji event"), receivedMessage.getSubject());
        assertEquals("user1@gmail.com", receivedMessage.getAllRecipients()[0].toString());
        assertNotNull(receivedMessage.getContent());
    }

    @Test
    public void sendDeadlineNotificationEmails_shouldNotSendAnyEmails_whenThereIsNoEventDeadlinesApproaching() throws MessagingException {
        // Act
        this.emailService.sendDeadlineNotificationEmails();

        // Assert
        assertFalse(this.greenMail.waitForIncomingEmail(3, 1));
    }

    @After
    public void stopSMTP() {
        this.greenMail.stop();
    }
}

