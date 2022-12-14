package calendar.repository;

import calendar.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface UserEventRepository extends JpaRepository<User, Long> {
        User findByEmail(String email);
}
