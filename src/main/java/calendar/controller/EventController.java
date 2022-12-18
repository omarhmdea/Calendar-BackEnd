package calendar.controller;

import calendar.ResponsHandler.SuccessResponse;
import calendar.entities.Event;
import calendar.entities.User;
import calendar.entities.UserDTO;
import calendar.exception.customException.ValidationErrorException;
import calendar.service.AuthService;
import calendar.service.EventService;
import calendar.utilities.Validator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.annotations.Fetch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping("/event")
public class EventController {
    private static final Logger logger = LogManager.getLogger(EventController.class.getName());

    @Autowired
    private EventService eventService;
    @Autowired
    private AuthService authService;

    @PostMapping(value = "newEvent")
    public Event addNewEvent(@RequestAttribute int userId, @RequestBody Event newEvent){
        // check in filter that user is logged in - has a token
        return eventService.addNewEvent(userId, newEvent);
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
    @PostMapping(value = "invite/{eventId}")
    public void invite(@RequestAttribute int userId,@PathVariable int eventId,@RequestBody String guestEmail ) {

    }
}
