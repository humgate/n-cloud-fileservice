package com.humga.cloudservice.controller;


import com.humga.cloudservice.dto.FileNameDTO;
import com.humga.cloudservice.dto.LoginFormDTO;
import com.humga.cloudservice.entity.File;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.humga.cloudservice.service.CloudService;


import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.CRC32;
import java.util.zip.Checksum;


@RestController
@CrossOrigin //CORS on: фронт, загруженный из одного источника сможет обращаться к приложению, запущенному на другом
@RequestMapping("/cloud")
public class CloudController {

    private final CloudService service;

    public CloudController(CloudService service) {
        this.service = service;
    }

    @PostMapping(value = "/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void postFile(
            @RequestParam("filename") String fileName, @RequestHeader("auth-token") String authToken,
            @RequestParam("hash") String hash, @RequestParam("file") MultipartFile file) throws IOException {

        service.saveFile(fileName, file.getBytes());
    }

    @DeleteMapping(value = "/file")
    public void deleteFile(
            @RequestParam("filename") String filename, @RequestHeader("auth-token") String authToken) {

        service.deleteFile(filename);
    }

    @GetMapping(value = "/file", produces = MediaType.MULTIPART_FORM_DATA_VALUE)
    public MultiValueMap<String, Object> getFile(
            @RequestParam("filename") String filename, @RequestHeader("auth-token") String authToken) {

        //класс для подсчета чек-суммы файла
        Checksum crc32 = new CRC32();
        //service.getFile

        byte[] bytes = service.getFile(filename);
        //обновляем(вычисляем) чек-сумму на основе байтового массива полученного из файла
        crc32.update(bytes, 0, bytes.length);
        MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
        formData.add("hash",  crc32.getValue());
        formData.add("file", bytes);
        return formData;
    }

    @PutMapping(value = "/file", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void updateFile(
            @RequestParam("filename") String filename, @RequestHeader("auth-token") String authToken,
            @RequestBody FileNameDTO fileNameDTO) {

        service.renameFile(filename, fileNameDTO.getName());
    }

    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Integer> getFilesList(
            @RequestParam("limit") int limit, @RequestHeader("auth-token") String authToken) {

        return service
                .getFilesList(limit)
                .stream()
                .collect(Collectors.toMap(File::getFilename, f -> f.getFile().length));
    }

    @PostMapping (value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public String login(@RequestBody LoginFormDTO loginFormDTO) {

        //service.login
        return "{\"auth-token\":"+"12312312}";
    }

    @PostMapping (value = "/logout")
    public void logout(@RequestHeader("auth-token") String authToken) {

        //service.logout
    }
}
