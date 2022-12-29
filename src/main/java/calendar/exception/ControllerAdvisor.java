package calendar.exception;

import calendar.responsHandler.ErrorResponse;
import calendar.exception.customException.ValidationErrorException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Map;

@ControllerAdvice
public class ControllerAdvisor extends ResponseEntityExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse<String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        logger.error(ex.getMessage());
        ErrorResponse<String> errorResponse = new ErrorResponse<>(ex.getMessage());
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(ValidationErrorException.class)
    public ResponseEntity<ErrorResponse<Map<String, String>>> handleValidationErrorException(ValidationErrorException ex, WebRequest request) {
        Map<String, String> errors = ex.getErrors();
        ErrorResponse<Map<String, String>> errorResponse = new ErrorResponse<>(errors);
        return ResponseEntity.badRequest().body(errorResponse);
    }
}
