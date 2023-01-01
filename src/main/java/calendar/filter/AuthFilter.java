package calendar.filter;

import calendar.entities.User;
import calendar.service.AuthService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

public class AuthFilter implements Filter {

    public static final Logger logger = LogManager.getLogger(AuthFilter.class);

    private AuthService authService;

    public AuthFilter(AuthService authService) {
        this.authService = authService;
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        logger.info("Auth filter is working on the following request: " + servletRequest);

        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse res = (HttpServletResponse) servletResponse;

        String token = req.getHeader("authorization");
        if(token == null) {
            logger.error("Could not find a user - token is null");
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
        Optional<User> user = authService.findByToken(token.substring(7));
        if(user.isPresent()) {
            req.setAttribute("user", user.get());
            filterChain.doFilter(req, res);
        }
        else {
            logger.error("Could not find a user with this token : " + token);
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.getOutputStream().write(("Could not find a user with this token : " + token.substring(7)).getBytes());
        }
    }

    public void init(FilterConfig filterConfig) {}

    public void destroy() {}
}