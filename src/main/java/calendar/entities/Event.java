package calendar.entities;

import lombok.*;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

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

    public String toEmailString() {
        return title + " event :\n" +
                "Starts at" + start +
                "\n and ends at " + end +
                "\nWe'll meet at " + location +
                "\n to "+ description +
                "\n\nAattachments='" + attachments;
    }
}
