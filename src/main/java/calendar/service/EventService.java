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

    /**
     * Add new event to the user's calendar
     * @param organizerId - the user that is trying to create the event
     * @param newEvent - the new event data
     * @return the new event after it was saved in the db
     */
    public Event addNewEvent(int organizerId, Event newEvent){
        logger.debug("Try to add new event");
        checkDateAndTime(newEvent);
        logger.debug("Check that the organizer with id " + organizerId + " exists in the db");
        User organizer = findUser(organizerId);
        Event savedEvent = eventRepository.save(newEvent);
        logger.debug("New event is saved : " + savedEvent.toString());
        UserEvent userEvent = new UserEvent(organizer, savedEvent, Status.APPROVED, Role.ORGANIZER);
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
        User organizer = findUser(organizerId);
        logger.debug("Check if there exists an event with the given event id");
        Event event = findEvent(eventId);
        logger.debug("Check if the user is the organizer of the event");
        Optional<UserEvent> organizerEvent = userEventRepository.findUserEventsByUserAndEventAndRole(organizer, event, Role.ORGANIZER);
        if(!organizerEvent.isPresent()){
            throw new IllegalArgumentException("The given user is not the organizer of the event - and cannot set guests as admins");
        }
        logger.debug("Check if the new admin exists in the db");
        User newAdmin = findUser(newAdminEmail);
        logger.debug("Check if the new admin approved the invitation");
        Optional<UserEvent> acceptedEvent = userEventRepository.findUserEventsByUserAndEventAndStatus(newAdmin, event, Status.APPROVED);
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
        logger.debug("Check if there exists a user with the given id (organizer or admin) in DB");
        User user = findUser(userId);
        logger.debug("Check if the event exist in DB");
        Event event = findEvent(updateEvent.getId());
        logger.debug("Check if the user is a part of the event");
        Optional<UserEvent> userInEvent = userEventRepository.findUserEventsByUserAndEvent(user, event);
        if(!userInEvent.isPresent()){
            throw new IllegalArgumentException("The user that is trying to change the event data is not a part of the event");
        }
        logger.debug("Check if the user is the organizer or the admin of the event");
        if(userInEvent.get().getRole() != Role.ADMIN && userInEvent.get().getRole() != Role.ORGANIZER){
            throw new IllegalArgumentException("The given user is not the organizer or the admin of the event - and cannot update the event's data");
        }
        logger.debug("Check if user is an admin - and if allowed to change the fields");
        if (userInEvent.get().getRole() == Role.ADMIN && isFieldsAdminCanNotChange(userInEvent.get().getEvent(), updateEvent)) {
            throw new IllegalArgumentException("Admin is not allowed to change one of those fields");
        }
        checkDateAndTime(updateEvent);
        // TODO : sent notification!!
        logger.info("Save the updated event in DB");
        return eventRepository.save(updateEvent);

//        logger.debug("Update event data");
//        if (userEventFromRepo.get().getRole() == Role.ORGANIZER) {
//            event.setStart(updateEvent.getStart());
//            event.setEnd(updateEvent.getEnd());
//            event.setTitle(updateEvent.getTitle());
//        }
//
//        event.setIsPublic(updateEvent.getIsPublic());
//        event.setLocation(updateEvent.getLocation());
//        event.setDescription(updateEvent.getDescription());
//        event.setAttachments(updateEvent.getAttachments());
//
//        logger.info("Save the updated event in DB");
//        return eventRepository.save(event);
    }

    public UserEvent addGuestToEvent(int organizerOrAdminId, String guestToAddEmail, int eventId) {
        logger.debug("Check if there exists an event with the given event id");
        Event event = findEvent(eventId);
        User guestToAdd = checkActionsAndGetUser(organizerOrAdminId, guestToAddEmail, event, "add");
        logger.debug("Check if the guest to add is a part of the event");
        Optional<UserEvent> guestToAddInEvent = userEventRepository.findUserEventsByUserAndEvent(guestToAdd, event);
        if(guestToAddInEvent.isPresent()){
            throw new IllegalArgumentException("The given user to add is already a part of the event - you cannot add them again");
        }
        // TODO : send invitation!!!!
        logger.debug("Adding user to event " + guestToAdd.toString());
        return userEventRepository.save(new UserEvent(guestToAdd, event, Status.TENTATIVE, Role.GUEST));
    }

    public User removeGuestFromEvent(int organizerOrAdminId, String guestToRemoveEmail, int eventId){
        logger.debug("Check if there exists an event with the given event id");
        Event event = findEvent(eventId);
        User guestToRemove = checkActionsAndGetUser(organizerOrAdminId, guestToRemoveEmail, event, "remove");
        logger.debug("Check if the guest to remove is a part of the event");
        Optional<UserEvent> guestToRemoveInEvent = userEventRepository.findUserEventsByUserAndEvent(guestToRemove, event);
        if(!guestToRemoveInEvent.isPresent()){
            throw new IllegalArgumentException("The given user to remove is not a part of the event - you cannot remove them");
        }
        logger.debug("Check if the guest to remove is the event's organizer");
        if(guestToRemoveInEvent.get().getRole() == Role.ORGANIZER){
            throw new IllegalArgumentException("The given user to remove is the event's admin - you cannot remove them");
        }
        // TODO : send notification!!!!
        logger.debug("Removing guest from event " + guestToRemove.toString());
        userEventRepository.delete(guestToRemoveInEvent.get());
        return guestToRemove;
    }

    private User checkActionsAndGetUser(int organizerOrAdminId, String guestEmail, Event event, String action){
        logger.debug("Check if there exists a user with the given id (organizer or admin)");
        User user = findUser(organizerOrAdminId);
        logger.debug("Check if the user is a part of the event");
        Optional<UserEvent> userInEvent = userEventRepository.findUserEventsByUserAndEvent(user, event);
        if(!userInEvent.isPresent()){
            throw new IllegalArgumentException("The user that is trying to " + action + " a guest is not a part of the event");
        }
        logger.debug("Check if the user is the organizer or the admin of the event");
        if(userInEvent.get().getRole() != Role.ADMIN && userInEvent.get().getRole() != Role.ORGANIZER){
            throw new IllegalArgumentException("The given user is not the organizer or the admin of the event - and cannot " + action + " guests from the event");
        }
        logger.debug("Check if the guest to " + action + " exists in the db");
        return findUser(guestEmail);
    }

    private User findUser(int id){
        Optional<User> user = userRepository.findById(id);
        if(!user.isPresent()){
            throw new IllegalArgumentException("Invalid user id - the user that tries to perform an action");
        }
        return user.get();
    }

    private User findUser(String email){
        Optional<User> user = userRepository.findByEmail(email);
        if(!user.isPresent()){
            throw new IllegalArgumentException("Invalid email - there is no user that matches the given email");
        }
        return user.get();
    }

    private Event findEvent(int eventId){
        Optional<Event> event = eventRepository.findEventsById(eventId);
        if(!event.isPresent()){
            throw new IllegalArgumentException("Invalid event id");
        }
        return event.get();
    }

    private void checkDateAndTime(Event event){
        logger.debug("Check that the start and end date and time are valid : " + event.getEnd());
        if(event.getEnd().isBefore(LocalDateTime.now()) || event.getEnd().isBefore(event.getStart())){
            throw new IllegalArgumentException("Invalid start or end date or time - you cannot set start time that had passed and an end time that is previous to start time");
        }
    }

    private boolean isFieldsAdminCanNotChange(Event dbEvent, Event updatedEvent) {
        return (!updatedEvent.getStart().equals(dbEvent.getStart())  ) ||
                !updatedEvent.getEnd().equals(dbEvent.getEnd()) ||
                !updatedEvent.getTitle().equals(dbEvent.getTitle());
    }
}
