package calendar.controller;

import calendar.ResponsHandler.SuccessResponse;
import calendar.entities.UserEvent;
import calendar.entities.UserEventDTO;
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

    @PutMapping(value = "approve/{eventId}")
    public ResponseEntity<SuccessResponse<UserEventDTO>> approveInvitation(@PathVariable int eventId, @PathParam("email") String email) {
        UserEventDTO userEventDTO = new UserEventDTO(eventService.approveOrRejectInvitation(email, eventId, Status.APPROVED));
        SuccessResponse<UserEventDTO> successApproveInvitation = new SuccessResponse<>(HttpStatus.OK, "Approved invitation successfully", userEventDTO);
        return ResponseEntity.ok().body(successApproveInvitation);
    }

    @PutMapping(value = "reject/{eventId}")
    public ResponseEntity<SuccessResponse<UserEventDTO>> rejectInvitation(@PathVariable int eventId, @PathParam("email") String email) {
        UserEventDTO userEventDTO = new UserEventDTO(eventService.approveOrRejectInvitation(email, eventId, Status.REJECTED));
        SuccessResponse<UserEventDTO> successRejectInvitation = new SuccessResponse<>(HttpStatus.OK, "Rejected invitation successfully", userEventDTO);
        return ResponseEntity.ok().body(successRejectInvitation);
    }
}
