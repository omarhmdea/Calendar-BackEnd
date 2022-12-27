package calendar.service;

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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

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
    @InjectMocks
    AuthService authService;
    User user;
    UserNotification userNotification;
    @BeforeEach
    void newUser(){
        user = new User(2,"E", "e@gmail.com", "A123456", Set.of());
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
    void login_tryLogin_() {

    }

    @Test
    void findByToken() {
    }
}