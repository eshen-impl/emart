package com.chuwa.accountservice.controller;

import com.chuwa.accountservice.payload.SignInUserDTO;
import com.chuwa.accountservice.payload.SignUpUserDTO;
import com.chuwa.accountservice.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Map;
import java.util.UUID;


@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/sign-up")
    public ResponseEntity<String> signUp(@RequestBody SignUpUserDTO signUpUserDTO) {
        authService.signUp(signUpUserDTO);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/sign-in")
    public ResponseEntity<Map<String, String>> signIn(@RequestBody SignInUserDTO signInUserDTO) {
        return ResponseEntity.ok(authService.signIn(signInUserDTO));
    }

    @PostMapping("/sign-out")
    public ResponseEntity<String> signOut(Principal principal) {
        UUID userId = UUID.fromString(principal.getName());
        authService.signOut(userId);
        return ResponseEntity.ok("User signed out successfully");
    }
}
