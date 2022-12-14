package calendar.controller;

import calendar.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/auth")

public class AuthController {

    @Autowired
    private AuthService authService;

}
