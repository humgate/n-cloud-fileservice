package com.humga.cloudservice.controller;

import com.humga.cloudservice.config.AppProperties;
import com.humga.cloudservice.exceptions.BadRequestException;
import com.humga.cloudservice.model.LoginFormDTO;
import com.humga.cloudservice.security.JwtTokenManager;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Objects;


@RestController
@CrossOrigin(origins = "http://localhost", allowCredentials = "true")
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

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public String login(HttpServletRequest request, HttpServletResponse response,
                        @RequestBody @Valid LoginFormDTO loginFormDTO, BindingResult errors) {

        if (errors.hasErrors()) {
            throw new BadRequestException(errors.getAllErrors().stream().map(x -> x.getDefaultMessage())
                    .filter(Objects::nonNull).reduce(String::concat).orElse("Bad request111"));
        }

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
