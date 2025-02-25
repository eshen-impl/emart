package com.chuwa.accountservice.service;

import com.chuwa.accountservice.payload.LoginUserDTO;
import com.chuwa.accountservice.payload.RegisterUserDTO;

public interface AuthService {
    void registerUser(RegisterUserDTO registerUserDTO);

    Object authenticateUser(LoginUserDTO loginUserDTO);
}
