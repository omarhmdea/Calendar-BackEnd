package calendar.service;

import calendar.entities.Event;
import calendar.entities.User;
import calendar.entities.UserEvent;
import calendar.enums.Role;
import calendar.enums.Status;
import calendar.repository.EventRepository;
import calendar.repository.UserEventRepository;
import calendar.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class EventService {
    private static final Logger logger = LogManager.getLogger(AuthService.class.getName());

    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private UserEventRepository userEventRepository;
    @Autowired
    private UserRepository userRepository;

    public Event addNewEvent(int organizerId, Event newEvent){
        logger.debug("Try to add new event");
        logger.debug("Check that the start date and time of the new event is valid : " + newEvent.getStart());
        if(newEvent.getStart().isBefore(LocalDateTime.now())){
            throw new IllegalArgumentException("Invalid start date or time - you cannot create new event for start time that had passed");
        }
        logger.debug("Check that the end date and time of the new event is valid : " + newEvent.getEnd());
        if(newEvent.getEnd().isBefore(LocalDateTime.now()) || newEvent.getEnd().isBefore(newEvent.getStart())){
            throw new IllegalArgumentException("Invalid end date or time - you cannot set end time that is previous to start time");
        }
        logger.debug("Check that the organizer with id " + organizerId + " exists in the db");
        Optional<User> organizer = userRepository.findById(organizerId);
        if(!organizer.isPresent()){
            throw new IllegalArgumentException("Invalid organizer id");
        }
        Event savedEvent = eventRepository.save(newEvent);
        logger.debug("New event is saved : " + newEvent);
        UserEvent userEvent = new UserEvent(organizer.get(), savedEvent, Status.APPROVED, Role.ORGANIZER);
        userEventRepository.save(userEvent);
        return savedEvent;
    }

    public UserEvent setGuestAsAdmin(int organizerId, String newAdminEmail, int eventId){
        logger.debug("Check if there exists a user with the given organizer id");
        Optional<User> organizer = userRepository.findById(organizerId);
        if(!organizer.isPresent()){
            throw new IllegalArgumentException("Invalid organizer id");
        }
        logger.debug("Check if there exists an event with the given event id");
        Optional<Event> event = eventRepository.findEventsById(eventId);
        if(!event.isPresent()){
            throw new IllegalArgumentException("Invalid event id");
        }
        logger.debug("Check if the user is the organizer of the event");
        Optional<UserEvent> organizerEvent = userEventRepository.findUserEventsByUserAndRole(organizer.get(), Role.ORGANIZER);
        if(!organizerEvent.isPresent()){
            throw new IllegalArgumentException("The given user is not the organizer of the event - and cannot set guests as admins");
        }
        logger.debug("Check if the new admin exists in the db");
        Optional<User> newAdmin = userRepository.findByEmail(newAdminEmail);
        if(!newAdmin.isPresent()){
            throw new IllegalArgumentException("Invalid new admin email - there is no user that matches the given email");
        }
        logger.debug("Check id the new admin approved the invitation");
        Optional<UserEvent> acceptedEvent = userEventRepository.findUserEventsByUserAndEventAndStatus(newAdmin.get(), event.get(), Status.APPROVED);
        if(!acceptedEvent.isPresent()){
            throw new IllegalArgumentException("The given new admin did not approve the event invitation - and cannot be set as admin");
        }
        acceptedEvent.get().setRole(Role.ADMIN);
        logger.debug("Set the guest as the event's admin " + acceptedEvent);
        return userEventRepository.save(acceptedEvent.get());
    }
}
