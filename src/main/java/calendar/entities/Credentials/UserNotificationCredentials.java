package calendar.entities.Credentials;

import calendar.entities.UserNotification;
import calendar.enums.NotificationSettings;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserNotificationCredentials {
    private NotificationSettings deleteEvent;
    private NotificationSettings updateEvent;
    private NotificationSettings invitation;
    private NotificationSettings removeGuest;
    private NotificationSettings userStatusChanged;
    private NotificationSettings upcomingEvent;
    private String timeZone;

    public static UserNotificationCredentials convertToUserNotificationCredentials(UserNotification userNotification){
        UserNotificationCredentials userNotificationCredentials = new UserNotificationCredentials();
        userNotificationCredentials.setDeleteEvent(userNotification.getDeleteEvent());
        userNotificationCredentials.setUpdateEvent(userNotification.getUpdateEvent());
        userNotificationCredentials.setInvitation(userNotification.getInvitation());
        userNotificationCredentials.setRemoveGuest(userNotification.getRemoveGuest());
        userNotificationCredentials.setUserStatusChanged(userNotification.getUserStatusChanged());
        userNotificationCredentials.setUpcomingEvent(userNotification.getUpcomingEvent());
        userNotificationCredentials.setTimeZone(userNotification.getTimeZone());
        return userNotificationCredentials;
    }
}


