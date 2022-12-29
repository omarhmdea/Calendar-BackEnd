package calendar.configuration;

import calendar.filter.AuthFilter;
import calendar.filter.CorsFilter;
import calendar.filter.PermissionFilter;
import calendar.repository.EventRepository;
import calendar.service.AuthService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import java.io.FileNotFoundException;
import java.util.List;

@Configuration
public class FilterConfig {

    public static final Logger logger = LogManager.getLogger(FilterConfig.class);

    @Autowired
    private AuthService authService;
    @Autowired
    private EventRepository eventRepository;
    @Value("#{'${auth.filter.patterns}'.split(',')}")
    private List<String> authPatterns;
    @Value("#{'${permission.filter.patterns}'.split(',')}")
    private List<String> permissionPatterns;

    /**
     * this method is used to register the cors filter
     *
     * @return FilterRegistrationBean<CorsFilter>
     */
    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilterBean() {
        logger.info("Cors FilterBean has been created");

        FilterRegistrationBean<CorsFilter> registrationBean = new FilterRegistrationBean<>();
        CorsFilter corsFilter = new CorsFilter();

        registrationBean.setFilter(corsFilter);
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(1);

        return registrationBean;
    }
    /**
     * this method is used to register the token filter
     * the token filter initialized with the auth service
     * the token filter is running first in the filter chain
     *
     * @return FilterRegistrationBean<TokenFilter>
     */
    @Bean
    public FilterRegistrationBean<AuthFilter> AuthFilterBean() throws FileNotFoundException {
        logger.info("Auth Filter has been created");

        FilterRegistrationBean<AuthFilter> registrationBean = new FilterRegistrationBean<>();
        AuthFilter authFilter = new AuthFilter(authService);

        registrationBean.setFilter(authFilter);

        registrationBean.addUrlPatterns(authPatterns.toArray(new String[0]));
        registrationBean.setOrder(2);

        return registrationBean;
    }

    /**
     * his method is used to register the token filter
     * the token filter initialized with the auth service
     * the token filter is running first in the filter chain
     *
     * @return FilterRegistrationBean<TokenFilter>
     */
    @Bean
    public FilterRegistrationBean<PermissionFilter> PermissionFilterBean() {
        logger.info("Permission Filter Bean has been created");

        FilterRegistrationBean<PermissionFilter> registrationBean = new FilterRegistrationBean<>();
        PermissionFilter permissionFilter = new PermissionFilter(eventRepository);

        registrationBean.setFilter(permissionFilter);

        registrationBean.addUrlPatterns(permissionPatterns.toArray(new String[0]));
        registrationBean.setOrder(3);

        return registrationBean;
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .antMatchers("/**");
    }
}