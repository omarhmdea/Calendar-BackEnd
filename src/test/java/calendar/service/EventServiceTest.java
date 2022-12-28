package calendar.service;

import calendar.entities.Credentials.EventCredentials;
import calendar.entities.Event;
import calendar.entities.User;
import calendar.entities.UserEvent;
import calendar.enums.Role;
import calendar.enums.Status;
import calendar.exception.ControllerAdvisor;
import calendar.repository.EventRepository;
import calendar.repository.UserRepository;
import org.assertj.core.util.Sets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

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
    Event eventInviteGuest3;

    List<Event> guest1Events;

    EventCredentials eventCredentials;

    UserEvent userEventGuest1;
    UserEvent userEventGuest2;
    UserEvent userEventGuest3;

    User user;
    User guest1;
    User guest2;
    User guest3;


    @BeforeEach
    void setUp(){
        user = new User(1, "Eden", "eden@gmail.com", "Eden123!@#", new HashSet<>());
        guest1 = new User(2, "Omar", "omar@gmail.com", "Omar123!@#", new HashSet<>());
        guest2 = new User(3, "Eli", "eli@gmail.com", "Eli123!@#", new HashSet<>());
        guest3 = new User(4, "Maya", "maya@gmail.com", "Maya123!@#", new HashSet<>());

        userEventGuest1 = new UserEvent(guest1,Status.APPROVED, Role.GUEST);
        userEventGuest2 = new UserEvent(guest2,Status.REJECTED, Role.GUEST);
        userEventGuest3 = new UserEvent(guest3,Status.TENTATIVE, Role.GUEST);

        event = new Event(5,
                false,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(4),
                "Tel Aviv",
                "Birthday",
                "fun",
                "none",
                false,
                new ArrayList<>(List.of(userEventGuest1, userEventGuest2)),
                user);

        guest1Events = new ArrayList<>();
        guest1Events.add(event);

        eventUpdated = new Event(5,
                false,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(5),
                "Tel Aviv",
                "New",
                "fun",
                "none",
                false,
               new ArrayList<>(List.of(userEventGuest1, userEventGuest2)),
                user);

        userEventGuest1.setRole(Role.ADMIN);
        List<UserEvent> users = new ArrayList<>();
        users.add(userEventGuest1);
        users.add(userEventGuest2);

        eventGuest1Admin = new Event(5,
                false,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(4),
                "Tel Aviv",
                "Birthday",
                "fun",
                "none",
                false,
                new ArrayList<>(List.of(userEventGuest1, userEventGuest2)),
                user);

        eventInviteGuest3 = new Event(5,
                false,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(4),
                "Tel Aviv",
                "Birthday",
                "fun",
                "none",
                false,
                new ArrayList<>(List.of(userEventGuest1, userEventGuest2)),
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
        given(eventRepository.save(eventUpdated)).willReturn(eventUpdated);
        assertEquals(eventUpdated, eventService.updateEvent(user, event, eventCredentials));
    }

//    @Test
//    void updateEvent_checkUpdateEvent_failUpdateEvent() {
//        event.setStart(LocalDateTime.now().minusDays(1));
//        assertThrows(IllegalArgumentException.class, ()-> eventService.addNewEvent(user,event));
//    }

    @Test
    void update_checkUpdateEvent_successUpdateEvent() {
        assertEquals(eventUpdated, eventService.update(event, eventCredentials));
    }

//    @Test
//    void setGuestAsAdmin_checkIfGuestIsAdmin_successSetAdmin() {
//        given(userRepository.findByEmail(guest1.getEmail())).willReturn(Optional.of(guest1));
//        given(eventRepository.save(eventGuest1Admin)).willReturn(eventGuest1Admin);
//        assertEquals(eventGuest1Admin, eventService.setGuestAsAdmin(user, guest1.getEmail(), event));
//    }

    @Test
    void setGuestAsAdmin_checkIfGuestIsAdmin_failSetAdmin() {
        given(userRepository.findByEmail(guest2.getEmail())).willReturn(Optional.of(guest2));
        assertThrows(IllegalArgumentException.class, ()-> eventService.setGuestAsAdmin(user,guest2.getEmail(),event));
    }

//    @Test
//    void deleteEvent_checkIfEventWasDeleted_successSoftDelete(){
//        event.setIsDeleted(true);
//        given(eventRepository.save(event)).willReturn(event);
//        assertEquals(event, eventService.deleteEvent(user, event));
//    }

//    @Test
//    void inviteGuestToEvent_checkIfPart_successInvite(){
//        given(userRepository.findByEmail(guest3.getEmail())).willReturn(Optional.ofNullable(guest3));
//        given(eventRepository.save(eventInviteGuest3)).willReturn(eventInviteGuest3);
//        assertEquals(eventInviteGuest3, eventService.inviteGuestToEvent(user, guest3.getEmail(), event));
//    }

    @Test
    void inviteGuestToEvent_checkIfPart_failInvite(){
//        given(userRepository.findByEmail(user.getEmail())).willReturn(Optional.ofNullable(user));
        assertThrows(IllegalArgumentException.class, ()-> eventService.inviteGuestToEvent(user,user.getEmail(),event));
    }

    @Test
    void inviteGuestToEvent_checkIfEventOrganizer_failInvite(){
        given(userRepository.findByEmail(guest2.getEmail())).willReturn(Optional.ofNullable(guest2));
        assertThrows(IllegalArgumentException.class, ()-> eventService.inviteGuestToEvent(user, guest2.getEmail(), event));
    }


//    @Test
//    void removeGuestToEvent_removeInvitedUser_successRemove(){
//        given(userRepository.findByEmail(guest2.getEmail())).willReturn(Optional.ofNullable(guest2));
//        given(eventRepository.save(event)).willReturn(event);
//        assertEquals(guest2, eventService.removeGuestFromEvent(user, guest2.getEmail(), event));
//    }

    @Test
    void removeGuestToEvent_removeUninvitedUser_failRemove(){
        given(userRepository.findByEmail(guest3.getEmail())).willReturn(Optional.ofNullable(guest3));
        assertThrows(IllegalArgumentException.class, ()-> eventService.removeGuestFromEvent(user,guest3.getEmail(),event));
    }

    @Test
    void removeGuestToEvent_removeOrganizer_failRemove(){
        given(userRepository.findByEmail(user.getEmail())).willReturn(Optional.ofNullable(user));
        assertThrows(IllegalArgumentException.class, ()-> eventService.removeGuestFromEvent(user,user.getEmail(),event));
    }

//    @Test
//    void removeGuestToEvent_isOrganizer_failRemove(){
//        given(userRepository.findByEmail(user.getEmail())).willReturn(Optional.ofNullable(user));
//        assertThrows(IllegalArgumentException.class, ()-> eventService.removeGuestFromEvent(user, user.getEmail(),event));
//    }

    @Test
    void getCalendar_existingUserWithEvents_successGet(){
        given(eventRepository.findAll()).willReturn(guest1Events);
        assertEquals(guest1Events, eventService.getCalendar(guest1, LocalDateTime.now().plusDays(1).getMonth().getValue(), LocalDateTime.now().plusDays(4).getYear()));
    }

    @Test
    void getCalendar_existingUserWithNoEvents_failGet(){
        given(eventRepository.findAll()).willReturn(List.of());
        assertThrows(IllegalArgumentException.class, ()-> eventService.getCalendar(guest2, LocalDateTime.now().plusDays(1).getMonth().getValue(), LocalDateTime.now().plusDays(4).getYear()));
    }

    @Test
    void showCalendar_canViewDifferentUser_successShow(){

    }

    @Test
    void showCalendar_cannotViewDifferentUser_failShow(){

        given(userRepository.findById(guest1.getId())).willReturn(Optional.ofNullable(guest1));
        assertThrows(IllegalArgumentException.class, ()-> eventService.showCalendar(guest2, guest1.getId(),
                LocalDateTime.now().getMonth().getValue(), LocalDateTime.now().getYear()));
    }



    @Test
    public void testDeleteEvent() {
        // Set up mock data
        User user = new User();
        Event eventToDelete = new Event();
        when(eventRepository.save(any(Event.class))).thenReturn(eventToDelete);

        // Call the deleteEvent method
        Event result = eventService.deleteEvent(user, eventToDelete);

        // Verify the result
        assertTrue(result.getIsDeleted());
    }



//    @Test
//    public void testShareCalendarWithNonExistentUser() {
////        User user = new User("john@example.com", "John", "Doe");
////        User userToShare = new User("jane@example.com", "Jane", "Doe");
//        guest1.addToShared(user);
//        when(userRepository.findById(guest1.getId())).thenReturn(Optional.empty());
//
//        User result = eventService.shareCalendar(user, guest1.getEmail());
//
//        verify(userRepository, never()).save(guest1);
//        assertNull(result);
//    }

//    @Test
//    public void testShareCalendarWithNonExistentUser() {
//        User user = new User("john@example.com", "John", "Doe");
//        User userToShare = new User("jane@example.com", "Jane", "Doe");
//        userToShare.addToShared(user);
//        when(userRepository.findById("jane@example.com")).thenReturn(Optional.empty());
//
//        User result = eventService.shareCalendar(user, "jane@example.com");
//
//        verify(userRepository, never()).save(userToShare);
//        assertNull(result);
//    }
//

//    @Test
//    public void testShareCalendarWithUserThatAlreadyHasCalendarShared() {
//        User user = new User("john@example.com", "John", "Doe");
//        User userToShare = new User(9,"jane@example.com", "Jane", "Doe",new HashSet<>());
//        userToShare.addToShared(user);
//        when(userRepository.findById(userToShare.getId())).thenReturn(Optional.of(userToShare));
//
//        assertThrows(IllegalArgumentException.class,()->eventService.shareCalendar(user, "jane@example.com"));
//
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