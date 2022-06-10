package com.humga.cloudservice.unittests.controllertests;

import com.humga.cloudservice.model.FileInfoDTO;
import com.humga.cloudservice.model.entity.File;
import com.humga.cloudservice.service.FileService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.shaded.com.fasterxml.jackson.core.type.TypeReference;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class FileControllerTests {

    @MockBean
    FileService fileService;

    @Autowired
    private MockMvc mockMvc;

    private final byte[] TEST_FILE = {1, 2, 3};

    //This token expires on 01/01/3000, so if the test is still relevant, it will work)))
    private final String TOKEN = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhbGV4QGVtYWlsLmNvbSIs" +
            "InNjb3BlcyI6W3siYXV0aG9yaXR5IjoiUk9MRV9SRUFEIn0seyJhdXRob3JpdHkiOiJST0xFX1dSSVR" +
            "FIn1dLCJpc3MiOiJodHRwOi8vaHVtZ2Fpc3N1ZXIuY29tIiwiaWF0IjoxNjU0NjIyMTAyLCJleHAiOj" +
            "kyNDY0NTc4MDAwfQ.fl3FwFZsx6IuuFbaQZuEOxC9oZPv6P3W0Jk4MUSxXt8";

    @Test
    void postFileTest() throws Exception {
        doNothing().when(fileService).saveFile(anyString(), any(), anyString());

        //when then
        mockMvc.perform(
                        multipart("/cloud/file")
                                .file("file", TEST_FILE)
                                .param("hash", "123")
                                .queryParam("filename", "file1.dat")
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .header("auth-token", TOKEN))
                .andExpect(status().isOk());
    }

    @Test
    void deleteFileTest() throws Exception {
        doNothing().when(fileService).deleteFile(anyString(), anyString());

        //when then
        mockMvc.perform(
                        delete("/cloud/file")
                                .queryParam("filename", "file1.dat")
                                .header("auth-token", TOKEN))
                .andExpect(status().isOk());
    }

    @Test
    void getFileTest() throws Exception {
        when(fileService.getFile(anyString(), anyString())).thenReturn(TEST_FILE);

        //when then
        MvcResult result = mockMvc.perform(
                        get("/cloud/file")
                                .queryParam("filename", "file1.dat")
                                .header("auth-token", TOKEN))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        String searchString = "Content-Disposition: form-data; name=\"file\"\r\n" +
                "Content-Type: application/octet-stream\r\n" +
                "Content-Length: " + TEST_FILE.length + "\r\n\r\n";
        int filePartIdx = response.indexOf(searchString) + searchString.length();
        byte[] actual = response.substring(filePartIdx, filePartIdx + TEST_FILE.length).getBytes();
        assertArrayEquals(TEST_FILE, actual);
    }

    @Test
    void renameFileTest() throws Exception {
        doNothing().when(fileService).renameFile(anyString(), anyString(), anyString());

        //when then
        mockMvc.perform(
                        put("/cloud/file")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"filename\":\"file1renamed.dat\"}")
                                .queryParam("filename", "file1.dat")
                                .header("auth-token", TOKEN))
                .andExpect(status().isOk());

    }

    @Test
    void listFileTest() throws Exception {
        //mock for service
        List<File> list = new ArrayList<>();
        list.add(new File("file1", TEST_FILE));
        list.add(new File("file2", TEST_FILE));
        list.add(new File("file3", TEST_FILE));
        when(fileService.getFilesList(anyInt(), anyString())).thenReturn(list);

        //expected
        List<FileInfoDTO> expectedList = new ArrayList<>();
        expectedList.add(new FileInfoDTO("file1", 3));
        expectedList.add(new FileInfoDTO("file2", 3));
        expectedList.add(new FileInfoDTO("file3", 3));

        //when then
        String result = mockMvc.perform(
                        get("/cloud/list")
                                .queryParam("limit", "3")
                                .header("auth-token", TOKEN))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();


        ObjectMapper mapper = new ObjectMapper();
        List<FileInfoDTO> actualList = mapper.readValue(result, new TypeReference<List<FileInfoDTO>>() {});

        //FileInfoDTO correctly overrides equals (by lombok @Data), as well as the List, so this will work:
        assertEquals(expectedList, actualList);
    }
}
