package calendar.utilities;

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
}
