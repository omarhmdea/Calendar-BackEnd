package calendar.service;

import calendar.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;
    private final Map<String, Integer> tokens;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.tokens = new HashMap<>();
    }

    public AuthService() {
        this.tokens = new HashMap<>();
    }

}
