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

import java.time.LocalDateTime;
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

    /**
     * Update event : update event data for admin & organizer
     * @param userId  - the user id
     * @param updateEvent  - the update event
     * @return event with updated data
     * @throws IllegalArgumentException when the Update event failed
     */
    public Event updateEvent(int userId, Event updateEvent){
        logger.debug("Check if the user exist in DB");
        Optional<User> user = userRepository.findById(userId);
        if(!user.isPresent()){
            throw new IllegalArgumentException("Invalid user id");
        }

        logger.debug("Check if the event exist in DB");
        Optional<Event> dbEvent = eventRepository.findById(updateEvent.getId());
        if (!dbEvent.isPresent()) {
            throw new IllegalArgumentException("Invalid event id");
        }

        System.out.println(dbEvent.get());
        logger.debug("Check if the eventUser exist in DB");
        List<UserEvent> dbEventUser = userEventRepository.findByUser(user.get());
        if (CollectionUtils.isEmpty(dbEventUser)) {
            throw new IllegalArgumentException("Invalid user id, user doesn't exist in userEvent repository");
        }

        logger.debug("Take the role of the user in specific event");
        Optional<UserEvent> userEventFromRepo = dbEventUser.stream().filter(u -> u.getEvent().getId() == updateEvent.getId()).findFirst();
        if (!userEventFromRepo.isPresent()) {
            throw new IllegalArgumentException("Invalid event id, event for this user doesn't exist in userEvent repository");
        }

        logger.debug("Check if admin allowed to change fields");
        if (userEventFromRepo.get().getRole()==Role.ADMIN && isFieldsAdminCanNotChange(userEventFromRepo.get().getEvent(),updateEvent)) {
            throw new IllegalArgumentException("Admin not allowed to change one of those fields");
        }

        if(updateEvent.getStart().isBefore(LocalDateTime.now()) || updateEvent.getEnd().isBefore(updateEvent.getStart())) {
            throw new IllegalArgumentException("Invalid start date or end date");
        }

            logger.debug("Update event data");
        if (userEventFromRepo.get().getRole()==Role.ORGANIZER) {
            dbEvent.get().setStart(updateEvent.getStart());
            dbEvent.get().setEnd(updateEvent.getEnd());
            dbEvent.get().setDuration(updateEvent.getDuration());
            dbEvent.get().setTitle(updateEvent.getTitle());
        }

        dbEvent.get().setIsPublic(updateEvent.getIsPublic());
        dbEvent.get().setLocation(updateEvent.getLocation());
        dbEvent.get().setDescription(updateEvent.getDescription());
        dbEvent.get().setAttachments(updateEvent.getAttachments());

        logger.info("Save the updated event in DB");
        System.out.println(dbEvent.get());
        return eventRepository.save(dbEvent.get());
    }

    private boolean isFieldsAdminCanNotChange(Event dbEvent, Event updatedEvent) {

        return (!updatedEvent.getStart().equals(dbEvent.getStart())  ) || updatedEvent.getDuration() != dbEvent.getDuration() ||
                !updatedEvent.getEnd().equals(dbEvent.getEnd()) || !updatedEvent.getTitle().equals(dbEvent.getTitle());
    }
}
