package calendar.repository;

import calendar.entities.UserNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface UserNotificationRepository extends JpaRepository<UserNotification, Integer> {

}
