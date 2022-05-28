package com.humga.cloudservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorDTO {
    String message;
    int id;
}
