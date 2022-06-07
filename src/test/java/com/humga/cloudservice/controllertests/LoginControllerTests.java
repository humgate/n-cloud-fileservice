package com.humga.cloudservice.controllertests;

import com.humga.cloudservice.CloudServiceApplication;
import com.humga.cloudservice.controller.LoginController;
import com.humga.cloudservice.model.LoginFormDTO;
import com.humga.cloudservice.security.JwtAuthenticationFilter;
import com.humga.cloudservice.security.JwtTokenManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.when;


@SpringBootTest
@AutoConfigureMockMvc
public class LoginControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtTokenManager jwtTokenManager;

    @Test
    void loginTest() throws Exception {
        //given
        when(jwtTokenManager.generateToken(anyString(), any(Collection.class))).thenReturn("testtoken");

        //when
        MvcResult result = mockMvc.perform(post("/cloud/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\n" +
                                "    \"login\" : \"alex@email.com\",\n" +
                                "    \"password\" : \"passAlex\"\n" +
                                "}"))
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();

        //then
        assertEquals(content,"{\"auth-token\":\"testtoken\"}");
    }

    @Test
    void loginFormatInvalidTest() throws Exception {
        //when-then
        mockMvc.perform(post("/cloud/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\n" +
                                "    \"login\" : \"alexcom\",\n" +
                                "    \"password\" : \"pass\"\n" +
                                "}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("{\"message\":" +
                        "\"Invalid login format. Login must be a valid e-mail.\",\"id\":101}"));
    }

//    @Test
//    void logoutNotLoggedInTest() throws Exception {
//        //given
//        when(jwtTokenManager.getTokenFromHeader(anyString())).thenReturn("testtoken");
//        doNothing().when(jwtTokenManager).invalidateToken(anyString());
//
//        //when-then
//        mockMvc.perform(post("/cloud/logout")
//                .contentType(MediaType.APPLICATION_JSON_VALUE))
//                .andExpect(status().isUnauthorized())
//                .andExpect(content().string("{\"message\":\"Unauthorized. Bad credentials.\",\"id\":100}"));
//    }
}
