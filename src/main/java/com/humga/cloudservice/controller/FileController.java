package com.humga.cloudservice.controller;


import com.humga.cloudservice.model.FileDTO;
import com.humga.cloudservice.model.FileInfoDTO;
import com.humga.cloudservice.model.FileNameDTO;
import org.springframework.http.MediaType;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.ModelMap;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.humga.cloudservice.service.FileService;

import java.io.IOException;
import java.util.List;

import java.util.stream.Collectors;
import java.util.zip.CRC32;
import java.util.zip.Checksum;


@RestController
@CrossOrigin(originPatterns = "http://localhost**", allowCredentials = "true")
@RequestMapping("/cloud")
public class FileController {
    private final FileService service;
    public FileController(FileService service) {
        this.service = service;
    }

    @PostMapping(value = "/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void postFile(@RequestParam("filename") String fileName, @ModelAttribute FileDTO file) throws IOException {
        service.saveFile(fileName, file.getFile().getBytes(), getCurrentUserLogin());
    }

    @DeleteMapping(value = "/file")
    public void deleteFile(@RequestParam("filename") String filename) {
        service.deleteFile(filename, getCurrentUserLogin());
    }

    @GetMapping(value = "/file", produces = MediaType.MULTIPART_FORM_DATA_VALUE)
    public MultiValueMap<String, Object> getFile(@RequestParam("filename") String filename) {
        byte[] bytes = service.getFile(filename, getCurrentUserLogin());

        //вычисляем чек-сумму на основе байтового массива полученного из файла
        Checksum crc32 = new CRC32();
        crc32.update(bytes, 0, bytes.length);
        MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
        formData.add("hash", crc32.getValue());
        formData.add("file", bytes);
        return formData;
    }

    @PutMapping(value = "/file", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void updateFile(@RequestParam("filename") String filename, @RequestBody FileNameDTO fileNameDTO) {
        service.renameFile(filename, fileNameDTO.getFilename(), getCurrentUserLogin());
    }

    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<FileInfoDTO> getFilesList(@RequestParam("limit") int limit) {
        return service
                .getFilesList(limit, getCurrentUserLogin())
                .stream()
                .map(f -> new FileInfoDTO(f.getFilename(), f.getFile().length))
                .collect(Collectors.toList());
    }

    private String getCurrentUserLogin()  {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
