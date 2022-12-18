package calendar.repository;

import calendar.entities.User;
import calendar.entities.UserNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository

public interface UserNotificationRepository extends JpaRepository<UserNotification, Integer> {
    Optional<UserNotification> findByUser(User user);
}
