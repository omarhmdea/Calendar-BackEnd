package calendar.entities;

import calendar.enums.Role;
import calendar.enums.Status;
import lombok.*;
import javax.persistence.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class UserEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private int id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    private Status status;
    private Role role;

    public UserEvent(User user, Event event, Status status, Role role) {
        this.user = user;
        this.event = event;
        this.status = status;
        this. role = role;
    }
}
