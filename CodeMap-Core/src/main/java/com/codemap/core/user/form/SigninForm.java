package com.codemap.core.user.form;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SigninForm {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 4, max = 10)
    private String password;
}