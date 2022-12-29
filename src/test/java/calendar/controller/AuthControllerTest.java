package calendar.controller;

import calendar.entities.Credentials.UserCredentials;
import calendar.entities.DTO.LoginDTO;
import calendar.entities.DTO.UserDTO;
import calendar.entities.User;
import calendar.exception.customException.ValidationErrorException;
import calendar.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import java.util.Objects;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {
    @Mock
    private AuthService authService;

    @InjectMocks
    AuthController authController;

    User user;
    UserCredentials userCredentials;
    @BeforeEach
    void newUser(){
        user = new User(2,"E", "e@gmail.com", "A123456", Set.of());
        userCredentials = new UserCredentials(user.getEmail(), user.getPassword());
    }

    @Test
    void registerUser_checkValidEmail_notValidEmail() {
        user.setEmail("R");
        assertThrows(ValidationErrorException.class,()->authController.registerUser(user));
    }
    @Test
    void registerUser_checkValidName_notValidName() {
        user.setName("9Eli");
        assertThrows(ValidationErrorException.class,()->authController.registerUser(user));
    }
    @Test
    void registerUser_checkValidPassword_notValidPassword() {
        user.setPassword("9hj");
        assertThrows(ValidationErrorException.class,()->authController.registerUser(user));
    }

    @Test
    void registerUser_tryToRegister_SuccessRegistrationAndUserData() {
        given(authService.registerUser(user)).willReturn(user);
        assertEquals(UserDTO.convertToUserDTO(user), Objects.requireNonNull(authController.registerUser(user).getBody()).getData());
    }
    @Test
    void registerUser_tryToRegister_SuccessRegistrationAndStatusCode() {
        given(authService.registerUser(user)).willReturn(user);
        assertEquals(HttpStatus.OK, Objects.requireNonNull(authController.registerUser(user).getStatusCode()));
    }

    @Test
    void login_tryToLogin_checkValidEmail_notValidEmail(){
        userCredentials.setEmail("R");
        assertThrows(ValidationErrorException.class,()->authController.login(userCredentials));
    }
    @Test
    void login_tryToLogin_checkValidPassword_notValidPassword(){
        userCredentials.setPassword("9hj");
        assertThrows(ValidationErrorException.class,()->authController.login(userCredentials));
    }
    @Test
    void login_tryToLogin_SuccessLoginAndUserData(){
        LoginDTO loginDTO = new LoginDTO(UserDTO.convertToUserDTO(user),"token");
        given(authService.login(userCredentials)).willReturn(loginDTO);
        assertEquals(loginDTO, Objects.requireNonNull(authController.login(userCredentials).getBody()).getData());
    }
    @Test
    void login_tryToLogin_SuccessLoginAndStatusCode(){
        LoginDTO loginDTO = new LoginDTO(UserDTO.convertToUserDTO(user),"token");
        given(authService.login(userCredentials)).willReturn(loginDTO);
        assertEquals(HttpStatus.OK, Objects.requireNonNull(authController.login(userCredentials).getStatusCode()));
    }
    @Test
    void loginViaGithub_tryToLogin_SuccessLoginAndUserData(){
        LoginDTO loginDTO = new LoginDTO(UserDTO.convertToUserDTO(user),"token");
        given(authService.login("code")).willReturn(loginDTO);
        assertEquals(loginDTO, Objects.requireNonNull(authController.login("code").getBody()).getData());
    }
    @Test
    void loginViaGithub_SuccessLoginAndStatusCode(){
        LoginDTO loginDTO = new LoginDTO(UserDTO.convertToUserDTO(user),"token");
        given(authService.login("code")).willReturn(loginDTO);
        assertEquals(HttpStatus.OK, Objects.requireNonNull(authController.login("code").getStatusCode()));
    }
}
