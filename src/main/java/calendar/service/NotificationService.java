package calendar.service;

import calendar.entities.Credentials.UserNotificationCredentials;
import calendar.entities.DTO.UserDTO;
import calendar.entities.DTO.UserEventDTO;
import calendar.entities.NotificationDetails;
import calendar.entities.User;
import calendar.entities.UserNotification;
import calendar.enums.NotificationSettings;
import calendar.enums.Role;
import calendar.enums.Status;
import calendar.exception.customException.NotificationNotFoundException;
import calendar.repository.UserNotificationRepository;
import calendar.utilities.EmailFacade;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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
    private SimpMessagingTemplate simpMessagingTemplate;

    public void sendNotificationToGuestsEvent(NotificationDetails notificationDetails) {
        List<UserEventDTO> usersInEvent = notificationDetails.getEvent().getGuests();
        usersInEvent.add(UserEventDTO.createUserEventDTO(notificationDetails.getEvent().getOrganizer(), Status.APPROVED, Role.ORGANIZER));

        for(UserEventDTO userEventDTO: usersInEvent) {
            if(userEventDTO.getStatus().equals(Status.APPROVED)) {
                UserNotification userNotification = findUserNotification(userEventDTO.getUser());
                UserDTO userDTO = userEventDTO.getUser();

                switch(notificationDetails.getNotificationType()) {
                    case DELETE_EVENT:
                        send(userNotification.getDeleteEvent(), userDTO, notificationDetails);
                        break;
                    case UPDATE_EVENT:
                        send(userNotification.getUpdateEvent(), userDTO, notificationDetails);
                        break;
                    case REMOVE_GUEST:
                        send(userNotification.getRemoveGuest(), userDTO, notificationDetails);
                        break;
                    case INVITE_GUEST:
                        send(userNotification.getInvitation(), userDTO, notificationDetails);
                        break;
                    case USER_STATUS_CHANGED:
                        send(userNotification.getUserStatusChanged(), userDTO, notificationDetails);
                        break;
                    case UPCOMING_EVENT:
                        send(userNotification.getUpcomingEvent(), userDTO, notificationDetails);
                        break;
                }
            }
        }
    }

    private void send(NotificationSettings notificationSettings, UserDTO user, NotificationDetails notificationDetails) {
        switch(notificationSettings) {
            case NONE:
                break;
            case POPUP:
                sendPopupNotification(user, notificationDetails);
                break;
            case EMAIL:
                sendEmailNotification(user, notificationDetails);
                break;
            case BOTH:
                sendPopupNotification(user, notificationDetails);
                sendEmailNotification(user, notificationDetails);
                break;
        }
    }

    private void sendPopupNotification(UserDTO user, NotificationDetails notificationDetails) {
        simpMessagingTemplate.convertAndSendToUser(user.getEmail(), "/private", notificationDetails.getMessage());
    }

    private void sendEmailNotification(UserDTO user, NotificationDetails notificationDetails) {
        logger.info(notificationDetails.getNotificationType() + " Notification has been sent to " + user.getName() + " by email");
        emailFacade.sendEmail(user.getEmail(), notificationDetails.getMessage(), notificationDetails.getNotificationType());
    }

    public UserNotification changeSettings(User user, UserNotificationCredentials updatedUserNotification) {
        logger.debug("Try to change settings to user " + user.getId());
        UserNotification userNotification = update(findUserNotification(user), updatedUserNotification);
        return userNotificationRepository.save(userNotification);
    }

    private UserNotification update(UserNotification originalNotification, UserNotificationCredentials updatedNotification) {
        originalNotification.setDeleteEvent(updatedNotification.getDeleteEvent());
        originalNotification.setUpdateEvent(updatedNotification.getUpdateEvent());
        originalNotification.setInvitation(updatedNotification.getInvitation());
        originalNotification.setRemoveGuest(updatedNotification.getRemoveGuest());
        originalNotification.setUserStatusChanged(updatedNotification.getUserStatusChanged());
        originalNotification.setUpcomingEvent(updatedNotification.getUpcomingEvent());
        originalNotification.setTimeZone(updatedNotification.getTimeZone());
        return originalNotification;
    }

    public UserNotification findUserNotification(User user) {
        Optional<UserNotification> userNotification = userNotificationRepository.findByUserId(user.getId());
        if(! userNotification.isPresent()) {
            throw new NotificationNotFoundException("There are no user notifications settings to the given user");
        }
        return userNotification.get();
    }

    public UserNotification findUserNotification(UserDTO user) {
        Optional<UserNotification> userNotification = userNotificationRepository.findByUserId(user.getId());
        if(! userNotification.isPresent()) {
            throw new NotificationNotFoundException("There are no user notifications settings to the given user");
        }
        return userNotification.get();
    }
}
