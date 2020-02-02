package com.siit.ticketist.service;

import com.siit.ticketist.model.RegisteredUser;
import com.siit.ticketist.service.EmailService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.test.util.ReflectionTestUtils;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * This class contains unit test methods for {@link EmailService}.
 */
public class EmailServiceUnitTest {

    private static final String EMAIL = "noreply.ticketist@gmail.com";

    private static final String SUBJECT = "subject";
    private static final String VERIFICATION_EMAIL_SUBJECT = "Please confirm your account";
    private static final String DEADLINE_NOTIFICATION_EMAIL_SUBJECT = "Your reservation is about to expire";
    private static final String TICKETS_PURCHASE_EMAIL_SUBJECT = "Tickets purchase confirmation";
    private static final String EVENT_CANCELLED_EMAIL_SUBJECT = "Event has been cancelled";

    private static final String CONTENT = "content";
    private static final String VERIFICATION_TEMPLATE_NAME = "verificationMail";
    private static final String TICKETS_PURCHASE_TEMPLATE_NAME = "ticketsMail";
    private static final String DEADLINE_NOTIFICATION_TEMPLATE_NAME = "notificationMail";
    private static final String EVENT_CANCELLED_TEMPLATE_NAME = "eventCancelledMail";

    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String FIRST_NAME = "First name";
    private static final String LAST_NAME = "Last name";
    private static final String RECIPIENT_EMAIL = "test@ticketist.org";
    private static final String VERIFICATION_CODE = "#123456789#";

    private static final int TIMEOUT = 5000;

    @Mock
    private JavaMailSender mailSenderMock;

    @Mock
    private ITemplateEngine springTemplateEngineMock;
//
//    @Mock
//    private PdfService pdfServiceMock;
//
//    @Mock
//    private EventService eventServiceMock;
//
//    @Mock
//    private TicketRepository ticketRepositoryMock;

    @InjectMocks
    private EmailService emailService;

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

    // TODO: Doc
    @Test
    public void sendEmail_shouldAlwaysSendEmail() {
        try {
            // Act
            this.emailService.sendEmail(RECIPIENT_EMAIL, SUBJECT, CONTENT);

            // Assert
            verify(this.mailSenderMock, timeout(TIMEOUT).times(1)).createMimeMessage();
            verify(this.mailSenderMock, timeout(TIMEOUT).times(1)).send(mimeMessage);
            assertEquals(1, mimeMessage.getFrom().length);
            assertEquals(EMAIL, mimeMessage.getFrom()[0].toString());
            assertEquals(1, mimeMessage.getAllRecipients().length);
            assertEquals(RECIPIENT_EMAIL, mimeMessage.getAllRecipients()[0].toString());
            assertEquals(SUBJECT, mimeMessage.getSubject());
            assertEquals(CONTENT, mimeMessage.getContent());
        } catch (Exception e) {
            fail();
        }
    }

    // TODO: Doc
    @Test
    public void sendEmailWithAttachment_shouldAlwaysSendEmail() {
        try {
            // Arrange
            MimeBodyPart textBodyPart = new MimeBodyPart();
            textBodyPart.setContent(CONTENT, "text/html");
            Multipart multipartContent = new MimeMultipart();
            multipartContent.addBodyPart(textBodyPart);

            // Act
            this.emailService.sendEmail(RECIPIENT_EMAIL, SUBJECT, multipartContent);

            // Assert
            verify(this.mailSenderMock, timeout(TIMEOUT).times(1)).createMimeMessage();
            verify(this.mailSenderMock, timeout(TIMEOUT).times(1)).send(mimeMessage);
            assertEquals(1, mimeMessage.getFrom().length);
            assertEquals(EMAIL, mimeMessage.getFrom()[0].toString());
            assertEquals(1, mimeMessage.getAllRecipients().length);
            assertEquals(RECIPIENT_EMAIL, mimeMessage.getAllRecipients()[0].toString());
            assertEquals(SUBJECT, mimeMessage.getSubject());
            assertNotNull(mimeMessage.getContent());
        } catch (Exception e) {
            fail();
        }
    }

    // TODO: Doc
    @Test
    public void sendVerificationEmail_shouldAlwaysSendVerificationEmail() {
        try {
            // Arrange
            final RegisteredUser registeredUser = new RegisteredUser(USERNAME, PASSWORD, RECIPIENT_EMAIL, FIRST_NAME, LAST_NAME);
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
            assertEquals(RECIPIENT_EMAIL, mimeMessage.getAllRecipients()[0].toString());
            assertEquals(VERIFICATION_EMAIL_SUBJECT, mimeMessage.getSubject());
            assertEquals(CONTENT, mimeMessage.getContent());
        } catch (Exception e) {
            fail();
        }
    }
}
