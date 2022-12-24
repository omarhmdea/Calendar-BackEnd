package calendar.entities;

import lombok.*;
import org.springframework.lang.NonNull;
import javax.persistence.*;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Pattern(regexp = "^[A-Za-z]+$")
    private String name;

    @NonNull
    @Column(unique = true)
    private String email;

    private String password;

    // ????
//    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<UserDTO> shared = new ArrayList<>();

    public void addToShared(User user){
        shared.add(new UserDTO(user));
    }
}
