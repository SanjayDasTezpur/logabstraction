package com.mylive.project.loggy.mailGenerator;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.*;
import javax.mail.internet.*;
import java.io.*;
import java.util.List;
import java.util.Properties;


public class LoggyReportGenerator {

    private static final Logger logger = LoggerFactory.getLogger(LoggyReportGenerator.class);

    public static void generateMailReport(String sTrace, List<String> lstSubscriber, String type)
    {
        if(null == lstSubscriber || lstSubscriber.isEmpty()) {
            return;
        }
        Properties props = System.getProperties();
        Session session = Session.getDefaultInstance(props, null);
        try
        {
            Message message = new MimeMessage(session);
            message.setFrom();
            message.addRecipients(Message.RecipientType.TO, getAllMailRecipients(session, lstSubscriber));
            message.setSubject("Product log Exception Report - Submitted by " + System.getProperty("user.name"));
            MimeMultipart multipart = new MimeMultipart("related");
            BodyPart messageBodyPart = new MimeBodyPart();
            String htmlText =  new LoggyHtmlFormator().getFormattedTable(sTrace, type);
            messageBodyPart.setContent(htmlText, "text/html");
            multipart.addBodyPart(messageBodyPart);
            message.setContent(multipart);
            Transport.send(message);
            logger.info("Mail sent to " +lstSubscriber.toString() );
          //  System.out.println("msg sent");
        }
        catch (MessagingException e)
        {
            logger.error("Got MessagingException in Mailing "+type+" : " + e.getMessage());
        } catch (InterruptedException e) {
            logger.error("Got InterruptedException in Mailing  "+type+" : " + e.getMessage());
        } catch (IOException e) {
            logger.error("Got InterruptedException in Mailing "+type+" : " + e.getMessage());
        }
    }


    protected static InternetAddress[] getAllMailRecipients(Session session, List<String> lstSubscriber) throws AddressException, IOException, InterruptedException {
        List<InternetAddress> lstInternetAddresses = Lists.newArrayList();
        //lstInternetAddresses.add(InternetAddress.getLocalAddress(session));
        for(String sSubsc : lstSubscriber){
            lstInternetAddresses.add(new InternetAddress(sSubsc));
        }
        return lstInternetAddresses.toArray(new InternetAddress[]{});
    }
}
