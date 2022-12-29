package calendar.controller;

import calendar.responsHandler.SuccessResponse;
import calendar.entities.Credentials.EventCredentials;
import calendar.entities.Credentials.UserNotificationCredentials;
import calendar.entities.DTO.EventDTO;
import calendar.entities.DTO.UserDTO;
import calendar.entities.Event;
import calendar.entities.NotificationDetails;
import calendar.entities.User;
import calendar.entities.UserNotification;
import calendar.enums.NotificationType;
import calendar.enums.Status;
import calendar.service.EventService;
import calendar.service.NotificationService;
import calendar.utilities.EmailFacade;
import calendar.utilities.TimeConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
@RequestMapping("/event")
public class EventController {
    private static final Logger logger = LogManager.getLogger(EventController.class.getName());

    @Autowired
    private EventService eventService;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private EmailFacade emailFacade;

    /**
     * Add new event to the user's calendar
     * @param user the user that is trying to create the event
     * @param newEvent the new event data
     * @return a SuccessResponse - OK status, a message, the new event data
     */
    @PostMapping(value = "create")
    public ResponseEntity<SuccessResponse<EventDTO>> addNewEvent(@RequestAttribute User user, @RequestBody Event newEvent) {
        logger.debug("Try to add new event event");
        EventDTO newEventDTO = EventDTO.convertToEventDTO(eventService.addNewEvent(user, newEvent));
        UserNotification userNotification = notificationService.findUserNotification(user);
        newEventDTO.setStart(TimeConverter.convertToUtc(newEventDTO.getStart(), ZoneId.of(userNotification.getTimeZone())));
        newEventDTO.setEnd(TimeConverter.convertToUtc(newEventDTO.getEnd(), ZoneId.of(userNotification.getTimeZone())));
        SuccessResponse<EventDTO> successAddNewEvent = new SuccessResponse<>("Add new event successfully", newEventDTO);
        logger.info("Adding new event was made successfully");
        return ResponseEntity.ok().body(successAddNewEvent);
    }

    /**
     * Update event data by admin & organizer
     * @param user the user that is trying to update the event
     * @param updateEvent - the update event
     * @return successResponse with updated data,Message,HttpStatus
     */
    @PutMapping(value = "update/{eventId}")
    public ResponseEntity<SuccessResponse<EventDTO>> updateEvent(@RequestAttribute User user, @RequestAttribute Event event, @RequestBody EventCredentials updateEvent) {
        logger.debug("try to update event");
        EventDTO updatedEventDTO = EventDTO.convertToEventDTO(eventService.updateEvent(user, event, updateEvent));
        UserNotification userNotification = notificationService.findUserNotification(user);
        updatedEventDTO.setStart(TimeConverter.convertToUtc(updatedEventDTO.getStart(), ZoneId.of(userNotification.getTimeZone())));
        updatedEventDTO.setEnd(TimeConverter.convertToUtc(updatedEventDTO.getEnd(), ZoneId.of(userNotification.getTimeZone())));
        SuccessResponse<EventDTO> successResponse = new SuccessResponse<>("Successful updating event", updatedEventDTO);
        logger.info("Updating was made successfully");
        return ResponseEntity.ok().body(successResponse);
    }

    /**
     * Set guest as admin in the given event
     * @param user  the user that created the event
     * @param event the event to set new admin to
     * @param email   the email of the guest that the organizer wants to set as admin
     * @return a SuccessResponse - OK status, a message,
     * the User event data - event id, new admin id, new admin role (admin), new admin status (approved)
     */
    @PutMapping(value = "guest/assign/{eventId}")
    public ResponseEntity<SuccessResponse<EventDTO>> setGuestAsAdmin(@RequestAttribute User user, @RequestAttribute Event event, @PathParam("email") String email) {
        logger.debug("Try to set guest as admin");
        EventDTO newAdminInEventDTO = EventDTO.convertToEventDTO(eventService.setGuestAsAdmin(user, email, event));
        SuccessResponse<EventDTO> successSetGuestAsAdmin = new SuccessResponse<>("Set admin successfully", newAdminInEventDTO);
        logger.info("Set admin was made successfully");
        return ResponseEntity.ok().body(successSetGuestAsAdmin);
    }

    /**
     * Delete event from DB
     * @param user  - the user id
     * @param event - the event to delete
     * @return successResponse with deleted event,Message,HttpStatus
     */
    @DeleteMapping(value = "delete/{eventId}")
    public ResponseEntity<?> deleteEvent(@RequestAttribute User user, @RequestAttribute Event event) {
        logger.debug("try to delete event");
        EventDTO deletedEventDTO = EventDTO.convertToEventDTO(eventService.deleteEvent(user, event));
        SuccessResponse<EventDTO> successResponse = new SuccessResponse<>( "Successful deleting event", deletedEventDTO);
        logger.info("Deleting event was made successfully");
        return ResponseEntity.noContent().build();
    }

    /**
     * Add new guest to an existing event
     * @param user  the id of the user that is trying to perform the action
     * @param event the id of the event to add the guest to
     * @param email   the email of the guest to add
     * @return a SuccessResponse - OK status, a message,
     *       the User event data - event id, new admin id, the guest role (guest), the guest status (tentative)
     */
    @PostMapping(value = "guest/invite/{eventId}")
    public ResponseEntity<SuccessResponse<EventDTO>> inviteGuestToEvent(@RequestAttribute User user, @RequestAttribute Event event, @PathParam("email") String email) {
        logger.debug("Try to invite a guest");
        EventDTO newGuestInEventDTO = EventDTO.convertToEventDTO(eventService.inviteGuestToEvent(user, email, event));
        SuccessResponse<EventDTO> successAddGuestToEvent = new SuccessResponse<>( "Added guest successfully", newGuestInEventDTO);
        logger.info("Invite a guest was made successfully");
        emailFacade.sendInvitation(email, event);
        return ResponseEntity.ok().body(successAddGuestToEvent);
    }

    /**
     * Remove guest from an existing event
     * @param user  the id of the user that is trying to perform the action
     * @param event the id of the event to remove the guest to
     * @param email   the email of the guest to remove
     * @return a SuccessResponse - OK status, a message, the User data
     */
    @DeleteMapping(value = "guest/delete/{eventId}")
    public ResponseEntity<SuccessResponse<UserDTO>> removeGuestFromEvent(@RequestAttribute User user, @RequestAttribute Event event, @PathParam("email") String email) {
        logger.debug("Try to remove a guest");
        UserDTO userDTO = UserDTO.convertToUserDTO(eventService.removeGuestFromEvent(user, email, event));
        SuccessResponse<UserDTO> successRemoveGuestFromEvent = new SuccessResponse<>( "Removed guest successfully", userDTO);
        logger.info("Remove a guest was made successfully");
        return ResponseEntity.ok().body(successRemoveGuestFromEvent);
    }

    /**
     * Get calendar (events) of a user by month and year
     * @param user the user that is trying to view the calendar
     * @param id the id of the user to view their calendar
     * @param month the month the user wants to present
     * @param year the year the user wants to present
     * @return list of events by month & year
     */
    @GetMapping(value = "showCalendar/{id}")
    public ResponseEntity<SuccessResponse<List<EventDTO>>> showCalendar(@RequestAttribute User user, @PathVariable int id, @PathParam("month") int month, @PathParam("year") int year) {
        logger.debug("Try to get calendar of user " + id);
        List<Event> calendarEvent = eventService.showCalendar(user, id, month, year);
        UserNotification userNotification = notificationService.findUserNotification(user);
        List<EventDTO> calendarEventDTO = calendarEvent.stream().map(event -> {
            EventDTO eventDTO = EventDTO.convertToEventDTO(event);
            eventDTO.setStart(TimeConverter.convertToUtc(event.getStart(), ZoneId.of(userNotification.getTimeZone())));
            eventDTO.setEnd(TimeConverter.convertToUtc(event.getEnd(), ZoneId.of(userNotification.getTimeZone())));
            return eventDTO;
        }).collect(Collectors.toList());
        SuccessResponse<List<EventDTO>> successResponse = new SuccessResponse<>( "Successful show other user calendar", calendarEventDTO);
        logger.info("show calendar was made successfully");
        return ResponseEntity.ok().body(successResponse);
    }

    /**
     * A user approved an event invitation
     * @param user the user that approved the invitation
     * @param eventId the event that is related to the invitation
     * @return a SuccessResponse - OK status, a message, the event data
     */
    @PutMapping(value = "approve/{eventId}")
    public ResponseEntity<SuccessResponse<EventDTO>> approveInvitation(@RequestAttribute User user, @PathVariable("eventId") int eventId) {
        logger.debug("Try to approve invitation");
        EventDTO approvedEventInvitationDTO = EventDTO.convertToEventDTO(eventService.approveOrRejectInvitation(user, eventId, Status.APPROVED));
        SuccessResponse<EventDTO> successApproveInvitation = new SuccessResponse<>( "Approved invitation successfully", approvedEventInvitationDTO);
        notificationService.sendNotificationToGuestsEvent(new NotificationDetails(user.getName() + " approve his invitation",approvedEventInvitationDTO, NotificationType.USER_STATUS_CHANGED));
        logger.info("Approve invitation was made successfully");
        return ResponseEntity.ok().body(successApproveInvitation);
    }

    /**
     * A user rejected an event invitation
     * @param user the user that rejected the invitation
     * @param eventId the event that is related to the invitation
     * @return a SuccessResponse - OK status, a message,
     *      the User event data - event id, new admin id, the guest role, the guest status (rejected)
     */
    @PutMapping(value = "reject/{eventId}")
    public ResponseEntity<SuccessResponse<EventDTO>> rejectInvitation(@RequestAttribute User user, @PathVariable int eventId) {
        logger.debug("Try to reject invitation");
        EventDTO rejectEventInvitationDTO = EventDTO.convertToEventDTO(eventService.approveOrRejectInvitation(user, eventId, Status.REJECTED));
        SuccessResponse<EventDTO> successRejectInvitation = new SuccessResponse<>("Rejected invitation successfully", rejectEventInvitationDTO);
        notificationService.sendNotificationToGuestsEvent(new NotificationDetails(user.getName() + " reject his invitation",rejectEventInvitationDTO, NotificationType.USER_STATUS_CHANGED));
        logger.info("Reject invitation was made successfully");
        return ResponseEntity.ok().body(successRejectInvitation);
    }

    /**
     * Change the user's notification settings
     * @param user the user that is trying to change its notification settings
     * @param userNotification
     * @return a SuccessResponse - OK status, a message, the user notification settings
     */
    @PutMapping(value = "settings")
    public ResponseEntity<SuccessResponse<UserNotificationCredentials>> changeSettings(@RequestAttribute User user, @RequestBody UserNotificationCredentials userNotification) {
        logger.debug("Try to change notification settings");
        UserNotificationCredentials userNotificationCredentials = UserNotificationCredentials.convertToUserNotificationCredentials(notificationService.changeSettings(user, userNotification));
        SuccessResponse<UserNotificationCredentials> successChangeSettings = new SuccessResponse<>( "Changed settings successfully", userNotificationCredentials);
        logger.info("Change notification settings was made successfully");
        return ResponseEntity.ok().body(successChangeSettings);
    }

    @GetMapping(value = "settings")
    public ResponseEntity<SuccessResponse<UserNotificationCredentials>> getSettings(@RequestAttribute User user) {
        logger.debug("Try to change notification settings");
        UserNotificationCredentials userNotificationCredentials = UserNotificationCredentials.convertToUserNotificationCredentials(notificationService.findUserNotification(user));
        SuccessResponse<UserNotificationCredentials> successChangeSettings = new SuccessResponse<>( "Changed settings successfully", userNotificationCredentials);
        logger.info("Change notification settings was made successfully");
        return ResponseEntity.ok().body(successChangeSettings);
    }

    /**
     * Share my calendar with another user
     * @param user the user that wants to share their calendar
     * @param email the email of the user to share the calendar to
     * @return a SuccessResponse - OK status, a message, the user info
     */
    @PutMapping(value = "share")
    public ResponseEntity<SuccessResponse<UserDTO>> shareCalendar(@RequestAttribute User user, @PathParam("email") String email) {
        logger.debug("Try to share my calendar to someone else");
        UserDTO userDTO = UserDTO.convertToUserDTO(eventService.shareCalendar(user, email));
        SuccessResponse<UserDTO> successShareCalendar = new SuccessResponse<>( "Shared calendar successfully", userDTO);
        logger.info("Share my calendar was made successfully");
        return ResponseEntity.ok().body(successShareCalendar);
    }

    /**
     * Get a list of all the users that the user can view their calenders
     * @param user the user to view their shared calendars list
     * @return a list of the users the user can view info
     */
    @GetMapping(value = "myCalendars")
    public ResponseEntity<SuccessResponse<List<UserDTO>>> getSharedCalendars(@RequestAttribute User user) {
        logger.debug("Try to get my shared calendars list");
        List<UserDTO> sharedCalendars = eventService.getSharedCalendars(user);
        SuccessResponse<List<UserDTO>> successSharedCalendars = new SuccessResponse<>( "Shared calendar successfully", sharedCalendars);
        logger.info("Remove a guest was made successfully");
        return ResponseEntity.ok().body(successSharedCalendars);
    }
}
