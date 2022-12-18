package calendar.controller;

import calendar.ResponsHandler.SuccessResponse;
import calendar.entities.Event;
import calendar.entities.LoginData;
import calendar.entities.UserEvent;
import calendar.service.AuthService;
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
    private static final Logger logger = LogManager.getLogger(AuthController.class.getName());

    @Autowired
    private EventService eventService;

    @PostMapping(value = "newEvent")
    public ResponseEntity<SuccessResponse<Event>> addNewEvent(@RequestAttribute int userId, @RequestBody Event newEvent){
        // check in filter that user is logged in - has a token
        SuccessResponse<Event> successAddNewEvente = new SuccessResponse<>(HttpStatus.OK, "Set admin successfully", eventService.addNewEvent(userId, newEvent));
        return ResponseEntity.ok().body(successAddNewEvente);
    }

    @PostMapping(value = "invite/{eventId}")
    public void invite(@RequestAttribute int userId, @PathVariable int eventId,@RequestBody String guestEmail ) {

    }

    @PutMapping(value = "newAdmin/{eventId}")
    public ResponseEntity<SuccessResponse<UserEvent>> setGuestAsAdmin(@RequestAttribute int userId, @PathVariable int eventId, @PathParam("email") String email){
        // check in filter that user is logged in - has a token
        SuccessResponse<UserEvent> successSetGuestAsAdmin = new SuccessResponse<>(HttpStatus.OK, "Set admin successfully", eventService.setGuestAsAdmin(userId, email, eventId));
        return ResponseEntity.ok().body(successSetGuestAsAdmin);
    }
}
