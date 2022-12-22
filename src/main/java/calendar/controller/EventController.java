package calendar.controller;

import calendar.ResponsHandler.SuccessResponse;
import calendar.entities.*;
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

@RestController
@CrossOrigin
@RequestMapping("/event")
public class EventController {
    private static final Logger logger = LogManager.getLogger(EventController.class.getName());

    @Autowired
    private EventService eventService;
    @Autowired
    private NotificationService notificationService;

    /**
     * Add new event to the user's calendar
     * @param userId   the user that is trying to create the event
     * @param newEvent the new event data
     * @return a SuccessResponse - OK status, a message, the new event data
     */
    @PostMapping(value = "create")
    public ResponseEntity<SuccessResponse<Event>> addNewEvent(@RequestAttribute int userId, @RequestBody Event newEvent) {
        logger.debug("Try to add new event event");
        SuccessResponse<Event> successAddNewEvent = new SuccessResponse<>(HttpStatus.OK, "Add new event successfully", eventService.addNewEvent(userId, newEvent));
        logger.info("Adding new event was made successfully");
        return ResponseEntity.ok().body(successAddNewEvent);
    }

    /**
     * Update event : update event data for admin & organizer
     * @param userId      - the user id
     * @param updateEvent - the update event
     * @return successResponse with updated data,Message,HttpStatus
     */
    @PutMapping(value = "update/{eventId}")
    public ResponseEntity<SuccessResponse<Event>> updateEvent(@RequestAttribute int userId, @RequestBody Event updateEvent, @PathVariable int eventId) {
        logger.debug("try to update event");
        Event updatedEvent = eventService.updateEvent(userId, updateEvent);
        SuccessResponse<Event> successResponse = new SuccessResponse<>(HttpStatus.OK, "Successful updating event", updatedEvent);
        //notificationService.sendNotification(updatedEvent, NotificationType.UPDATE_EVENT);
        logger.info("Updating was made successfully");
        return ResponseEntity.ok().body(successResponse);
    }

    /**
     * Delete event : delete event from DB
     * @param userId  - the user id
     * @param eventId - the event to delete
     * @return successResponse with deleted event,Message,HttpStatus
     */
    @DeleteMapping(value = "delete/{eventId}")
    public ResponseEntity<SuccessResponse<Event>> deleteEvent(@RequestAttribute int userId, @PathVariable int eventId) {
        logger.debug("try to delete event");
        Event deletedEvent = eventService.deleteEvent(userId, eventId);
        SuccessResponse<Event> successResponse = new SuccessResponse<>(HttpStatus.OK, "Successful deleting event", deletedEvent);
        //notificationService.sendNotification(deletedEvent, NotificationType.UPDATE_EVENT);
        logger.info("Deleting event was made successfully");
        return ResponseEntity.ok().body(successResponse);
    }

    /**
     * Get calendar : get the event calendar from DB According to month year
     * @param userId - the user id
     * @param month  - the month we want to present
     * @param year   - the year we want to present
     * @return list event of month & year
     */
    @GetMapping(value = "calendar")
    public ResponseEntity<SuccessResponse<List<Event>>> getCalendar(@RequestAttribute int userId, @PathParam("month") int month, @PathParam("year") int year) {
        logger.debug("Try to get calendar");
        SuccessResponse<List<Event>> successResponse = new SuccessResponse<>(HttpStatus.OK, "Successful get calendar", eventService.getCalendar(userId, month, year));
        logger.info("Get calendar was made successfully");
        return ResponseEntity.ok().body(successResponse);
    }
    /**
     * Show calendar : get the event calendar of shareUserId from DB According to month year
     * @param userId - the user id
     * @param month  - the month we want to present
     * @param year   - the year we want to present
     * @return list event of month & year
     */
    @GetMapping(value = "showCalendar")
    public ResponseEntity<SuccessResponse<List<Event>>> showCalendar(@RequestAttribute int userId, @PathParam("month") int month,
                                                                     @PathParam("year") int year, @PathParam("shareUserId") int shareUserId) {
        logger.debug("try to get calendar");
        List<Event> calendarEvent = eventService.showCalendar(shareUserId, month, year);
        SuccessResponse<List<Event>> successResponse = new SuccessResponse<>(HttpStatus.OK, "Successful show calendar", calendarEvent);
        logger.info("show calendar was made successfully");
        return ResponseEntity.ok().body(successResponse);
    }

    /**
     * Set guest as admin in the given event
     * @param userId  the user that created the event
     * @param eventId the event to set new admin to
     * @param email   the email of the guest that the organizer wants to set as admin
     * @return a SuccessResponse - OK status, a message,
     * the User event data - event id, new admin id, new admin role (admin), new admin status (approved)
     */
    @PutMapping(value = "guest/assign/{eventId}")
    public ResponseEntity<SuccessResponse<UserEventDTO>> setGuestAsAdmin(@RequestAttribute int userId, @PathVariable int eventId, @PathParam("email") String email) {
        logger.debug("Try to set guest as admin");
        UserEventDTO userEventDTO = new UserEventDTO(eventService.setGuestAsAdmin(userId, email, eventId));
        SuccessResponse<UserEventDTO> successSetGuestAsAdmin = new SuccessResponse<>(HttpStatus.OK, "Set admin successfully", userEventDTO);
        logger.info("Set admin was made successfully");
        return ResponseEntity.ok().body(successSetGuestAsAdmin);
    }

    /**
     * Remove new guest to an existing event
     *
     * @param userId  the id of the user that is trying to perform the action
     * @param eventId the id of the event to remove the guest to
     * @param email   the email of the guest to remove
     * @return a SuccessResponse - OK status, a message, the User data
     */
    @DeleteMapping(value = "guest/delete/{eventId}")
    public ResponseEntity<SuccessResponse<UserDTO>> removeGuestFromEvent(@RequestAttribute int userId, @PathVariable int eventId, @PathParam("email") String email) {
        logger.debug("Try to remove a guest");
        UserDTO userDTO = new UserDTO(eventService.removeGuestFromEvent(userId, email, eventId));
        SuccessResponse<UserDTO> successRemoveGuestFromEvent = new SuccessResponse<>(HttpStatus.OK, "Removed guest successfully", userDTO);
        logger.info("Remove a guest was made successfully");
        return ResponseEntity.ok().body(successRemoveGuestFromEvent);
    }

    /**
     * Add new guest to an existing event
     * @param userId  the id of the user that is trying to perform the action
     * @param eventId the id of the event to add the guest to
     * @param email   the email of the guest to add
     * @return a SuccessResponse - OK status, a message,
     *       the User event data - event id, new admin id, the guest role (guest), the guest status (tentative)
     */
    @PostMapping(value = "guest/invite/{eventId}")
    public ResponseEntity<SuccessResponse<UserEventDTO>> inviteGuestToEvent(@RequestAttribute int userId, @PathVariable int eventId, @PathParam("email") String email) {
        logger.debug("Try to invite a guest");
        UserEventDTO userEventDTO = new UserEventDTO(eventService.inviteGuestToEvent(userId, email, eventId));
        SuccessResponse<UserEventDTO> successAddGuestToEvent = new SuccessResponse<>(HttpStatus.OK, "Added guest successfully", userEventDTO);
        logger.info("Invite a guest was made successfully");
        return ResponseEntity.ok().body(successAddGuestToEvent);
    }

    /**
     * Change the user's nitification settings
     * @param userId the user that is trying to change it's notification settings
     * @param userNotification
     * @return a SuccessResponse - OK status, a message, the user notification settings
     */
    @PutMapping(value = "settings")
    public ResponseEntity<SuccessResponse<UserNotification>> changeSettings(@RequestAttribute int userId, @RequestBody UserNotification userNotification) {
        logger.debug("Try to change notification settings");
        //TODO : add userNotificationDTO
        SuccessResponse<UserNotification> successChangeSettings = new SuccessResponse<>(HttpStatus.OK, "Changed settings successfully", notificationService.changeSettings(userId, userNotification));
        logger.info("Change notification settings was made successfully");
        return ResponseEntity.ok().body(successChangeSettings);
    }

    /**
     * A user approved an event invitation
     * @param userId the user that approved the invitation
     * @param eventId the event that is related to the invitation
     * @return a SuccessResponse - OK status, a message,
     *      the User event data - event id, new admin id, the guest role, the guest status (approved)
     */
    @PutMapping(value = "approve/{eventId}")
    public ResponseEntity<SuccessResponse<UserEventDTO>> approveInvitation(@RequestAttribute int userId, @PathVariable int eventId) {
        logger.debug("Try to approve invitation");
        UserEventDTO userEventDTO = new UserEventDTO(eventService.approveOrRejectInvitation(userId, eventId, Status.APPROVED));
        SuccessResponse<UserEventDTO> successApproveInvitation = new SuccessResponse<>(HttpStatus.OK, "Approved invitation successfully", userEventDTO);
        logger.info("Approve invitation was made successfully");
        return ResponseEntity.ok().body(successApproveInvitation);
    }

    /**
     * A user rejected an event invitation
     * @param userId the user that rejected the invitation
     * @param eventId the event that is related to the invitation
     * @return a SuccessResponse - OK status, a message,
     *      the User event data - event id, new admin id, the guest role, the guest status (rejected)
     */
    @PutMapping(value = "reject/{eventId}")
    public ResponseEntity<SuccessResponse<UserEventDTO>> rejectInvitation(@RequestAttribute int userId, @PathVariable int eventId) {
        logger.debug("Try to reject invitation");
        UserEventDTO userEventDTO = new UserEventDTO(eventService.approveOrRejectInvitation(userId, eventId, Status.REJECTED));
        SuccessResponse<UserEventDTO> successRejectInvitation = new SuccessResponse<>(HttpStatus.OK, "Rejected invitation successfully", userEventDTO);
        logger.info("Reject invitation was made successfully");
        return ResponseEntity.ok().body(successRejectInvitation);
    }
}
