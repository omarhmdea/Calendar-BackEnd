package calendar.controller;

import calendar.entities.User;
import calendar.entities.UserCredentials;
import calendar.response.SuccessResponse;
import calendar.service.AuthService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping("/auth")
public class AuthController {
    private static final Logger logger = LogManager.getLogger(AuthController.class.getName());

    @Autowired
    private AuthService authService;


    @PostMapping(value = "register")
    public ResponseEntity<SuccessResponse<User>> registerUser(@Valid @RequestBody User user) {
        SuccessResponse<User> response = new SuccessResponse<>();
  //      try {
//            Optional<CustomResponse<UserDTO>> isValid = checkValidEmail(user.getEmail(), response);
//            if(isValid.isPresent()){ return ResponseEntity.badRequest().body(isValid.get());}
//            isValid = checkValidName(user.getName(), response);
//            if(isValid.isPresent()){ return ResponseEntity.badRequest().body(isValid.get());}
//            isValid = checkValidPassword(user.getPassword(), response);
//            if(isValid.isPresent()){ return ResponseEntity.badRequest().body(isValid.get());}

        logger.info(user.getEmail() + " try to register");
        User registerUser = authService.registerUser(user);
        response.setData(registerUser);
        response.setHttpStatus(HttpStatus.OK);
        logger.info(user.getEmail() + " Registration was made successfully");
        return ResponseEntity.ok().body(response);
//        } catch (IllegalArgumentException e) {
//            logger.error(e.getMessage());
//            response.setMessage(e.getMessage());
//            return ResponseEntity.badRequest().body(response);
//        }
    }

    @RequestMapping(value = "login", method = RequestMethod.POST)
    public String login(@RequestBody UserCredentials userCredentials) {
        String token = authService.addTokenToUser(userCredentials);
        return token;
    }

}
