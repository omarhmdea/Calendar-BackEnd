package calendar.entities;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class UserNotification {
    @Id
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    private boolean isPopUp;
    private boolean newEvent;
    private boolean statusChanged;
    private boolean dataChanged;
    private boolean eventCanceled;
    private boolean uninvitedUser;
    private boolean upcomingEvents;

}
