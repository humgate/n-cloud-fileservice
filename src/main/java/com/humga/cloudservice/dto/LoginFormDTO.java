package com.humga.cloudservice.dto;

import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Pattern;

@Data
@Validated
public class LoginFormDTO {
    @Pattern(regexp = "^[A-Za-z0-9._]+[@][A-Za-z0-9.-]+[.][A-Za-z]+$",
            message = "{login.invalid}")
    private String login;
    private String password;
}
