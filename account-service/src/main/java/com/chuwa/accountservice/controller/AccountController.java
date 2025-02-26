package com.chuwa.accountservice.controller;


import com.chuwa.accountservice.payload.UserInfoDTO;
import com.chuwa.accountservice.service.AccountService;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;


@RestController
@RequestMapping("/api/v1/user/account")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }


    @PutMapping("/info")
    public ResponseEntity<UserInfoDTO> updateUserInfo(@Valid @RequestBody UserInfoDTO userInfoDTO, Principal principal) {
        UUID userId = UUID.fromString(principal.getName());
        UserInfoDTO updatedUserInfo = accountService.updateUserInfo(userId, userInfoDTO);
        return new ResponseEntity<>(updatedUserInfo, HttpStatus.OK);
    }


    @PutMapping("/password")
    public ResponseEntity<UserInfoDTO> updateUserPassword(@RequestBody UserInfoDTO userInfoDTO, Principal principal) {
        UUID userId = UUID.fromString(principal.getName());
        UserInfoDTO updatedUserInfo = accountService.updateUserPassword(userId, userInfoDTO);
        return new ResponseEntity<>(updatedUserInfo, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<UserInfoDTO> getAccountById(Principal principal) {
        UUID userId = UUID.fromString(principal.getName());
        UserInfoDTO userInfoDTO = accountService.getAccountById(userId);
        return new ResponseEntity<>(userInfoDTO, HttpStatus.OK);
    }

//    private UUID getUserIdFromSecurityContext() {
//        return UUID.fromString(
//                ((UserSession) SecurityContextHolder.getContext()
//                        .getAuthentication()
//                        .getPrincipal())
//                        .getUsername()
//        );
//    }

}
