package calendar.exception.customException;

public class DeletedEventException extends IllegalArgumentException {
    public DeletedEventException(String s) {
        super(s);
    }
}