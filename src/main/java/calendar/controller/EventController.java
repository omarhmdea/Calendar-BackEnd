package calendar.controller;

import calendar.ResponsHandler.SuccessResponse;
import calendar.entities.LoginData;
import calendar.entities.UserCredentials;
import calendar.exception.customException.ValidationErrorException;
import calendar.utilities.Validator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping("/event")
public class EventController {


    @PostMapping(value = "invite/{eventId}")
    public void invite(@RequestAttribute int userId,@PathVariable int eventId,@RequestBody String guestEmail ) {

    }
}
