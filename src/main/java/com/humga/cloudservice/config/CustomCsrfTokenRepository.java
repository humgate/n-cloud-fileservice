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
    private static final String INCOMING_TOKEN_ADDED_PREFIX = "Bearer ";
    private static final String CSRF_HEADER_NAME = "auth-token";

    private static final String CSRF_TOKEN_ATTR_NAME = ".CSRF_TOKEN";
    private final HttpSessionCsrfTokenRepository repository;

    CustomCsrfTokenRepository() {
        this.repository = new HttpSessionCsrfTokenRepository();
        repository.setHeaderName(CSRF_HEADER_NAME);
        repository.setSessionAttributeName(CSRF_TOKEN_ATTR_NAME);
    }

    @Override
    public CsrfToken generateToken(HttpServletRequest request) {
        return repository.generateToken(request);
    }

    @Override
    public void saveToken(CsrfToken token, HttpServletRequest request, HttpServletResponse response) {
        CsrfToken tokenToSave =
                new DefaultCsrfToken(
                        token.getHeaderName(),
                        token.getParameterName(),
                        INCOMING_TOKEN_ADDED_PREFIX + token.getToken());
        repository.saveToken(tokenToSave, request, response);
    }

    @Override
    public CsrfToken loadToken(HttpServletRequest request) {
        return repository.loadToken(request);
    }

    public void removeToken(HttpServletRequest request) {
        request.getSession(false).removeAttribute(CSRF_TOKEN_ATTR_NAME);
    }
}
