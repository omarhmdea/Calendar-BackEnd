package calendar.utilities;


import calendar.entities.User;
import calendar.entities.Credentials.UserCredentials;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

public class Validator {

    private static final Map<String, String> errorsMap = new HashMap<String, String>();
    private static final Logger logger = LogManager.getLogger(Validator.class.getName());


    public static Optional<Map<String, String>> validateRegister(User user) {
        errorsMap.clear();

        if(! isValidName(user.getName())) {
            errorsMap.put("name", "name" + getNameConstraints());
        }
        if(! isValidEmail(user.getEmail())) {
            errorsMap.put("email", "invalid email");
        }
        if(! isValidPassword(user.getPassword())) {
            errorsMap.put("password", getPasswordConstraints());
        }
        if(errorsMap.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(errorsMap);
    }


    public static Optional<Map<String, String>> validateLogin(UserCredentials user) {
        errorsMap.clear();

        if(! isValidEmail(user.getEmail())) {
            errorsMap.put("email", "invalid email");
        }
        if(! isValidPassword(user.getPassword())) {
            errorsMap.put("password", getPasswordConstraints());
        }
        if(errorsMap.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(errorsMap);
    }


    /**
     * Is valid name : check if The length of the name > 2 & contain only letters
     *
     * @param name - the name
     * @return true if valid name else false
     */
    public static boolean isValidName(String name) {
        logger.debug("Check valid name");
        String regex1 = "^[A-Z][a-z]*\\s[A-Z][a-z]*$";
        Pattern pattern = Pattern.compile(regex1);

        return name != null && pattern.matcher(name).matches();
    }

    /**
     * Is valid password : check if The length of the password > 6 & At least one capital letter
     *
     * @param password - the password
     * @return true if valid password else false
     */
    public static boolean isValidPassword(String password) {
        logger.debug("Check valid password");
        //&& password.matches(".[A-Z].")
        return password != null  && password.length() >= 6;
    }

    /**
     * Is valid email: check if syntax of email is valid
     *
     * @param emailAddress - the user email
     * @return true if valid emailAddress else false
     */
    public static boolean isValidEmail(String emailAddress) {
        logger.debug("Check valid email");
        String regexPattern = "^(.+)@(\\S+)$";
        return emailAddress != null && Pattern.compile(regexPattern).matcher(emailAddress).matches();
    }


    public static String getPasswordConstraints() {
        return "password length must be at least 6 characters long and contain one capital letter";
    }


    public static String getNameConstraints() {
        return " must contains at least 2 alphabetical letters and contain only characters";
    }

}
