package com.humga.cloudservice.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.humga.cloudservice.model.ErrorDTO;
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

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); //хотя спецификация openApi требует 400
        response.addHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        response.flushBuffer();

        var dto = new ErrorDTO("Unauthorized. Incorrect login or password", 121);
        PrintWriter writer = response.getWriter();
        writer.println(mapper.writeValueAsString(dto));
        writer.close();
    }
}