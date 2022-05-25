package com.humga.cloudservice.util;

import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static com.humga.cloudservice.model.Constants.TOKEN_BLACKLIST_MAX_SIZE;
@Component
public class TokenBlackList {
    private final ExpiringMap<String, LocalDateTime> tokenBlackList;

    public TokenBlackList() {
        tokenBlackList = ExpiringMap.builder()
                .maxSize(TOKEN_BLACKLIST_MAX_SIZE)
                .expirationPolicy(ExpirationPolicy.CREATED)
                .variableExpiration()
                .build();
    }

    public void add(String token) {
        LocalDateTime expirationTime = convertToLocalDateTime(JwtTokenUtil.getExpirationDateFromToken(token));
        LocalDateTime now = LocalDateTime.now();

        if (expirationTime.isAfter(now)) {
            tokenBlackList.put(
                    token,
                    LocalDateTime.now(),
                    ChronoUnit.SECONDS.between(now, expirationTime), TimeUnit.SECONDS);
        }
    }

    public boolean contains (String token) {
        return tokenBlackList.containsKey(token);
    }

    private LocalDateTime convertToLocalDateTime(Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }
}
