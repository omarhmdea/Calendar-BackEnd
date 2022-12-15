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

    private final RequestMatcher uriMatcher = new AntPathRequestMatcher("/user/**");

    //    @Override
    //    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    //        HttpServletRequest req = (HttpServletRequest) request;
    //   //     HttpServletResponse res = (HttpServletResponse) response;
    ////        res.setHeader("Access-Control-Allow-Origin", "http://localhost:9000");
    ////        res.setHeader("Access-Control-Allow-Credentials", "true");
    ////        res.setHeader("Access-Control-Allow-Methods", "ACL, CANCELUPLOAD, CHECKIN, CHECKOUT, PATCH, COPY, DELETE, GET, HEAD, LOCK, MKCALENDAR, MKCOL, MOVE, OPTIONS, POST, PROPFIND, PROPPATCH, PUT, REPORT, SEARCH, UNCHECKOUT, UNLOCK, UPDATE, VERSION-CONTROL");
    ////        res.setHeader("Access-Control-Max-Age", "86400");
    ////        res.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Key, Authorization");
    //    //    String auth = req.getHeader("token");
    //        String path = req.getRequestURI();
    //        System.out.println(path);
    //        if (permissionPathsForAll.stream().anyMatch(path::contains)) {
    ////            if (!authService.getKeyTokensValEmails().containsKey(auth)) {
    ////                res.setStatus(HttpServletResponse.SC_FORBIDDEN);
    ////                return;
    ////            } else {
    ////                String userEmail = authService.getKeyTokensValEmails().get(auth);
    ////                if (!auth.equals(authService.getKeyEmailsValTokens().get(userEmail))) {
    ////                    res.setStatus(HttpServletResponse.SC_FORBIDDEN);
    ////                    return ;
    ////                }
    ////            }
    //            if (req.getHeader("authorization") != null) {
    //                String token = req.getHeader("authorization");
    //                int userId = authService.findByToken(token.substring(7)).getId();
    //                request.setAttribute("userId", userId);
    //            } else {
    //                logger.error("in AuthorizationFilter -> doFilter -> Could not find a token in the request");
    //                ((HttpServletResponse) response).setStatus(400);
    //                response.getOutputStream().write("ExceptionMessage.WRONG_SEARCH.toString()".getBytes());
    //                return;
    //            }
    //        }
    //        chain.doFilter(request, response);
    //    }


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