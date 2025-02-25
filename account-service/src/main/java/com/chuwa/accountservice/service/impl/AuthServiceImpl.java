package com.chuwa.accountservice.service.impl;

import com.chuwa.accountservice.payload.LoginUserDTO;
import com.chuwa.accountservice.payload.RegisterUserDTO;
import com.chuwa.accountservice.service.AuthService;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {
    @Override
    public void registerUser(RegisterUserDTO registerUserDTO) {

    }

    @Override
    public Object authenticateUser(LoginUserDTO loginUserDTO) {
        return null;
    }
}
