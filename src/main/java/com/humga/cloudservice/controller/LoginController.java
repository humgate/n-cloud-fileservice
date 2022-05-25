package com.humga.cloudservice.controller;

import com.humga.cloudservice.util.JwtTokenUtil;
import com.humga.cloudservice.model.LoginFormDTO;
import com.humga.cloudservice.exceptions.BadRequestException;
import com.humga.cloudservice.service.UserService;
import com.humga.cloudservice.util.TokenBlackList;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Objects;

import static com.humga.cloudservice.model.Constants.HEADER_STRING;
import static com.humga.cloudservice.model.Constants.TOKEN_PREFIX;

@RestController
@CrossOrigin(origins = "http://localhost:8080", allowCredentials = "true")
@RequestMapping("/cloud")
public class LoginController {
    private final JwtTokenUtil jwtTokenUtil;

    private final TokenBlackList tokenBlackList;

    private final AuthenticationManager authenticationManager;

    public LoginController(JwtTokenUtil jwtTokenUtil, TokenBlackList blackList, UserService userService,
                           AuthenticationManager authenticationManager) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.tokenBlackList = blackList;
        this.authenticationManager = authenticationManager;
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
        tokenBlackList.add(request.getHeader(HEADER_STRING).replace(TOKEN_PREFIX,""));
    }
}
