package com.humga.cloudservice.servicetests;

import com.humga.cloudservice.model.entity.File;
import com.humga.cloudservice.repository.FileCrudRepository;
import com.humga.cloudservice.service.FileService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class FileServiceTests {
    @Autowired
    FileService fileService;

    @Autowired
    FileCrudRepository fileRepo;

   private final  byte[] testFile = {1,2,3};

    @Test
    @Transactional
    void saveFileTest() {
        //given service nd testfile

        //when
        fileService.saveFile("testfile.txt", testFile, "alex@email.com");

        //then
        File file = fileRepo.findFileByFilenameAndUser_Login("testfile.txt","alex@email.com")
                .orElse(new File());
        assertNotNull(file);
        assertEquals(file.getFilename(),"testfile.txt");
        assertArrayEquals(file.getFile(), testFile);
    }

    @Test
    @Transactional
    void saveFileDuplicateTest() {
        //given service and testfile saved once
        fileService.saveFile("testfile.txt", testFile, "alex@email.com");

        //when attempt to save duplicate
        DataIntegrityViolationException e = assertThrows(DataIntegrityViolationException.class,
                () -> fileService.saveFile("testfile.txt", testFile, "alex@email.com"));

        //then
        assertNotNull(Objects.requireNonNull(e.getMessage()));
        assertTrue(e.getMessage().contains("uk_files"));
    }

}
