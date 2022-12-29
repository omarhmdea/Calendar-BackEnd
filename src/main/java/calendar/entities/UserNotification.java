package calendar.entities;

import calendar.enums.NotificationSettings;
import lombok.*;
import javax.persistence.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class UserNotification {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private int id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private NotificationSettings deleteEvent = NotificationSettings.NONE;
    private NotificationSettings updateEvent = NotificationSettings.NONE;
    private NotificationSettings invitation = NotificationSettings.NONE;
    private NotificationSettings removeGuest = NotificationSettings.NONE;
    private NotificationSettings userStatusChanged = NotificationSettings.NONE;
    private NotificationSettings upcomingEvent = NotificationSettings.NONE;
    private String timeZone;

    public UserNotification(User user){
        this.user = user;
    }
}
