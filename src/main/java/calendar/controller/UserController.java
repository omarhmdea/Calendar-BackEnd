package calendar.controller;

import calendar.ResponsHandler.SuccessResponse;
import calendar.entities.UserEvent;
import calendar.enums.Status;
import calendar.service.EventService;
import calendar.service.UserService;
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

    // TODO : overload approveOrRejectInvitation to accept string email
    @PutMapping(value = "approve/{eventId}")
    public ResponseEntity<SuccessResponse<UserEvent>> approveInvitation(@PathVariable int eventId, @PathParam("email") String email) {
        SuccessResponse<UserEvent> successApproveInvitation = new SuccessResponse<>(HttpStatus.OK, "Approved invitation successfully", eventService.approveOrRejectInvitation(email, eventId, Status.APPROVED));
        return ResponseEntity.ok().body(successApproveInvitation);
    }

    @PutMapping(value = "reject/{eventId}")
    public ResponseEntity<SuccessResponse<UserEvent>> rejectInvitation(@PathVariable int eventId, @PathParam("email") String email) {
        SuccessResponse<UserEvent> successRejectInvitation = new SuccessResponse<>(HttpStatus.OK, "Rejected invitation successfully", eventService.approveOrRejectInvitation(email, eventId, Status.REJECTED));
        return ResponseEntity.ok().body(successRejectInvitation);
    }
}
