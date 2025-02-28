package com.chuwa.accountservice.controller;


import com.chuwa.accountservice.payload.UserInfoDTO;
import com.chuwa.accountservice.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
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
    @Operation(summary = "Update username and user email",
            description = "Required to be authenticated (have signed in).")
    public ResponseEntity<UserInfoDTO> updateUserInfo(@Valid @RequestBody UserInfoDTO userInfoDTO, Principal principal) {
        UUID userId = UUID.fromString(principal.getName());
        UserInfoDTO updatedUserInfo = accountService.updateUserInfo(userId, userInfoDTO);
        return new ResponseEntity<>(updatedUserInfo, HttpStatus.OK);
    }


    @PutMapping("/password")
    @Operation(summary = "Update user account password",
            description = "Required to be authenticated (have signed in).")
    public ResponseEntity<UserInfoDTO> updateUserPassword(@RequestBody UserInfoDTO userInfoDTO, Principal principal) {
        UUID userId = UUID.fromString(principal.getName());
        UserInfoDTO updatedUserInfo = accountService.updateUserPassword(userId, userInfoDTO);
        return new ResponseEntity<>(updatedUserInfo, HttpStatus.OK);
    }

    @GetMapping
    @Operation(summary = "Get user account info",
            description = "Required to be authenticated (have signed in).")
    public ResponseEntity<UserInfoDTO> getAccountById(Principal principal) {
        UUID userId = UUID.fromString(principal.getName());
        UserInfoDTO userInfoDTO = accountService.getAccountById(userId);
        return new ResponseEntity<>(userInfoDTO, HttpStatus.OK);
    }



}
