package calendar.response;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;


@Getter
@Setter
public class SuccessResponse<T> {
    T data;
    HttpStatus httpStatus;
}
