package org.coffee.web;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Named
@RequestScoped
@NoArgsConstructor
public class EmailSenderBean {

    @Getter
    @Setter
    private String sender;

    @Getter
    @Setter
    private String recipient;

    @Getter
    @Setter
    private String subject;

    @Getter
    @Setter
    private String body;

    @Getter
    @Setter
    private boolean defaultSender;

    @Setter
    private Properties properties;

    @Setter
    private Authenticator authenticator;

    public void sendEmail(){

        Session session = Session.getInstance(
                properties == null
                        ? SetDefaultSmtp()
                        : properties,
                authenticator == null
                        ? SetDefaultAuthenticator()
                        : authenticator);

        try {
            Message message = new MimeMessage(session);
            message.setFrom(
                    defaultSender
                            ? new InternetAddress("coffeebean@sandboxed93749178dd48e6ba10314f32bcd44a.mailgun.org")
                            : new InternetAddress(sender));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);

        } catch (Exception e) {
            e.printStackTrace(); // Log properly in production
        }
    }

    private Properties SetDefaultSmtp() {
        properties = new Properties();

        properties.put("mail.smtp.host", "smtp.mailgun.org");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");

        return properties;
    }

    private Authenticator SetDefaultAuthenticator() {
        authenticator = new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(
                        "coffeebean@sandboxed93749178dd48e6ba10314f32bcd44a.mailgun.org",
                        "coffeebean12345");
            }
        };

        return authenticator;
    }
}
