package org.coffee.service.interfaces;

import org.coffee.exception.EmailException;

import javax.mail.Authenticator;
import javax.mail.Session;
import java.util.Properties;

public interface EmailService {

    void sendEmail(String to, String from, String subject, String body) throws EmailException;

    void sendEmail(String to, String subject, String body) throws EmailException;

    Session getSessionInstance();

    Session getSessionInstance(Properties properties, Authenticator authenticator);
}
