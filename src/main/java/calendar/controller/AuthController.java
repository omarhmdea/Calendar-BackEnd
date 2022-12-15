package calendar.controller;

import calendar.ResponsHandler.SuccessResponse;
import calendar.entities.LoginData;
import calendar.entities.User;
import calendar.entities.UserCredentials;
import calendar.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping(value = "register")
    public void register(@RequestBody User user) {
        authService.saveUSer(user);
    }

    /**
     * Login using email and password
     * @param userCredentials - email, password
     * @return a SuccessResponse - OK status, a message, the login data - user's DTO and the generated token
     */
    @PostMapping(value = "loginEmail")
    public SuccessResponse<LoginData> login(@RequestBody UserCredentials userCredentials){
        LoginData loginData = authService.login(userCredentials);
        return new SuccessResponse<>(HttpStatus.OK, "Successful login", loginData);
    }
}
