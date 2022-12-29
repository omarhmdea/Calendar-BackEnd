package calendar.service;

import calendar.entities.*;
import calendar.entities.Credentials.EventCredentials;
import calendar.entities.DTO.UserDTO;
import calendar.enums.Role;
import calendar.enums.Status;
import calendar.exception.customException.UserNotPartOfEventException;
import calendar.repository.EventRepository;
import calendar.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class EventService {
    private static final Logger logger = LogManager.getLogger(AuthService.class.getName());

    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private UserRepository userRepository;

    /**
     * Add new event to the user's calendar
     * @param organizer - the user that is trying to create the event
     * @param newEvent - the new event data
     * @return the new event after it was saved in the db
     */
    public Event addNewEvent(User organizer, Event newEvent){
        logger.debug("Try to add new event");
        checkDateAndTime(newEvent);
        newEvent.setOrganizer(organizer);
        logger.debug("New event is saved : " + newEvent.toString());
        return eventRepository.save(newEvent);
    }

    /**
     * Update event data by admin & organizer
     * @param user the user that is trying to update the event
     * @param originalEvent the original event, without any changes
     * @param updatedEvent the updated event, with the new changes
     * @return
     */
    public Event updateEvent(User user, Event originalEvent, EventCredentials updatedEvent){
        if(!userIsEventOrganizer(originalEvent, user)){
            if(!isFieldsAdminCanNotChange(originalEvent, updatedEvent)){
                throw new IllegalArgumentException("Admin cannot change one of the fields they made changes in");
            }
        }
        logger.debug("Try to update event");
        checkDateAndTime(updatedEvent);
        logger.info("Save the updated event in DB");
        return eventRepository.save(update(originalEvent, updatedEvent));
    }

    public Event update(Event originalEvent, EventCredentials updatedEvent){
        originalEvent.setIsPublic(updatedEvent.getIsPublic());
        originalEvent.setStart(updatedEvent.getStart());
        originalEvent.setEnd(updatedEvent.getEnd());
        originalEvent.setLocation(updatedEvent.getLocation());
        originalEvent.setTitle(updatedEvent.getTitle());
        originalEvent.setDescription(updatedEvent.getDescription());
        originalEvent.setAttachments(updatedEvent.getAttachments());
        return originalEvent;
    }

    /**
     * Set guest as admin in the given event
     * @param organizer- the user that created the event
     * @param newAdminEmail - the email of the guest that the organizer wants to set as admin
     * @param event - the event to set new admin to
     * @return The event
     */
    public Event setGuestAsAdmin(User organizer, String newAdminEmail, Event event){
        logger.debug("Try to set guest as admin");
        User newAdmin = findUser(newAdminEmail);
        logger.debug("Check if the new admin approved the invitation");
        UserEvent adminInEvent = getUserEvent(event, newAdmin);
        if(!adminInEvent.getStatus().equals(Status.APPROVED)){
            throw new IllegalArgumentException("The given new admin did not approve the event invitation - and cannot be set as admin");
        }
        event.removeUserEvent(adminInEvent);
        adminInEvent.setRole(Role.ADMIN);
        event.addUserEvent(adminInEvent);
        logger.debug("Set the guest as the event's admin");
        return eventRepository.save(event);
    }

    /**
     * Delete event : delete event from DB
     * @param user  - the user id
     * @param eventToDelete  - the event to delete
     * @return Deleted event
     * @throws IllegalArgumentException when the delete event failed
     */
    public Event deleteEvent(User user, Event eventToDelete){
        logger.debug("Soft delete the event - set is deleted true");
        eventToDelete.setIsDeleted(true);
        return eventRepository.save(eventToDelete);
    }

    /**
     * Add new guest to an existing event
     * Only the organizer and the admins of the event can perform the add guest action
     * @param user  the user that is trying to perform the action
     * @param guestToAddEmail the email of the guest to add
     * @param event the id of the event to add the guest to
     * @return User event - the event id, the guest id, the guest role (guest), the guest status (tentative)
     */
    public Event inviteGuestToEvent(User user, String guestToAddEmail, Event event) {
        User guestToAdd = findUser(guestToAddEmail);
        logger.debug("Check if the guest to add is a part of the event");
        if(guestIsPartOfEvent(event, guestToAdd)){
            logger.debug("The given user to add is already a part of the event - you cannot add them again");
            throw new IllegalArgumentException("The given user to add is already a part of the event - you cannot add them again");
        }
        if(guestToAdd.equals(event.getOrganizer())){
            logger.debug("The given user to add is the event organizer - and already a part of the event - you cannot add them again");
            throw new IllegalArgumentException("The given user to add is the event organizer - and already a part of the event - you cannot add them again");
        }
        logger.debug("Adding user to event " + guestToAdd.toString());
        event.addUserEvent(new UserEvent(guestToAdd, Status.TENTATIVE, Role.GUEST));
        return eventRepository.save(event);
    }

    /**
     * Remove new guest to an existing event
     * Only the organizer and the admins of the event can perform the remove guest action
     * @param user the user that is trying to perform the action
     * @param guestToRemoveEmail the email of the guest to remove
     * @param event the event to remove the guest from
     * @return The info of the user that was removed from the event
     */
    public User removeGuestFromEvent(User user, String guestToRemoveEmail, Event event){
        User guestToRemove = findUser(guestToRemoveEmail);
        logger.debug("Check if the guest to remove is a part of the event");
        if(!guestIsPartOfEvent(event, guestToRemove)){
            throw new IllegalArgumentException("The given user to remove is not a part of the event - you cannot remove them");
        }
        logger.debug("Check if the guest to remove is the event's organizer");
        if(userIsEventOrganizer(event, guestToRemove)){
            throw new IllegalArgumentException("The given user to remove is the event's organizer - you cannot remove them");
        }
        logger.debug("Removing guest from event " + guestToRemove.toString());
        UserEvent userEvent = getUserEvent(event, guestToRemove);
        event.removeUserEvent(userEvent);
        eventRepository.save(event);
        return guestToRemove;
    }

    /**
     * Get calendar (events) of a user by month and year
     * @param user  - the user id
     * @param month  - the month we want to present
     * @param year  - the year we want to present
     * @return list of events by month & year
     * @throws IllegalArgumentException when the get calendar failed
     */
    public List<Event> getCalendar(User user, int month, int year){
        logger.debug("Try to get my events by month and year of user " + user.getEmail());
        List<Event> userEvents = getUserEvents(user);
        List<Event> userEventsByMothAndYear = new ArrayList<>();
        for (Event event : userEvents) {
            if(!event.getIsDeleted()){
                if ((event.getStart().getMonth().getValue() == month && event.getStart().getYear() == year) ||
                        (event.getEnd().getMonth().getValue() == month && event.getEnd().getYear() == year)) {
                    userEventsByMothAndYear.add(event);
                }
            }
        }
        logger.debug("Return events of user " + user.getEmail());
        return userEventsByMothAndYear;
    }

    /**
     * Get calendar (events) of a user by month and year
     * If the user is trying to view their own calendar -
     * they will see all the events - private and public
     * If the user is trying to view someone else's calendar -
     * - if they have permission to view - they will see all the public events(without private)
     * - if the don't have permission - throw exception
     * @param user the user that is trying to view the calendar
     * @param userToShowCalendarId the user to view their calendar
     * @param month the month the user wants to present
     * @param year the year the user wants to present
     * @return list of events by month & year
     */
    public List<Event> showCalendar(User user, int userToShowCalendarId, int month, int year){
        logger.debug("Try to get calendar of user " + userToShowCalendarId);
        User userToShowCalendar = findUser(userToShowCalendarId);
        List<UserDTO> sharedCalendars = getSharedCalendars(user);
        if(!sharedCalendars.contains(UserDTO.convertToUserDTO(userToShowCalendar))){
            throw new IllegalArgumentException("the given user has no permission to view the other user's calendar");
        }
        List<Event> userEvents = getCalendar(userToShowCalendar, month, year);
        if(user.equals(userToShowCalendar)){
            return userEvents;
        }
        List<Event> sharedEvents = new ArrayList<>();
        for(Event event: userEvents){
            if(event.getIsPublic()){
                sharedEvents.add(event);
            }
        }
        logger.debug("Return events of user " + userToShowCalendar.getId());
        return sharedEvents;
    }

    /**
     * A user approved / rejected an event invitation
     * @param user the user that approved / rejected the invitation
     * @param eventId the event that is related to the invitation
     * @param status approve / reject
     * @return the event that the user was invited to
     */
    public Event approveOrRejectInvitation(User user, int eventId, Status status){
        Event event = findEvent(eventId);
        UserEvent userInEvent = getUserEvent(event, user);
        event.removeUserEvent(userInEvent);
        switch (status){
            case APPROVED:
                userInEvent.setStatus(Status.APPROVED);
                break;
            case REJECTED:
                userInEvent.setStatus(Status.REJECTED);
                break;
        }
        event.addUserEvent(userInEvent);
        return eventRepository.save(event);
    }

    /**
     * A user approved / rejected an event invitation
     * @param email the email of the user that approved / rejected the invitation
     * @param eventId the event that is related to the invitation
     * @param status approve / reject
     * @return the event that the user was invited to
     */
    public Event approveOrRejectInvitation(String email, int eventId, Status status){
        User user = findUser(email);
        return approveOrRejectInvitation(user, eventId, status);
    }

    /**
     * Share my calendar with another user
     * @param user the user that wants to share their calendar
     * @param userToShareToEmail the email of the user to share the calendar to
     * @return a SuccessResponse - OK status, a message, the user info
     */
    public User shareCalendar(User user, String userToShareToEmail){
        if(user.getEmail() == userToShareToEmail){
            return user;
        }
        User userToShare = findUser(userToShareToEmail);
        userToShare.addToShared(user);
        return userRepository.save(userToShare);
    }

    /**
     * Get a list of all the users that the user can view their calenders
     * @param user the user to view their shared calendars list
     * @return a list of the users the user can view info
     */
    public List<UserDTO> getSharedCalendars(User user){
        Set<Integer> usersId = user.getShared();
        List<UserDTO> shredCalendars = new ArrayList<>();
        shredCalendars.add(UserDTO.convertToUserDTO(user));
        for(Integer id: usersId){
            shredCalendars.add(UserDTO.convertToUserDTO(findUser(id)));
        }
        return shredCalendars;
    }

    // ---------------------------------------- helper methods ----------------------------------------

    private List<Event> getUserEvents(User user){
        logger.debug("Check if the there exist any events that the user is invited to in the DB");
        List<Event> userEvents = new ArrayList<>();
        List<Event> events = eventRepository.findAll();
        for (Event event: events) {
            if(guestIsPartOfEvent(event, user) || event.getOrganizer().equals(user)){
                userEvents.add(event);
            }
        }
        if(userEvents.isEmpty()){
            logger.error("The given user is not a part of any event");
            throw new UserNotPartOfEventException("The given user is not a part of any event");
        }
        return userEvents;
    }

    private boolean guestIsPartOfEvent(Event event, User user){
        Optional<UserEvent> usersEvent = event.getGuests().stream().filter(userEvent -> userEvent.getUser().equals(user)).findFirst();
        if(!usersEvent.isPresent()){
            return false;
        }
        return true;
    }

    private UserEvent getUserEvent(Event event, User user){
        if(!guestIsPartOfEvent(event, user)){
            throw new IllegalArgumentException("The user is not a part of the event");
        }
        return event.getGuests().stream().filter(userEvent -> userEvent.getUser().equals(user)).findFirst().get();
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

    private void checkDateAndTime(EventCredentials event){
        logger.debug("Check that the start and end date and time are valid : " + event.getStart() + ", "+ event.getEnd());
        if(event.getStart().isBefore(LocalDateTime.now()) || event.getEnd().isBefore(event.getStart())){
            throw new IllegalArgumentException("Invalid start or end date or time - you cannot set start time that had passed or an end time that is previous to start time");
        }
    }

    private boolean userIsEventOrganizer(Event event, User user){
        logger.debug("Check if the user is the organizer of the event");
        return event.getOrganizer().equals(user);
    }

    private boolean isFieldsAdminCanNotChange(Event originalEvent, EventCredentials updatedEvent) {
        logger.debug("Check if the admin is allowed to change the given fields");
        return !updatedEvent.getStart().equals(originalEvent.getStart()) ||
                !updatedEvent.getEnd().equals(originalEvent.getEnd()) ||
                !updatedEvent.getTitle().equals(originalEvent.getTitle());
    }
}


