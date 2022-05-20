package com.humga.cloudservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class FileDTO {
    String filename;
    int size;
}
