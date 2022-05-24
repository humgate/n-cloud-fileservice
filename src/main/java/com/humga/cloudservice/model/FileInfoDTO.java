package com.humga.cloudservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class FileInfoDTO {
    String filename;
    int size;
}
