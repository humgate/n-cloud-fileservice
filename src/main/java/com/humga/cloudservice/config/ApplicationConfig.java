package com.humga.cloudservice.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.stereotype.Component;

@Configuration
@EnableWebSecurity
//@EnableAspectJAutoProxy
public class ApplicationConfig {

    @Autowired
    private CustomCsrfTokenRepository csrfTokenRepo;

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {

        http.csrf().csrfTokenRepository(csrfTokenRepo)
                .and()
                .csrf().ignoringAntMatchers("/cloud/login")
                .and()
                .authorizeRequests().antMatchers("/cloud/login").permitAll()
                .and()
                .authorizeRequests().anyRequest().authenticated();
        return http.build();
    }

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }
}

