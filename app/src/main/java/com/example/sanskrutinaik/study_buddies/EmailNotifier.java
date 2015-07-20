package com.example.sanskrutinaik.study_buddies;


import android.content.pm.PackageInstaller;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.util.Properties;

import javax.activation.*;
import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

// Used http://stackoverflow.com/questions/2020088/sending-email-in-android-using-javamail-api-without-using-the-default-built-in-a as Reference in developing this feature.
public class EmailNotifier extends AsyncTask<MimeMessage, String, Boolean> {

    public Session createSession()
    {
        String senderEmail = "devdummytester@gmail.com";
        Properties properties = System.getProperties();
        properties.setProperty("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.user", senderEmail);
        properties.put("mail.smtp.password", "21ARif1rhGKB");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");

        return Session.getDefaultInstance(properties, null);
    }

    public MimeMessage prepareMessage(String toAddress,String messageBody, String subjectBody) throws IOException, MessagingException {

        Session session = createSession();
        DataHandler handler = new DataHandler(new ByteArrayDataSource("".toString(), "text/plain"));
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(session.getProperty("mail.smtp.user")));
        message.setDataHandler(handler);
        Multipart multiPart = new MimeMultipart();
        InternetAddress recipient= new InternetAddress(toAddress);
        message.addRecipient(Message.RecipientType.TO, recipient);
        message.setSubject(subjectBody);
        message.setContent(multiPart);
        message.setText(messageBody);

        return message;

    }
    @Override
    protected Boolean doInBackground(MimeMessage... params) {
        for (int i =0; i < params.length;i++)
        {
            try {
                Session session = createSession();
                Transport transport = session.getTransport("smtp");
                transport.connect("smtp.gmail.com", session.getProperty("mail.smtp.user"), "Letmepass!@");
                transport.sendMessage(params[i], params[i].getAllRecipients());
                transport.close();
            } catch (NoSuchProviderException e) {
                e.printStackTrace();
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }


        return true;
    }
}
