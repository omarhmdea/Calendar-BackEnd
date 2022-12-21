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
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private int id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    private boolean isPopUp = true;
//    private boolean email = false;
    private boolean newEvent = false;
    private boolean statusChanged = false;
    private boolean dataChanged = false;
    private boolean eventCanceled = false;
    private boolean uninvitedUser = false;
    private boolean upcomingEvents = false;

    public UserNotification(User user){
        this.user = user;
    }
}
