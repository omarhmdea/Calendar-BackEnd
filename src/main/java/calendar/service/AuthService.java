package calendar.service;

import calendar.entities.*;
import calendar.entities.Credentials.UserCredentials;
import calendar.entities.DTO.LoginDTO;
import calendar.entities.DTO.UserDTO;
import calendar.exception.customException.UserNotFoundException;
import calendar.repository.UserNotificationRepository;
import calendar.repository.UserRepository;
import calendar.utilities.Github.GitRequest;
import calendar.utilities.Github.GitToken;
import calendar.utilities.Github.GitUser;
import calendar.utilities.TokenGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthService {
    private static final Logger logger = LogManager.getLogger(AuthService.class.getName());

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserNotificationRepository userNotificationRepository;
    @Autowired
    private Environment environment;

    private final Map<String, Integer> loginTokenId;

    public AuthService() {
        this.loginTokenId = new HashMap<>();
    }

    /**
     * Encode the user's password
     * And saves the new user in the database
     * @param user the user's info
     * @return the user after it was saved n the db
     */
    public User registerUser(User user) {
        logger.info("Try to register using email");
        logger.debug("Check if email already exist in DB");
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            logger.error("Email already in use");
            throw new IllegalArgumentException("Email already in use");
        }
        logger.info("New user saved in the DB");
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);
        userNotificationRepository.save(new UserNotification(savedUser));
        return savedUser;
    }

    /**
     * Login using email and password
     * @param userCredentials - email , password
     * @return Login data - user's DTO and the generated token
     */
    public LoginDTO login (UserCredentials userCredentials){
        logger.info("Try to login using email");
        Optional<User> user = userRepository.findByEmail(userCredentials.getEmail());
        if(!user.isPresent()){
            throw new IllegalArgumentException("Invalid email");
        }
        if(!bCryptPasswordEncoder.matches(userCredentials.getPassword(), user.get().getPassword())){
            throw new IllegalArgumentException("Invalid email or password");
        }
        String token = TokenGenerator.generateNewToken();
        loginTokenId.put(token ,user.get().getId());
        logger.info("Successful login using email");
        return new LoginDTO(UserDTO.convertToUserDTO(user.get()), token);
    }

    /**
     * Login using github
     * @param code - unique code that is generated by github after clicking "authorize"
     * @return - Login data - user's DTO and the generated token
     */
    public LoginDTO login(String code) {
        logger.info("Try to login using github");
        GitUser githubUser = getGithubUser(code);
        if(githubUser == null || githubUser.getEmail() == null){
            throw new UserNotFoundException("Git hub user account was not found");
        }
        Optional<User> user = userRepository.findByEmail(githubUser.getEmail());
        if (user.isPresent()) {
            logger.info("User has logged in in the past using github. Login to account now");
            String token = TokenGenerator.generateNewToken();
            loginTokenId.put(token ,user.get().getId());
            logger.info("Successful not first time login using github");
            return new LoginDTO(UserDTO.convertToUserDTO(user.get()), token);
        }
        logger.info("First time login using github");
        User newUser;
        if (githubUser.getName() != null && githubUser.getName() != "") {
             newUser = new User(githubUser.getName(), githubUser.getEmail(), githubUser.getAccessToken());
        } else {
            newUser = new User(githubUser.getEmail(), githubUser.getEmail(), githubUser.getAccessToken());
        }
        User savedUser = userRepository.save(newUser);
        String token = TokenGenerator.generateNewToken();
        loginTokenId.put(token ,savedUser.getId());
        logger.info("Successful first time login using github");
        return new LoginDTO(UserDTO.convertToUserDTO(savedUser), token);
    }

    /**
     * Get the user info from github -
     * from <a href="https://api.github.com/user"></a>
     * @param code - the code that was generated by github after clicking "authorize"
     * @return Git User data - login, name, email access token
     */
    private GitUser getGithubUser(String code) {
        logger.info("Try to get github user by code");
        GitToken gitTokenResponse = getGithubToken(code);
        if(gitTokenResponse == null){
            logger.error("Cannot get user from code");
            return null;
        }
        try {
            String token = gitTokenResponse.getAccess_token();
            String linkGetUser = "https://api.github.com/user";
            logger.info("Got github user successfully");
            return GitRequest.reqGitGetUser(linkGetUser, token);
        } catch (NullPointerException e) {
            logger.error("Cannot get user from code " + e.getMessage());
            return null;
        }
    }

    /**
     * First request to gitHub authorization process
     * Get the token authorization from - <a href="https://github.com/login/oauth/access_token"></a>
     *      by the client-id and client-secret
     * @param code - the code that was generated by github after clicking "authorize"
     * @return Git Token data - access token, token type, scope
     */
    private GitToken getGithubToken(String code) {
        logger.info("Try to get github token by code");
        logger.info("in AuthService -> getGithubToken");
        String baseLink = "https://github.com/login/oauth/access_token?";
        String clientId = environment.getProperty("spring.security.oauth2.client.registration.github.client-id");
        String clientSecret = environment.getProperty("spring.security.oauth2.client.registration.github.client-secret");
        String linkGetToken = baseLink + "client_id=" + clientId + "&client_secret=" + clientSecret + "&code=" + code;
        logger.info("Got github token successfully");
        return GitRequest.reqGitGetToken(linkGetToken);
    }

    /**
     * Finds a user by token
     * First search user id by token from loginTokenId map
     * Then, if the id was found, find the user by id
     * @param token - the user's token
     * @return if the token is valid - the user that matches the given token
     *          if the token is invalid / no such user was found in the db - Optional.empty
     */
    public Optional<User> findByToken(String token) {
        if (loginTokenId.containsKey(token)) {
            Optional<User> user = userRepository.findById(loginTokenId.get(token));
            if (user.isPresent()) {
                return user;
            }
        }
        return Optional.empty();
    }
}
