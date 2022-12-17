package calendar.service;

import calendar.controller.AuthController;
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
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class EventService {
    private static final Logger logger = LogManager.getLogger(EventService.class.getName());

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
    public Event updateEvent(int userId, Event updateEvent){
        logger.debug("Check if the user exist in DB");
        Optional<User> organizer = userRepository.findById(userId);
        if(!organizer.isPresent()){
           // logger.debug("Invalid user id");
            throw new IllegalArgumentException("Invalid user id");
        }

        logger.debug("Check if the event exist in DB");
        Optional<Event> dbEvent = eventRepository.findById(updateEvent.getId());
        if (!dbEvent.isPresent()) {
           // logger.debug("Invalid event id");
            throw new IllegalArgumentException("Invalid event id");
        }

        logger.debug("Check if the eventUser exist in DB");
        List<UserEvent> dbEventUser = userEventRepository.findByUser(organizer.get());
        if (CollectionUtils.isEmpty(dbEventUser)) {
           // logger.debug("Invalid user id, user doesn't exist in userEvent repository");
            throw new IllegalArgumentException("Invalid user id, user doesn't exist in userEvent repository");
        }

        logger.debug("Take the role of the user in specific event");
        Optional<UserEvent> userEventFromRepo = dbEventUser.stream().filter(u -> u.getEvent().getId() == updateEvent.getId()).findFirst();
        if (!userEventFromRepo.isPresent()) {
           // logger.debug("Invalid event id, event for this user doesn't exist in userEvent repository");
            throw new IllegalArgumentException("Invalid event id, event for this user doesn't exist in userEvent repository");
        }

        logger.debug("Check if admin allowed to change fields");
        if (userEventFromRepo.get().getRole()==Role.ADMIN && isFieldsAdminCanNotChange(updateEvent)) {
           // logger.debug("Admin not allowed to change one of those fields");
            throw new IllegalArgumentException("Admin not allowed to change one of those fields");
        }

        logger.debug("Update event data");
        if (userEventFromRepo.get().getRole()==Role.ORGANIZER){
            dbEvent.get().setTime(updateEvent.getTime());
            dbEvent.get().setDate(updateEvent.getDate());
            dbEvent.get().setDuration(updateEvent.getDuration());
            dbEvent.get().setTitle(updateEvent.getTitle());

        }

        dbEvent.get().setPublic(updateEvent.isPublic());
        dbEvent.get().setLocation(updateEvent.getLocation());
        dbEvent.get().setDescription(updateEvent.getDescription());
        dbEvent.get().setAttachments(updateEvent.getAttachments());

        logger.info("Save the updated event in DB");
        userEventRepository.save(new UserEvent(organizer.get(), dbEvent.get(), Status.APPROVED, Role.ORGANIZER));
        return eventRepository.save(dbEvent.get());
    }

    private boolean isFieldsAdminCanNotChange(Event event) {

        return (event.getTime() != null) || event.getDuration() != 0 ||
                event.getDate() != null || !event.getTitle().equals("");
    }
}
