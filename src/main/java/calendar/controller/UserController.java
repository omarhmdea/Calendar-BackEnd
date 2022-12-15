package calendar.controller;

import calendar.entities.UserCredentials;
import calendar.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/event")
public class UserController {

    @Autowired
    private UserService userService;


    @GetMapping(value = "stam")
    public String stam(@RequestAttribute int userId) {
        return "I'am in stam func and i pass auth filter";
    }
}
