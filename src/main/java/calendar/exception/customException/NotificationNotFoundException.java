package calendar.exception.customException;

import java.util.Map;

public class NotificationNotFoundException extends IllegalArgumentException{

    public NotificationNotFoundException(String s) {
        super(s);
    }
}
