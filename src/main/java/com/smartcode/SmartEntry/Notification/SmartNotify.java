/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smartcode.SmartEntry.Notification;

import java.util.Properties;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 *
 * @author ADEBAYO ADEMOLA
 */
public class SmartNotify {

    private static final String SMTP_HOST_NAME = "smtp.gmail.com"; //can be your host server smtp@yourdomain.com
    private static final String SMTP_AUTH_USER = "hello@bowenuniversity.edu.ng"; //your login username/email
    private static final String SMTP_AUTH_PWD = "hello@12345?"; //password/secret

    private static Message message;

    public static void sendEmail(String to, String subject, String msg) {
        // Recipient's email ID needs to be mentioned.

        // Sender's email ID needs to be mentioned
        String from = "hello@bowenuniversity.edu.ng"; //from

        final String username = SMTP_AUTH_USER;
        final String password = SMTP_AUTH_PWD;

        // Assuming you are sending email through relay.jangosmtp.net
        String host = SMTP_HOST_NAME;

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", "587");

        // Get the Session object.
        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            // Create a default MimeMessage object.
            message = new MimeMessage(session);

            // Set From: header field of the header.
            message.setFrom(new InternetAddress(from));

            // Set To: header field of the header.
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(to));

            // Set Subject: header field
            message.setSubject(subject);

            // Create the message part
            BodyPart messageBodyPart = new MimeBodyPart();

            // Now set the actual message
            messageBodyPart.setContent(msg, "text/html");

            // Create a multipar message
            Multipart multipart = new MimeMultipart();

            // Set text message part
            multipart.addBodyPart(messageBodyPart);

//            // Part two is attachment
//            messageBodyPart = new MimeBodyPart();
//            String filename = Context.;
//            DataSource source = new FileDataSource(filename);
//            messageBodyPart.setDataHandler(new DataHandler(source));
//            messageBodyPart.setFileName(filename);
//            multipart.addBodyPart(messageBodyPart);n              // Send the complete message parts
            message.setContent(multipart);

            Thread thread = new Thread(new Runnable() {

                @Override
                public void run() {
                    try {

                        // Send message
                        Transport.send(message);
                        System.out.println("Sent message successfully....");
                    } catch (Exception e) {

                        e.printStackTrace();
                        sendEmail(to, subject, msg);
                        int c = 5;
                        if (c !=0) {
                            sendEmail(to, subject, msg);
                            c--;

                        }
                         if (c ==0) {
                            System.out.println("Attempted to send 5 Times but it failed....");

                        }

                    }
                }
            });

            thread.start();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {

        //sendEmail("ademoladebayo54@gmail.com", "Entry Notification","Your ward just arrived at school");
        String message = "Dear Guardian, \nyour ward " + "Adebayo" + " " + "Ademola" + ", with Matriculation number " + "SSE/016/17037" + " got SIGNED into the school at exactly " + "2018-09-11 2:45:43" + ". \n Notificaion is for you to keep track of your ward's  movement in and out of the school.\n\nBOWEN UNIVERSITY, \nIWO  OSUN STATE. ";
        sendEmail("ademoladebayo54@gmail.com", "ENTRY NOTIFICATION", message);

    }
}
