package com.chuwa.accountservice.payload;

import com.chuwa.accountservice.model.Role;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;


@Getter
@Setter
@NoArgsConstructor

public class UserInfoDTO {

    private String email;

    private String username;

    private String password;

    private Set<Role> roles;

    public UserInfoDTO(String email, String username, Set<Role> roles) {
        this.email = email;
        this.username = username;
        this.roles = roles;
    }
}
