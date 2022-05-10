package com.humga.cloudservice.dto;

import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Email;

@Data
@Validated
public class LoginFormDTO {
    @Email(message = "{login.invalid}")
    private String login;
    private String password;
}
