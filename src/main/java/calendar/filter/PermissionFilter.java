package calendar.filter;

import calendar.entities.UserEvent;
import calendar.enums.Role;
import calendar.service.UserEventService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

public class PermissionFilter implements Filter {

    public static final Logger logger = LogManager.getLogger(PermissionFilter.class);

    @Autowired
    private UserEventService userEventService;


    /**
     * Called by the web container to indicate to a filter that it is being placed into service.
     * The servlet container calls the init method exactly once after instantiating the filter.
     * The init method must complete successfully before the filter is asked to do any filtering work.
     *
     * @param filterConfig The configuration information associated with the
     *                     filter instance being initialised
     * @throws ServletException
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }


    /**
     * Processes a request/response pair passed through the Filter Chain due to a client request for a resource at the end of the chain.
     * The token in the header of the request is being checked, if token is valid and correct, this filter passes on the request and response to the next entity in the chain.
     * If token invalid the filter return an Unauthorized response.
     *
     * @param servletRequest  The request to process
     * @param servletResponse The response associated with the request
     * @param filterChain     Provides access to the next filter in the chain for this
     *                        filter to pass the request and response to for further
     *                        processing
     * @throws IOException      if an I/O exception occurs during the processing of the request/response.
     * @throws ServletException if the processing fails.
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        logger.info("Role filter is working on the following request: " + servletRequest);

        String[] listOfAdminPermissions = {"/event/guest", "/event/invite", "/event/update"};

        //MutableHttpServletRequest req = new MutableHttpServletRequest((HttpServletRequest) servletRequest);
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse res = (HttpServletResponse) servletResponse;

        String tempEventId = req.getParameter("eventId");
        String url = req.getRequestURI();

        int userId = (int) req.getAttribute("userId");
        int eventId = 0;

        if(tempEventId != null) {
            eventId = Integer.parseInt(tempEventId);
        }

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
                    res.sendError(401, "Admin is not allowed to change one of those fields");
                }
            }
            else {
                res.sendError(401, "The given user is not the organizer or the admin of the event");
            }
        }
        else {
            res.sendError(401, "The given user is not a part of this event in the DB");
        }
    }


    /**
     * indicate to a filter that it is being taken out of service.
     * This method is only called once all threads within the filter's doFilter method have exited or after a timeout period has passed.
     * After the web container calls this method, it will not call the doFilter method again on this instance of the filter.
     * This method gives the filter an opportunity to clean up any resources that are being held.
     */
    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}