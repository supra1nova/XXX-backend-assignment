package com.assignment.common.exception;

import com.assignment.common.model.ResponseCode;
import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
    private final ResponseCode responseCode;

    public CustomException(ResponseCode responseCode) {
        super(responseCode.getDetail());
        this.responseCode = responseCode;
    }

    public CustomException(ResponseCode responseCode, Exception cause) {
        super(responseCode.getDetail(), cause);
        this.responseCode = responseCode;
    }
}
