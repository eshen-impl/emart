package com.chuwa.accountservice.service;


import com.chuwa.accountservice.payload.UserInfoDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;


public interface AccountService {

    UserInfoDTO updateUserInfo(UUID userId, UserInfoDTO userInfoDTO);

    UserInfoDTO updateUserPassword(UUID userId, UserInfoDTO userInfoDTO);

    UserInfoDTO getAccountById(UUID userId);
    Page<UserInfoDTO> getAllAccounts(Pageable pageable);
}
