//package calendar.service;
//import static org.mockito.Mockito.*;
//
//import calendar.entities.*;
//import calendar.entities.Credentials.EventCredentials;
//import calendar.entities.DTO.EventDTO;
//import calendar.enums.NotificationSettings;
//import calendar.enums.NotificationType;
//import calendar.enums.Role;
//import calendar.enums.Status;
//import calendar.repository.UserNotificationRepository;
//import calendar.utilities.EmailFacade;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.MockitoJUnitRunner;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.messaging.simp.SimpMessagingTemplate;
//
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Optional;
//
//@ExtendWith(MockitoExtension.class)
//public class NotificationServiceTest {
//
//    @Mock
//    private UserNotificationRepository userNotificationRepository;
//
//    @Mock
//    private EmailFacade emailFacade;
//
//    @Mock
//    private SimpMessagingTemplate simpMessagingTemplate;
//
//    @InjectMocks
//    private NotificationService notificationService;
//    Event event;
//    Event eventGuest1Admin;
//    Event eventUpdated;
//    Event eventInviteGuest3;
//
//    List<Event> guest1Events;
//
//    EventCredentials eventCredentials;
//
//    UserEvent userEventGuest1;
//    UserEvent userEventGuest2;
//    UserEvent userEventGuest3;
//
//    User user;
//    User guest1;
//    User guest2;
//    User guest3;
//
//
//    @BeforeEach
//    void setUp(){
//        user = new User(1, "Eden", "eden@gmail.com", "Eden123!@#", new HashSet<>());
//        guest1 = new User(2, "Omar", "omar@gmail.com", "Omar123!@#", new HashSet<>());
//        guest2 = new User(3, "Eli", "eli@gmail.com", "Eli123!@#", new HashSet<>());
//        guest3 = new User(4, "Maya", "maya@gmail.com", "Maya123!@#", new HashSet<>());
//
//        userEventGuest1 = new UserEvent(guest1, Status.APPROVED, Role.GUEST);
//        userEventGuest2 = new UserEvent(guest2,Status.REJECTED, Role.GUEST);
//        userEventGuest3 = new UserEvent(guest3,Status.TENTATIVE, Role.GUEST);
//
//        event = new Event(5,
//                false,
//                LocalDateTime.now().plusDays(1),
//                LocalDateTime.now().plusDays(4),
//                "Tel Aviv",
//                "Birthday",
//                "fun",
//                "none",
//                false,
//                new ArrayList<>(List.of(userEventGuest1, userEventGuest2)),
//                user);
//
//        guest1Events = new ArrayList<>();
//        guest1Events.add(event);
//
//        eventUpdated = new Event(5,
//                false,
//                LocalDateTime.now().plusDays(1),
//                LocalDateTime.now().plusDays(5),
//                "Tel Aviv",
//                "New",
//                "fun",
//                "none",
//                false,
//                new ArrayList<>(List.of(userEventGuest1, userEventGuest2)),
//                user);
//
//        userEventGuest1.setRole(Role.ADMIN);
//        List<UserEvent> users = new ArrayList<>();
//        users.add(userEventGuest1);
//        users.add(userEventGuest2);
//
//        eventGuest1Admin = new Event(5,
//                false,
//                LocalDateTime.now().plusDays(1),
//                LocalDateTime.now().plusDays(4),
//                "Tel Aviv",
//                "Birthday",
//                "fun",
//                "none",
//                false,
//                new ArrayList<>(List.of(userEventGuest1, userEventGuest2)),
//                user);
//
//        eventInviteGuest3 = new Event(5,
//                false,
//                LocalDateTime.now().plusDays(1),
//                LocalDateTime.now().plusDays(4),
//                "Tel Aviv",
//                "Birthday",
//                "fun",
//                "none",
//                false,
//                new ArrayList<>(List.of(userEventGuest1, userEventGuest2)),
//                user);
//
//
//
//        eventCredentials = new EventCredentials(eventUpdated);
//    }
//
//    @Test
//    public void testSendNotificationToGuestsEvent() {
//        // Create a mock event and a mock user
//        Event event = mock(Event.class);
//        User user = mock(User.class);
//
//        // Create a mock notification details object
//        NotificationDetails notificationDetails = new NotificationDetails("Test message", new EventDTO(), NotificationType.DELETE_EVENT);
//
//        // Create a mock UserNotification object
//        UserNotification userNotification = mock(UserNotification.class);
//        when(userNotification.getDeleteEvent()).thenReturn(NotificationSettings.EMAIL);
//
//        // When findUserNotification is called, return the mock UserNotification object
//        when(userNotificationRepository.findByUserId(user.getId())).thenReturn(Optional.of(userNotification));
//
//        // Call the sendNotificationToGuestsEvent method
//        notificationService.sendNotificationToGuestsEvent(notificationDetails);
//
//    }
//}