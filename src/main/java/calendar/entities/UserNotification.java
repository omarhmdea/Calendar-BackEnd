package calendar.entities;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Getter
@Setter
@AllArgsConstructor
//@NoArgsConstructor
@EqualsAndHashCode
public class UserNotification {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private boolean isPopUp;
}
