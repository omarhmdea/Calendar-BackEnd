package calendar.service;

import calendar.entities.Event;
import calendar.entities.User;
import calendar.entities.UserEvent;
import calendar.enums.Role;
import calendar.repository.EventRepository;
import calendar.repository.UserEventRepository;
import calendar.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserEventService {

    @Autowired
    private UserEventRepository userEventRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EventRepository eventRepository;

    //Check if there is a way to do this in JPA
    public Optional<UserEvent> findUserEventByUserIdAndEventId(int userId, int eventId) {

        Optional<User> user = userRepository.findById(userId);
        Optional<Event> event = eventRepository.findEventsById(eventId);

         return userEventRepository.findUserEventsByUserAndEvent(user.get(), event.get());
    }

}
