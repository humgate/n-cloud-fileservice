package com.humga.cloudservice.controller;


import com.humga.cloudservice.config.CustomCsrfTokenRepository;
import com.humga.cloudservice.dto.FileDTO;
import com.humga.cloudservice.dto.FileNameDTO;
import com.humga.cloudservice.dto.LoginFormDTO;
import com.humga.cloudservice.entity.File;
import com.humga.cloudservice.exceptions.BadRequestException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.MediaType;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.csrf.CsrfToken;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.humga.cloudservice.service.CloudService;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

import static org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;


@RestController
@CrossOrigin(originPatterns = "http://localhost**", allowCredentials = "true") //CORS on
@RequestMapping("/cloud")
public class CloudController {

    private final CloudService service;
    private final CustomCsrfTokenRepository csrfTokenRepo;

    private final AuthenticationManager authenticationManager;

    public CloudController(CloudService service, CustomCsrfTokenRepository csrfTokenRepo, AuthenticationManager authenticationManager ) {
        this.service = service;
        this.csrfTokenRepo = csrfTokenRepo;
        this.authenticationManager = authenticationManager;
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
    public List<FileDTO> getFilesList(
            @RequestParam("limit") int limit, @RequestHeader("auth-token") String authToken) {
        System.out.println("authToken: " + authToken);
        return service
                .getFilesList(limit)
                .stream()
                .map(f -> new FileDTO(f.getFilename(), f.getFile().length))
                .collect(Collectors.toList());
    }



    @PostMapping (value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public String login(HttpServletRequest request, HttpServletResponse response,
                        @RequestBody @Valid LoginFormDTO loginFormDTO, BindingResult errors) {
        if (errors.hasErrors()) {
            String msg = errors.getAllErrors()
                    .stream()
                    .map(x -> x.getDefaultMessage())
                    .filter(Objects::nonNull)
                    .reduce(String::concat)
                    .orElse("Bad request");
            throw new BadRequestException(msg);
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginFormDTO.getLogin(), loginFormDTO.getPassword()));
        boolean isAuthenticated = authentication != null &&
                        !(authentication instanceof AnonymousAuthenticationToken) &&
                        authentication.isAuthenticated();
        if (isAuthenticated) {
            SecurityContext sc = SecurityContextHolder.getContext();
            sc.setAuthentication(authentication);
            HttpSession session = request.getSession(true);
            session.setAttribute(SPRING_SECURITY_CONTEXT_KEY, sc);
        }

        CsrfToken csrfToken = csrfTokenRepo.generateToken(request);
        csrfTokenRepo.saveToken(csrfToken, request, response);
        System.out.println("login auth-token: " + csrfToken.getToken());

        //service.login
        return "{\"auth-token\":\"" + csrfToken.getToken() + "\"}";
    }

    @PostMapping (value = "/logout")
    public void logout(@RequestHeader("auth-token") String authToken, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        session.removeAttribute("_csrfToken");
        //service.logout
    }
}
