package calendar.utilities.Github;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class GitUser {
    public String login;
    public String name;
    public String email;
    public String accessToken;
}
