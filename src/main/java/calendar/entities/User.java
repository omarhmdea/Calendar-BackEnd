package calendar.entities;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Pattern;

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

    @Column(unique = true)
    private String email;
    private String password;
}
