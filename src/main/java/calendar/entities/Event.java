package calendar.entities;

import lombok.*;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private Boolean isPublic = false;
    private LocalDateTime start;
    private LocalDateTime end;
    private String location;
    private String title;
    private String description;
    private String attachments;
    private  Boolean isDeleted = false;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<UserEvent> guests = new ArrayList<>();

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User organizer;

    public String toEmailString() {
        return title + " event :\n" +
                "Starts at " + start +
                "\nand ends at " + end +
                "\nWe'll meet at " + location +
                "\n to "+ description +
                "\n\nAttachments -'" + attachments;
    }

    public void addUserEvent(UserEvent userEvent){
        this.guests.add(userEvent);
    }

    public void removeUserEvent(UserEvent userEvent){
        this.guests.remove(userEvent);
    }
}
