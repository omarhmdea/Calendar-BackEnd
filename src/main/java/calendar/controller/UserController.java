package calendar.controller;

import calendar.ResponsHandler.SuccessResponse;
import calendar.entities.Event;
import calendar.enums.Status;
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
@RequestMapping("/user")
public class UserController {
    private static final Logger logger = LogManager.getLogger(EventController.class.getName());

    @Autowired
    private EventService eventService;

    @GetMapping(value = "stam")
    public String stam(@RequestAttribute int userId) {
        return "I'am in stam func and i pass auth filter";
    }

    @PutMapping(value = "approve/{eventId}")
    public ResponseEntity<SuccessResponse<Event>> approveInvitation(@PathVariable int eventId, @PathParam("email") String email) {
        Event event = eventService.approveOrRejectInvitation(email, eventId, Status.APPROVED);
        SuccessResponse<Event> successApproveInvitation = new SuccessResponse<>(HttpStatus.OK, "Approved invitation successfully", event);
        return ResponseEntity.ok().body(successApproveInvitation);
    }

    @PutMapping(value = "reject/{eventId}")
    public ResponseEntity<SuccessResponse<Event>> rejectInvitation(@PathVariable int eventId, @PathParam("email") String email) {
        Event event = eventService.approveOrRejectInvitation(email, eventId, Status.REJECTED);
        SuccessResponse<Event> successRejectInvitation = new SuccessResponse<>(HttpStatus.OK, "Rejected invitation successfully", event);
        return ResponseEntity.ok().body(successRejectInvitation);
    }
}
