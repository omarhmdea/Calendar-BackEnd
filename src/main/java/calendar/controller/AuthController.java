package calendar.controller;

import calendar.ResponsHandler.SuccessResponse;
import calendar.entities.LoginData;
import calendar.entities.User;
import calendar.entities.UserCredentials;
import calendar.entities.UserDTO;
import calendar.exception.customException.ValidationErrorException;
import calendar.service.AuthService;
import calendar.utilities.Validator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import javax.validation.ValidationException;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping("/auth")
public class AuthController {
    private static final Logger logger = LogManager.getLogger(AuthController.class.getName());

    @Autowired
    private AuthService authService;

    /**
     * Register using email, password and name
     * @param user
     * @return
     */
    @PostMapping(value = "register")
    public ResponseEntity<SuccessResponse<UserDTO>> registerUser(@Valid @RequestBody User user) {
        logger.info(user.getEmail() + " try to register");

        Optional<Map<String, String>> validationErrors = Validator.validateRegister(user);
        if(validationErrors.isPresent()) {
            throw new ValidationErrorException("validate input", validationErrors.get());
        }

        User registerUser = authService.registerUser(user);
        SuccessResponse<UserDTO> successResponse = new SuccessResponse<>(HttpStatus.OK, "Successful registration", new UserDTO(registerUser));
        logger.info(user.getEmail() + " Registration was made successfully");
        return ResponseEntity.ok().body(successResponse);
    }

    /**
     * Login using email and password
     * @param userCredentials - email, password
     * @return a SuccessResponse - OK status, a message, the login data - user's DTO and the generated token
     */
    @PostMapping(value = "loginEmail")
    public ResponseEntity<SuccessResponse<LoginData>> login(@RequestBody UserCredentials userCredentials){
        Optional<Map<String, String>> validationErrors = Validator.validateLogin(userCredentials);
        if(validationErrors.isPresent()) {
            throw new ValidationErrorException("validate input", validationErrors.get());
        }

        LoginData loginData = authService.login(userCredentials);
        SuccessResponse<LoginData> successResponse = new SuccessResponse<>(HttpStatus.OK, "Successful registration", loginData);
        return ResponseEntity.ok().body(successResponse);
    }
}
