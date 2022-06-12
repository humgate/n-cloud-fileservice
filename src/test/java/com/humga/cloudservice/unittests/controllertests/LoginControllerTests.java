package com.humga.cloudservice.unittests.controllertests;

import com.humga.cloudservice.security.JwtTokenManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
public class LoginControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @SpyBean
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
                        "\"Invalid login format. Login must be a valid e-mail.\",\"id\":102}"));
    }

    @Test
    void logoutTest() throws Exception {
        //given
        //This token expires on 01/01/3000, so if the test is still relevant, it will work)))
        final String token = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhbGV4QGVtYWlsLmNvbSIs" +
                "InNjb3BlcyI6W3siYXV0aG9yaXR5IjoiUk9MRV9SRUFEIn0seyJhdXRob3JpdHkiOiJST0xFX1dSSVR" +
                "FIn1dLCJpc3MiOiJodHRwOi8vaHVtZ2Fpc3N1ZXIuY29tIiwiaWF0IjoxNjU0NjIyMTAyLCJleHAiOj" +
                "kyNDY0NTc4MDAwfQ.fl3FwFZsx6IuuFbaQZuEOxC9oZPv6P3W0Jk4MUSxXt8";

        doNothing().when(jwtTokenManager).invalidateToken(anyString());

        //when-then
        mockMvc.perform(post("/cloud/logout")
                .header("auth-token", token))
                .andExpect(status().isOk());
    }
}
