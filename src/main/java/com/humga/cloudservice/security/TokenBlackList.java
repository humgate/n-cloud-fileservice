package com.humga.cloudservice.security;

import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class TokenBlackList implements AutoExpiringBlackList {
    private final ExpiringMap<String, Object> tokenBlackList;

    public TokenBlackList(int maxSize) {
        tokenBlackList = ExpiringMap.builder()
                .maxSize(maxSize)
                .expirationPolicy(ExpirationPolicy.CREATED)
                .variableExpiration()
                .build();
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
