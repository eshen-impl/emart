package com.chuwa.accountservice.service;

import com.chuwa.accountservice.model.UserSession;


public interface RedisUserSessionService {
    void saveUserSession(String userId, UserSession session, long expiration);

    UserSession getUserSession(String userId);

    void deleteUserSession(String userId);
}
