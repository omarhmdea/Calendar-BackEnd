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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.websocket.server.PathParam;
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

    // TODO : eventDTO - holds everything but the userEvent list - so we cant see the user's password
    //  shold we return event or event DTO? because in the front we want guests list. maybe do eventToSho that holds userDTO list
    //  maybe do a method that return the guests list by event

    /**
     * Add new event to the user's calendar
     * @param user the user that is trying to create the event
     * @param newEvent the new event data
     * @return a SuccessResponse - OK status, a message, the new event data
     */
    @PostMapping(value = "create")
    public ResponseEntity<SuccessResponse<EventDTO>> addNewEvent(@RequestAttribute User user, @RequestBody Event newEvent) {
        logger.debug("Try to add new event event");
        EventDTO newEventDTO = new EventDTO(eventService.addNewEvent(user, newEvent));
        SuccessResponse<EventDTO> successAddNewEvent = new SuccessResponse<>(HttpStatus.OK, "Add new event successfully", newEventDTO);
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
        EventDTO updatedEventDTO = new EventDTO(eventService.updateEvent(user, event, updateEvent));
        SuccessResponse<EventDTO> successResponse = new SuccessResponse<>(HttpStatus.OK, "Successful updating event", updatedEventDTO);
        //notificationService.sendNotification(updatedEvent, NotificationType.UPDATE_EVENT);
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
        // TODO : check in filter if userId is  organizer
        logger.debug("Try to set guest as admin");
        EventDTO newAdminInEventDTO = new EventDTO(eventService.setGuestAsAdmin(user, email, event));
        SuccessResponse<EventDTO> successSetGuestAsAdmin = new SuccessResponse<>(HttpStatus.OK, "Set admin successfully", newAdminInEventDTO);
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
    public ResponseEntity<SuccessResponse<EventDTO>> deleteEvent(@RequestAttribute User user, @RequestAttribute Event event) {
        logger.debug("try to delete event");
        EventDTO deletedEventDTO = new EventDTO(eventService.deleteEvent(user, event));
        SuccessResponse<EventDTO> successResponse = new SuccessResponse<>(HttpStatus.OK, "Successful deleting event", deletedEventDTO);
        //TODO : notificationService.sendNotification(deletedEvent, NotificationType.UPDATE_EVENT);
        logger.info("Deleting event was made successfully");
        return ResponseEntity.ok().body(successResponse);
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
        EventDTO newGuestInEventDTO = new EventDTO(eventService.inviteGuestToEvent(user, email, event));
        SuccessResponse<EventDTO> successAddGuestToEvent = new SuccessResponse<>(HttpStatus.OK, "Added guest successfully", newGuestInEventDTO);
        logger.info("Invite a guest was made successfully");
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
        UserDTO userDTO = new UserDTO(eventService.removeGuestFromEvent(user, email, event));
        SuccessResponse<UserDTO> successRemoveGuestFromEvent = new SuccessResponse<>(HttpStatus.OK, "Removed guest successfully", userDTO);
        logger.info("Remove a guest was made successfully");
        return ResponseEntity.ok().body(successRemoveGuestFromEvent);
    }

    /**
     * Get calendar (events) of a different user by month and year
     * @param user - the user id
     * @param month  - the month we want to present
     * @param year   - the year we want to present
     * @return list event of month & year
     */
    // TODO : in filter check userId and return the userToShowCalendar
    @GetMapping(value = "showCalendar/{userId}")
    public ResponseEntity<SuccessResponse<List<EventDTO>>> showCalendar(@RequestAttribute User user, @PathVariable int userId, @PathParam("month") int month, @PathParam("year") int year) {
        logger.debug("Try to get calendar of user " + userId);
        List<Event> calendarEvent = eventService.showCalendar(user, userId, month, year);
        List<EventDTO> calendarEventDTO =  calendarEvent.stream().map(event -> new EventDTO(event)).collect(Collectors.toList());
        SuccessResponse<List<EventDTO>> successResponse = new SuccessResponse<>(HttpStatus.OK, "Successful show other user calendar", calendarEventDTO);
        logger.info("show calendar was made successfully");
        return ResponseEntity.ok().body(successResponse);
    }

    /**
     * A user approved an event invitation
     * @param user the user that approved the invitation
     * @param eventId the event that is related to the invitation
     * @return a SuccessResponse - OK status, a message,
     *      the User event data - event id, new admin id, the guest role, the guest status (approved)
     */
    @PutMapping(value = "approve/{eventId}")
    public ResponseEntity<SuccessResponse<EventDTO>> approveInvitation(@RequestAttribute User user, @PathVariable("eventId") int eventId) {
        logger.debug("Try to approve invitation");
        EventDTO approvedEventInvitationDTO = new EventDTO(eventService.approveOrRejectInvitation(user, eventId, Status.APPROVED));
        SuccessResponse<EventDTO> successApproveInvitation = new SuccessResponse<>(HttpStatus.OK, "Approved invitation successfully", approvedEventInvitationDTO);
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
        EventDTO rejectEventInvitationDTO = new EventDTO(eventService.approveOrRejectInvitation(user, eventId, Status.REJECTED));
        SuccessResponse<EventDTO> successRejectInvitation = new SuccessResponse<>(HttpStatus.OK, "Rejected invitation successfully", rejectEventInvitationDTO);
        logger.info("Reject invitation was made successfully");
        return ResponseEntity.ok().body(successRejectInvitation);
    }

    /**
     * Change the user's notification settings
     * @param user the user that is trying to change it's notification settings
     * @param userNotification
     * @return a SuccessResponse - OK status, a message, the user notification settings
     */
    @PutMapping(value = "settings")
    public ResponseEntity<SuccessResponse<UserNotification>> changeSettings(@RequestAttribute User user, @RequestBody UserNotificationCredentials userNotification) {
        logger.debug("Try to change notification settings");
        //TODO : add userNotificationDTO
        UserNotification userNotification1 = notificationService.changeSettings(user, userNotification);
        SuccessResponse<UserNotification> successChangeSettings = new SuccessResponse<>(HttpStatus.OK, "Changed settings successfully", userNotification1);
        logger.info("Change notification settings was made successfully");
        return ResponseEntity.ok().body(successChangeSettings);
    }

    @PutMapping(value = "share")
    public ResponseEntity<SuccessResponse<UserDTO>> shareCalendar(@RequestAttribute User user, @PathParam("email") String email){
        logger.debug("Try to share my calendar to someone else");
        UserDTO userDTO = new UserDTO(eventService.shareCalendar(user, email));
        SuccessResponse<UserDTO> successShareCalendar = new SuccessResponse<>(HttpStatus.OK, "Shared calendar successfully", userDTO);
        logger.info("Share my calendar was made successfully");
        return ResponseEntity.ok().body(successShareCalendar);
    }

    @GetMapping(value = "myCalendars")
    public ResponseEntity<SuccessResponse<List<UserDTO>>> getSharedCalendars(@RequestAttribute User user){
        logger.debug("Try to get my shared calendars list");
        List<UserDTO> sharedCalendars = eventService.getSharedCalendars(user);
        SuccessResponse<List<UserDTO>> successSharedCalendars = new SuccessResponse<>(HttpStatus.OK, "Shared calendar successfully", sharedCalendars);
        logger.info("Remove a guest was made successfully");
        return ResponseEntity.ok().body(successSharedCalendars);
    }
}
