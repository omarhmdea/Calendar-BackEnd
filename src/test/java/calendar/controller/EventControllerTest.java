package calendar.controller;

import calendar.ResponsHandler.SuccessResponse;
import calendar.entities.*;
import calendar.entities.Credentials.EventCredentials;
import calendar.entities.Credentials.UserNotificationCredentials;
import calendar.entities.DTO.EventDTO;
import calendar.entities.DTO.UserDTO;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;


@ExtendWith(MockitoExtension.class)
class EventControllerTest {
    @Mock
    EventService eventService;
    @Mock
    NotificationService notificationService;
    @InjectMocks
    EventController eventController;

    Event event;

    List<Event> events;

    //UserDTO userDTO;

   // static EventDTO eventDTO;
    UserNotificationCredentials userNotificationCredentials;

    static EventCredentials eventCredentials;

    static User user;
    int userId = 3;
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
    }
    @BeforeAll
    static void newUser(){
        user = new User();
        user.setEmail("@");
        user.setName("E");
        user.setPassword("A123456");
        user.setId(2);
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
         EventDTO eventDTO = new EventDTO(event);
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
        EventDTO eventDTO = new EventDTO(event);
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
        EventDTO eventDTO = new EventDTO(event);
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
    void deleteEvent_checkDelete_responseOkTheEventDeleted() {
        given(eventService.deleteEvent(user, event)).willReturn(event);
        EventDTO eventDTO = new EventDTO(event);
        ResponseEntity<SuccessResponse<EventDTO>> successDeletedEvent = eventController.deleteEvent(user, event);
        assertEquals(eventDTO, Objects.requireNonNull(successDeletedEvent.getBody()).getData());
    }
    @Test
    void deleteEvent_checkDelete_responseOkTheStatusCode() {
        given(eventService.deleteEvent(user, event)).willReturn(event);
        ResponseEntity<SuccessResponse<EventDTO>> successDeletedEvent = eventController.deleteEvent(user, event);
        assertEquals(HttpStatus.OK, successDeletedEvent.getStatusCode());
    }
    @Test
    void inviteGuestToEvent_checkInviteGuestToEvent_responseOkAndInviteGuestToEvent() {
        given(eventService.inviteGuestToEvent(user,"r@r.com", event)).willReturn(event);
        EventDTO eventDTO = new EventDTO(event);
        ResponseEntity<SuccessResponse<EventDTO>> successInviteGuestToEvent = eventController.inviteGuestToEvent(user, event,"r@r.com");
        assertEquals(eventDTO, Objects.requireNonNull(successInviteGuestToEvent.getBody()).getData());
    }
    @Test
    void inviteGuestToEvent_checkInviteGuestToEvent_responseOkTheStatusCode() {
        given(eventService.inviteGuestToEvent(user,"r@r.com", event)).willReturn(event);
        ResponseEntity<SuccessResponse<EventDTO>> successInviteGuestToEvent = eventController.inviteGuestToEvent(user, event,"r@r.com");
        assertEquals(HttpStatus.OK, successInviteGuestToEvent.getStatusCode());
    }
    @Test
    void removeGuestFromEvent_checkRemoveGuestFromEvent_responseOkAndRemoveGuestFromEvent() {
        given(eventService.removeGuestFromEvent(user,"r@r.com",event)).willReturn(user);
        UserDTO userDTO = new UserDTO(user);
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
    void approveInvitation_checkIfApproveInvitation_responseOkAndUpdateTheStatusToApprove() {
        given(eventService.approveOrRejectInvitation(user, event.getId(), Status.APPROVED)).willReturn(event);
        EventDTO eventDTO = new EventDTO(event);
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
    void rejectInvitation_checkIfRejectInvitation_responseOkAndUpdateTheStatusToReject() {
        given(eventService.approveOrRejectInvitation(user, event.getId(),Status.REJECTED)).willReturn(event);
        EventDTO eventDTO = new EventDTO(event);
        ResponseEntity<SuccessResponse<EventDTO>> successRejectInvitation = eventController.rejectInvitation(user, event.getId());
        assertEquals(eventDTO, Objects.requireNonNull(successRejectInvitation.getBody()).getData());
    }
    @Test
    void rejectInvitation_checkIfRejectInvitation_responseOkTheStatusCode() {
        given(eventService.approveOrRejectInvitation(user, event.getId(),Status.REJECTED)).willReturn(event);
        ResponseEntity<SuccessResponse<EventDTO>> successRejectInvitation = eventController.rejectInvitation(user, event.getId());
        assertEquals(HttpStatus.OK, successRejectInvitation.getStatusCode());
    }

//    @Test
//    void changeSettings() {
//        given(notificationService.changeSettings(user,userNotificationCredentials)).willReturn(userNotification);
//        UserNotification userNotification = new UserNotification(userNotificationCredentials);
//        ResponseEntity<SuccessResponse<UserNotification>> successChangeSettings = eventController.changeSettings(user,userNotificationCredentials);
//        assertEquals(HttpStatus.OK, successChangeSettings.getStatusCode());
//        assertEquals(event, Objects.requireNonNull(successChangeSettings.getBody()).getData());
//    }

//    //check again
//    // maybe we need to create list of events
//    @Test
//    void getCalendar_checkGetCalendar_responseOkWithTheCorrectEvents() {
//        given(eventService.getCalendar(user,12,2022)).willReturn(events);
//        ResponseEntity<SuccessResponse<List<Event>>> successGetCalendarEvents = eventController.getCalendar(user,12,2022);
//        assertEquals(HttpStatus.OK, successGetCalendarEvents.getStatusCode());
//        assertEquals(events, Objects.requireNonNull(successGetCalendarEvents.getBody()).getData());
//    }
//    //check again
//    @Test
//    void showCalendar_checkShowCalendar_responseOkWithTheCorrectEvents() {
//        given(eventService.showCalendar(1,12,2022)).willReturn(events);
//        ResponseEntity<SuccessResponse<List<Event>>> successShowCalendarEvents = eventController.showCalendar(user,12,2022,1);
//        assertEquals(HttpStatus.OK, successShowCalendarEvents.getStatusCode());
//        assertEquals(events, Objects.requireNonNull(successShowCalendarEvents.getBody()).getData());
//    }
    //    @Test
//    void showCalendar_(){
//        given(eventService.showCalendar(user,"r@r.com",event)).willReturn(user);
//
//    }
//    @Test
//    void showCalendar_checkShowCalendar_responseOkWithTheCorrectEvents() {
//        given(eventService.showCalendar(user,userId,12,2022)).willReturn(events);
//        List<EventDTO> eventsDTO = new EventDTO(events);
//        ResponseEntity<SuccessResponse<List<EventDTO>>> successGetCalendarEvents = eventController.showCalendar(user,userId,12,2022);
//        assertEquals(HttpStatus.OK, successGetCalendarEvents.getStatusCode());
//        assertEquals(eventsDTO, Objects.requireNonNull(successGetCalendarEvents.getBody()).getData());
//    }
//






}
//========================================================================================================================
//package docSharing.controller;
//
//        import com.google.api.services.gmail.Gmail;
//        import docSharing.entity.User;
//        import docSharing.repository.UserRepository;
//        import docSharing.service.AuthService;
//        import docSharing.service.FolderService;
//        import docSharing.service.UserService;
//        import docSharing.utils.EmailUtil;
//        import org.junit.jupiter.api.BeforeEach;
//        import org.junit.jupiter.api.DisplayName;
//        import org.junit.jupiter.api.Test;
//        import org.junit.jupiter.api.extension.ExtendWith;
//        import org.mockito.InjectMocks;
//        import org.mockito.Mock;
//        import org.mockito.junit.jupiter.MockitoExtension;
//
//        import static org.mockito.BDDMockito.given;
//        import static org.mockito.Mockito.*;
//
//        import javax.security.auth.login.AccountNotFoundException;
//
//        import static org.junit.jupiter.api.Assertions.assertEquals;
//
//@ExtendWith(MockitoExtension.class)
//class FacadeAuthControllerTest {
//    @Mock
//    private AuthService authService;
//    @Mock
//    private UserService userService;
//    @Mock
//    private FolderService folderService;
//    @Mock
//    private static Gmail service;
//
//    @InjectMocks
//    private EmailUtil emailUtil;
//    @InjectMocks
//    private FacadeAuthController facadeAuthController;
//
//    private User goodUser;
//    private User badEmailUser;
//    private User badPasswordUser;
//    private User badNameUser;
//
//    @BeforeEach
//    @DisplayName("Make sure we have all necessary items, good as new, for each test when it's called")
//    void setUp() {
//        goodUser = User.createUser("asaf396@gmai.com", "dvir1234", "dvir");
//        goodUser.setId(1l);
//        goodUser.setActivated(true);
//
//        badEmailUser = User.createUser("Dvgmai.com", "dvir1234567", "dvir");
//        badPasswordUser = User.createUser("Dvgmai@gmail.com", "4", "dviros");
//        badNameUser = User.createUser("Dvgmai@gmail.com", "dvir1234567", "1");
//    }
//
//    @Test
//    @DisplayName("When sending all the correct parameters, successfully register the user to the database")
//    void register_goodUser_Successfully() throws AccountNotFoundException {
//        given(authService.register(goodUser.getEmail(), goodUser.getPassword(), goodUser.getName())).willReturn(goodUser);
//        doNothing().when(folderService).createRootFolders(goodUser);
//        assertEquals(201, facadeAuthController.register(goodUser).getStatusCode(), "register with good user parameters did not return 201");
//    }
//
//    @Test
//    @DisplayName("Given a wrong email address, expect a bad response to be returned")
//    void register_badUserEmail_BAD_REQUEST() {
//        assertEquals(400, facadeAuthController.register(badEmailUser).getStatusCode(), "register with bad email user parameters did not return 400");
//    }
//
//    @Test
//    @DisplayName("Given a wrong password input, expect a bad response to be returned")
//    void register_badUserPassword_BAD_REQUEST() {
//        assertEquals(400, facadeAuthController.register(badPasswordUser).getStatusCode(), "register with bad password user parameters did not return 400");
//    }
//
//    @Test
//    @DisplayName("Given a wrong name input, expect a bad response to be returned")
//    void register_badUserName_BAD_REQUEST() {
//        assertEquals(400, facadeAuthController.register(badNameUser).getStatusCode(), "register with bad name user parameters did not return 400");
//    }
//
//    @Test
//    @DisplayName("Given an email address that already exists in the database, when trying to register, expect a bad response to be returned")
//    void register_withSameEmailAgain_BAD_REQUEST() {
//        assertEquals(400, facadeAuthController.register(goodUser).getStatusCode(), "register with good user parameters did not return 400");
//    }
//
//    @Test
//    @DisplayName("When trying to log in with all the correct parameters, successfully return a positive response")
//    void login_goodUser_success() throws AccountNotFoundException {
//        given(userService.findByEmail(goodUser.getEmail())).willReturn(goodUser);
//        when(authService.login(goodUser.getEmail(), goodUser.getPassword())).thenReturn("token");
//        assertEquals(200, facadeAuthController.login(goodUser).getStatusCode(), "Login with correct input did not return positive response");
//    }
//
//    @Test
//    @DisplayName("When given the wrong password input, after an account with a different password, return negative response")
//    void login_failWrongPassword_FORBIDDEN() throws AccountNotFoundException {
//        given(userService.findByEmail(goodUser.getEmail())).willReturn(goodUser);
//        when(authService.login(goodUser.getEmail(), goodUser.getPassword())).thenThrow(new IllegalArgumentException("test to login with a wrong password"));
//        assertEquals(400, facadeAuthController.login(goodUser).getStatusCode(), "test to login with a wrong password did not return errorCode 400");
//    }
//
//    @Test
//    @DisplayName("When given an unregistered account's email, return a negatie response")
//    void login_noEmailInDB_UNAUTHORIZED() throws AccountNotFoundException {
//        given(userService.findByEmail(goodUser.getEmail())).willThrow(new AccountNotFoundException("No existing account in the db"));
//        assertEquals(401, facadeAuthController.login(goodUser).getStatusCode(), "test to login with a non-existing account did not return errorCode 401");
//    }
//
//    @Test
//    @DisplayName("When given a wrong token string, expect to throw an exception")
//    void activate_wrongToken_throwException() {
//        assertEquals(400, facadeAuthController.activate("shalom lah geveret Dvir").getStatusCode(), "Activation a user with wrong token did not throw exception");
//    }
//}




//===============================================================================
//    @Test
//    void addNewEvent_checkAddEvent_responseBadRequest() {
//        //event.setStart(LocalDateTime.now().minusDays(1));
//        // when(eventService.addNewEvent(userId,event)).thenThrow(IllegalArgumentException.class);
//        // assertThrows(IllegalArgumentException.class, () ->{eventController.addNewEvent(userId,event);});
//
//    }
//    @Test
//    void addNewEvent_checkValidTime_timeNotValid() {
//        //event.setStart(LocalDateTime.now().minusDays(1));
//       // when(eventService.addNewEvent(userId,event)).thenThrow(IllegalArgumentException.class);
//       // assertThrows(IllegalArgumentException.class, () ->{eventController.addNewEvent(userId,event);});
//
//    }
//    @Test
//    void addNewEvent_checkValidTime_timeValid() {
//    }
