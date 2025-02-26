package com.chuwa.accountservice.payload;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



@Getter
@Setter
@NoArgsConstructor

public class UserInfoDTO {

    private String email;

    private String username;

    private String password;

    public UserInfoDTO(String email, String username) {
        this.email = email;
        this.username = username;
    }
}
