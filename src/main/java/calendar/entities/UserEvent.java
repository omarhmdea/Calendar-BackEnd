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
@ToString
public class UserEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private int id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private Status status;
    private Role role;

    public UserEvent(User user, Status status, Role role) {
        this.user = user;
        this.status = status;
        this. role = role;
    }
}
