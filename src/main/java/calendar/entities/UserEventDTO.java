package calendar.entities;

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
    private Event event;
    private Status status;
    private Role role;

    public UserEventDTO(UserEvent userEvent){
        this.user = new UserDTO(userEvent.getUser());
        this.event = userEvent.getEvent();
        this.status = userEvent.getStatus();
        this.role = userEvent.getRole();
    }
}
