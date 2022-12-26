package calendar.utilities.Github;

import lombok.Getter;
import lombok.ToString;
import org.junit.jupiter.api.Test;

@Getter
@ToString
public class GitToken {
    public String access_token;
    public String token_type;
    public String scope;
}
