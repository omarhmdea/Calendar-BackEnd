package calendar.service;

import calendar.entities.*;
import calendar.entities.Credentials.UserNotificationCredentials;
import calendar.enums.NotificationSettings;
import calendar.enums.NotificationType;
import calendar.repository.EventRepository;
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
    private EventRepository eventRepository;


    /**
     * Sending a notification to all users who belong to the event according to notificationType
     * @param event - event obj
     * @param notificationType - notificationType enum
     */
    public void sendNotification(Event event, NotificationType notificationType) {
        List<UserEvent> userEventList = event.getGuests();
        for(UserEvent userEvent: userEventList) {
            sendNotificationToUser(userEvent.getUser(), event, notificationType);
        }
        // TODO : if it's invite we need to sent invitation only to the person that is invited
    }

    /**
     * helper method that send a notification to specific user according to notificationType
     * @param event - event obj
     * @param notificationType - notificationType enum
     */
    private void sendNotificationToUser(User user, Event event, NotificationType notificationType) {
        UserNotification userNotification = findUserNotification(user);

        switch(notificationType) {
            case DELETE_EVENT:
                send(userNotification.getDeleteEvent(), user, "", notificationType);
                break;
            case UPDATE_EVENT:
                send(userNotification.getUpdateEvent(), user, "", notificationType);
                break;
            case REMOVE_GUEST:
                send(userNotification.getRemoveGuest(), user, "", notificationType);
                break;
            case INVITE_GUEST:
                send(userNotification.getInvitation(), user, "", notificationType);
                break;
            case USER_STATUS_CHANGED:
                send(userNotification.getUserStatusChanged(), user, "", notificationType);
                break;
            case UPCOMING_EVENT:
                send(userNotification.getUpcomingEvent(), user, "", notificationType);
                break;
        }
    }

    /**
     * helper method that send a notification to specific user according to notificationType by email or pop-up
     * @param notificationSettings
     * @param user
     * @param message
     * @param notificationType
     */
    private void send(NotificationSettings notificationSettings, User user, String message, NotificationType notificationType){
        switch (notificationSettings){
            case NONE:
                break;
            case POPUP:
                sendPopUp(user, message, notificationType);
                break;
            case EMAIL:
                sendEmail(user, message, notificationType);
                break;
            case BOTH:
                sendPopUp(user, message, notificationType);
                sendEmail(user, message, notificationType);
                break;
        }
    }

    private void sendPopUp(User user, String message, NotificationType notificationType){
        logger.info(notificationType + " Notification has been sent to " + user.getName() + " by pop-up");

    }

    /**
     * Helper method that send email
     * @param user - user
     * @param message - email content
     * @param notificationType - notificationType enum
     */
    private void sendEmail(User user, String message, NotificationType notificationType) {
        logger.info(notificationType + " Notification has been sent to " + user.getName() + " by email");
        emailFacade.sendEmail(user.getEmail(),message,notificationType);
    }

    public UserNotification changeSettings(User user, UserNotificationCredentials updatedUserNotification){
        UserNotification userNotification = update(findUserNotification(user), updatedUserNotification);
        return userNotificationRepository.save(userNotification);
    }

    private UserNotification update(UserNotification originalNotification, UserNotificationCredentials updatedNotification){
        originalNotification.setDeleteEvent(updatedNotification.getDeleteEvent());
        originalNotification.setUpdateEvent(updatedNotification.getUpdateEvent());
        originalNotification.setInvitation(updatedNotification.getInvitation());
        originalNotification.setRemoveGuest(updatedNotification.getRemoveGuest());
        originalNotification.setUserStatusChanged(updatedNotification.getUserStatusChanged());
        originalNotification.setUpcomingEvent(updatedNotification.getUpcomingEvent());
        return originalNotification;
    }

    private UserNotification findUserNotification(User user){
        Optional<UserNotification> userNotification = userNotificationRepository.findByUser(user);
        if(!userNotification.isPresent()){
            throw new IllegalArgumentException("There are no user notifications settings to the given user");
        }
        return userNotification.get();
    }
}
