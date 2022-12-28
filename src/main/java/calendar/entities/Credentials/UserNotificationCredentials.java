package calendar.entities.Credentials;

import calendar.entities.UserNotification;
import calendar.enums.NotificationSettings;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
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

    public UserNotificationCredentials(UserNotification userNotification){
        this.deleteEvent = userNotification.getDeleteEvent();
        this.updateEvent = userNotification.getUpdateEvent();
        this.invitation = userNotification.getInvitation();
        this.removeGuest = userNotification.getRemoveGuest();
        this.userStatusChanged = userNotification.getUserStatusChanged();
        this.upcomingEvent = userNotification.getUpcomingEvent();
        this.timeZone = userNotification.getTimeZone();
    }
}


