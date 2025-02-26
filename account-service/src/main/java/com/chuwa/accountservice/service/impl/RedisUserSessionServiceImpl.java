package com.chuwa.accountservice.service.impl;

import com.chuwa.accountservice.model.UserSession;
import com.chuwa.accountservice.service.RedisUserSessionService;
import com.chuwa.accountservice.util.RedisUtil;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RedisUserSessionServiceImpl implements RedisUserSessionService {
    private final RedisUtil redisUtil;

    public RedisUserSessionServiceImpl(RedisUtil redisUtil) {
        this.redisUtil = redisUtil;
    }

    public void saveUserSession(String userId, UserSession session, long expiration) {
        redisUtil.save("login:" + userId, session, expiration);
    }

    public UserSession getUserSession(String userId) {
        return (UserSession) redisUtil.get("login:" + userId);
    }

    public void deleteUserSession(String userId) {
        redisUtil.delete("login:" + userId);
    }
}
