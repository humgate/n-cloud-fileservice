package com.humga.cloudservice.controller;


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
@CrossOrigin(origins = "http://localhost:8080", allowCredentials = "true") //CORS on
@RequestMapping("/cloud")
public class FileController {
    private final FileService service;
    public FileController(FileService service) {
        this.service = service;
    }

    @PostMapping(value = "/file")
    public void postFile(
            @RequestParam("filename") String fileName, @RequestBody MultipartFile file, ModelMap modelMap
    ) throws IOException {
        SecurityContext sc = SecurityContextHolder.getContext();
        service.saveFile(fileName, file.getBytes());
    }

    @DeleteMapping(value = "/file")
    public void deleteFile(@RequestParam("filename") String filename) {
        service.deleteFile(filename);
    }

    @GetMapping(value = "/file", produces = MediaType.MULTIPART_FORM_DATA_VALUE)
    public MultiValueMap<String, Object> getFile(@RequestParam("filename") String filename) {

        //класс для подсчета чек-суммы файла
        Checksum crc32 = new CRC32();
        //service.getFile

        byte[] bytes = service.getFile(filename);
        //обновляем(вычисляем) чек-сумму на основе байтового массива полученного из файла
        crc32.update(bytes, 0, bytes.length);
        MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
        formData.add("hash", crc32.getValue());
        formData.add("file", bytes);
        return formData;
    }

    @PutMapping(value = "/file", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void updateFile(@RequestParam("filename") String filename, @RequestBody FileNameDTO fileNameDTO) {
        System.out.println(fileNameDTO.getFilename());
        service.renameFile(filename, fileNameDTO.getFilename());
    }

    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<FileInfoDTO> getFilesList(@RequestParam("limit") int limit) {
        return service
                .getFilesList(limit)
                .stream()
                .map(f -> new FileInfoDTO(f.getFilename(), f.getFile().length))
                .collect(Collectors.toList());
    }
}
