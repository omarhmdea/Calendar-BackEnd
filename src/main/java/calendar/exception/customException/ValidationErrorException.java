package calendar.exception.customException;

import java.util.Map;

public class ValidationErrorException extends IllegalArgumentException{
    private Map<String, String> errors;

    public ValidationErrorException(String s, Map<String, String> errors) {
        super(s);
        this.errors = errors;
    }

    public Map<String, String> getErrors() {
        return errors;
    }
}
