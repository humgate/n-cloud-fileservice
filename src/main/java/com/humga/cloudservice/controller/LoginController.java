package com.humga.cloudservice.controller;

import com.humga.cloudservice.config.AppProperties;
import com.humga.cloudservice.model.LoginFormDTO;
import com.humga.cloudservice.security.JwtTokenManager;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;


@RestController
@CrossOrigin(originPatterns = "http://localhost**", allowCredentials = "true")
@RequestMapping("/cloud")
public class LoginController {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenManager tokenManager;
    private final AppProperties properties;

    public LoginController(AuthenticationManager authenticationManager, JwtTokenManager tokenManager,
                           AppProperties properties) {
        this.authenticationManager = authenticationManager;
        this.tokenManager = tokenManager;
        this.properties = properties;
    }

    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public String login(@RequestBody @Valid LoginFormDTO loginFormDTO) {

        Authentication authentication =  authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginFormDTO.getLogin(), loginFormDTO.getPassword()));

        final String token = tokenManager.generateToken(authentication.getName(), authentication.getAuthorities());

        return "{\"" + properties.getHeader() + "\":\"" + token + "\"}";
    }

    @PostMapping (value = "/logout")
    public void logout(HttpServletRequest request) {
        String token = tokenManager.getTokenFromRequest(request);
        tokenManager.invalidateToken(token);
    }
}
