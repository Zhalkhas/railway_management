package org.yoptascript.inc.other;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.Message.RecipientType;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class EmailNotificator {
    private final String username = "scumlean.railway@gmail.com";
    private final String password = "javahtml";

    public EmailNotificator() {
    }

    public void sendEdit(String mail, String subject, String text) {
        Properties prop = new Properties();
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "587");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.socketFactory.port", "465");
        prop.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        Session session = Session.getInstance(prop, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication()     {
                return new PasswordAuthentication("scumlean.railway@gmail.com", "javahtml");
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(RecipientType.TO, InternetAddress.parse(mail));
            message.setSubject(subject);
            message.setText(text);
            Transport.send(message);
            System.out.println("Done");
        } catch (MessagingException var7) {
            var7.printStackTrace();
        }
    }

    public void sendPdf(String mail, String subject, String text, String file) {
        Properties prop = new Properties();
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "587");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.socketFactory.port", "465");
        prop.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        Session session = Session.getInstance(prop, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(RecipientType.TO, InternetAddress.parse(mail));
            message.setSubject(subject);
            message.setText(text);
            BodyPart messageBodyPart = new MimeBodyPart();
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);
            messageBodyPart = new MimeBodyPart();
            DataSource source = new FileDataSource(file);
            messageBodyPart.setDataHandler(new DataHandler(source));
            messageBodyPart.setFileName(file);
            multipart.addBodyPart(messageBodyPart);
            Transport.send(message);
            System.out.println("Done");
        } catch (MessagingException var11) {
            var11.printStackTrace();
        }

    }

    public void sendEdit(ResultSet rs) throws SQLException {
        while(rs.next()) {
            Properties prop = new Properties();
            prop.put("mail.smtp.host", "smtp.gmail.com");
            prop.put("mail.smtp.port", "587");
            prop.put("mail.smtp.auth", "true");
            prop.put("mail.smtp.socketFactory.port", "465");
            prop.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            Session session = Session.getInstance(prop);

            try {
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(username));
                message.setRecipients(RecipientType.TO, InternetAddress.parse(rs.getString(1)));
                message.setSubject("TicketEdit");
                message.setText("Railway schedule was changed to: \n Arrival time:" + rs.getString(2) + "\n Departure time: " + rs.getString(3));
                Transport.send(message);
                System.out.println("Done");
            } catch (MessagingException var5) {
                var5.printStackTrace();
            }
        }

    }
}