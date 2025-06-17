package com.codemap.core.infra.error.exceptions;

import com.codemap.core.infra.response.ResponseCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CommonException extends RuntimeException {

    private final ResponseCode code;

    public CommonException(ResponseCode code) {
        super(code.message());
        this.code = code;
    }

    public CommonException(ResponseCode code, Exception e) {
        this.code = code;
        //log.error(e.getMessage(), e);
    }

    public ResponseCode code() {
        return code;
    }
}