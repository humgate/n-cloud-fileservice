package com.humga.cloudservice.config;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.DefaultCsrfToken;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class CustomCsrfTokenRepository implements CsrfTokenRepository {
    private final HttpSessionCsrfTokenRepository repository;

    CustomCsrfTokenRepository() {
        this.repository = new HttpSessionCsrfTokenRepository();
        repository.setHeaderName("auth-token");
        repository.setSessionAttributeName("_csrfToken");
    }

    @Override
    public CsrfToken generateToken(HttpServletRequest request) {
        return repository.generateToken(request);
    }

    @Override
    public void saveToken(CsrfToken token, HttpServletRequest request, HttpServletResponse response) {
        CsrfToken tokenToSave =
                new DefaultCsrfToken(token.getHeaderName(), token.getParameterName(), "Bearer " + token.getToken());
        repository.saveToken(tokenToSave, request, response);
    }

    @Override
    public CsrfToken loadToken(HttpServletRequest request) {
        return repository.loadToken(request);
    }
}
