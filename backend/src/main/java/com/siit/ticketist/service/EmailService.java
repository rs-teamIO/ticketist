package com.siit.ticketist.service;

import com.siit.ticketist.dto.PdfTicket;
import com.siit.ticketist.model.Event;
import com.siit.ticketist.model.RegisteredUser;
import com.siit.ticketist.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * E-mail generation and delivery service implementation.
 */
@Service
public class EmailService {

    private static final int DEADLINE_NOTIFICATIONS_RATE = 86400000;

    @Value("${spring.mail.username}")
    private String email;

    @Value("${templates.html.verification}")
    private String verificationTemplateName;

    @Value("${templates.html.ticketsPurchase}")
    private String ticketsPurchaseTemplateName;

    @Value("${templates.html.deadlineNotification}")
    private String deadlineNotificationTemplateName;

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine springTemplateEngine;
    private final PdfService pdfService;
    private final EventService eventService;
    private final TicketRepository ticketRepository;

    @Autowired
    public EmailService(JavaMailSender mailSender, SpringTemplateEngine springTemplateEngine, PdfService pdfService,
                        EventService eventService, TicketRepository ticketRepository) {
        this.mailSender = mailSender;
        this.springTemplateEngine = springTemplateEngine;
        this.pdfService = pdfService;
        this.eventService = eventService;
        this.ticketRepository = ticketRepository;
    }

    /**
     * Sends an email.
     * In case an messaging error on the SMTP server occurs, a {@link MessagingException} is thrown.
     *
     * @param emailAddress Recipient's email address
     * @param subject Email subject
     * @param content Email content
     * @throws MessagingException Exception thrown in case an error on the SMTP server occurs
     */
    @Async
    void sendEmail(String emailAddress, String subject, String content) throws MessagingException {
        MimeMessage mailMessage = this.mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(mailMessage, "utf-8");

        messageHelper.setFrom(this.email);
        messageHelper.setTo(emailAddress);
        messageHelper.setSubject(subject);
        mailMessage.setContent(content, "text/html");

        this.mailSender.send(mailMessage);
    }

    /**
     * Sends an email with multipart content.
     * In case an messaging error on the SMTP server occurs, a {@link MessagingException} is thrown.
     *
     * @param emailAddress Recipient's email address
     * @param subject Email subject
     * @param content Email content
     * @throws MessagingException Exception thrown in case an error on the SMTP server occurs
     */
    @Async
    void sendEmail(String emailAddress, String subject, Multipart content) throws MessagingException {
        MimeMessage mailMessage = this.mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(mailMessage, "utf-8");

        messageHelper.setFrom(this.email);
        messageHelper.setTo(emailAddress);
        messageHelper.setSubject(subject);
        mailMessage.setContent(content);

        this.mailSender.send(mailMessage);
    }

    /**
     * Sends a verification e-mail to the registered user.
     * In case an messaging error on the SMTP server occurs, a {@link MessagingException} is thrown.
     *
     * @param registeredUser RegisteredUser instance as recipient
     * @throws MessagingException Exception thrown in case an error on the SMTP server occurs
     */
    @Async
    public void sendVerificationEmail(RegisteredUser registeredUser) throws MessagingException {
        this.sendEmail(registeredUser.getEmail(), "Please confirm your account", this.generateVerificationMail(registeredUser));
    }

    /**
     * Generates verification e-mail content using a template, based on the user's info.
     *
     * @param registeredUser RegisteredUser instance as recipient, used to fill template data
     * @return Verification email content
     */
    private String generateVerificationMail(RegisteredUser registeredUser) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("recipient", registeredUser.getFirstName());
        variables.put("code", registeredUser.getVerificationCode());

        return this.springTemplateEngine
                .process(this.verificationTemplateName, new Context(Locale.getDefault(), variables));
    }

    /**
     * Scheduled method that filters out specific users and sends deadline notification emails to them every day.
     * In case an messaging error on the SMTP server occurs, a {@link MessagingException} is thrown.
     *
     * @throws MessagingException Exception thrown in case an error on the SMTP server occurs
     */
    @Async
    @Scheduled(fixedRate = DEADLINE_NOTIFICATIONS_RATE)
    public void sendDeadlineNotificationEmails() throws MessagingException {
        List<Event> events = this.eventService.filterEventsByDeadline();
        Set<String> emailsToBeNotified = new LinkedHashSet<>();
        events.forEach(event -> emailsToBeNotified.addAll(this.ticketRepository.findEmailsToBeNotified(event.getId())));
        for (String emailAddress : emailsToBeNotified) {
            this.sendEmail(emailAddress, "Your reservation is about to expire", this.generateDeadlineNotificationMail());
        }
    }

    /**
     * Generates deadline notification e-mail content using a template.
     *
     * @return Deadline notification email content
     */
    private String generateDeadlineNotificationMail() {
        Map<String, Object> variables = new HashMap<>();

        return this.springTemplateEngine
                .process(this.deadlineNotificationTemplateName, new Context(Locale.getDefault(), variables));
    }

    /**
     * Sends a ticket purchase confirmation e-mail to the registered user.
     * In case an messaging error on the SMTP server occurs, a {@link MessagingException} is thrown.
     *
     * @param registeredUser RegisteredUser instance as recipient
     * @param tickets List of purchased ticket instances
     * @throws MessagingException Exception thrown in case an error on the SMTP server occurs
     */
    @Async
    public void sendTicketsPurchaseEmail(RegisteredUser registeredUser, List<PdfTicket> tickets) throws MessagingException {
        MimeBodyPart textBodyPart = new MimeBodyPart();
        textBodyPart.setContent(this.generateTicketsPurchaseEmail(registeredUser), "text/html");

        byte[] pdf = this.pdfService.generatePdfInvoice(tickets);

        MimeBodyPart attachmentBodyPart = new MimeBodyPart();
        attachmentBodyPart.setContent(pdf, "application/pdf");
        String currentDate = new SimpleDateFormat("yyyyMMddHHmmss").format(Calendar.getInstance().getTime());
        attachmentBodyPart.setFileName(String.format("%s_%s_%s.pdf", registeredUser.getFirstName(), registeredUser.getLastName(), currentDate));

        Multipart multipartContent = new MimeMultipart();
        multipartContent.addBodyPart(textBodyPart);
        multipartContent.addBodyPart(attachmentBodyPart);

        this.sendEmail(registeredUser.getEmail(), "Tickets purchase confirmation", multipartContent);
    }

    /**
     * Generates tickets purchase e-mail content using a template, based on the user's info.
     *
     * @param registeredUser RegisteredUser instance as recipient, used to fill template data
     * @return Tickets purchase email content
     */
    private String generateTicketsPurchaseEmail(RegisteredUser registeredUser) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("recipient", registeredUser.getFirstName());

        return this.springTemplateEngine
                .process(this.ticketsPurchaseTemplateName, new Context(Locale.getDefault(), variables));
    }
}
