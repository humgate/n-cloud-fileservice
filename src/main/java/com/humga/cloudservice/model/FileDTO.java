package com.humga.cloudservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import org.springframework.web.multipart.MultipartFile;
@Data @AllArgsConstructor
public class FileDTO {
    String hash;
    MultipartFile file;
}
