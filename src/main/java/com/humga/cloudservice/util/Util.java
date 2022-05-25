package com.humga.cloudservice.util;

import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class Util {
    public static String getSessionUserLogin()  {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    public static LocalDateTime convertToLocalDateTime(Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }
}
