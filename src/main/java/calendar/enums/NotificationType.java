package calendar.enums;

public enum NotificationType {
    DELETE_EVENT, //delete event √
    UPDATE_EVENT, //update √
    INVITE_GUEST, //invite
    REMOVE_GUEST, //remove guest from event
    USER_STATUS_CHANGED, //status
    UPCOMING_EVENT // ??? notification 1 hour before
}
