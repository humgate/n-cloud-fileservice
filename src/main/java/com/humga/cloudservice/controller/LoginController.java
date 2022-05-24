package com.humga.cloudservice.controller;

import com.humga.cloudservice.config.CustomCsrfTokenRepository;
import com.humga.cloudservice.model.LoginFormDTO;
import com.humga.cloudservice.exceptions.BadRequestException;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.Objects;

import static org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;

@RestController
@CrossOrigin(origins = "http://localhost:8080", allowCredentials = "true")
@RequestMapping("/cloud")
public class LoginController {
    private final CustomCsrfTokenRepository csrfTokenRepo;
    private final AuthenticationManager authenticationManager;

    public LoginController(CustomCsrfTokenRepository csrfTokenRepo, AuthenticationManager authenticationManager) {
        this.csrfTokenRepo = csrfTokenRepo;
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
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginFormDTO.getLogin(), loginFormDTO.getPassword()));
        boolean isAuthenticated = authentication != null &&
                !(authentication instanceof AnonymousAuthenticationToken) &&
                authentication.isAuthenticated();

        //Если пользователь аутентифицирован, сохраняем аутентификацию в сессии
        if (isAuthenticated) {
            SecurityContext sc = SecurityContextHolder.getContext();
            sc.setAuthentication(authentication);
            HttpSession session = request.getSession(true);
            session.setAttribute(SPRING_SECURITY_CONTEXT_KEY, sc);
        }

        //генерируем csrf токен
        CsrfToken csrfToken = csrfTokenRepo.generateToken(request);
        csrfTokenRepo.saveToken(csrfToken, request, response);
        System.out.println("login auth-token: " + csrfToken.getToken());

        //отправляем токен фронт приложению
        return "{\"auth-token\":\"" + csrfToken.getToken() + "\"}";
    }

    @PostMapping (value = "/logout")
    public void logout(HttpServletRequest request) {
        csrfTokenRepo.removeToken(request);
    }
}
