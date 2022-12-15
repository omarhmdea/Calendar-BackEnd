package calendar.controller;

import calendar.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;
}
