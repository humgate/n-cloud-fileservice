package com.humga.cloudservice.util;

import java.time.LocalDateTime;
import java.util.Map;

public interface AutoExpiringBlackList {
    void add(String token, Object info, LocalDateTime expirationTime);
    boolean contains(String token);
    Object getInfo(String token);
    Map<String, Object> getAll();
}
