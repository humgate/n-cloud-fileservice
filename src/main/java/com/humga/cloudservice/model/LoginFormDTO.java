package com.humga.cloudservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Email;

@Data
@Validated @AllArgsConstructor
public class LoginFormDTO {
    @Email(message = "{username.format.invalid}")
    private String login;
    private String password;
}
