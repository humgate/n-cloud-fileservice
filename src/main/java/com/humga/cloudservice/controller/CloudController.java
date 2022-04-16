package com.humga.cloudservice.controller;


import lombok.Data;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.zip.CRC32;
import java.util.zip.Checksum;


@RestController
@CrossOrigin //CORS on: фронт, загруженный из одного источника сможет обращаться к приложению, запущенному на другом
@RequestMapping("/cloud")
public class CloudController {

    @PostMapping(value = "/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void postFile(
            @RequestParam("filename") String name, @RequestHeader("auth-token") String authToken,
            @RequestParam("hash") String hash, @RequestParam("file") MultipartFile file) throws IOException {
        file.transferTo(Paths.get("downloaded.http"));
        //service.saveFile
    }

    @DeleteMapping(value = "/file")
    public void deleteFile(
            @RequestParam("filename") String name, @RequestHeader("auth-token") String authToken) {
        //service.deleteFile
    }

    @GetMapping(value = "/file", produces = MediaType.MULTIPART_FORM_DATA_VALUE)
    public MultiValueMap<String, Object> getFile(
            @RequestParam("filename") String name, @RequestHeader("auth-token") String authToken) {
        //класс для подсчета чек-суммы файла
        Checksum crc32 = new CRC32();
        //service.getFile
        FileSystemResource fileResource = new FileSystemResource("src/test/test-requests.http");
        byte[] bytes = fileResource.toString().getBytes();
        //обновляем(вычисляем) чек-сумму на основе байтового массива полученного из файла
        crc32.update(bytes, 0, bytes.length);
        MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
        formData.add("hash",  crc32.getValue());
        formData.add("file", fileResource);
        return formData;
    }

    @PutMapping (value = "/file", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void updateFile(
            @RequestParam("filename") String name, @RequestHeader("auth-token") String authToken,
            @RequestBody String newName) {
        //service.updateFile
    }
}