package calendar.filter;

import calendar.entities.UserEvent;
import calendar.enums.Role;
import calendar.service.UserEventService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;


public class PermissionFilter implements Filter {

    public static final Logger logger = LogManager.getLogger(PermissionFilter.class);

    private UserEventService userEventService;

    public PermissionFilter(UserEventService userEventService) {
        this.userEventService = userEventService;
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

        int eventId = Integer.parseInt(splintedUrl[splintedUrl.length -1]);
        int userId = (int) req.getAttribute("userId");


        Optional<UserEvent> userEvent = userEventService.findUserEventByUserIdAndEventId(userId, eventId);
        if(userEvent.isPresent()) {
            Role userRole = userEvent.get().getRole();
            if(userRole == Role.ORGANIZER) {
                req.setAttribute("role", userRole);
                filterChain.doFilter(req, res);
            }
            else if(userRole == Role.ADMIN) {
                if(Arrays.asList(listOfAdminPermissions).contains(url)) {
                    req.setAttribute("role", userRole);
                    filterChain.doFilter(req, res);
                }
                else {
                    res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    res.getOutputStream().write("Admin is not allowed to change one of those fields".getBytes());
                    //res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Admin is not allowed to change one of those fields");
                }
            }
            else {
                res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                res.getOutputStream().write("The given user is not the organizer or the admin of the event".getBytes());
                //res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "The given user is not the organizer or the admin of the event");
            }
        }
        else {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.getOutputStream().write("The given user is not a part of this event in the DB".getBytes());
            //res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "The given user is not a part of this event in the DB");
        }
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}