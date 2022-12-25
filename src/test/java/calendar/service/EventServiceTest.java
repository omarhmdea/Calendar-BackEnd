package calendar.service;

import calendar.ResponsHandler.SuccessResponse;
import calendar.controller.EventController;
import calendar.entities.Event;
import calendar.entities.User;
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
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
@ExtendWith(MockitoExtension.class)
class EventServiceTest {
    @Mock
    EventRepository eventRepository;

//    @Mock
//    Us eventRepository;
    @Mock
    UserRepository userRepository;


    @Mock
    NotificationService notificationService;
    @InjectMocks
    EventService eventService;
    @InjectMocks
    ControllerAdvisor controllerAdvisor;

    Event event;

   static User user;

    int userId = 5;
    @BeforeEach
    void newEvent(){
        event = new Event();
        event.setId(5);
        event.setIsDeleted(false);
        event.setStart(LocalDateTime.now().plusDays(1));
        event.setEnd(LocalDateTime.now().plusDays(4));
        event.setLocation("Tel Aviv");
        event.setOrganizer(user);
       // event.setUsers();
//        event.setTitle("Final");
//        event.setDescription("hhhhh");
//        event.setAttachments("hhhhh");
    }

    @BeforeAll
    static void newUser(){
        user = new User();
        user.setEmail("w@w.com");
    }
    @Test
    void addNewEvent_checkAddEvent_successAddEevent() {
        given(eventRepository.save(event)).willReturn(event);
        assertEquals(event, eventService.addNewEvent(user,event));
    }
    @Test
    void addNewEvent_checkAddEvent_failAddEevent() {
        event.setStart(LocalDateTime.now().minusDays(1));
        assertThrows(IllegalArgumentException.class, ()-> eventService.addNewEvent(user,event));
    }


//    @Test
//    void setGuestAsAdmin() {
//    }
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