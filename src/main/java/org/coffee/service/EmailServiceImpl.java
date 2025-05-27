package org.coffee.service;

import javax.enterprise.context.RequestScoped;
import lombok.Setter;
import org.coffee.exception.EmailException;
import org.coffee.service.interfaces.EmailService;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@RequestScoped
public class EmailServiceImpl implements EmailService {

    private final String defaultSender = "coffeebean@sandboxed93749178dd48e6ba10314f32bcd44a.mailgun.org";
    private final String defaultPass = "coffeebean12345";

    @Setter
    private Properties properties;

    @Setter
    private Authenticator authenticator;

    public void sendEmail(String to, String from, String subject, String body)
            throws EmailException{

        Session session = getSessionInstance();
        if(session == null) return;

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);
        }
        catch (Exception e) {
            throw new EmailException("E-mail could not be sent", e);
        }
    }

    public void sendEmail(String to, String subject, String body) throws EmailException {

        Session session = getSessionInstance();
        if(session == null) return;

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(defaultSender));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);

        }
        catch (Exception e) {
            throw new EmailException("E-mail could not be sent", e);
        }
    }

    public Session getSessionInstance() {
        return Session.getInstance(setDefaultSmtp(), setDefaultAuthenticator());
    }

    public Session getSessionInstance(Properties properties, Authenticator authenticator) {
        return Session.getInstance(properties, authenticator);
    }

    private Properties setDefaultSmtp() {
        properties = new Properties();

        properties.put("mail.smtp.host", "smtp.mailgun.org");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");

        return properties;
    }

    private Authenticator setDefaultAuthenticator() {
        authenticator = new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(
                        defaultSender,
                        defaultPass);
            }
        };

        return authenticator;
    }
}
