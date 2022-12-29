package calendar.service;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

import calendar.entities.*;
import calendar.entities.Credentials.UserNotificationCredentials;
import calendar.entities.DTO.UserDTO;
import calendar.enums.NotificationSettings;
import calendar.exception.customException.NotificationNotFoundException;
import calendar.repository.UserNotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {

    @Mock
    private UserNotificationRepository userNotificationRepository;

    @InjectMocks
    private NotificationService notificationService;

    User user;

    UserDTO userDTO;

    UserNotification userNotification;
    UserNotification updatedUserNotification;

    UserNotificationCredentials userNotificationCredentials;

    @BeforeEach
    void setUp(){
        user = new User(1, "Eden", "eden@gmail.com", "Eden123!@#", new HashSet<>());

        userDTO = UserDTO.convertToUserDTO(user);

        userNotification = new UserNotification(user);
        updatedUserNotification = new UserNotification(user);
        updatedUserNotification.setDeleteEvent(NotificationSettings.POPUP);

        userNotificationCredentials = UserNotificationCredentials.convertToUserNotificationCredentials(updatedUserNotification);
    }

    @Test
    void changeSettings_deleteEventPopUp_successChange(){
        given(userNotificationRepository.findByUserId(user.getId())).willReturn(Optional.ofNullable(userNotification));
        given(userNotificationRepository.save(userNotification)).willReturn(updatedUserNotification);
        assertEquals(updatedUserNotification, notificationService.changeSettings(user,userNotificationCredentials));
    }

    @Test
    void findUserNotification_noneExistingUser_failFind(){
        given(userNotificationRepository.findByUserId(user.getId())).willReturn(Optional.empty());
        assertThrows(NotificationNotFoundException.class, ()-> notificationService.findUserNotification(user));
    }

    @Test
    void findUserNotification_existingUserDTO_successFind(){
        given(userNotificationRepository.findByUserId(userDTO.getId())).willReturn(Optional.ofNullable(userNotification));
        assertEquals(userNotification, notificationService.findUserNotification(userDTO));
    }

    @Test
    void findUserNotification_noneExistingUserDTO_failFind(){
        given(userNotificationRepository.findByUserId(userDTO.getId())).willReturn(Optional.empty());
        assertThrows(NotificationNotFoundException.class, ()-> notificationService.findUserNotification(userDTO));
    }

//    @Test
//    public void testSendNotificationToGuestsEvent() {
//        // Create a mock event and a mock user
//        Event event = mock(Event.class);
//        User user = mock(User.class);
//        // Create a mock notification details object
//        NotificationDetails notificationDetails = new NotificationDetails("Test message", new EventDTO(), NotificationType.DELETE_EVENT);
//        // Create a mock UserNotification object
//        UserNotification userNotification = mock(UserNotification.class);
//        when(userNotification.getDeleteEvent()).thenReturn(NotificationSettings.EMAIL);
//        // When findUserNotification is called, return the mock UserNotification object
//        when(userNotificationRepository.findByUserId(user.getId())).thenReturn(Optional.of(userNotification));
//        // Call the sendNotificationToGuestsEvent method
//        notificationService.sendNotificationToGuestsEvent(notificationDetails);
//    }
}