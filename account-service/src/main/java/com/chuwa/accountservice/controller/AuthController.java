package com.chuwa.accountservice.controller;

import com.chuwa.accountservice.payload.LoginUserDTO;
import com.chuwa.accountservice.payload.RegisterUserDTO;
import com.chuwa.accountservice.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Manages login and token generation (JWT)
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody RegisterUserDTO registerUserDTO) {
        authService.registerUser(registerUserDTO);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<String> authenticateUser(@RequestBody LoginUserDTO loginUserDTO) {
        return ResponseEntity.ok(authService.authenticateUser(loginUserDTO));
    }
}
