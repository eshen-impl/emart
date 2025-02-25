package com.chuwa.accountservice.payload;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterUserDTO {
    @NotBlank(message = "User email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "User password is required")
    private String password;

    @NotBlank(message = "Username is required")
    private String username;
}
