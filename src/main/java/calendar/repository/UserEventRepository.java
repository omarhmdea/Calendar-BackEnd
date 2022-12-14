package calendar.repository;

import calendar.entities.User;
import calendar.entities.UserEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface UserEventRepository extends JpaRepository<UserEvent, Integer> {
}
