package com.humga.cloudservice.model;

import lombok.Data;

import org.springframework.web.multipart.MultipartFile;
@Data
public class FileDTO {
    String hash;
    MultipartFile file;
}
