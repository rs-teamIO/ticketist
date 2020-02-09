package com.siit.ticketist.unit.service;

import com.siit.ticketist.dto.PdfTicket;
import com.siit.ticketist.model.Event;
import com.siit.ticketist.model.RegisteredUser;
import com.siit.ticketist.model.TicketStatus;
import com.siit.ticketist.repository.TicketRepository;
import com.siit.ticketist.service.EmailService;
import com.siit.ticketist.service.EventService;
import com.siit.ticketist.service.PdfService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.test.util.ReflectionTestUtils;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * This class contains unit test methods for {@link EmailService}.
 */
public class EmailServiceTest {

    private static final String EMAIL = "noreply.ticketist@gmail.com";

    private static final String SUBJECT = "subject";
    private static final String VERIFICATION_EMAIL_SUBJECT = "Please confirm your account";
    private static final String DEADLINE_NOTIFICATION_EMAIL_SUBJECT = "Your reservation is about to expire";
    private static final String TICKETS_PURCHASE_EMAIL_SUBJECT = "Tickets purchase confirmation";
    private static final String EVENT_CANCELLED_EMAIL_SUBJECT = "Event %s has been cancelled.";

    private static final String CONTENT = "content";
    private static final String VERIFICATION_TEMPLATE_NAME = "verificationMail";
    private static final String TICKETS_PURCHASE_TEMPLATE_NAME = "ticketsMail";
    private static final String DEADLINE_NOTIFICATION_TEMPLATE_NAME = "notificationMail";
    private static final String EVENT_CANCELLED_TEMPLATE_NAME = "eventCancelledMail";

    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String FIRST_NAME = "First name";
    private static final String LAST_NAME = "Last name";
    private static final String RECIPIENT_EMAIL_1 = "test1@ticketist.org";
    private static final String RECIPIENT_EMAIL_2 = "test2@ticketist.org";
    private static final String RECIPIENT_EMAIL_3 = "test3@ticketist.org";
    private static final String VERIFICATION_CODE = "#123456789#";

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

    private static final int TIMEOUT = 5000;

    @Mock
    private JavaMailSender mailSenderMock;

    @Mock
    private ITemplateEngine springTemplateEngineMock;

    @Mock
    private PdfService pdfServiceMock;

    @Mock
    private EventService eventServiceMock;

    @Mock
    private TicketRepository ticketRepositoryMock;

    @InjectMocks
    private EmailService emailService;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    private MimeMessage mimeMessage;

    /**
     * Initializes mocks
     */
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        ReflectionTestUtils.setField(emailService, "email", EMAIL, String.class);
        ReflectionTestUtils.setField(emailService, "verificationTemplateName", VERIFICATION_TEMPLATE_NAME, String.class);
        ReflectionTestUtils.setField(emailService, "ticketsPurchaseTemplateName", TICKETS_PURCHASE_TEMPLATE_NAME, String.class);
        ReflectionTestUtils.setField(emailService, "deadlineNotificationTemplateName", DEADLINE_NOTIFICATION_TEMPLATE_NAME, String.class);
        ReflectionTestUtils.setField(emailService, "eventCancelledTemplateName", EVENT_CANCELLED_TEMPLATE_NAME, String.class);

        mimeMessage = new JavaMailSenderImpl().createMimeMessage();
        when(this.mailSenderMock.createMimeMessage())
                .thenReturn(mimeMessage);
    }

    /**
     * Tests sendEmail from {@link EmailService}.
     */
//    @Test
//    public void sendEmail_shouldSendEmail_ifCredentialsAreValid() {
//        try {
//            // Act
//            this.emailService.sendEmail(RECIPIENT_EMAIL_1, SUBJECT, CONTENT);
//
//            // Assert
//            verify(this.mailSenderMock, timeout(TIMEOUT).times(1)).createMimeMessage();
//            verify(this.mailSenderMock, timeout(TIMEOUT).times(1)).send(mimeMessage);
//            assertEquals(1, mimeMessage.getFrom().length);
//            assertEquals(EMAIL, mimeMessage.getFrom()[0].toString());
//            assertEquals(1, mimeMessage.getAllRecipients().length);
//            assertEquals(RECIPIENT_EMAIL_1, mimeMessage.getAllRecipients()[0].toString());
//            assertEquals(SUBJECT, mimeMessage.getSubject());
//            assertEquals(CONTENT, mimeMessage.getContent());
//        } catch (Exception e) {
//            fail();
//        }
//    }

    /**
     * Tests sendEmail from {@link EmailService}.
     */
//    @Test
//    public void sendEmailWithAttachment_shouldSendEmailWithAttachment_ifCredentialsAreValid() {
//        try {
//            // Arrange
//            MimeBodyPart textBodyPart = new MimeBodyPart();
//            textBodyPart.setContent(CONTENT, "text/html");
//            Multipart multipartContent = new MimeMultipart();
//            multipartContent.addBodyPart(textBodyPart);
//
//            // Act
//            this.emailService.sendEmail(RECIPIENT_EMAIL_1, SUBJECT, multipartContent);
//
//            // Assert
//            verify(this.mailSenderMock, timeout(TIMEOUT).times(1)).createMimeMessage();
//            verify(this.mailSenderMock, timeout(TIMEOUT).times(1)).send(mimeMessage);
//            assertEquals(1, mimeMessage.getFrom().length);
//            assertEquals(EMAIL, mimeMessage.getFrom()[0].toString());
//            assertEquals(1, mimeMessage.getAllRecipients().length);
//            assertEquals(RECIPIENT_EMAIL_1, mimeMessage.getAllRecipients()[0].toString());
//            assertEquals(SUBJECT, mimeMessage.getSubject());
//            assertNotNull(mimeMessage.getContent());
//        } catch (Exception e) {
//            fail();
//        }
//    }

    /**
     * Tests sendVerificationEmail from {@link EmailService} when {@link RegisteredUser} is not null.
     */
    @Test
    public void sendVerificationEmail_shouldSendVerificationEmail_whenRegisteredUserIsNotNull() {
        try {
            // Arrange
            final RegisteredUser registeredUser = new RegisteredUser(USERNAME, PASSWORD, RECIPIENT_EMAIL_1, FIRST_NAME, LAST_NAME);
            registeredUser.setVerificationCode(VERIFICATION_CODE);
            when(this.springTemplateEngineMock.process(eq(VERIFICATION_TEMPLATE_NAME), any(Context.class)))
                    .thenReturn(CONTENT);

            // Act
            this.emailService.sendVerificationEmail(registeredUser);

            // Assert
            verify(this.mailSenderMock, timeout(TIMEOUT).times(1)).createMimeMessage();
            verify(this.mailSenderMock, timeout(TIMEOUT).times(1)).send(mimeMessage);
            assertEquals(1, mimeMessage.getFrom().length);
            assertEquals(EMAIL, mimeMessage.getFrom()[0].toString());
            assertEquals(1, mimeMessage.getAllRecipients().length);
            assertEquals(RECIPIENT_EMAIL_1, mimeMessage.getAllRecipients()[0].toString());
            assertEquals(VERIFICATION_EMAIL_SUBJECT, mimeMessage.getSubject());
            assertEquals(CONTENT, mimeMessage.getContent());
        } catch (Exception e) {
            fail();
        }
    }

    /**
     * Tests sendVerificationEmail from {@link EmailService} when {@link RegisteredUser} is null.
     * Should throw {@link NullPointerException}.
     */
    @Test
    public void sendVerificationEmail_shouldThrowNullPointerException_whenRegisteredUserIsNull() throws MessagingException {
        // Arrange
        exceptionRule.expect(NullPointerException.class);
        when(this.springTemplateEngineMock.process(eq(VERIFICATION_TEMPLATE_NAME), any(Context.class)))
                .thenReturn(CONTENT);

        // Act
        this.emailService.sendVerificationEmail(null);
    }

    /**
     * Tests sendTicketsPurchaseEmail from {@link EmailService} when the ticket list is empty.
     */
    @Test
    public void sendTicketsPurchaseEmail_shouldSendTicketsPurchaseEmail_whenTicketListIsEmpty() {
        try {
            // Arrange
            ArrayList<PdfTicket> tickets = new ArrayList<>();

            final RegisteredUser registeredUser = new RegisteredUser(USERNAME, PASSWORD, RECIPIENT_EMAIL_1, FIRST_NAME, LAST_NAME);
            when(this.springTemplateEngineMock.process(eq(TICKETS_PURCHASE_TEMPLATE_NAME), any(Context.class)))
                    .thenReturn(CONTENT);
            when(this.pdfServiceMock.generatePdfInvoice(tickets))
                    .thenReturn(CONTENT.getBytes(StandardCharsets.UTF_8));

            // Act
            this.emailService.sendTicketsPurchaseEmail(registeredUser, tickets);

            // Assert
            verify(this.mailSenderMock, timeout(TIMEOUT).times(1)).createMimeMessage();
            verify(this.mailSenderMock, timeout(TIMEOUT).times(1)).send(mimeMessage);
            assertEquals(1, mimeMessage.getFrom().length);
            assertEquals(EMAIL, mimeMessage.getFrom()[0].toString());
            assertEquals(1, mimeMessage.getAllRecipients().length);
            assertEquals(RECIPIENT_EMAIL_1, mimeMessage.getAllRecipients()[0].toString());
            assertEquals(TICKETS_PURCHASE_EMAIL_SUBJECT, mimeMessage.getSubject());
            assertNotNull(mimeMessage.getContent());
        } catch (Exception e) {
            fail();
        }
    }

    /**
     * Tests sendTicketsPurchaseEmail from {@link EmailService} when the ticket list contains exactly one ticket.
     */
    @Test
    public void sendTicketsPurchaseEmail_shouldSendTicketsPurchaseEmail_whenTicketListContainsOneTicket() {
        try {
            // Arrange
            final ArrayList<PdfTicket> tickets = new ArrayList<>();
            tickets.add(new PdfTicket(TICKET_ID, EVENT_NAME, VENUE_NAME, EVENT_SECTOR, ROW, COLUMN, DATE, PRICE, IMAGE));

            final RegisteredUser registeredUser = new RegisteredUser(USERNAME, PASSWORD, RECIPIENT_EMAIL_1, FIRST_NAME, LAST_NAME);
            when(this.springTemplateEngineMock.process(eq(TICKETS_PURCHASE_TEMPLATE_NAME), any(Context.class)))
                    .thenReturn(CONTENT);
            when(this.pdfServiceMock.generatePdfInvoice(tickets))
                    .thenReturn(CONTENT.getBytes(StandardCharsets.UTF_8));

            // Act
            this.emailService.sendTicketsPurchaseEmail(registeredUser, tickets);

            // Assert
            verify(this.mailSenderMock, timeout(TIMEOUT).times(1)).createMimeMessage();
            verify(this.mailSenderMock, timeout(TIMEOUT).times(1)).send(mimeMessage);
            assertEquals(1, mimeMessage.getFrom().length);
            assertEquals(EMAIL, mimeMessage.getFrom()[0].toString());
            assertEquals(1, mimeMessage.getAllRecipients().length);
            assertEquals(RECIPIENT_EMAIL_1, mimeMessage.getAllRecipients()[0].toString());
            assertEquals(TICKETS_PURCHASE_EMAIL_SUBJECT, mimeMessage.getSubject());
            assertNotNull(mimeMessage.getContent());
        } catch (Exception e) {
            fail();
        }
    }

    /**
     * Tests sendTicketsPurchaseEmail from {@link EmailService} when the ticket list contains multiple tickets.
     */
    @Test
    public void sendTicketsPurchaseEmail_shouldSendTicketsPurchaseEmail_whenTicketListContainsMultipleTickets() {
        try {
            // Arrange
            final ArrayList<PdfTicket> tickets = new ArrayList<>();
            tickets.add(new PdfTicket(TICKET_ID, EVENT_NAME, VENUE_NAME, EVENT_SECTOR, ROW, COLUMN, DATE, PRICE, IMAGE));
            tickets.add(new PdfTicket(TICKET_ID, EVENT_NAME, VENUE_NAME, EVENT_SECTOR, ROW, COLUMN, DATE, PRICE, IMAGE));
            tickets.add(new PdfTicket(TICKET_ID, EVENT_NAME, VENUE_NAME, EVENT_SECTOR, ROW, COLUMN, DATE, PRICE, IMAGE));

            final RegisteredUser registeredUser = new RegisteredUser(USERNAME, PASSWORD, RECIPIENT_EMAIL_1, FIRST_NAME, LAST_NAME);
            when(this.springTemplateEngineMock.process(eq(TICKETS_PURCHASE_TEMPLATE_NAME), any(Context.class)))
                    .thenReturn(CONTENT);
            when(this.pdfServiceMock.generatePdfInvoice(tickets))
                    .thenReturn(CONTENT.getBytes(StandardCharsets.UTF_8));

            // Act
            this.emailService.sendTicketsPurchaseEmail(registeredUser, tickets);

            // Assert
            verify(this.mailSenderMock, timeout(TIMEOUT).times(1)).createMimeMessage();
            verify(this.mailSenderMock, timeout(TIMEOUT).times(1)).send(mimeMessage);
            assertEquals(1, mimeMessage.getFrom().length);
            assertEquals(EMAIL, mimeMessage.getFrom()[0].toString());
            assertEquals(1, mimeMessage.getAllRecipients().length);
            assertEquals(RECIPIENT_EMAIL_1, mimeMessage.getAllRecipients()[0].toString());
            assertEquals(TICKETS_PURCHASE_EMAIL_SUBJECT, mimeMessage.getSubject());
            assertNotNull(mimeMessage.getContent());
        } catch (Exception e) {
            fail();
        }
    }

    /**
     * Tests sendTicketsPurchaseEmail from {@link EmailService} when {@link RegisteredUser} is null.
     * Should throw {@link NullPointerException}.
     */
    @Test
    public void sendTicketsPurchaseEmail_shouldThrowNullPointerException_whenRegisteredUserIsNull() throws MessagingException {
        // Arrange
        exceptionRule.expect(NullPointerException.class);
        final ArrayList<PdfTicket> tickets = new ArrayList<>();

        when(this.springTemplateEngineMock.process(eq(TICKETS_PURCHASE_TEMPLATE_NAME), any(Context.class)))
                .thenReturn(CONTENT);
        when(this.pdfServiceMock.generatePdfInvoice(tickets))
                .thenReturn(CONTENT.getBytes(StandardCharsets.UTF_8));

        // Act
        this.emailService.sendTicketsPurchaseEmail(null, tickets);
    }

    /**
     * Tests sendEventCancelledEmails from {@link EmailService} when there is only one email to be notified.
     */
    @Test
    public void sendEventCancelledEmails_shouldSendEventCancelledEmails_whenThereIsOnlyOneEmailToBeNotified() {
        try {
            // Arrange
            final Event event = new Event();
            event.setId(EVENT_ID);
            event.setName(EVENT_NAME);

            final HashSet<String> emails = new HashSet<>();
            emails.add(RECIPIENT_EMAIL_1);

            when(this.ticketRepositoryMock.findEmailsToBeNotified(event.getId(), TicketStatus.PAID.ordinal()))
                    .thenReturn(emails);
            when(this.springTemplateEngineMock.process(eq(EVENT_CANCELLED_TEMPLATE_NAME), any(Context.class)))
                    .thenReturn(CONTENT);

            // Act
            this.emailService.sendEventCancelledEmails(event);

            // Assert
            verify(this.mailSenderMock, timeout(TIMEOUT).times(1)).createMimeMessage();
            verify(this.mailSenderMock, timeout(TIMEOUT).times(1)).send(mimeMessage);
            assertEquals(1, mimeMessage.getFrom().length);
            assertEquals(EMAIL, mimeMessage.getFrom()[0].toString());
            assertEquals(1, mimeMessage.getAllRecipients().length);
            assertEquals(RECIPIENT_EMAIL_1, mimeMessage.getAllRecipients()[0].toString());
            assertEquals(String.format(EVENT_CANCELLED_EMAIL_SUBJECT, event.getName()), mimeMessage.getSubject());
            assertEquals(CONTENT, mimeMessage.getContent());
        } catch (Exception e) {
            fail();
        }
    }

    /**
     * Tests sendEventCancelledEmails from {@link EmailService} when there are multiple emails to be notified.
     */
    @Test
    public void sendEventCancelledEmails_shouldSendEventCancelledEmails_whenThereAreMultipleEmailsToBeNotified() {
        try {
            // Arrange
            final Event event = new Event();
            event.setId(EVENT_ID);
            event.setName(EVENT_NAME);

            final HashSet<String> emails = new HashSet<>();
            emails.add(RECIPIENT_EMAIL_1);
            emails.add(RECIPIENT_EMAIL_2);
            emails.add(RECIPIENT_EMAIL_3);

            when(this.ticketRepositoryMock.findEmailsToBeNotified(event.getId(), TicketStatus.PAID.ordinal()))
                    .thenReturn(emails);
            when(this.springTemplateEngineMock.process(eq(EVENT_CANCELLED_TEMPLATE_NAME), any(Context.class)))
                    .thenReturn(CONTENT);

            // Act
            this.emailService.sendEventCancelledEmails(event);

            // Assert
            verify(this.mailSenderMock, timeout(TIMEOUT).times(3)).createMimeMessage();
            verify(this.mailSenderMock, timeout(TIMEOUT).times(3)).send(mimeMessage);

        } catch (Exception e) {
            fail();
        }
    }

    /**
     * Tests sendEventCancelledEmails from {@link EmailService} when the {@link Event} is null.
     * Should throw {@link NullPointerException}.
     */
    @Test
    public void sendEventCancelledEmails_shouldThrowNullPointerException_whenEventIsNull() throws MessagingException {
        // Arrange
        exceptionRule.expect(NullPointerException.class);

        // Act
        this.emailService.sendEventCancelledEmails(null);
    }

    /**
     * Tests sendDeadlineNotificationEmails from {@link EmailService} when there are events with an approaching deadline.
     */
    @Test
    public void sendDeadlineNotificationEmails_shouldSendEmails_whenThereIsOneEventDeadlineApproaching() {
        try {
            // Arrange
            final Event event = new Event();
            event.setId(EVENT_ID);
            final List<Event> events = new ArrayList<>();
            events.add(event);

            when(this.eventServiceMock.filterEventsByDeadline())
                    .thenReturn(events);

            final Set<String> emails = new HashSet<>();
            emails.add(RECIPIENT_EMAIL_1);

            when(this.ticketRepositoryMock.findEmailsToBeNotified(EVENT_ID, TicketStatus.RESERVED.ordinal()))
                    .thenReturn(emails);

            when(this.springTemplateEngineMock.process(eq(DEADLINE_NOTIFICATION_TEMPLATE_NAME), any(Context.class)))
                    .thenReturn(CONTENT);
            // Act
            this.emailService.sendDeadlineNotificationEmails();

            // Assert
            verify(this.eventServiceMock, timeout(TIMEOUT).times(1)).filterEventsByDeadline();
            verify(this.ticketRepositoryMock, timeout(TIMEOUT).times(1)).findEmailsToBeNotified(EVENT_ID, TicketStatus.RESERVED.ordinal());
            assertEquals(1, mimeMessage.getFrom().length);
            assertEquals(EMAIL, mimeMessage.getFrom()[0].toString());
            assertEquals(1, mimeMessage.getAllRecipients().length);
            assertEquals(RECIPIENT_EMAIL_1, mimeMessage.getAllRecipients()[0].toString());
            assertEquals(DEADLINE_NOTIFICATION_EMAIL_SUBJECT, mimeMessage.getSubject());
            assertEquals(CONTENT, mimeMessage.getContent());
        } catch (Exception e) {
            fail();
        }
    }

    /**
     * Tests sendDeadlineNotificationEmails from {@link EmailService} when there are no events with an approaching deadline.
     */
    @Test
    public void sendDeadlineNotificationEmails_shouldNotSendAnyEmails_whenThereIsNoEventDeadlinesApproaching() throws MessagingException {
        // Arrange
        final List<Event> events = new ArrayList<>();
        when(this.eventServiceMock.filterEventsByDeadline())
                .thenReturn(events);

        final Set<String> emails = new HashSet<>();
        when(this.ticketRepositoryMock.findEmailsToBeNotified(EVENT_ID, TicketStatus.RESERVED.ordinal()))
                .thenReturn(emails);

        when(this.springTemplateEngineMock.process(eq(DEADLINE_NOTIFICATION_TEMPLATE_NAME), any(Context.class)))
                .thenReturn(CONTENT);
        // Act
        this.emailService.sendDeadlineNotificationEmails();

        // Assert
        verify(this.eventServiceMock, timeout(TIMEOUT).times(1)).filterEventsByDeadline();
        verify(this.ticketRepositoryMock, timeout(TIMEOUT).times(0)).findEmailsToBeNotified(EVENT_ID, TicketStatus.RESERVED.ordinal());
    }
}
