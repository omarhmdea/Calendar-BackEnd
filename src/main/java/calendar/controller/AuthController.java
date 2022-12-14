package calendar.controller;

import calendar.entities.User;
import calendar.entities.UserCredentials;
import calendar.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @RequestMapping(value = "register", method = RequestMethod.POST)
    public void register(@RequestBody User user) {
        authService.saveUSer(user);
    }

    @RequestMapping(value = "login", method = RequestMethod.POST)
    public String login(@RequestBody UserCredentials userCredentials) {
        String token = authService.addTokenToUser(userCredentials);
        return token;
    }

}
