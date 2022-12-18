package calendar.repository;

import calendar.entities.Event;
import calendar.entities.User;
import calendar.entities.UserEvent;
import calendar.enums.Role;
import calendar.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository

public interface UserEventRepository extends JpaRepository<UserEvent, Integer> {
    List<UserEvent> findByUser(User user);
    Optional<UserEvent> findUserEventsByEventAndRole(Event event, Role role);
    Optional<UserEvent> findUserEventsByUserAndRole(User user, Role role);
    Optional<UserEvent> findUserEventsByUserAndEventAndStatus(User user, Event event, Status status);
    Optional<UserEvent> findUserEventsByUserAndEvent(User user, Event event);
    List<UserEvent> findByEvent(Event event);
}
