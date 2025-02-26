package com.chuwa.accountservice.service;

import com.chuwa.accountservice.payload.SignInUserDTO;
import com.chuwa.accountservice.payload.SignUpUserDTO;

import java.util.Map;
import java.util.UUID;

public interface AuthService {
    void signUp(SignUpUserDTO signUpUserDTO);

    Map<String, String> signIn(SignInUserDTO signInUserDTO);

    void signOut(UUID userId);
}
