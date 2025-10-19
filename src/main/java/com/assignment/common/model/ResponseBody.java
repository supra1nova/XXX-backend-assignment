package com.assignment.common.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

@Slf4j
@Getter
@ToString
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseBody {
    private String timestamp;
    private String code;
    private String message;

    public static ResponseEntity<ResponseBody> toResponseEntity(ResponseCode responseCode) {
        log.info("[toResponse] {}, {}, {}", responseCode.getHttpStatus(), responseCode.getCode(), responseCode.getDetail());
        return ResponseEntity
            .status(responseCode.getHttpStatus())
            .body(ResponseBody.builder()
                .message(responseCode.getDetail())
                .build()
            );
    }

    public static <T> ResponseEntity<T> toResponseEntity(ResponseCode responseCode, T data) {
        log.info("[toResponse] {}, {}, {}, {}", responseCode.getHttpStatus(), responseCode.getCode(), responseCode.getDetail(), data);
        return ResponseEntity
            .status(responseCode.getHttpStatus())
            .body(data);
    }

    public static ResponseEntity<ResponseBody> toErrorResponseEntity(ResponseCode responseCode, Exception e) {
        log.error("[toErrorResponse] {}, {}", responseCode.getHttpStatus(), responseCode.getCode());
        return ResponseEntity
            .status(responseCode.getHttpStatus())
            .contentType(MediaType.APPLICATION_JSON)
            .body(ResponseBody.builder()
                .timestamp(LocalDateTime.now().toString())
                .code(responseCode.getCode())
                .message(responseCode.getDetail())
                .build()
            );
    }
}
