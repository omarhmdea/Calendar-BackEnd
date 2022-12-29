package calendar.responsHandler;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
public class SuccessResponse<T> {
    private String message;
    private T data;
}
