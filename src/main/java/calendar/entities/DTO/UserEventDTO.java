package calendar.entities.DTO;

import calendar.entities.UserEvent;
import calendar.enums.Role;
import calendar.enums.Status;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class UserEventDTO {
    private UserDTO user;
    private Status status;
    private Role role;

    public UserEventDTO(UserEvent userevent){
        this.user = new UserDTO(userevent.getUser());
        this.status = userevent.getStatus();
        this.role = userevent.getRole();
    }
}
