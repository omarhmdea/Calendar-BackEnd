package calendar.repository;

import calendar.entities.User;
import calendar.entities.UserEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository

public interface UserEventRepository extends JpaRepository<UserEvent, Integer> {
   List<UserEvent> findByUser(User user);


}
