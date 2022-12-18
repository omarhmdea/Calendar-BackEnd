package calendar.service;

import calendar.entities.Event;
import calendar.entities.User;
import calendar.entities.UserEvent;
import calendar.entities.UserNotification;
import calendar.enums.NotificationType;
import calendar.exception.customException.NotificationNotFoundException;
import calendar.repository.UserEventRepository;
import calendar.repository.UserNotificationRepository;
import calendar.utilities.EmailFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {

    @Autowired
    private UserNotificationRepository userNotificationRepository;
    @Autowired
    private EmailFacade emailFacade;
    @Autowired
    private UserEventRepository userEventRepository;


    public void sendNotification(Event event, NotificationType notificationType) {
        List<UserEvent> userEventList = userEventRepository.findByEvent(event);
        for(UserEvent userEvent: userEventList) {
            sendNotificationToUser(userEvent.getUser(), event, notificationType);
        }
    }


    private void sendNotificationToUser(User user, Event event, NotificationType notificationType) {
        Optional<UserNotification> userNotification = userNotificationRepository.findByUser(user);
        if(! userNotification.isPresent()) {
            throw new NotificationNotFoundException("Could not find user notification settings");
        }

        switch(notificationType) {
            case NEW_EVENT:
                if(userNotification.get().isNewEvent()) {
                    send(userNotification.get(), user, "", notificationType);
                }
                break;
            case USER_STATUS_CHANGED:
                if(userNotification.get().isStatusChanged()) {
                    send(userNotification.get(), user, "", notificationType);
                }
                break;
            case EVENT_DATA_CHANGED:
                if(userNotification.get().isDataChanged()) {
                    String message = event.getTitle() + " data has been changed\n" + event.toString();
                    send(userNotification.get(), user, message, notificationType);
                }
                break;
            case EVENT_CANCELED:
                if(userNotification.get().isEventCanceled()) {
                    send(userNotification.get(), user, "", notificationType);
                }
                break;
            case USER_UNINVITED:
                if(userNotification.get().isUninvitedUser()) {
                    send(userNotification.get(), user, "", notificationType);
                }
                break;
            case UPCOMING_EVENT:
                if(userNotification.get().isUpcomingEvents()) {
                    send(userNotification.get(), user, "", notificationType);
                }
                break;
        }

    }

    private void send(UserNotification userNotification,User user, String message, NotificationType notificationType) {
        if(userNotification.isPopUp()) {
            sendPopUp(user, message, notificationType);
        }
        else {
            sendEmail(user, message, notificationType);
        }
    }


    private void sendPopUp(User user, String message, NotificationType notificationType) {

    }


    private void sendEmail(User user, String message, NotificationType notificationType) {
        String email = user.getEmail();
        emailFacade.sendEmail(email,message,notificationType);
    }

}
