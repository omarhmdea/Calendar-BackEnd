package calendar.utilities;

import calendar.entities.Event;
import calendar.enums.NotificationType;
import calendar.service.EmailSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.Properties;

import static javax.mail.Message.RecipientType.TO;

@Component
public class EmailFacade {

    @Autowired
    private EmailSenderService emailSenderService;


    public void sendEmail(String email, String content, NotificationType notificationType) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(email);
        mailMessage.setSubject(notificationType + " Notification");
        mailMessage.setFrom("chatappgroup11@gmail.com");
        mailMessage.setText(content);
        emailSenderService.sendEmail(mailMessage);
    }


    public void sendInvitation(String email, Event event) {

        Properties props = new Properties();
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.trust","mail.man.com");
        Session session = Session.getDefaultInstance(props, null);
        MimeMessage mimeMessage = new MimeMessage(session);


        try {
            mimeMessage.setFrom(new InternetAddress("chatappgroup11@gmail.com"));
            mimeMessage.setSubject("This is the invitation for - " + event.getTitle());
            mimeMessage.addRecipient(TO, new InternetAddress(email));
            String htmlString = EmailBuilder.buildEmail("Event Invitation", event.toEmailString(), "http://localhost:8080/user/approve/" + event.getId() + "?email=" + email, "http://localhost:8080/user/reject/" + event.getId() + "?email=" + email);

            MimeBodyPart htmlPart = new MimeBodyPart();
            htmlPart.setContent(htmlString, "text/html");
            MimeMultipart mimeContent = new MimeMultipart();

            mimeContent.addBodyPart(htmlPart);
            Transport.send(mimeMessage);
        } catch(MessagingException e) {
            throw new RuntimeException(e);
        }

    }
}
