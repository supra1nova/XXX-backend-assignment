package com.assignment.common.exception;

import com.assignment.common.model.ResponseCode;
import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
    private final ResponseCode responseCode;
    private final Exception exception;

    public CustomException(ResponseCode responseCode) {
        this.responseCode = responseCode;
        this.exception = new RuntimeException(responseCode.getDetail());
    }

    public CustomException(ResponseCode responseCode, Exception exception) {
        this.responseCode = responseCode;
        this.exception = exception;
    }
}
