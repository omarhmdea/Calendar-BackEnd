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
     * Get git token info by sending restTemplate.exchange request
     * @param link the link to send the request to
     * @return Git Token data - access token, token type, scope
     */
    public static GitToken reqGitGetToken(String link) {
        logger.info("Try to get github token by link");
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        try {
            return restTemplate.exchange(link, HttpMethod.POST, entity, GitToken.class).getBody();
        } catch (Exception e) {
            logger.error("Failed to fetch token from " +link);
            return null;
        }
    }

    /**
     * Get git user info by sending restTemplate.exchange request
     * If the user's email is private - sends another request - <a href="https://api.github.com/user/emails"></a>
     * @param link the link to send the request to
     * @param bearerToken the access_token from the git token data
     * @return Git User data - login, name, email, access token
     */
    public static GitUser reqGitGetUser(String link, String bearerToken) {
        logger.info("Try to get github user by link");
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + bearerToken);
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        try {
            GitUser gitUser = restTemplate.exchange(link, HttpMethod.GET, entity, GitUser.class).getBody();
            gitUser.accessToken = bearerToken;
            if (gitUser.getEmail() == null) {
                logger.info("User is private in github");
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
            logger.error("Failed to fetch user from " +link);
            return null;
        }
    }
}
