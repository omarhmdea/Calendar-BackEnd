package calendar.service;

import calendar.entities.LoginData;
import calendar.entities.User;
import calendar.entities.UserCredentials;
import calendar.entities.UserDTO;
import calendar.repository.UserRepository;
import calendar.utilities.TokenGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthService {
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private UserRepository userRepository;
    private final Map<String, Integer> loginTokenId;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.loginTokenId = new HashMap<>();
    }

    // TODO : what is this for?
    public AuthService() {
        this.loginTokenId = new HashMap<>();
    }

    /**
     * Login using email and password
     * @param userCredentials - email , password
     * @return Login data - user's DTO and the generated token
     */
    // TODO : fix throw
    public LoginData login (UserCredentials userCredentials){
        Optional<User> user = userRepository.findByEmail(userCredentials.getEmail());
        if(!user.isPresent()){
            throw new IllegalArgumentException("Invalid email");
        }
        if(!bCryptPasswordEncoder.matches(userCredentials.getPassword(), user.get().getPassword())){
            throw new IllegalArgumentException("Invalid email or password");
        }
        String token = TokenGenerator.generateNewToken();
        loginTokenId.put(token ,user.get().getId());
        return new LoginData(new UserDTO(user.get()), token);
    }

    /**
     * Encode the user's password
     * And saves the new user in the database
     * @param user
     */
    public void saveUSer(User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    /**
     * Finds a user by token
     * @param token - the user's token
     * @return if the token is valid - the user that matches the given token
     *          if the token is invalid - Optional.empty
     */
    public Optional<User> findByToken(String token){
        if(loginTokenId.get(token) != null){
            Optional<User> user = userRepository.findById(loginTokenId.get(token));
            if(!user.isPresent()){
                return Optional.empty();
            }
            return user;
        }
        return Optional.empty();
    }
}
