package calendar.ResponsHandler;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Setter
@Getter
public class SuccessResponse<T> {
    private HttpStatus status;
    private String message;
    private T data;
}
