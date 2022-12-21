package calendar.configuration;

import calendar.filter.AuthFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@EnableWebSecurity
@Configuration
public class CustomWebSecurityConfigurerAdapter {
    //    @Autowired
    //    private CorsFilter corsFilter;

    @Autowired
    private AuthFilter authFilter;

    //    @Autowired
    //    private PermissionFilter permissionFilter;


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers(HttpMethod.OPTIONS, "/")
                .permitAll()
                .antMatchers("*")
                .authenticated()
                .and()
                .httpBasic()
                .and()
                .csrf().disable()
                .cors();
        http.addFilterAfter(authFilter, BasicAuthenticationFilter.class);
        //        http.addFilterAfter(permissionFilter, AuthFilter.class);


        //        http.addFilterAfter(corsFilter, BasicAuthenticationFilter.class);
        //        http.addFilterAfter(authFilter, CorsFilter.class);
        //        http.addFilterAfter(permissionFilter, AuthFilter.class);

        return http.build();
    }


    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**").allowedOrigins("*");
            }
        };
    }

}