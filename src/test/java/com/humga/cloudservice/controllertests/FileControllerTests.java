package com.humga.cloudservice.controllertests;

import com.humga.cloudservice.service.FileService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;
import java.util.Collection;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class FileControllerTests {

    @MockBean
    FileService fileService;

    @Autowired
    private MockMvc mockMvc;

    private final byte[] testFile = {1,2,3};

    @Test
    void postFileTest() throws Exception {
        doNothing().when(fileService).saveFile(anyString(), any(), anyString());

        //when
        MvcResult result = mockMvc.perform(
                multipart("/cloud/file")
                        .file("file", testFile)
                        .param("hash","123")
                        .queryParam("filename", "file1.dat")
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        //This token expires on 01/01/3000, so if the test is still relevant, it will work)))
                        .header("auth-token", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhbGV4QGVtYWlsLmNvbSIs" +
                                "InNjb3BlcyI6W3siYXV0aG9yaXR5IjoiUk9MRV9SRUFEIn0seyJhdXRob3JpdHkiOiJST0xFX1dSSVR" +
                                "FIn1dLCJpc3MiOiJodHRwOi8vaHVtZ2Fpc3N1ZXIuY29tIiwiaWF0IjoxNjU0NjIyMTAyLCJleHAiOj" +
                                "kyNDY0NTc4MDAwfQ.fl3FwFZsx6IuuFbaQZuEOxC9oZPv6P3W0Jk4MUSxXt8"))
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();

        //then

    }


}
