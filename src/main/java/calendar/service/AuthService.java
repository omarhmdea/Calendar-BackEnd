package calendar.service;

import calendar.repository.UserRepository;
import org.springframework.stereotype.Service;


import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {
    private final UserRepository userRepository;

    private final Map<String, String> loginTokens;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.loginTokens = new HashMap<>();
    }
}
