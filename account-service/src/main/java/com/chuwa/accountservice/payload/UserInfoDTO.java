package com.chuwa.accountservice.payload;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoDTO {

    @NotBlank(message = "User email is required")
    private String email;

    @NotBlank(message = "Username is required")
    private String username;

}
