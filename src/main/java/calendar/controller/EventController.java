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
    public ResponseEntity<SuccessResponse<Event>> addNewEvent(@RequestAttribute User user, @RequestBody Event newEvent) {
        logger.debug("Try to add new event event");
        SuccessResponse<Event> successAddNewEvent = new SuccessResponse<>(HttpStatus.OK, "Add new event successfully", eventService.addNewEvent(user, newEvent));
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
    public ResponseEntity<SuccessResponse<Event>> updateEvent(@RequestAttribute User user, @RequestAttribute Event event, @RequestBody EventCredentials updateEvent) {
        // TODO : check in filter if userId is admin / organizer
        logger.debug("try to update event");
        Event updatedEvent = eventService.updateEvent(user, event, updateEvent);
        SuccessResponse<Event> successResponse = new SuccessResponse<>(HttpStatus.OK, "Successful updating event", updatedEvent);
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
    public ResponseEntity<SuccessResponse<Event>> setGuestAsAdmin(@RequestAttribute User user, @RequestAttribute Event event, @PathParam("email") String email) {
        // TODO : check in filter if userId is  organizer
        logger.debug("Try to set guest as admin");
        Event eventDTO = eventService.setGuestAsAdmin(user, email, event);
        SuccessResponse<Event> successSetGuestAsAdmin = new SuccessResponse<>(HttpStatus.OK, "Set admin successfully", eventDTO);
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
    public ResponseEntity<SuccessResponse<Event>> deleteEvent(@RequestAttribute User user, @RequestAttribute Event event) {
        // TODO : check in filter if userId is organizer
        logger.debug("try to delete event");
        Event deletedEvent = eventService.deleteEvent(user, event);
        SuccessResponse<Event> successResponse = new SuccessResponse<>(HttpStatus.OK, "Successful deleting event", deletedEvent);
        //notificationService.sendNotification(deletedEvent, NotificationType.UPDATE_EVENT);
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
    public ResponseEntity<SuccessResponse<Event>> inviteGuestToEvent(@RequestAttribute User user, @RequestAttribute Event event, @PathParam("email") String email) {
        logger.debug("Try to invite a guest");
        Event event1 = eventService.inviteGuestToEvent(user, email, event);
        SuccessResponse<Event> successAddGuestToEvent = new SuccessResponse<>(HttpStatus.OK, "Added guest successfully", event1);
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

//    /**
//     * Get calendar (events) by month and year
//     * @param user - the user id
//     * @param month  - the month we want to present
//     * @param year   - the year we want to present
//     * @return list event of month & year
//     */
//    @GetMapping(value = "calendar")
//    public ResponseEntity<SuccessResponse<List<Event>>> getCalendar(@RequestAttribute User user, @PathParam("month") int month, @PathParam("year") int year) {
//        logger.debug("Try to get my events by month and year");
//        SuccessResponse<List<Event>> successResponse = new SuccessResponse<>(HttpStatus.OK, "Successful get calendar", eventService.getCalendar(user, month, year));
//        logger.info("Get calendar was made successfully");
//        return ResponseEntity.ok().body(successResponse);
//    }

    /**
     * Get calendar (events) of a different user by month and year
     * @param user - the user id
     * @param month  - the month we want to present
     * @param year   - the year we want to present
     * @return list event of month & year
     */
    // TODO : in filter check userId and return the userToShowCalendar
    @GetMapping(value = "showCalendar/{userId}")
    public ResponseEntity<SuccessResponse<List<Event>>> showCalendar(@RequestAttribute User user, @RequestAttribute User userToShowCalendar, @PathParam("month") int month, @PathParam("year") int year) {
        logger.debug("Try to get calendar of user " + userToShowCalendar.getId());
        List<Event> calendarEvent = eventService.showCalendar(user, userToShowCalendar, month, year);
        SuccessResponse<List<Event>> successResponse = new SuccessResponse<>(HttpStatus.OK, "Successful show other user calendar", calendarEvent);
        logger.info("show calendar was made successfully");
        return ResponseEntity.ok().body(successResponse);
    }

    /**
     * A user approved an event invitation
     * @param user the user that approved the invitation
     * @param event the event that is related to the invitation
     * @return a SuccessResponse - OK status, a message,
     *      the User event data - event id, new admin id, the guest role, the guest status (approved)
     */
    @PutMapping(value = "approve/{eventId}")
    public ResponseEntity<SuccessResponse<Event>> approveInvitation(@RequestAttribute User user, @RequestAttribute Event event) {
        logger.debug("Try to approve invitation");
        Event event1 = eventService.approveOrRejectInvitation(user, event, Status.APPROVED);
        SuccessResponse<Event> successApproveInvitation = new SuccessResponse<>(HttpStatus.OK, "Approved invitation successfully", event1);
        logger.info("Approve invitation was made successfully");
        return ResponseEntity.ok().body(successApproveInvitation);
    }

    /**
     * A user rejected an event invitation
     * @param user the user that rejected the invitation
     * @param event the event that is related to the invitation
     * @return a SuccessResponse - OK status, a message,
     *      the User event data - event id, new admin id, the guest role, the guest status (rejected)
     */
    @PutMapping(value = "reject/{eventId}")
    public ResponseEntity<SuccessResponse<Event>> rejectInvitation(@RequestAttribute User user, @RequestAttribute Event event) {
        logger.debug("Try to reject invitation");
        Event event1 = eventService.approveOrRejectInvitation(user, event, Status.REJECTED);
        SuccessResponse<Event> successRejectInvitation = new SuccessResponse<>(HttpStatus.OK, "Rejected invitation successfully", event1);
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
    public ResponseEntity<SuccessResponse<UserNotification>> changeSettings(@RequestAttribute User user, @RequestBody UserNotification userNotification) {
        logger.debug("Try to change notification settings");
        //TODO : add userNotificationDTO
        UserNotification userNotification1 = notificationService.changeSettings(user, userNotification);
        SuccessResponse<UserNotification> successChangeSettings = new SuccessResponse<>(HttpStatus.OK, "Changed settings successfully", userNotification1);
        logger.info("Change notification settings was made successfully");
        return ResponseEntity.ok().body(successChangeSettings);
    }

    // TODO : shareCalendar - we need to add to user a list of users he can view their calendar -
    //  and when we do show calendar check if the user that is trying to see has the permission to view the other user calendar
    @PutMapping(value = "share")
    public ResponseEntity<SuccessResponse<UserDTO>> shareCalendar(@RequestAttribute User user, @PathParam("email") String email){
        logger.debug("Try to share my calendar to someone else");
        UserDTO userDTO = new UserDTO(eventService.shareCalendar(user, email));
        SuccessResponse<UserDTO> successShareCalendar = new SuccessResponse<>(HttpStatus.OK, "Shared calendar successfully", userDTO);
        logger.info("Remove a guest was made successfully");
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
