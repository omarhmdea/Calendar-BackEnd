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
@ToString
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

    public static EventDTO convertToEventDTO(Event event){
        EventDTO eventDTO = new EventDTO();
        eventDTO.setId(event.getId());
        eventDTO.setIsPublic(event.getIsPublic());
        eventDTO.setStart(event.getStart());
        eventDTO.setEnd(event.getEnd());
        eventDTO.setLocation(event.getLocation());
        eventDTO.setTitle(event.getTitle());
        eventDTO.setDescription(event.getDescription());
        eventDTO.setAttachments(event.getAttachments());
        eventDTO.setOrganizer(UserDTO.convertToUserDTO(event.getOrganizer()));
        eventDTO.setGuests(convertGuests(event.getGuests()));
        return eventDTO;
    }

    private static List<UserEventDTO> convertGuests(List<UserEvent> userEvents){
        return userEvents.stream().map(userEvent -> UserEventDTO.convertToUserEventDTO(userEvent)).collect(Collectors.toList());
    }
}
