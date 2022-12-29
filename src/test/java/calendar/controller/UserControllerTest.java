package calendar.controller;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import calendar.responsHandler.SuccessResponse;
import calendar.entities.DTO.EventDTO;
import calendar.entities.Event;
import calendar.entities.User;
import calendar.enums.Status;
import calendar.service.EventService;
import calendar.service.NotificationService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @InjectMocks
    private UserController controller;

    @Mock
    private EventService eventService;

    @Mock
    private NotificationService notificationService;
    Event event;
    List<Event> events;

    static User user;

    @BeforeEach
    void newEvent(){
        event = new Event();
        event.setId(1);
        event.setStart(LocalDateTime.now().minusDays(1));
        event.setEnd(LocalDateTime.now().plusDays(1));
        event.setLocation("Tel Aviv");
        event.setTitle("Final");
        event.setDescription("hhhhh");
        event.setAttachments("hhhhh");
        event.setOrganizer(user);

        events = new ArrayList<>();
        events.add(event);
    }

    @BeforeAll
    static void newUser(){
        user = new User(2,"E", "e@gmail.com", "A123456", Set.of());
    }


    @Test
    public void testApproveInvitation_Success() {
        when(eventService.approveOrRejectInvitation(user.getEmail(), event.getId(), Status.APPROVED)).thenReturn(event);
        ResponseEntity<SuccessResponse<EventDTO>> result = controller.approveInvitation(event.getId(), user.getEmail());
        assertEquals(200, result.getStatusCodeValue());
    }
    @Test
    public void testApproveInvitation_Success1() {
        EventDTO eventDTO = EventDTO.convertToEventDTO(event);
        when(eventService.approveOrRejectInvitation(user.getEmail(), event.getId(), Status.APPROVED)).thenReturn(event);
        ResponseEntity<SuccessResponse<EventDTO>> result = controller.approveInvitation(event.getId(), user.getEmail());
        assertEquals(eventDTO, Objects.requireNonNull(result.getBody()).getData());
    }


    @Test
    public void testRejectInvitation_Success() {
        when(eventService.approveOrRejectInvitation(user.getEmail(), event.getId(), Status.REJECTED)).thenReturn(event);
        ResponseEntity<SuccessResponse<EventDTO>> result = controller.rejectInvitation(event.getId(), user.getEmail());
        assertEquals(200, result.getStatusCodeValue());
    }
    @Test
    public void testRejectInvitation_Success1() {
        EventDTO eventDTO = EventDTO.convertToEventDTO(event);
        when(eventService.approveOrRejectInvitation(user.getEmail(), event.getId(), Status.REJECTED)).thenReturn(event);
        ResponseEntity<SuccessResponse<EventDTO>> result = controller.rejectInvitation(event.getId(), user.getEmail());
        assertEquals(eventDTO, Objects.requireNonNull(result.getBody()).getData());
    }
}
