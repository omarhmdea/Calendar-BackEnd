package calendar.service;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import calendar.entities.User;
import calendar.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserService service;

    @Mock
    private UserRepository userRepository;

    @Test
    public void testFindById_Success() {
        // Arrange
        int id = 1;
        User user = new User(); // dummy user object
        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        // Act
        User result = service.findById(id);

        // Assert
        assertEquals(user, result);
    }

}
