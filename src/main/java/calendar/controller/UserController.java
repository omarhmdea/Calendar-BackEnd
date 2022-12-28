package calendar.controller;

import calendar.ResponsHandler.SuccessResponse;
import calendar.entities.DTO.EventDTO;
import calendar.entities.Event;
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

@RestController
@CrossOrigin
@RequestMapping("/user")
public class UserController {
    private static final Logger logger = LogManager.getLogger(EventController.class.getName());

    @Autowired
    private EventService eventService;

    @Autowired
    private NotificationService notificationService;

    
    @GetMapping(value = "approve/{eventId}")
    public ResponseEntity<SuccessResponse<EventDTO>> approveInvitation(@PathVariable int eventId, @PathParam("email") String email) {
        EventDTO event = new EventDTO(eventService.approveOrRejectInvitation(email, eventId, Status.APPROVED));
        SuccessResponse<EventDTO> successApproveInvitation = new SuccessResponse<>(HttpStatus.OK, "Approved invitation successfully", event);
        return ResponseEntity.ok().body(successApproveInvitation);
    }

    @GetMapping(value = "reject/{eventId}")
    public ResponseEntity<SuccessResponse<EventDTO>> rejectInvitation(@PathVariable int eventId, @PathParam("email") String email) {
        EventDTO event = new EventDTO(eventService.approveOrRejectInvitation(email, eventId, Status.REJECTED));
        SuccessResponse<EventDTO> successRejectInvitation = new SuccessResponse<>(HttpStatus.OK, "Rejected invitation successfully", event);
        return ResponseEntity.ok().body(successRejectInvitation);
    }
}
