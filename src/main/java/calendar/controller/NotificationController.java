package calendar.controller;

import calendar.entities.NotificationDetails;
import calendar.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Controller
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    /**
     * Sending notifications to users using socket
     * @param notificationDetails a message, event info, notification type
     * @return the notificationDetails
     */
    @MessageMapping("/event-notification")
    public NotificationDetails receiveNotification(@Payload NotificationDetails notificationDetails){
        notificationService.sendNotificationToGuestsEvent(notificationDetails);
        return notificationDetails;
    }
}
