package calendar.utilities.Github;

public class GitUser {
    public String login;
    public String name;
    public String email;
    public String accessToken;

    public String getAccessToken() {
        return accessToken;
    }

    public String getLogin() {
        return login;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return "GitUser{" +
                "login='" + login + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
