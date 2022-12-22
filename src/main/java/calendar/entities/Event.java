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

    private Boolean isPublic;
    private LocalDateTime start;
    private LocalDateTime end;
    private String location;
    private String title;
    private String description;
    private String attachments;
    private  Boolean isDeleted;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserEvent> users = new ArrayList<>();

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User organizer;

    public String toEmailString() {
        return title + " event :\n" +
                "Starts at" + start +
                "\n and ends at " + end +
                "\nWe'll meet at " + location +
                "\n to "+ description +
                "\n\nAttachments='" + attachments;
    }

    public UserEvent addUserEvent(UserEvent userEvent){
        this.users.add(userEvent);
        return userEvent;
    }

    public UserEvent removeUserEvent(UserEvent userEvent){
        this.users.remove(userEvent);
        return userEvent;
    }
}
