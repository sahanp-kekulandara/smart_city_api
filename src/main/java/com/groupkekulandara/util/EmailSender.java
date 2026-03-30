package com.groupkekulandara.util;

import jakarta.mail.*;
import jakarta.mail.internet.*;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

public class EmailSender {

    public static void sendVerificationEmail(String recipientEmail, String code) {
        // 1. SMTP Server Settings
        String host = "smtp.gmail.com";
        final String user = "sahankekulandara4321@gmail.com"; // Your email
        final String password = "zinzrmhdxdsuvhby"; // Your App Password

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com"); // or your host
        props.put("mail.smtp.port", "587");

        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        props.put("mail.smtp.ssl.trust", "*"); // This tells Java to trust all SMTP hosts

        // 2. Create Session
        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, password);
            }
        });

        try {
            // 3. Construct the Message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(user));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject("Smart City - Your Verification Code");
            message.setText("Welcome! Your verification code is: " + code +
                    "\nThis code will expire in 15 minutes.");
            message.setFrom(new InternetAddress(user, "SmartCity Admin"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));

            // 4. Send the Email
            Transport.send(message);
            System.out.println("Email sent successfully to " + recipientEmail);

        } catch (MessagingException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}