package com.codemap.core.user.form;

import com.codemap.core.user.dto.UserDto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SignupForm {


    @NotBlank
    @Email
    private String email;
    @NotBlank
    private String nickname;
    @NotBlank
    @Size(min = 4, max = 10)
    private String password;

    @NotBlank
    @Size(min = 4, max = 10)
    private String passwordConfirm; // 비번 확인용 필드


    public UserDto toDto() {
        UserDto user = new UserDto();
        user.setEmail(email);
        user.setPassword(password);
        user.setNickname(nickname);
        return user;
    }
}