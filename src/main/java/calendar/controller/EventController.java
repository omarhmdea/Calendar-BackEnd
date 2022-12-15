package calendar.controller;

import calendar.entities.Event;
import calendar.service.AuthService;
import calendar.service.EventService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/event")
public class EventController {
    private static final Logger logger = LogManager.getLogger(AuthController.class.getName());

    @Autowired
    private EventService eventService;
    @Autowired
    private AuthService authService;

    @PostMapping(value = "newEvent")
    public Event addNewEvent(@RequestAttribute int userId, @RequestBody Event newEvent){
        // check in filter that user is logged in - has a token
        return eventService.addNewEvent(userId, newEvent);
    }

    @PostMapping(value = "invite/{eventId}")
    public void invite(@RequestAttribute int userId,@PathVariable int eventId,@RequestBody String guestEmail ) {

    }
}
