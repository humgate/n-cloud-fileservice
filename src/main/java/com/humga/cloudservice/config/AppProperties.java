package com.humga.cloudservice.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConfigurationProperties(prefix = "app.config.token")
@ConstructorBinding
@AllArgsConstructor
@Getter
public class AppProperties {
    private final String issuer;
    private final long validitySeconds;
    private final String signingKey;
    private final String prefix;
    private final String header;
    private final int blacklistMaxSize;
}


