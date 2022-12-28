package calendar.controller;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import calendar.ResponsHandler.SuccessResponse;
import calendar.entities.Credentials.EventCredentials;
import calendar.entities.Credentials.UserNotificationCredentials;
import calendar.entities.DTO.EventDTO;
import calendar.entities.Event;
import calendar.entities.NotificationDetails;
import calendar.entities.User;
import calendar.entities.UserNotification;
import calendar.enums.NotificationSettings;
import calendar.enums.NotificationType;
import calendar.enums.Status;
import calendar.service.EventService;
import calendar.service.NotificationService;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
    UserNotification userNotification;

    static UserNotificationCredentials userNotificationCredentials;
    static EventCredentials eventCredentials;
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
//        user.setId(2);
//        user.setEmail("@");
//        user.setName("E");
//        user.setPassword("A123456");
    }

    @BeforeEach
    void newUserNotification(){
        userNotification = new UserNotification(user);
    }

    @BeforeAll
    static void newNotificationCredentials(){
        userNotificationCredentials =
                new UserNotificationCredentials(
                        NotificationSettings.POPUP,
                        NotificationSettings.NONE,
                        NotificationSettings.NONE,
                        NotificationSettings.NONE,
                        NotificationSettings.NONE,
                        NotificationSettings.NONE);
    }

    @BeforeAll
    static void newEventCredentials(){
        eventCredentials = new EventCredentials();
        eventCredentials.setStart(LocalDateTime.now().minusDays(1));
        eventCredentials.setEnd(LocalDateTime.now().plusDays(1));
        eventCredentials.setLocation("Jerusalem");
        eventCredentials.setTitle("final");
        eventCredentials.setDescription("hhhhh");
        eventCredentials.setAttachments("hhhhh");
    }
    @Test
    public void testApproveInvitation_Success() {
        // Arrange
        int eventId = 1;
        String email = "john.doe@example.com";
       // Event event = new Event(); // dummy event object
        EventDTO eventDTO = EventDTO.convertToEventDTO(event);
        when(eventService.approveOrRejectInvitation(email, eventId, Status.APPROVED)).thenReturn(event);

        // Act
        ResponseEntity<SuccessResponse<EventDTO>> result = controller.approveInvitation(eventId, email);

        // Assert
        assertEquals(200, result.getStatusCodeValue());
        assertEquals("Approved invitation successfully", result.getBody().getMessage());
        assertEquals(eventDTO, result.getBody().getData());
    }

//    @Test
//    public void testApproveInvitation_NotificationSent() {
//        // Arrange
//        int eventId = 2;
//        String email = "jane.doe@example.com";
//        Event event = new Event(); // dummy event object
//        EventDTO eventDTO = new EventDTO(event);
//        when(eventService.approveOrRejectInvitation(email, eventId, Status.APPROVED)).thenReturn(event);
//
//        // Act
//        ResponseEntity<SuccessResponse<EventDTO>> result = controller.approveInvitation(eventId, email);
//
//        // Assert
//        assertEquals(200, result.getStatusCodeValue());
//        assertEquals("Approved invitation successfully", result.getBody().getMessage());
//        assertEquals(eventDTO, result.getBody().getData());
//
//        // Verify that the notification was sent to the guests
//        verify(notificationService).sendNotificationToGuestsEvent(new NotificationDetails(email + " approve his invitation", eventDTO, NotificationType.USER_STATUS_CHANGED));
//    }

    @Test
    public void testRejectInvitation_Success() {
        // Arrange
        int eventId = 1;
        String email = "john.doe@example.com";
        // Event event = new Event(); // dummy event object
        EventDTO eventDTO = EventDTO.convertToEventDTO(event);
        when(eventService.approveOrRejectInvitation(email, eventId, Status.REJECTED)).thenReturn(event);

        // Act
        ResponseEntity<SuccessResponse<EventDTO>> result = controller.rejectInvitation(eventId, email);

        // Assert
        assertEquals(200, result.getStatusCodeValue());
        assertEquals("Rejected invitation successfully", result.getBody().getMessage());
        assertEquals(eventDTO, result.getBody().getData());
    }
}