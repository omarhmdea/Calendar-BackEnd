//package calendar.filter;
//
//import calendar.entities.User;
//import calendar.repository.UserRepository;
//import calendar.service.AuthService;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
//import org.springframework.security.web.util.matcher.RequestMatcher;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import javax.servlet.FilterChain;
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.util.Optional;
//
//@Component
////public class PermissionFilter extends GenericFilterBean {
//public class PermissionFilter extends OncePerRequestFilter {
//
//    @Autowired
//    AuthService authService;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    private final RequestMatcher uriMatcher = new AntPathRequestMatcher("/event/**");
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
//        if(uriMatcher.matches(request)) {
//            int userId = (int) request.getAttribute("userId");
//            User user = findUser(userId, response);
//            request.setAttribute("user", user);
//            // ??
//            filterChain.doFilter(request, response);
//        }
//    }
//
//    private User findUser(int id, HttpServletResponse response) throws IOException {
//        logger.debug("Check if there exists a user with the given id in the DB");
//        Optional<User> user = userRepository.findById(id);
//        if(!user.isPresent()){
//            logger.error("in AuthorizationFilter -> doFilter -> Could not find a user with this id : " + id);
//            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
////            response.getOutputStream().write(("Could not find a user with this id : " + id));
//        }
//        return user.get();
//    }
//}
