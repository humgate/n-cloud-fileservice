package com.humga.cloudservice.util;

import org.springframework.security.core.context.SecurityContextHolder;

public class Util {
    public static String getSessionUserLogin()  {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
