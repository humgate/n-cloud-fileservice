package com.humga.cloudservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class UnauthorizedResponseDTO {
    String message;
    int id;
}
