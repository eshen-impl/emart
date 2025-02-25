package com.chuwa.accountservice.service;

import com.chuwa.accountservice.payload.RegisterUserDTO;
import com.chuwa.accountservice.payload.UserInfoDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;


public interface AccountService {
    UserInfoDTO createAccount(RegisterUserDTO registerUserDTO);
    UserInfoDTO updateUserInfo(UUID userId, UserInfoDTO userInfoDTO);

    UserInfoDTO updateUserPassword(UUID userId, String password);

    UserInfoDTO getAccountById(UUID userId);
    Page<UserInfoDTO> getAllAccounts(Pageable pageable);
}
