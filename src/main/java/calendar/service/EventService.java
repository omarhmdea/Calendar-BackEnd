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

import java.time.LocalDateTime;
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
        if(newEvent.getStart().isBefore(LocalDateTime.now())){
            throw new IllegalArgumentException("Invalid start date or time - you cannot create new event for start time that had passed");
        }
        if(newEvent.getEnd().isBefore(LocalDateTime.now()) || newEvent.getEnd().isBefore(newEvent.getStart()){
            throw new IllegalArgumentException("Invalid end date or time - you cannot set end time that is previous to start time");
        }
        Event savedEvent = eventRepository.save(newEvent);
        Optional<User> organizer = userRepository.findById(organizerId);
        if(!organizer.isPresent()){
            throw new IllegalArgumentException("Invalid organizer id");
        }
        UserEvent userEvent = new UserEvent(organizer.get(), savedEvent, Status.APPROVED, Role.ORGANIZER);
        userEventRepository.save(userEvent);
        return savedEvent;
    }

    public UserEvent setGuestAsAdmin(int organizerId, String newAdminEmail, int eventId){
        // check if there exists a user with the given organizer id
        Optional<User> organizer = userRepository.findById(organizerId);
        if(!organizer.isPresent()){
            throw new IllegalArgumentException("Invalid organizer id");
        }
        // check if there exists an event with the given event id
        Optional<Event> event = eventRepository.findEventsById(eventId);
        if(!event.isPresent()){
            throw new IllegalArgumentException("Invalid event id");
        }
        // check if the user is the organizer of the event
        Optional<UserEvent> organizerEvent = userEventRepository.findUserEventsByUserAndRole(organizer.get(), Role.ORGANIZER);
        if(!organizerEvent.isPresent()){
            throw new IllegalArgumentException("The given user is not the organizer of the event - and cannot set guests as admins");
        }
        // check if the new admin exists in the db
        Optional<User> newAdmin = userRepository.findByEmail(newAdminEmail);
        if(!newAdmin.isPresent()){
            throw new IllegalArgumentException("Invalid new admin email - there is no user that matches the given email");
        }
        // check id the new admin approved the invitation
        Optional<UserEvent> acceptedEvent = userEventRepository.findUserEventsByUserAndEventAndStatus(organizer.get(), event.get(), Status.APPROVED);
        if(!acceptedEvent.isPresent()){
            throw new IllegalArgumentException("The given new admin did not approve the event invitation - and cannot be set as admin");
        }
        acceptedEvent.get().setRole(Role.ADMIN);
        return userEventRepository.save(acceptedEvent.get());
    }
}
