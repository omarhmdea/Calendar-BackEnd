package calendar.filter;

import calendar.entities.User;
import calendar.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;


@Component
public class AuthFilter extends OncePerRequestFilter { // GenericFilterBean

    @Autowired
    AuthService authService;

    private final RequestMatcher uriMatcher = new AntPathRequestMatcher("/event/**");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if(uriMatcher.matches(request)) {
            String token = request.getHeader("authorization");
            if(token != null) {
                Optional<User> user = authService.findByToken(token.substring(7));
                if(user.isPresent()) {
                    request.setAttribute("userId", user.get().getId());
                    filterChain.doFilter(request, response);
                } else {
                    logger.error("in AuthorizationFilter -> doFilter -> Could not find a user with this token : " + token);
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getOutputStream().write(("Could not find a user with this token : " + token.substring(7)).getBytes());
                }
            }
            else {
                logger.error("in AuthorizationFilter -> doFilter -> Could not find a token in the request");
                response.setStatus(400);
                response.getOutputStream().write("ExceptionMessage.WRONG_SEARCH.toString()".getBytes());
                return;
            }
        }
    }


    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        RequestMatcher matcher = new NegatedRequestMatcher(uriMatcher);
        return matcher.matches(request);
    }
}