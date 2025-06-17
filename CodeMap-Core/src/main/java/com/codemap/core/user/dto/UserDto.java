package com.codemap.core.user.dto;

import com.codemap.core.infra.auth.Role;
import com.codemap.core.user.domain.User;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class UserDto { // 회원가입, 로그인용

    private String email;
    private String nickname;
    private String password;
    private Role role;
    private LocalDateTime createdAt = LocalDateTime.now();

    public UserDto(User user) {
        this.email = user.getEmail();
        this.nickname = user.getNickname();
//        this.password = password;
    }
}