package com.siit.ticketist.service;

import com.siit.ticketist.model.Event;
import com.siit.ticketist.model.RegisteredUser;
import com.siit.ticketist.repository.EventRepository;
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
import javax.mail.internet.MimeMessage;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * E-mail delivery service
 */
@Service
public class EmailService {

    @Value("${spring.mail.username}")
    private String email;

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine springTemplateEngine;

    @Autowired
    public EmailService(JavaMailSender mailSender, SpringTemplateEngine springTemplateEngine) {
        this.mailSender = mailSender;
        this.springTemplateEngine = springTemplateEngine;
    }

    /**
     * Sends a verification e-mail to the registered user.
     * In case an messaging error on the SMTP server occurs, a {@link MessagingException} is thrown.
     *
     * @param registeredUser RegisteredUser instance as recepient
     * @throws MessagingException
     */
    @Async
    public void sendVerificationEmail(RegisteredUser registeredUser) throws MessagingException {

        MimeMessage mailMessage = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(mailMessage, "utf-8");

        mailMessage.setContent(generateVerificationMail(registeredUser), "text/html");
        messageHelper.setTo(registeredUser.getEmail());
        messageHelper.setSubject("Please confirm your account");
        messageHelper.setFrom(this.email);

        mailSender.send(mailMessage);
    }

    /**
     * Generates verification e-mail content using a template, based on the user's info.
     *
     * @param registeredUser RegisteredUser instance as recepient, used to fill template data
     * @return Email content
     */
    @Async
    private String generateVerificationMail(RegisteredUser user) {
        Map<String, Object> variables = new HashMap<>();

        variables.put("recipient", user.getFirstName());
        variables.put("code", user.getVerificationCode());

        final String templateFileName = "verificationMail";
        String output = this.springTemplateEngine
                .process(templateFileName, new Context(Locale.getDefault(), variables));

        return output;
    }

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Async
    @Scheduled(fixedRate = 15000)
    public void sendDeadlineNotifications() throws MessagingException, ParseException {
        List<Event> events = filterEventsByDeadline(eventRepository.findAll());
        List<String> emails;
        for(Event e : events){
            emails = ticketRepository.findEmailsToBeNotified(e.getId());
            //emails.stream().forEach(this::sendNotificationEmail);
            for(String email : emails){
                sendNotificationEmail(email);
            }
        }
    }

    private void sendNotificationEmail(String email) throws MessagingException {
        MimeMessage mailMessage = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(mailMessage, "utf-8");

        mailMessage.setContent("Consider yourself notified.", "text/html");
        messageHelper.setTo(email);
        messageHelper.setSubject("Consider yourself notified.");
        messageHelper.setFrom(this.email);

        mailSender.send(mailMessage);
    }

    private void generateNotificationMail(){

    }

    private List<Event> filterEventsByDeadline(List<Event> allEvents){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String threeDaysFromNow = sdf.format(addDays(new Date(), 3));

        List<Event> filteredEvents = new ArrayList<>();
        for(Event e : allEvents){
            if(sdf.format(e.getReservationDeadline()).equals(threeDaysFromNow))
                filteredEvents.add(e);
        }
        return filteredEvents;
    }

    private Date addDays(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days); //minus number would decrement the days
        return cal.getTime();
    }
}
