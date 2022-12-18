package calendar.controller;

import calendar.ResponsHandler.SuccessResponse;
import calendar.entities.Event;
import calendar.entities.UserEvent;
import calendar.service.EventService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.websocket.server.PathParam;

@RestController
@CrossOrigin
@RequestMapping("/event")
public class EventController {
    private static final Logger logger = LogManager.getLogger(EventController.class.getName());

    @Autowired
    private EventService eventService;

    /**
     * Add new event to the user's calendar
     * @param userId the user that is trying to create the event
     * @param newEvent the new event data
     * @return a SuccessResponse - OK status, a message, the new event data
     */
    @PostMapping(value = "newEvent")
    public ResponseEntity<SuccessResponse<Event>> addNewEvent(@RequestAttribute int userId, @RequestBody Event newEvent){
        // check in filter that user is logged in - has a token
        SuccessResponse<Event> successAddNewEvent = new SuccessResponse<>(HttpStatus.OK, "Set admin successfully", eventService.addNewEvent(userId, newEvent));
        return ResponseEntity.ok().body(successAddNewEvent);
    }

    /**
     * Update event : update event data for admin & organizer
     * @param userId  - the user id
     * @param updateEvent  - the update event
     * @return successResponse with updated data,Message,HttpStatus
     */
    @PutMapping(value = "updateEvent")
    public ResponseEntity<SuccessResponse<Event>> updateEvent(@RequestAttribute int userId, @RequestBody Event updateEvent) {
        logger.debug("try to update event");
        Event updatedEvent = eventService.updateEvent(userId,updateEvent);
        SuccessResponse<Event> successResponse = new SuccessResponse<>(HttpStatus.OK, "Successful updating event", updatedEvent);
        logger.info("Updating was made successfully");
        return ResponseEntity.ok().body(successResponse);
    }
    /**
     * Delete event : delete event from DB
     * @param userId  - the user id
     * @param deleteEventId  - the event to delete
     * @return successResponse with deleted event,Message,HttpStatus
     */
    @PostMapping(value = "deleteEvent/{deleteEventId}")
    public ResponseEntity<SuccessResponse<Event>> deleteEvent(@RequestAttribute int userId, @PathVariable int deleteEventId) {
        logger.debug("try to delete event");
        Event deletedEvent = eventService.deleteEvent(userId,deleteEventId);
        SuccessResponse<Event> successResponse = new SuccessResponse<>(HttpStatus.OK, "Successful deleting event", deletedEvent);
        logger.info("Deleting event was made successfully");
        return ResponseEntity.ok().body(successResponse);
    }


    @PostMapping(value = "invite/{eventId}")
    public void invite(@RequestAttribute int userId, @PathVariable int eventId,@RequestBody String guestEmail ) {

    }

    /**
     * Set guest as admin in the given event
     * @param userId the user that created the event
     * @param eventId the event to set new admin to
     * @param email the email of the guest that the organizer wants to set as admin
     * @return a SuccessResponse - OK status, a message,
     *      the User event data - event id, new admin id, new admin role (admin), new admin status (approved)
     */
    @PutMapping(value = "newAdmin/{eventId}")
    public ResponseEntity<SuccessResponse<UserEvent>> setGuestAsAdmin(@RequestAttribute int userId, @PathVariable int eventId, @PathParam("email") String email){
        // check in filter that user is logged in - has a token
        SuccessResponse<UserEvent> successSetGuestAsAdmin = new SuccessResponse<>(HttpStatus.OK, "Set admin successfully", eventService.setGuestAsAdmin(userId, email, eventId));
        return ResponseEntity.ok().body(successSetGuestAsAdmin);
    }
}
