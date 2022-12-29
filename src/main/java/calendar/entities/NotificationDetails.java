package calendar.entities;

import calendar.entities.DTO.EventDTO;
import calendar.enums.NotificationType;
import lombok.*;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class NotificationDetails {
    private String message;
    private EventDTO event;
    private NotificationType notificationType;
}
