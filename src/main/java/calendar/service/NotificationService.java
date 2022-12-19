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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {

    private static final Logger logger = LogManager.getLogger(NotificationService.class.getName());
    @Autowired
    private UserNotificationRepository userNotificationRepository;
    @Autowired
    private EmailFacade emailFacade;
    @Autowired
    private UserEventRepository userEventRepository;


    /**
     * Sending a notification to all users who belong to the event according to notificationType
     * @param event - event obj
     * @param notificationType - notificationType enum
     */
    public void sendNotification(Event event, NotificationType notificationType) {
        List<UserEvent> userEventList = userEventRepository.findByEvent(event);
        for(UserEvent userEvent: userEventList) {
            sendNotificationToUser(userEvent.getUser(), event, notificationType);
        }
    }

    /**
     * helper method that send a notification to specific user according to notificationType
     * @param event - event obj
     * @param notificationType - notificationType enum
     */
    private void sendNotificationToUser(User user, Event event, NotificationType notificationType) {
        Optional<UserNotification> userNotification = userNotificationRepository.findByUser(user);
        if(! userNotification.isPresent()) {
            throw new NotificationNotFoundException("Could not find user notification settings");
        }

        switch(notificationType) {
            case INVITE_GUEST:
                if(userNotification.get().isNewEvent()) {
                    send(userNotification.get(), user, "", notificationType);
                }
                break;
            case USER_STATUS_CHANGED:
                if(userNotification.get().isStatusChanged()) {
                    send(userNotification.get(), user, "", notificationType);
                }
                break;
            case UPDATE_EVENT:
                if(userNotification.get().isDataChanged()) {
                    String message = event.getTitle() + " data has been changed\n" + event.toString();
                    send(userNotification.get(), user, message, notificationType);
                }
                break;
            case DELETE_EVENT:
                if(userNotification.get().isEventCanceled()) {
                    String message = event.getTitle() + " has been canceled\n";
                    send(userNotification.get(), user, message, notificationType);
                }
                break;
            case REMOVE_GUEST:
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

    /**
     * helper method that send a notification to specific user according to notificationType by email or pop-up
     * @param message - email content
     * @param notificationType - notificationType enum
     * @param userNotification - notification setting
     * @param user - user
     */
    private void send(UserNotification userNotification, User user, String message, NotificationType notificationType) {
        if(userNotification.isPopUp()) {
            sendPopUp(user, message, notificationType);
            logger.info(notificationType + " Notification has been sent to " + user.getName() + " by pop-up");
        }
        else {
            sendEmail(user, message, notificationType);
            logger.info(notificationType + " Notification has been sent to " + user.getName() + " by email");

        }
    }


    private void sendPopUp(User user, String message, NotificationType notificationType) {

    }


    /**
     * Helper method that send email
     * @param user - user
 x    * @param message - email content
     * @param notificationType - notificationType enum
     */
    private void sendEmail(User user, String message, NotificationType notificationType) {
        String email = user.getEmail();
        emailFacade.sendEmail(email,message,notificationType);
    }

}
