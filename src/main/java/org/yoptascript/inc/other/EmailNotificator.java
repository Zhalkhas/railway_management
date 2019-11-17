package org.yoptascript.inc.other;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class EmailNotificator {
  final private String username = "scumlean.railway@gmail.com";
  final private String password = "javahtml";

  public void sendEdit(String mail, String subject, String text) {

    Properties prop = new Properties();
    prop.put("mail.smtp.host", "smtp.gmail.com");
    prop.put("mail.smtp.port", "587");
    prop.put("mail.smtp.auth", "true");
    prop.put("mail.smtp.socketFactory.port", "465");
    prop.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

    Session session = Session.getInstance(prop,
        new javax.mail.Authenticator() {
          protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(username, password);
          }
        });

    try {

      Message message = new MimeMessage(session);
      message.setFrom(new InternetAddress(username));
      message.setRecipients(
          Message.RecipientType.TO,
          InternetAddress.parse(mail)
      );
      message.setSubject(subject);
      message.setText(text);

      Transport.send(message);

      System.out.println("Done");

    } catch (MessagingException e) {
      e.printStackTrace();
    }
  }

  public void sendPdf(String mail, String subject, String text, String file) {
    Properties prop = new Properties();
    prop.put("mail.smtp.host", "smtp.gmail.com");
    prop.put("mail.smtp.port", "587");
    prop.put("mail.smtp.auth", "true");
    prop.put("mail.smtp.socketFactory.port", "465");
    prop.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

    Session session = Session.getInstance(prop,
        new javax.mail.Authenticator() {
          protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(username, password);
          }
        });

    try {

      Message message = new MimeMessage(session);
      message.setFrom(new InternetAddress(username));
      message.setRecipients(
          Message.RecipientType.TO,
          InternetAddress.parse(mail)
      );
      message.setSubject(subject);
      message.setText(text);

      BodyPart messageBodyPart = new MimeBodyPart();

      // Create a multipart message for attachment
      Multipart multipart = new MimeMultipart();

      // Set text message part
      multipart.addBodyPart(messageBodyPart);

      // Second part is attachment
      messageBodyPart = new MimeBodyPart();
      DataSource source = new FileDataSource(file);
      messageBodyPart.setDataHandler(new DataHandler(source));
      messageBodyPart.setFileName(file);
      multipart.addBodyPart(messageBodyPart);

      Transport.send(message);

      System.out.println("Done");

    } catch (MessagingException e) {
      e.printStackTrace();
    }
  }
  public void sendEdit(ResultSet rs) throws SQLException {
    while(rs.next()!=false){
//    final String username = "dafna251198@gmail.com";
//    final String password = "ainur2009";

    Properties prop = new Properties();
    prop.put("mail.smtp.host", "smtp.gmail.com");
    prop.put("mail.smtp.port", "587");
    prop.put("mail.smtp.auth", "true");
    prop.put("mail.smtp.socketFactory.port", "465");
    prop.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

    Session session = Session.getInstance(prop,
            new javax.mail.Authenticator() {
              protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
              }
            });

    try {

      Message message = new MimeMessage(session);
      message.setFrom(new InternetAddress("dafna251198@gmail.com"));
      message.setRecipients(
              Message.RecipientType.TO,
              InternetAddress.parse(rs.getString(1))
      );
      message.setSubject("TicketEdit");
      message.setText("Railway schedule was changed to: \n Arrival time:"+ rs.getString(2) +"\n Departure time: "+rs.getString(3));

      Transport.send(message);

      System.out.println("Done");

    } catch (MessagingException e) {
      e.printStackTrace();
    }
  }}}
