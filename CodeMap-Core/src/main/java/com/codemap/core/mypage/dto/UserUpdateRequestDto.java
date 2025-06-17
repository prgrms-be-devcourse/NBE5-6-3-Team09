package com.codemap.core.mypage.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateRequestDto {

    private String nickname;
    private String password;
    private Boolean notificationEnabled;
    private Boolean alertStretchEnabled;
}