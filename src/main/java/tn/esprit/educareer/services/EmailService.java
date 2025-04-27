package tn.esprit.educareer.services;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class EmailService {
    private final String username;
    private final String appPassword;  // Now using App Password instead of regular password
    private final String host;
    private final int port;
    private final boolean useTLS;

    /**
     * Create an EmailService for Gmail using App Password
     *
     * @param gmailAddress Your Gmail address
     * @param appPassword App Password generated from Google Account settings
     */
    public EmailService(String gmailAddress, String appPassword) {
        this.username = gmailAddress;
        this.appPassword = appPassword;
        this.host = "smtp.gmail.com";
        this.port = 587;
        this.useTLS = true;
    }

    /**
     * Full constructor for any email service
     */
    public EmailService(String username, String password, String host, int port, boolean useTLS) {
        this.username = username;
        this.appPassword = password;
        this.host = host;
        this.port = port;
        this.useTLS = useTLS;
    }

    public void sendEmail(String recipient, String subject, String content) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);

        if (useTLS) {
            props.put("mail.smtp.starttls.enable", "true");
        }

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, appPassword);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
            message.setSubject(subject);
            message.setContent(content, "text/html; charset=utf-8");

            Transport.send(message);
            System.out.println("Email sent successfully to " + recipient);
        } catch (MessagingException e) {
            System.err.println("Failed to send email: " + e.getMessage());
            e.printStackTrace();
        }
    }
}