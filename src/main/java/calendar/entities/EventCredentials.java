package calendar.entities;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class EventCredentials {
    private int id;
    private Boolean isPublic;
    private LocalDateTime start;
    private LocalDateTime end;
    private String location;
    private String title;
    private String description;
    private String attachments;

    public EventCredentials(Event event){
        this.id = event.getId();
        this.isPublic = event.getIsPublic();
        this.start = event.getStart();
        this.end = event.getEnd();
        this.location = event.getLocation();
        this.title = event.getTitle();
        this.description = event.getDescription();
        this.attachments = event.getAttachments();
    }
}
