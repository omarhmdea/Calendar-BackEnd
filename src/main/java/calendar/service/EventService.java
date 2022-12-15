package calendar.service;

import calendar.entities.Event;
import calendar.entities.User;
import calendar.entities.UserEvent;
import calendar.enums.Role;
import calendar.enums.Status;
import calendar.repository.EventRepository;
import calendar.repository.UserEventRepository;
import calendar.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private UserEventRepository userEventRepository;
    @Autowired
    private UserRepository userRepository;

    public Event addNewEvent(int organizerId, Event newEvent){
        Event savedEvent = eventRepository.save(newEvent);
        Optional<User> organizer = userRepository.findById(organizerId);
        if(!organizer.isPresent()){
            throw new IllegalArgumentException("Invalid organizer id");
        }
        UserEvent userEvent = new UserEvent(organizer.get(), savedEvent, Status.APPROVED, Role.ORGANIZER);
        userEventRepository.save(userEvent);
        return savedEvent;
    }
}
