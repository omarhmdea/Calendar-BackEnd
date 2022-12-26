package calendar.service;

import calendar.ResponsHandler.SuccessResponse;
import calendar.controller.EventController;
import calendar.entities.Credentials.EventCredentials;
import calendar.entities.Event;
import calendar.entities.User;
import calendar.entities.UserEvent;
import calendar.enums.Role;
import calendar.enums.Status;
import calendar.exception.ControllerAdvisor;
import calendar.repository.EventRepository;
import calendar.repository.UserRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
@ExtendWith(MockitoExtension.class)
class EventServiceTest {
    @Mock
    EventRepository eventRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    NotificationService notificationService;

    @InjectMocks
    EventService eventService;
    @InjectMocks
    ControllerAdvisor controllerAdvisor;

    Event event;
    Event eventGuest1Admin;
    Event eventUpdated;
    EventCredentials eventCredentials;

    static User user;
    static User guest1;
    static User guest2;
    static User guest3;

    @BeforeEach
    void setUp(){
        user = new User(1, "Eden", "eden@gmail.com", "Eden123!@#", Set.of());
        guest1 = new User(2, "Omar", "omar@gmail.com", "Omar123!@#", Set.of());
        guest2 = new User(3, "Eli", "eli@gmail.com", "Eli123!@#", Set.of());
        guest3 = new User(4, "Maya", "maya@gmail.com", "Maya123!@#", Set.of());

        event = new Event(5,
                false,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(4),
                "Tel Aviv",
                "Birthday",
                "fun",
                "none",
                false,
                List.of(new UserEvent(guest1,Status.APPROVED, Role.GUEST),
                        new UserEvent(guest2,Status.REJECTED, Role.GUEST)),
                user);

        eventGuest1Admin = new Event(5,
                false,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(4),
                "Tel Aviv",
                "Birthday",
                "fun",
                "none",
                false,
                List.of(new UserEvent(guest1,Status.APPROVED, Role.ADMIN),
                        new UserEvent(guest2,Status.REJECTED, Role.GUEST)),
                user);

        eventUpdated = new Event(5,
                false,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(5),
                "Tel Aviv",
                "New",
                "fun",
                "none",
                false,
                List.of(),
                user);

        eventCredentials = new EventCredentials(eventUpdated);
    }

    @Test
    void addNewEvent_checkAddEvent_successAddEvent() {
        given(eventRepository.save(event)).willReturn(event);
        assertEquals(event, eventService.addNewEvent(user,event));
    }

    @Test
    void addNewEvent_checkAddEvent_failAddEvent() {
        event.setStart(LocalDateTime.now().minusDays(1));
        assertThrows(IllegalArgumentException.class, ()-> eventService.addNewEvent(user,event));
    }

    @Test
    void updateEvent_checkUpdateEvent_successUpdateEvent() {
        event.setTitle("new");
        given(eventRepository.save(event)).willReturn(event);
        assertEquals(event, eventService.addNewEvent(user,event));
    }

    @Test
    void updateEvent_checkUpdateEvent_failUpdateEvent() {
        event.setStart(LocalDateTime.now().minusDays(1));
        assertThrows(IllegalArgumentException.class, ()-> eventService.addNewEvent(user,event));
    }

    @Test
    void update_checkUpdateEvent_successUpdateEvent() {
        assertEquals(eventUpdated, eventService.update(event, eventCredentials));
    }

    @Test
    void setGuestAsAdmin_checkIfGuestIsAdmin_successSetAdmin() {
        given(userRepository.findByEmail(guest1.getEmail())).willReturn(Optional.of(guest1));
        given(eventRepository.save(event)).willReturn(eventGuest1Admin);
        assertEquals(eventGuest1Admin, eventService.setGuestAsAdmin(user, guest1.getEmail(), event));
    }

    @Test
    void setGuestAsAdmin_checkIfGuestIsAdmin_failSetAdmin() {
        given(userRepository.findByEmail(guest2.getEmail())).willReturn(Optional.of(guest2));
        assertThrows(IllegalArgumentException.class, ()-> eventService.setGuestAsAdmin(user,guest2.getEmail(),event));
    }
//
//    @Test
//    void updateEvent() {
//    }
//
//    @Test
//    void inviteGuestToEvent() {
//        given(userRepository.findByEmail(user.getEmail())).willReturn(Optional.ofNullable(user));
//       // eventService.inviteGuestToEvent(user,user.getEmail(),event);
//        assertEquals(Status.TENTATIVE,eventService.inviteGuestToEvent(user,user.getEmail(),event ).getUsers().get(user.getId()).getStatus());
//    }
//
//
//    @Test
//    void removeGuestFromEvent() {
//    }
//
//    @Test
//    void deleteEvent() {
//        eventService.addNewEvent(user,event);
//        event.setIsDeleted(true);
//        given(eventRepository.save(event)).willReturn(event);
//        assertEquals(true, eventService.deleteEvent(user,event).getIsDeleted());
//    }

//    @Test
//    void getCalendar() {
//        given(eventRepository.save(event)).willReturn(event);
//        assertEquals(,eventService.getCalendar(user,LocalDateTime.now().getMonth().getValue(),LocalDateTime.now().getYear()));
//    }

//    @Test
//    void showCalendar() {
//    }
//
//    @Test
//    void approveOrRejectInvitation() {
//        eventService.inviteGuestToEvent(user,"@",event);
//        given(eventRepository.save(event)).willReturn(event);
//       // event.getUsers().get(user.getId()).setStatus(Status.APPROVED);
//        assertEquals(Status.APPROVED,eventService.approveOrRejectInvitation(user,event, Status.APPROVED).getUsers().get(user.getId()).getStatus());
//    }
//
//    @Test
//    void testApproveOrRejectInvitation() {
//    }
}