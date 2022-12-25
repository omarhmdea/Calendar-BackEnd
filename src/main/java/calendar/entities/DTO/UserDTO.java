package calendar.entities.DTO;

import calendar.entities.User;
import lombok.*;

@Getter
@Setter
@EqualsAndHashCode
public class UserDTO {
    private int id;
    private String name;
    private String email;

    public UserDTO(User user){
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
    }
}
