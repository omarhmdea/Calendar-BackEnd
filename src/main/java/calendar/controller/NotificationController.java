package calendar.controller;

import calendar.entities.NotificationDetails;
import calendar.entities.User;
import calendar.service.NotificationService;
import calendar.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;


@Controller
public class NotificationController {

    @Autowired
    private NotificationService notificationService;
    @Autowired
    private UserService userService;

    @MessageMapping("/event-notification")
    public NotificationDetails receiveNotification(@Payload NotificationDetails notificationDetails){
        notificationService.sendNotificationToGuestsEvent(notificationDetails);
        return notificationDetails;
    }

}
