package calendar.enums;

public enum NotificationType {
    NEW_EVENT, //invite
    USER_STATUS_CHANGED, //status
    EVENT_DATA_CHANGED, //update
    EVENT_CANCELED, //delete event
    USER_UNINVITED, //remove guest from event
    UPCOMING_EVENT // ???
}
