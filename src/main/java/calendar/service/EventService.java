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
import java.util.ArrayList;
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
        checkDateAndTime(newEvent);
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
        User organizer = findUser(organizerId);
        Event event = findEvent(eventId);
        logger.debug("Check if the user is the organizer of the event");
        Optional<UserEvent> organizerEvent = userEventRepository.findUserEventsByUserAndEventAndRole(organizer, event, Role.ORGANIZER);
        if(!organizerEvent.isPresent()){
            throw new IllegalArgumentException("The given user is not the organizer of the event - and cannot set guests as admins");
        }
        UserEvent organizerInEvent = checkIfUserInEvent(organizer, event, "set a guest as an admin");
        checkIfUserInEventIsOrganizer(organizerInEvent, "set a guest as an admin");
        
        User newAdmin = findUser(newAdminEmail);
        logger.debug("Check if the new admin approved the invitation");
        Optional<UserEvent> acceptedEvent = userEventRepository.findUserEventsByUserAndEventAndStatus(newAdmin, event, Status.APPROVED);
        if(!acceptedEvent.isPresent()){
            throw new IllegalArgumentException("The given new admin did not approve the event invitation - and cannot be set as admin");
        }
        acceptedEvent.get().setRole(Role.ADMIN);
        logger.debug("Set the guest as the event's admin " + acceptedEvent);
        // TODO : sent notification - not really needed
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
        User user = findUser(userId);
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
    }

    /**
     * Add new guest to an existing event
     * Only the organizer and the admins of the event can perform the add guest action
     * @param userId the id of the user that is trying to perform the action
     * @param guestToAddEmail the email of the guest to add
     * @param eventId the id of the event to add the guest to
     * @return User event - the event id, the guest id, the guest role (guest), the guest status (tentative)
     */
    public UserEvent inviteGuestToEvent(int userId, String guestToAddEmail, int eventId) {
        Event event = findEvent(eventId);
        User guestToAdd = checkPermissionsAndGetUser(userId, guestToAddEmail, event, "add");
        logger.debug("Check if the guest to add is a part of the event");
        Optional<UserEvent> guestToAddInEvent = userEventRepository.findUserEventsByUserAndEvent(guestToAdd, event);
        if(guestToAddInEvent.isPresent()){
            throw new IllegalArgumentException("The given user to add is already a part of the event - you cannot add them again");
        }
        // TODO : send invitation!!!!
        logger.debug("Adding user to event " + guestToAdd.toString());
        return userEventRepository.save(new UserEvent(guestToAdd, event, Status.TENTATIVE, Role.GUEST));
    }

    /**
     * Remove new guest to an existing event
     * Only the organizer and the admins of the event can perform the remove guest action
     * @param userId the id of the user that is trying to perform the action
     * @param guestToRemoveEmail the email of the guest to remove
     * @param eventId the id of the event to remove the guest from
     * @return The info of the user that was removed from the event
     */
    public User removeGuestFromEvent(int userId, String guestToRemoveEmail, int eventId){
        logger.debug("Check if there exists an event with the given event id");
        Event event = findEvent(eventId);
        User guestToRemove = checkPermissionsAndGetUser(userId, guestToRemoveEmail, event, "remove");
        logger.debug("Check if the guest to remove is a part of the event");
        Optional<UserEvent> guestToRemoveInEvent = userEventRepository.findUserEventsByUserAndEvent(guestToRemove, event);
        if(!guestToRemoveInEvent.isPresent()){
            throw new IllegalArgumentException("The given user to remove is not a part of the event - you cannot remove them");
        }
        logger.debug("Check if the guest to remove is the event's organizer");
        if(guestToRemoveInEvent.get().getRole() == Role.ORGANIZER){
            throw new IllegalArgumentException("The given user to remove is the event's organizer - you cannot remove them");
        }
        // TODO : send notification!!!!
        logger.debug("Removing guest from event " + guestToRemove.toString());
        userEventRepository.delete(guestToRemoveInEvent.get());
        return guestToRemove;
    }

    /**
     * Delete event : delete event from DB
     * @param userId  - the user id
     * @param deleteEvent  - the event to delete
     * @return Deleted event
     * @throws IllegalArgumentException when the delete event failed
     */
    public Event deleteEvent(int userId, int deleteEvent){
        User user = findUser(userId);
        Event event = findEvent(deleteEvent);
        logger.debug("Check if the eventUser exist in DB");
        Optional<UserEvent> acceptedEvent = userEventRepository.findUserEventsByUserAndEvent(user, event);
        if(!acceptedEvent.isPresent()){
            throw new IllegalArgumentException("The given new admin did not approve the event invitation - and cannot be set as admin");
        }
        logger.debug("Delete the event from DB");
        notificationService.sendNotification(event, NotificationType.DELETE_EVENT);
        userEventRepository.delete(acceptedEvent.get());
        eventRepository.delete(event);
        return event;
    }

    /**
     * Get calendar : get the event calendar from DB According to month year
     * @param userId  - the user id
     * @param month  - the month we want to present
     * @param year  - the year we want to present
     * @return list event of month & year
     * @throws IllegalArgumentException when the get calendar failed
     */
    public List<Event> getCalendar(int userId, int month, int year){
        User user = findUser(userId);
        logger.debug("Check if the there exist any events that the user is invited to in the DB");
        List<UserEvent> dbEventUser = userEventRepository.findByUser(user);
        if (CollectionUtils.isEmpty(dbEventUser)) {
            throw new IllegalArgumentException("The given user is not a part of any event in the DB");
        }
        List<Event> userEventFromRepo = new ArrayList<>();
        for (int i=0; i<dbEventUser.size(); i++) {
            if ((dbEventUser.get(i).getEvent().getStart().getMonth().getValue() == month
                    && dbEventUser.get(i).getEvent().getStart().getYear() == year) ||
                    (dbEventUser.get(i).getEvent().getEnd().getMonth().getValue() == month
                            && dbEventUser.get(i).getEvent().getEnd().getYear() == year)) {
                userEventFromRepo.add(dbEventUser.get(i).getEvent());
            }
        }
        logger.debug("Return events of user " + user.getEmail());
        return userEventFromRepo;
    }

    public UserEvent approveOrRejectInvitation(int userId, int eventId, Status status){
        User user = findUser(userId);
        Event event = findEvent(eventId);
        logger.debug("Check if the user is a part of the event");
        Optional<UserEvent> userInEvent = userEventRepository.findUserEventsByUserAndEvent(user, event);
        if(!userInEvent.isPresent()){
            throw new IllegalArgumentException("The user that is trying to approve or reject the invitation is not a part of the event");
        }
        switch (status){
            case APPROVED:
                userInEvent.get().setStatus(Status.APPROVED);
                break;
            case REJECTED:
                userInEvent.get().setStatus(Status.REJECTED);
                break;
        }
        // TODO : send notification!!!!
        return userEventRepository.save(userInEvent.get());
    }

    // ---------------------------------------- helper methods ----------------------------------------

    private User checkPermissionsAndGetUser(int organizerOrAdminId, String guestEmail, Event event, String action){
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
        logger.debug("Check if there exists a user with the given id in the DB");
        Optional<User> user = userRepository.findById(id);
        if(!user.isPresent()){
            throw new IllegalArgumentException("Invalid user id - the user that tries to perform an action");
        }
        return user.get();
    }

    private User findUser(String email){
        logger.debug("Check if there exists a guest with the given email in the DB");
        Optional<User> user = userRepository.findByEmail(email);
        if(!user.isPresent()){
            throw new IllegalArgumentException("Invalid email - there is no user that matches the given email");
        }
        return user.get();
    }

    private Event findEvent(int eventId){
        logger.debug("Check if there exists an event with the given id in the DB");
        Optional<Event> event = eventRepository.findEventsById(eventId);
        if(!event.isPresent()){
            throw new IllegalArgumentException("Invalid event id");
        }
        return event.get();
    }

    private void checkDateAndTime(Event event){
        logger.debug("Check that the start and end date and time are valid : " + event.getEnd());
        if(event.getStart().isBefore(LocalDateTime.now()) || event.getEnd().isBefore(event.getStart())){
            throw new IllegalArgumentException("Invalid start or end date or time - you cannot set start time that had passed or an end time that is previous to start time");
        }
    }

    private UserEvent checkIfUserInEvent(User user, Event event, String message){
        logger.debug("Check if the user is a part of the event");
        Optional<UserEvent> userInEvent = userEventRepository.findUserEventsByUserAndEvent(user, event);
        if(!userInEvent.isPresent()){
            throw new IllegalArgumentException("The user that is trying to "+ message +" is not a part of the event");
        }
        return  userInEvent.get();
    }

    private void checkIfUserInEventIsOrganizer(UserEvent userEvent, String message){
        logger.debug("Check if the user is the organizer of the event");
        if(userEvent.getRole() != Role.ORGANIZER){
            throw new IllegalArgumentException("The given user is not the organizer of the event - and cannot " + message);
        }
    }

    private boolean isFieldsAdminCanNotChange(Event dbEvent, Event updatedEvent) {
        return (!updatedEvent.getStart().equals(dbEvent.getStart())  ) ||
                !updatedEvent.getEnd().equals(dbEvent.getEnd()) ||
                !updatedEvent.getTitle().equals(dbEvent.getTitle());
    }
}
