package com.codemap.core.infra.response;

import org.springframework.http.HttpStatus;

public enum ResponseCode {
    OK("200", HttpStatus.OK, "정상적으로 완료되었습니다."),
    BAD_REQUEST("400", HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    UNAUTHORIZED("403", HttpStatus.UNAUTHORIZED, "권한이 없습니다."),
    INTERNAL_SERVER_ERROR("500", HttpStatus.INTERNAL_SERVER_ERROR, "서버에러 입니다."),
    USER_NOT_FOUND("404", HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다.");

    private final String code;
    private final HttpStatus status;
    private final String message;

    ResponseCode(String code, HttpStatus status, String message) {
        this.code = code;
        this.status = status;
        this.message = message;
    }

    public String code() {
        return code;
    }

    public HttpStatus status() {
        return status;
    }

    public String message() {
        return message;
    }
}