package com.siit.ticketist.service;

import com.siit.ticketist.model.RegisteredUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

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
}
