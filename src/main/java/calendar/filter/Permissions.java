package calendar.filter;

import calendar.enums.Role;

import java.util.*;

import static java.util.Map.entry;

public class Permissions {
    private Map<String, Set<Role>> permissions;

    public Permissions(){
        permissions = new HashMap<>();
        permissions.put("event/", Set.of(Role.ORGANIZER,  Role.ADMIN));
    }

    public boolean get(String path, Role role){
        return permissions.containsKey(path) && permissions.get(path).contains(role);
    }
}
