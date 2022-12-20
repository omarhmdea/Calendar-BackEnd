package calendar.utilities;

import calendar.entities.Event;
import calendar.enums.NotificationType;
import calendar.service.EmailSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;

@Component
public class EmailFacade {
    @Autowired
    private EmailSenderService emailSenderService;

    public void sendEmail(String email, String content, NotificationType notificationType) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(email);
        mailMessage.setSubject(notificationType + " Notification");
        mailMessage.setFrom("omar.ah.2014@gmail.com");
        mailMessage.setText(content);
        emailSenderService.sendEmail(mailMessage);
    }

    public void sendInvitation(String email, Event event, NotificationType notificationType) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(email);
        mailMessage.setSubject("This is the invitation for - " + event.getTitle());
        mailMessage.setFrom("omar.ah.2014@gmail.com");
        String htmlString = EmailBuilder.buildEmail("Event Invitation", event.toEmailString(), "http://localhost:8080/user/approve/"+ event.getId()+"?email="+email, "http://localhost:8080/user/reject/"+ event.getId()+"?email="+email);
        mailMessage.setText(htmlString);
        emailSenderService.sendEmail(mailMessage);


        //   @PutMapping(value = "approve/{eventId}")
        //  http://localhost:8080/event/approve/{eventId}?email=omar@
    }
}
