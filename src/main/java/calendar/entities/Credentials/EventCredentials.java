package calendar.entities.Credentials;

import calendar.entities.Event;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class EventCredentials {
    private int id;
    private Boolean isPublic;
    private LocalDateTime start;
    private LocalDateTime end;
    private String location;
    private String title;
    private String description;
    private String attachments;

    public static EventCredentials convertToEventCredentials(Event event){
        EventCredentials eventCredentials = new EventCredentials();
        eventCredentials.setId(event.getId());
        eventCredentials.setIsPublic(event.getIsPublic());
        eventCredentials.setStart(event.getStart());
        eventCredentials.setEnd(event.getEnd());
        eventCredentials.setLocation(event.getLocation());
        eventCredentials.setTitle(event.getTitle());
        eventCredentials.setDescription(event.getDescription());
        eventCredentials.setAttachments(event.getAttachments());
        return eventCredentials;
    }
}
