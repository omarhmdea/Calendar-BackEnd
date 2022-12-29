package calendar.exception.customException;

public class NotificationNotFoundException extends IllegalArgumentException{
    public NotificationNotFoundException(String s) {
        super(s);
    }
}
