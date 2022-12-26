package calendar.controller;

import calendar.ResponsHandler.SuccessResponse;
import calendar.entities.DTO.LoginDTO;
import calendar.entities.User;
import calendar.entities.Credentials.UserCredentials;
import calendar.entities.DTO.UserDTO;
import calendar.exception.customException.ValidationErrorException;
import calendar.service.AuthService;
import calendar.utilities.Validator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.Map;
import java.util.Optional;
import java.net.URI;

@RestController
@CrossOrigin
@RequestMapping("/auth")
public class AuthController {
    private static final Logger logger = LogManager.getLogger(AuthController.class.getName());

    @Autowired
    private AuthService authService;

    /**
     * Register using email, password and name
     * @param user - the user's info
     * @return a SuccessResponse - OK status, a message, user's DTO - email and name
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
    @PostMapping(value = "login/email")
    public ResponseEntity<SuccessResponse<LoginDTO>> login(@RequestBody UserCredentials userCredentials){
        Optional<Map<String, String>> validationErrors = Validator.validateLogin(userCredentials);
        if(validationErrors.isPresent()) {
            throw new ValidationErrorException("validate input", validationErrors.get());
        }
        LoginDTO loginDTO = authService.login(userCredentials);
        SuccessResponse<LoginDTO> successResponse = new SuccessResponse<>(HttpStatus.OK, "Successful login", loginDTO);
        logger.info(loginDTO.getUser().getEmail() + " login was made successfully");
        return ResponseEntity.ok().body(successResponse);
    }

    // TODO : why not post??
    @GetMapping(value = "login/github")
    public ResponseEntity<SuccessResponse<LoginDTO>> login(@RequestParam String code){
        logger.debug("Try to login using github");
        LoginDTO loginDTO = authService.login(code);
        SuccessResponse<LoginDTO> successResponse = new SuccessResponse<>(HttpStatus.OK, "Successful login", authService.loginGithub(code));
//        logger.info(loginDTO.getUser().getEmail() + " login was made successfully");
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("http://localhost:3000"));
        return new ResponseEntity<>(successResponse, headers, HttpStatus.MOVED_PERMANENTLY);
//        return ResponseEntity.ok().body(successResponse);
    }
}
