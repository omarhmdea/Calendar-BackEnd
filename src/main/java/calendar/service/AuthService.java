package calendar.service;

import calendar.entities.User;
import calendar.entities.UserCredentials;
import calendar.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class AuthService {
    private static final Logger logger = LogManager.getLogger(AuthService.class.getName());
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


    public User registerUser(User user) {
       // try {
            logger.debug("Check if already exist in DB");
            if (userRepository.findByEmail(user.getEmail()) != null) {
                logger.error("Email already exists in users table");
                throw new IllegalArgumentException("Email already exists in users table");
            }
            logger.info("New user saved in the DB");
       // user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
//        } catch (RuntimeException e) {
//            logger.error(e.getMessage());
//            throw new IllegalArgumentException(e.getMessage());
//        }
    }


    public String addTokenToUser(UserCredentials user) {
        String token = createToken();
        tokens.put(token, user.getEmail());
        return token;
    }

    public Optional<User> findByToken(String token) {
        if(tokens.containsKey(token)) {
            return Optional.of(userRepository.findByEmail(tokens.get(token)));
        }
        return Optional.empty();
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
