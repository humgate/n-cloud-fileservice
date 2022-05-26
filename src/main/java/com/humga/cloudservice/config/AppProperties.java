package com.humga.cloudservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConfigurationProperties(prefix = "app.config")
public class AppProperties {
    public static String TOKEN_ISSUER;
    public static long ACCESS_TOKEN_VALIDITY_SECONDS;
    public static String SIGNING_KEY;
    public static String TOKEN_PREFIX;
    public static String HEADER_STRING;
    public static int TOKEN_BLACKLIST_MAX_SIZE;
}
