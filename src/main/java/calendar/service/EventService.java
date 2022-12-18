package calendar.service;

import calendar.entities.Event;
import calendar.entities.User;
import calendar.entities.UserEvent;
import calendar.enums.NotificationType;
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
import java.time.LocalDateTime;
import java.util.List;
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
    @Autowired
    private  NotificationService notificationService;

    /**
     * Add new event to the user's calendar
     * @param organizerId - the user that is trying to create the event
     * @param newEvent - the new event data
     * @return the new event after it was saved in the db
     */
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

    /**
     * Set guest as admin in the given event
     * @param organizerId - the user that created the event
     * @param newAdminEmail - the email of the guest that the organizer wants to set as admin
     * @param eventId - the event to set new admin to
     * @return User event - the event id, the new admin id, the new admin role (admin), the new admin status (approved)
     */
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
        logger.debug("Check if the eventUser exist in DB");
        List<UserEvent> dbEventUser = userEventRepository.findByUser(user.get());
        if (CollectionUtils.isEmpty(dbEventUser)) {
            throw new IllegalArgumentException("Invalid user id, user doesn't exist in userEvent repository");
        }
        // TODO : use findUserEventsByUserAndEventAndStatus
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
        if (userEventFromRepo.get().getRole() == Role.ORGANIZER) {
            dbEvent.get().setStart(updateEvent.getStart());
            dbEvent.get().setEnd(updateEvent.getEnd());
            dbEvent.get().setTitle(updateEvent.getTitle());
        }

        dbEvent.get().setIsPublic(updateEvent.getIsPublic());
        dbEvent.get().setLocation(updateEvent.getLocation());
        dbEvent.get().setDescription(updateEvent.getDescription());
        dbEvent.get().setAttachments(updateEvent.getAttachments());

        logger.info("Save the updated event in DB");
        return eventRepository.save(dbEvent.get());
    }
    /**
     * Delete event : delete event from DB
     * @param userId  - the user id
     * @param deleteEvent  - the event to delete
     * @return Deleted event
     * @throws IllegalArgumentException when the delete event failed
     */
    public Event deleteEvent(int userId, int deleteEvent){
        logger.debug("Check if the user exist in DB");
        Optional<User> user = userRepository.findById(userId);
        if(!user.isPresent()){
            throw new IllegalArgumentException("Invalid user id");
        }

        logger.debug("Check if the event exist in DB");
        Optional<Event> dbEvent = eventRepository.findById(deleteEvent);
        if (!dbEvent.isPresent()) {
            throw new IllegalArgumentException("Invalid event id");
        }

        logger.debug("Check if the eventUser exist in DB");
        Optional<UserEvent> acceptedEvent = userEventRepository.findUserEventsByUserAndEvent(user.get(), dbEvent.get());
        if(!acceptedEvent.isPresent()){
            throw new IllegalArgumentException("The given new admin did not approve the event invitation - and cannot be set as admin");
        }

        logger.debug("Delete the event from DB");
        notificationService.sendNotification(dbEvent.get(), NotificationType.EVENT_CANCELED);
        userEventRepository.delete(acceptedEvent.get());
        eventRepository.delete(dbEvent.get());
        return dbEvent.get();
    }




    private boolean isFieldsAdminCanNotChange(Event dbEvent, Event updatedEvent) {
        return (!updatedEvent.getStart().equals(dbEvent.getStart())  ) ||
                !updatedEvent.getEnd().equals(dbEvent.getEnd()) ||
                !updatedEvent.getTitle().equals(dbEvent.getTitle());
    }
}
