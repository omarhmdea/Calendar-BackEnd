package calendar.utilities;

import java.util.ArrayList;
import java.util.List;

public class utility {
    public static List<String> permissionPathsForAll = new ArrayList<>(List.of("user","ws"));
    public static List<String> permissionPathsForGuest = new ArrayList<>(List.of("/logout", "update/status", "chat/mainchatroom", "chat/downloadmainchatroom", "/topic", "/app", "/plain"));
    public static List<String> noPermissionsPathsForRegistered = new ArrayList<>(List.of("update/mute"));

}
