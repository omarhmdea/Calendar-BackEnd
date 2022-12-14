package calendar.service;

import calendar.entities.User;
import calendar.entities.UserCredentials;
import calendar.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;
    private final Map<String, String> tokens;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.tokens = new HashMap<>();
    }

    public AuthService() {
        this.tokens = new HashMap<>();
    }

    public void saveUSer(User user) {
        userRepository.save(user);
    }

    public String addTokenToUser(UserCredentials user) {
        String token = createToken();
        tokens.put(token, user.getEmail());
        return token;
    }

    public User findByToken(String token) {
        if(tokens.containsKey(token)) {
            return userRepository.findByEmail(tokens.get(token));
        }
        return null;
    }

    private String createToken() {
        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder stringBuilder;
        do {
            stringBuilder = new StringBuilder(6);
            for(int i = 0; i < 6; i++) {
                stringBuilder.append(chars.charAt(ThreadLocalRandom.current().nextInt(chars.length())));
            }
        }
        while(tokens.get(stringBuilder) != null);
        return stringBuilder.toString();
    }
}
