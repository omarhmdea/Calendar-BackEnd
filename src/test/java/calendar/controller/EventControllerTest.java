package calendar.controller;

import calendar.responsHandler.SuccessResponse;
import calendar.entities.*;
import calendar.entities.Credentials.EventCredentials;
import calendar.entities.Credentials.UserNotificationCredentials;
import calendar.entities.DTO.EventDTO;
import calendar.entities.DTO.UserDTO;
import calendar.enums.NotificationSettings;
import calendar.enums.Status;
import calendar.service.EventService;
import calendar.service.NotificationService;
import calendar.utilities.EmailFacade;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;


@ExtendWith(MockitoExtension.class)
class EventControllerTest {
    @Mock
    EventService eventService;
    @Mock
    NotificationService notificationService;
    @Mock
    EmailFacade emailFacade;
    @InjectMocks
    EventController eventController;

    Event event;
    List<Event> events;
    UserNotification userNotification;

    UserNotificationCredentials userNotificationCredentials;
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
        user = new User(2,"E", "e@gmail.com", "A123456", new HashSet<>());
    }

    @BeforeEach
    void newUserNotification(){
        userNotification = new UserNotification(user);
    }

    @BeforeAll
    void newNotificationCredentials(){
        userNotificationCredentials = UserNotificationCredentials.convertToUserNotificationCredentials(userNotification);
        userNotificationCredentials.setDeleteEvent(NotificationSettings.POPUP);
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
    void addNewEvent_checkAddEvent_responseOkTheEventCreated() {
         given(eventService.addNewEvent(user, event)).willReturn(event);
         EventDTO eventDTO = EventDTO.convertToEventDTO(event);
         ResponseEntity<SuccessResponse<EventDTO>> successAddNewEvent = eventController.addNewEvent(user, event);
         assertEquals(eventDTO, Objects.requireNonNull(successAddNewEvent.getBody()).getData());
    }

    @Test
    void addNewEvent_checkAddEvent_responseOkTheStatusCode() {
        given(eventService.addNewEvent(user, event)).willReturn(event);
        ResponseEntity<SuccessResponse<EventDTO>> successAddNewEvent = eventController.addNewEvent(user, event);
        assertEquals(HttpStatus.OK, successAddNewEvent.getStatusCode());
    }

    @Test
    void updateEvent_checkUpdate_responseOkTheEventUpdated() {
        event.setLocation(eventCredentials.getLocation());
        given(eventService.updateEvent(user,event,eventCredentials)).willReturn(event);
        EventDTO eventDTO = EventDTO.convertToEventDTO(event);
        ResponseEntity<SuccessResponse<EventDTO>> successUpdatedEvent = eventController.updateEvent(user,event,eventCredentials);
        assertEquals(eventDTO, Objects.requireNonNull(successUpdatedEvent.getBody()).getData());
    }

    @Test
    void updateEvent_checkUpdate_responseOkTheStatusCode() {
        event.setLocation(eventCredentials.getLocation());
        given(eventService.updateEvent(user,event,eventCredentials)).willReturn(event);
        ResponseEntity<SuccessResponse<EventDTO>> successUpdatedEvent = eventController.updateEvent(user,event,eventCredentials);
        assertEquals(HttpStatus.OK, successUpdatedEvent.getStatusCode());
    }

    @Test
    void setGuestAsAdmin_checkSetGuestAsAdmin_responseOkAndUpdateGuestToAdmin() {
        given(eventService.setGuestAsAdmin(user,"r@r.com", event)).willReturn(event);
        EventDTO eventDTO = EventDTO.convertToEventDTO(event);
        ResponseEntity<SuccessResponse<EventDTO>> successSetGuestAsAdmin = eventController.setGuestAsAdmin(user, event,"r@r.com");
        assertEquals(eventDTO, Objects.requireNonNull(successSetGuestAsAdmin.getBody()).getData());
    }

    @Test
    void setGuestAsAdmin_checkSetGuestAsAdmin_responseOkTheStatusCode() {
        given(eventService.setGuestAsAdmin(user,"r@r.com", event)).willReturn(event);
        ResponseEntity<SuccessResponse<EventDTO>> successSetGuestAsAdmin = eventController.setGuestAsAdmin(user, event,"r@r.com");
        assertEquals(HttpStatus.OK, successSetGuestAsAdmin.getStatusCode());
    }

    @Test
    void deleteEvent_checkDelete_responseOkTheStatusCode() {
        given(eventService.deleteEvent(user, event)).willReturn(event);
        assertEquals(HttpStatus.NO_CONTENT,  eventController.deleteEvent(user, event).getStatusCode());
    }

    @Test
    void inviteGuestToEvent_checkInviteGuestToEvent_responseOkAndInviteGuestToEvent() {
        given(eventService.inviteGuestToEvent(user,"r@r.com", event)).willReturn(event);
        EventDTO eventDTO = EventDTO.convertToEventDTO(event);
        ResponseEntity<SuccessResponse<EventDTO>> successInviteGuestToEvent = eventController.inviteGuestToEvent(user, event,"r@r.com");
        assertEquals(eventDTO, Objects.requireNonNull(successInviteGuestToEvent.getBody()).getData());
    }

    @Test
    void inviteGuestToEvent_checkInviteGuestToEvent_responseOkTheStatusCode() {
        given(eventService.inviteGuestToEvent(user,"r@r.com", event)).willReturn(event);
       // given(emailFacade.sendInvitation("elchananm10@gmail.com", event));
        doNothing().when(emailFacade).sendInvitation("r@r.com",event);
        ResponseEntity<SuccessResponse<EventDTO>> successInviteGuestToEvent = eventController.inviteGuestToEvent(user, event,"r@r.com");
        assertEquals(HttpStatus.OK, successInviteGuestToEvent.getStatusCode());
    }

    @Test
    void removeGuestFromEvent_checkRemoveGuestFromEvent_responseOkAndRemoveGuestFromEvent() {
        given(eventService.removeGuestFromEvent(user,"r@r.com",event)).willReturn(user);
        UserDTO userDTO = UserDTO.convertToUserDTO(user);
        ResponseEntity<SuccessResponse<UserDTO>> successRemoveGuestFromEvent = eventController.removeGuestFromEvent(user,event,"r@r.com");
        assertEquals(userDTO, Objects.requireNonNull(successRemoveGuestFromEvent.getBody()).getData());
    }

    @Test
    void removeGuestFromEvent_checkRemoveGuestFromEvent_responseOkTheStatusCode() {
        given(eventService.removeGuestFromEvent(user,"r@r.com",event)).willReturn(user);
        ResponseEntity<SuccessResponse<UserDTO>> successRemoveGuestFromEvent = eventController.removeGuestFromEvent(user,event,"r@r.com");
        assertEquals(HttpStatus.OK, successRemoveGuestFromEvent.getStatusCode());
    }

    @Test
    void showCalendar_checkShowCalendar_responseOkWithTheCorrectEvents() {
        given(eventService.showCalendar(user,user.getId(), LocalDate.now().getMonth().getValue(), LocalDate.now().getYear())).willReturn(events);
        ResponseEntity<SuccessResponse<List<EventDTO>>> successShowCalendarEvents = eventController.showCalendar(user,user.getId(), LocalDate.now().getMonth().getValue(), LocalDate.now().getYear());
        assertEquals(EventDTO.convertToEventDTO(events.get(0)), Objects.requireNonNull(successShowCalendarEvents.getBody()).getData().get(0));
    }

    @Test
    void showCalendar_checkShowCalendar_responseOkEqualsTheStatusCode() {
        given(eventService.showCalendar(user,user.getId(), LocalDate.now().getMonth().getValue(), LocalDate.now().getYear())).willReturn(events);
        ResponseEntity<SuccessResponse<List<EventDTO>>> successShowCalendarEvents = eventController.showCalendar(user,user.getId(), LocalDate.now().getMonth().getValue(), LocalDate.now().getYear());
        assertEquals(HttpStatus.OK, successShowCalendarEvents.getStatusCode());
    }

    @Test
    void approveInvitation_checkIfApproveInvitation_responseOkAndUpdateTheStatusToApprove() {
        given(eventService.approveOrRejectInvitation(user, event.getId(), Status.APPROVED)).willReturn(event);
        EventDTO eventDTO = EventDTO.convertToEventDTO(event);
        ResponseEntity<SuccessResponse<EventDTO>> successApproveInvitation = eventController.approveInvitation(user, event.getId());
        assertEquals(eventDTO, Objects.requireNonNull(successApproveInvitation.getBody()).getData());
    }
    @Test
    void approveInvitation_checkIfApproveInvitation_responseOkTheStatusCode() {
        given(eventService.approveOrRejectInvitation(user, event.getId(), Status.APPROVED)).willReturn(event);
        ResponseEntity<SuccessResponse<EventDTO>> successApproveInvitation = eventController.approveInvitation(user, event.getId());
        assertEquals(HttpStatus.OK, successApproveInvitation.getStatusCode());

    }
    @Test
    void rejectInvitation_checkIfRejectInvitation_updateTheStatusToReject() {
        given(eventService.approveOrRejectInvitation(user, event.getId(),Status.REJECTED)).willReturn(event);
        ResponseEntity<SuccessResponse<EventDTO>> successRejectInvitation = eventController.rejectInvitation(user, event.getId());
        assertEquals(EventDTO.convertToEventDTO(event), Objects.requireNonNull(successRejectInvitation.getBody()).getData());
    }
    @Test
    void rejectInvitation_checkIfRejectInvitation_responseOkEqualsStatusCode() {
        given(eventService.approveOrRejectInvitation(user, event.getId(),Status.REJECTED)).willReturn(event);
        ResponseEntity<SuccessResponse<EventDTO>> successRejectInvitation = eventController.rejectInvitation(user, event.getId());
        assertEquals(HttpStatus.OK, successRejectInvitation.getStatusCode());
    }

    @Test
    void changeSettings_checkIfChangeSettings_updateTheSettings() {
        userNotification.setDeleteEvent(NotificationSettings.POPUP);
        given(notificationService.changeSettings(user,userNotificationCredentials)).willReturn(userNotification);
        ResponseEntity<SuccessResponse<UserNotificationCredentials>> successChangeSettings = eventController.changeSettings(user, userNotificationCredentials);
        assertEquals(userNotificationCredentials.getDeleteEvent(), Objects.requireNonNull(successChangeSettings.getBody()).getData().getDeleteEvent());
    }

    @Test
    void changeSettings_checkIfChangeSettings_responseOkEqualStatusCode() {
        userNotification.setDeleteEvent(NotificationSettings.POPUP);
        given(notificationService.changeSettings(user,userNotificationCredentials)).willReturn(userNotification);
        ResponseEntity<SuccessResponse<UserNotificationCredentials>> successChangeSettings = eventController.changeSettings(user, userNotificationCredentials);
        assertEquals(HttpStatus.OK, successChangeSettings.getStatusCode());
    }
}