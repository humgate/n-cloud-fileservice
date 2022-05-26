package com.humga.cloudservice.security;

import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.humga.cloudservice.config.AppProperties.TOKEN_BLACKLIST_MAX_SIZE;

@Component
public class TokenBlackList implements AutoExpiringBlackList {
    private final ExpiringMap<String, Object> tokenBlackList;
    @Value("${app.config.token_blacklist_max_size}")
    private int maxSize;

    public TokenBlackList() {
        tokenBlackList = ExpiringMap.builder()
                .expirationPolicy(ExpirationPolicy.CREATED)
                .variableExpiration()
                .build();
    }

    @PostConstruct
    private void setMaxSize() {
        tokenBlackList.setMaxSize(maxSize);
    }
    @Override
    public void add(String token, Object login, LocalDateTime expirationTime) {
        LocalDateTime now = LocalDateTime.now();

        if (expirationTime.isAfter(now)) {
            tokenBlackList.put(
                    token,
                    login.toString(),
                    ChronoUnit.SECONDS.between(now, expirationTime), TimeUnit.SECONDS);
        }
    }
    @Override
    public boolean contains (String token) {
        return tokenBlackList.containsKey(token);
    }

    @Override
    public Object getInfo(String token) {
        return tokenBlackList.get(token);
    }

    @Override
    public Map<String, Object> getAll() {
        return tokenBlackList;
    }
}
