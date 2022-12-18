package calendar.enums;

public enum NotificationType {
    NEW_EVENT,
    USER_STATUS_CHANGED,
    EVENT_DATA_CHANGED,
    EVENT_CANCELED,
    USER_UNINVITED,
    UPCOMING_EVENT
}


    public UserEvent addGuestToEvent(int organizerOrAdminId, String guestToAddEmail, int eventId) {
        logger.debug("Check if there exists a user with the given id (organizer or admin)");
        User user = findUser(organizerOrAdminId);
        logger.debug("Check if there exists an event with the given event id");
        Event event = findEvent(eventId);
        logger.debug("Check if the user is a part of the event");
        Optional<UserEvent> userInEvent = userEventRepository.findUserEventsByUserAndEvent(user, event);
        if(!userInEvent.isPresent()){
            throw new IllegalArgumentException("The user that is trying to add a guest is not a part of the event");
        }
        logger.debug("Check if the user is the organizer or the admin of the event");
        if(userInEvent.get().getRole() != Role.ADMIN && userInEvent.get().getRole() != Role.ORGANIZER){
            throw new IllegalArgumentException("The given user is not the organizer or the admin of the event - and cannot add guests to the event");
        }
        logger.debug("Check if the guest to add exists in the db");
        User guestToAdd = findUser(guestToAddEmail);


        logger.debug("Check if the guest to add is a part of the event");
        Optional<UserEvent> guestToAddToEvent = userEventRepository.findUserEventsByUserAndEvent(guestToAdd, event);
        if(guestToAddToEvent.isPresent()){
            throw new IllegalArgumentException("The given user to add is already a part of the event - you cannot add them again");
        }
        // TODO : send invitation!!!!
        logger.debug("Adding user to event " + guestToAdd);
        return userEventRepository.save(new UserEvent(guestToAdd, event, Status.TENTATIVE, Role.GUEST));
    }

    User guestToRemove = checkActionsAndGetUser(organizerOrAdminId, guestToRemoveEmail, eventId, "remove");
