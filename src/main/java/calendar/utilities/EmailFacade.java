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
        mailMessage.setFrom("chatappgroup11@gmail.com");
        mailMessage.setText(content);
        emailSenderService.sendEmail(mailMessage);
    }

    public void sendInvitation(String email, Event event) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(email);
        mailMessage.setSubject(NotificationType.INVITE_GUEST + " notification to event " + event.getTitle());
        mailMessage.setFrom("chatappgroup11@gmail.com");
        mailMessage.setText("You were invited to \n" +
                            event.toEmailString() + "\n" +
                            "To confirm your arrival at the event please click - " +
                            "http://localhost:8080/user/approve/" + event.getId() + "?email=" + email + "\n\n" +
                            "To reject your arrival at the event please click - " +
                            "http://localhost:8080/user/reject/" + event.getId() + "?email=" + email + "\n\n\n" +
                            "Have a lovely day,\nCalendar EOE.");
        emailSenderService.sendEmail(mailMessage);
    }
}
