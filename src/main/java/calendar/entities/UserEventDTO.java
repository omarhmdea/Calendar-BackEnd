package calendar.entities;

import calendar.enums.Status;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class UserEventDTO {
    private int id;
    private String name;
    private String email;
    private Status status;

    public UserEventDTO(UserEvent userevent){
        this.id = userevent.getUser().getId();
        this.name = userevent.getUser().getName();
        this.email = userevent.getUser().getEmail();
        this.status = userevent.getStatus();
    }
}
