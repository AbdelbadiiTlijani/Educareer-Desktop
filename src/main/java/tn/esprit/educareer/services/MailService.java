package tn.esprit.educareer.services;

import jakarta.mail.*;
import jakarta.mail.internet.*;

import java.io.File;
import java.util.Properties;

public class MailService {

    private final String username = "ton.email@gmail.com";
    private final String password = "ton-mot-de-passe";

    public void envoyerMailAvecPDF(String destinataire, String sujet, String texte, File pdf) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(destinataire)
            );
            message.setSubject(sujet);

            MimeBodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText(texte);

            MimeBodyPart attachmentPart = new MimeBodyPart();
            attachmentPart.attachFile(pdf);

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);
            multipart.addBodyPart(attachmentPart);

            message.setContent(multipart);

            Transport.send(message);

            System.out.println("E-mail envoyé avec succès !");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
