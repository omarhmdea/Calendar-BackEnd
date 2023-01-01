package calendar.controller;

import calendar.responsHandler.SuccessResponse;
import calendar.entities.DTO.EventDTO;
import calendar.entities.NotificationDetails;
import calendar.enums.NotificationType;
import calendar.enums.Status;
import calendar.service.EventService;
import calendar.service.NotificationService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ResponseEntity<SuccessResponse<String>> approveInvitation(@PathVariable int eventId, @PathParam("email") String email) {
        logger.info("Try to approve invitation using email");
        EventDTO event = EventDTO.convertToEventDTO(eventService.approveOrRejectInvitation(email, eventId, Status.APPROVED));
        SuccessResponse<String> successApproveInvitation = new SuccessResponse<>( "Approved invitation successfully", "Thank you for approving the invitation for event " + event.getTitle());
        notificationService.sendNotificationToGuestsEvent(new NotificationDetails(email + " approve his invitation", event, NotificationType.USER_STATUS_CHANGED));
        return ResponseEntity.ok().body(successApproveInvitation);
    }


    @GetMapping(value = "reject/{eventId}")
    public ResponseEntity<SuccessResponse<String>> rejectInvitation(@PathVariable int eventId, @PathParam("email") String email) {
        logger.info("Try to reject invitation using email");
        EventDTO event = EventDTO.convertToEventDTO(eventService.approveOrRejectInvitation(email, eventId, Status.REJECTED));
        SuccessResponse<String> successRejectInvitation = new SuccessResponse<>( "Rejected invitation successfully", "Thank you for rejecting the invitation for event " + event.getTitle());
        notificationService.sendNotificationToGuestsEvent(new NotificationDetails(email + " reject his invitation",event, NotificationType.USER_STATUS_CHANGED));
        return ResponseEntity.ok().body(successRejectInvitation);
    }
}
