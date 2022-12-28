package calendar.service;

import calendar.entities.Credentials.UserCredentials;
import calendar.entities.DTO.LoginDTO;
import calendar.entities.DTO.UserDTO;
import calendar.entities.User;
import calendar.entities.UserNotification;
import calendar.repository.UserNotificationRepository;
import calendar.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock
    UserRepository userRepository;
    @Mock
    UserNotificationRepository userNotificationRepository;
    @Mock
    BCryptPasswordEncoder bCryptPasswordEncoder;
    @Mock
    private Environment environment;

    @InjectMocks
    AuthService authService;
    User user;
    UserCredentials userCredentials;
    UserNotification userNotification;

    @BeforeEach
    void newUser(){
        user = new User(2,"E", "e@gmail.com", "A123456", Set.of());
        userCredentials = new UserCredentials(user.getEmail(), user.getPassword());
        userNotification = new UserNotification(user);
    }
    @Test
    void registerUser_tryToRegister_successRegistration() {
        given(userRepository.findByEmail(user.getEmail())).willReturn(Optional.empty());
        given(bCryptPasswordEncoder.encode(user.getPassword())).willReturn(user.getPassword());
        given(userRepository.save(user)).willReturn(user);
        given(userNotificationRepository.save(userNotification)).willReturn(userNotification);
        assertEquals(user,authService.registerUser(user));
    }
    @Test
    void registerUser_tryToRegister_failRegistrationUserExist() {
        given(userRepository.findByEmail(user.getEmail())).willReturn(Optional.ofNullable(user));
        assertThrows(IllegalArgumentException.class,()-> authService.registerUser(user));
    }
    @Test
    void login_tryToLogin_failLoginInvalidEmail() {
        given(userRepository.findByEmail(userCredentials.getEmail())).willReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class,()-> authService.login(userCredentials));
    }
    @Test
    void login_tryToLogin_failLoginInvalidPassword() {
        given(userRepository.findByEmail(userCredentials.getEmail())).willReturn(Optional.ofNullable(user));
        given(bCryptPasswordEncoder.matches(userCredentials.getPassword(), user.getPassword())).willReturn(false);
        assertThrows(IllegalArgumentException.class,()-> authService.login(userCredentials));
    }

    @Test
    void login_tryToLogin_successLoginAndUserData() {
        given(userRepository.findByEmail(userCredentials.getEmail())).willReturn(Optional.ofNullable(user));
        given(bCryptPasswordEncoder.matches(userCredentials.getPassword(), user.getPassword())).willReturn(true);
        assertEquals(new LoginDTO(new UserDTO(user), "token").getUser(),authService.login(userCredentials).getUser());
    }

//    @Test
//    void loginViaGithub_tryToLoginUserExist_SuccessLoginAndGetNewToken(){
//        given(userRepository.findByEmail(userCredentials.getEmail())).willReturn(Optional.ofNullable(user));
//        given(environment.getProperty("string")).willReturn("string");
//        LoginDTO loginDTO = new LoginDTO(new UserDTO(user),"token");
//        assertEquals(loginDTO, Objects.requireNonNull(authService.login("code").getUser()));
//    }
}
//    LoginDTO loginDTO = new LoginDTO(new UserDTO(user),"token");
//    given(authService.login("code")).willReturn(loginDTO);
//        assertEquals(loginDTO, Objects.requireNonNull(authController.login("code").getBody()).getData());
//        }