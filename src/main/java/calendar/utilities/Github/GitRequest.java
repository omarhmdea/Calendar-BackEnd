package calendar.utilities.Github;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

public class GitRequest {
    private static Logger logger = LogManager.getLogger(GitRequest.class.getName());

    /**
     * send restTemplate.exchange to get the info for our GitToken.class entity.
     *
     * @param link - where to send the request
     * @return GitToken - access_token; token_type; scope
     */
    public static GitToken reqGitGetToken(String link) {
        logger.info("in GitRequest -> reqGitGetToken");
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        try {
            return restTemplate.exchange(link, HttpMethod.POST, entity, GitToken.class).getBody();
        } catch (Exception e) {
            logger.error("in GitRequest -> reqGitGetToken -> failed to fetch token from link: " +link);
            return null;
        }
    }

    /**
     * send restTemplate.exchange to get the info for our GitUser.class entity.
     * if the mail of the user is private, sends another request with <a href="https://api.github.com/user/emails"></a>
     * add the /emails to get the email of the user who just did an authorization via gitHub.
     *
     * @param link        - where to send the request
     * @param bearerToken - put the access_token from reqGitGetToken in the header authorization
     * @return GitUser - login; name; email;
     */
    public static GitUser reqGitGetUser(String link, String bearerToken) {
        logger.info("in GitRequest -> reqGitGetUser");
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + bearerToken);
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        try {
            GitUser gitUser = restTemplate.exchange(link, HttpMethod.GET, entity, GitUser.class).getBody();
            gitUser.accessToken = bearerToken;
            if (gitUser.getEmail() == null) {
                logger.info("in GitRequest -> reqGitGetToken -> user is private in github.");
                GithubEmail[] githubEmail = restTemplate.exchange(link + "/emails", HttpMethod.GET, entity, GithubEmail[].class).getBody();
                for (GithubEmail gEmail : githubEmail) {
                    if (gEmail.isPrimary()) {
                        gitUser.email = gEmail.getEmail();
                        break;
                    }
                }
            }
            return gitUser;
        } catch (Exception e) {
            logger.error("in GitRequest -> reqGitUser -> failed to fetch user from link: " +link);
            return null;
        }
    }
}
