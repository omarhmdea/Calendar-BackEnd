package calendar.entities.Credentials;

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
}


