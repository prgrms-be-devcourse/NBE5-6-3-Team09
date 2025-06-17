package com.codemap.core.admin.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminUserUpdateDto {

    private String nickname;
    private String email;
    private String newPassword;     // 새로 저장할 비밀번호
}