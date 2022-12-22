package calendar.entities;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode
public class EventDTO {
    private int id;
    private Boolean isPublic;
    private LocalDateTime start;
    private LocalDateTime end;
    private String location;
    private String title;
    private String description;
    private String attachments;

    public EventDTO(Event event){
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
