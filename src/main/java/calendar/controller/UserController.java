package calendar.controller;

import calendar.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/user")
public class UserController {
    private static final Logger logger = LogManager.getLogger(EventController.class.getName());

    @Autowired
    private UserService userService;

    @GetMapping(value = "stam")
    public String stam(@RequestAttribute int userId) {
        return "I'am in stam func and i pass auth filter";
    }
}
