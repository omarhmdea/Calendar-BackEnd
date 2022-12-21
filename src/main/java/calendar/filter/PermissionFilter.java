//package calendar.filter;
//
//import calendar.repository.UserRepository;
//import calendar.service.AuthService;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.GenericFilterBean;
//
//import javax.servlet.FilterChain;
//import javax.servlet.ServletException;
//import javax.servlet.ServletRequest;
//import javax.servlet.ServletResponse;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//
//import static calendar.utilities.utility.*;
//
//
//@Component
//public class PermissionFilter extends GenericFilterBean {
//
//    @Autowired
//    AuthService authService;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Override
//    public void doFilter(ServletRequest request,  ServletResponse response, FilterChain chain) throws IOException, ServletException {
//        if (permissionPathsForAll.stream().noneMatch(path::contains)) {
//            if (auth == null) {
//                res.setStatus(HttpServletResponse.SC_FORBIDDEN);
//                return;
//            } else if (!authService.getKeyTokensValEmails().containsKey(auth)) {
//                res.setStatus(HttpServletResponse.SC_FORBIDDEN);
//                return;
//            } else {
//                String userEmail = authService.getKeyTokensValEmails().get(auth);
//                if (!auth.equals(authService.getKeyEmailsValTokens().get(userEmail))) {
//                    res.setStatus(HttpServletResponse.SC_FORBIDDEN);
//                    return;
//                }
//
//                User dbUser = User.dbUser(userRepository.findByEmail(userEmail));
//                if (dbUser.getType() == UserType.GUEST) {
//                    if (permissionPathsForGuest.stream().noneMatch(path::contains)) {
//                        res.setStatus(HttpServletResponse.SC_FORBIDDEN);
//                        return;
//                    }
//                }
//                if (dbUser.getType() == UserType.REGISTERED) {
//                    if (noPermissionsPathsForRegistered.stream().anyMatch(path::contains)) {
//                        res.setStatus(HttpServletResponse.SC_FORBIDDEN);
//                        return;
//                    }
//                }
//            }
//        }
//        chain.doFilter(request, response);
//    }
//}
