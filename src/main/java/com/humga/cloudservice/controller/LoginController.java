package com.humga.cloudservice.controller;

import com.humga.cloudservice.config.AppProperties;
import com.humga.cloudservice.exceptions.BadRequestException;
import com.humga.cloudservice.model.LoginFormDTO;
import com.humga.cloudservice.security.JwtTokenUtil;
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
@CrossOrigin(origins = "http://localhost:8080", allowCredentials = "true")
@RequestMapping("/cloud")
public class LoginController {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final AppProperties properties;

    public LoginController(AuthenticationManager authenticationManager,
                           JwtTokenUtil jwtTokenUtil, AppProperties properties) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
        this.properties = properties;
    }

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public String login(HttpServletRequest request, HttpServletResponse response,
                        @RequestBody @Valid LoginFormDTO loginFormDTO, BindingResult errors) {
        //проверка наличия ошибок валидации формата login
        if (errors.hasErrors()) {
            String msg = errors.getAllErrors()
                    .stream()
                    .map(x -> x.getDefaultMessage())
                    .filter(Objects::nonNull)
                    .reduce(String::concat)
                    .orElse("Bad request");
            throw new BadRequestException(msg);
        }

        //аутентифицируем пользователя
        Authentication authentication =  authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginFormDTO.getLogin(), loginFormDTO.getPassword()));

        //генерируем токен для логина с набором authorities полученным при аутентификации из БД
        final String token = jwtTokenUtil.generateToken(authentication.getName(), authentication.getAuthorities());

        return "{\"auth-token\":\"" + token + "\"}";
    }

    @PostMapping (value = "/logout")
    public void logout(HttpServletRequest request) {
        String header = request.getHeader(properties.getHeader());
        String token = jwtTokenUtil.getTokenFromHeader(header);
        jwtTokenUtil.invalidateToken(token);
    }
}
