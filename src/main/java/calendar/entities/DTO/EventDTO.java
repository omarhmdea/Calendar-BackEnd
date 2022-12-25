package calendar.entities.DTO;

import calendar.entities.Event;
import calendar.entities.UserEvent;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
    private UserDTO organizer;
    private List<UserEventDTO> guests;

    public EventDTO(Event event){
        this.id = event.getId();
        this.isPublic = event.getIsPublic();
        this.start = event.getStart();
        this.end = event.getEnd();
        this.location = event.getLocation();
        this.title = event.getTitle();
        this.description = event.getDescription();
        this.attachments = event.getAttachments();
        this.organizer = new UserDTO(event.getOrganizer());
        this.guests = convertGuests(event.getGuests());
    }

    private List<UserEventDTO> convertGuests(List<UserEvent> userEvents){
        return userEvents.stream().map(userEvent -> new UserEventDTO(userEvent)).collect(Collectors.toList());
    }
}
