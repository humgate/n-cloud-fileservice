package com.humga.cloudservice.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.humga.cloudservice.model.UnauthorizedResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint, Serializable {
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); //именно 400 требует спецификация openApi
        response.addHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        response.flushBuffer();

        var dto = new UnauthorizedResponseDTO("Incorrect login or password", 401);
        PrintWriter writer = response.getWriter();
        writer.println(mapper.writeValueAsString(dto));
        writer.close();
    }
}