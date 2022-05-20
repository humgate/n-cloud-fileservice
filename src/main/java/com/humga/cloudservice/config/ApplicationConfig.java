package com.humga.cloudservice.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletRequest;

@Configuration
@EnableWebSecurity
//@EnableAspectJAutoProxy
public class ApplicationConfig extends WebSecurityConfigurerAdapter{

    @Autowired
    private CustomCsrfTokenRepository csrfTokenRepo;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        RequestMatcher csrfRequestMatcher = new RequestMatcher() {
            private final AntPathRequestMatcher requestMatcher =
                    new AntPathRequestMatcher ("/cloud/login", null);

            @Override
            public boolean matches(HttpServletRequest request) {
                // Disable the CSRF for matched and enable CSRF for other requests
                return !requestMatcher.matches(request);
            }
        };

        http
                .cors()
                    .and()
                .csrf()
                    .requireCsrfProtectionMatcher(csrfRequestMatcher)
                    .csrfTokenRepository(csrfTokenRepo)
                    .and()
                .authorizeRequests()
                    .antMatchers("/cloud/login").permitAll()
                    .antMatchers("/cloud/list").hasRole("USER");
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication().withUser("user@email.com").password("{noop}password").roles("USER");
    }

    @Bean
    public PasswordEncoder getPasswordEncoder(){
        return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}

