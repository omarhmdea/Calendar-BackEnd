package calendar.exception.customException;

public class UserNotPartOfEventException extends IllegalArgumentException {
    public UserNotPartOfEventException(String s) {
        super(s);
    }

}
