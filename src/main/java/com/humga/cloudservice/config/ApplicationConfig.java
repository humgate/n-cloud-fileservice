package com.humga.cloudservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
//@EnableAspectJAutoProxy
public class ApplicationConfig {


    //encoder для шифрования данных на базе BCrypt
//        @Bean
//        public PasswordEncoder encoder() {
//            return new BCryptPasswordEncoder();
//        }

    //в конфигурации Spring security укажем, чтобы наше api не требовало аутентификации
    @EnableWebSecurity
    public static class SecurityConfig extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.csrf().disable();
        }
    }
}

