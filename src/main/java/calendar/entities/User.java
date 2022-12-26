package calendar.entities;

import lombok.*;
import org.springframework.lang.NonNull;
import javax.persistence.*;
import javax.validation.constraints.Pattern;
import java.util.*;

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

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name="shared_calendars", joinColumns=@JoinColumn(name="user_id"))
    @Column(name="can_view")
    private Set<Integer> shared = new HashSet<>();

    public void addToShared(User user){
        shared.add(user.getId());
    }

    public User (String name, String email, String password){
        this.name = name;
        this.email = email;
        this.password = password;
    }
}
