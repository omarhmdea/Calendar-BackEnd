package calendar.entities.DTO;

import calendar.entities.UserEvent;
import calendar.enums.Role;
import calendar.enums.Status;
import lombok.*;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserEventDTO {
    private UserDTO user;
    private Status status;
    private Role role;

    public static UserEventDTO convertToUserEventDTO(UserEvent userevent){
        UserEventDTO userEventDTO = new UserEventDTO();
        userEventDTO.setUser(UserDTO.convertToUserDTO(userevent.getUser()));
        userEventDTO.setStatus(userevent.getStatus());
        userEventDTO.setRole(userevent.getRole());
        return userEventDTO;
    }

    public static UserEventDTO createUserEventDTO(UserDTO user, Status status, Role role){
        UserEventDTO userEventDTO = new UserEventDTO();
        userEventDTO.setUser(user);
        userEventDTO.setStatus(status);
        userEventDTO.setRole(role);
        return userEventDTO;
    }
}
