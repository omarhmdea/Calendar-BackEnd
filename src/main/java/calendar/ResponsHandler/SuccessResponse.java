package calendar.ResponsHandler;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.util.List;

@AllArgsConstructor
@Setter
@Getter
public class SuccessResponse<T> {
    private String message;
    private T data;
}
