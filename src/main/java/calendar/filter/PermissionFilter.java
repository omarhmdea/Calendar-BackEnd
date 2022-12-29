package calendar.filter;

import calendar.entities.Event;
import calendar.entities.User;
import calendar.entities.UserEvent;
import calendar.enums.Role;
import calendar.repository.EventRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;


public class PermissionFilter implements Filter {

    public static final Logger logger = LogManager.getLogger(PermissionFilter.class);

    private EventRepository eventRepository;

    public PermissionFilter(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        logger.info("Role filter is working on the following request: " + servletRequest);

        String[] listOfAdminPermissions = {"/event/guest/invite", "/event/guest/delete", "/event/update"};

        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse res = (HttpServletResponse) servletResponse;

        String url = req.getRequestURI();
        String[] splintedUrl = url.split("/");
        int eventId = Integer.parseInt(splintedUrl[splintedUrl.length - 1]);

        User user = (User) req.getAttribute("user");
        Optional<Event> event = eventRepository.findEventsById(eventId);

        if(event.isPresent()) {
            if(event.get().getOrganizer().equals(user)) {
                req.setAttribute("event", event.get());
                filterChain.doFilter(req, res);
                return;
            }
        }
        else {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            res.getOutputStream().write("Invalid event id".getBytes());
        }

        Optional<UserEvent> userEvent = event.get().getGuests().stream().filter(userEvent1 -> userEvent1.getUser().equals(user)).findFirst();

        if(userEvent.isPresent()) {
            Role userRole = userEvent.get().getRole();
            if(userRole == Role.ADMIN) {
                if(Arrays.asList(listOfAdminPermissions).contains(url)) {
                    req.setAttribute("event", event.get());
                    filterChain.doFilter(req, res);
                    return;
                }
                else {
                    res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    res.getOutputStream().write("Admin is not allowed to change one of those fields".getBytes());
                }
            }
            else {
                res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                res.getOutputStream().write("The given user is not the organizer or the admin of the event".getBytes());
            }
        }
        else {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.getOutputStream().write("The given user is not a part of this event in the DB".getBytes());
        }
    }


    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}