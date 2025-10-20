package com.assignment.common.model;

import com.assignment.common.utils.CommonUtils;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.time.Instant;

@Schema(description = "응답 래퍼용 객체")
@Slf4j
@Getter
@ToString
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseBody {
    @Schema(description = "응답 래퍼용 객체")
    private String timestamp;
    @Schema(description = "응답 코드")
    private String code;
    @Schema(description = "응답 메세지")
    private String message;

    public static ResponseEntity<ResponseBody> toResponseEntity(ResponseCode responseCode) {
        log.info("[toResponse] {}, {}, {}", responseCode.getHttpStatus(), responseCode.getCode(), responseCode.getDetail());
        return ResponseEntity
            .status(responseCode.getHttpStatus())
            .body(ResponseBody.builder()
                .timestamp(CommonUtils.toCustomDateTimeString(Instant.now()))
                .code(responseCode.getCode())
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
                .timestamp(CommonUtils.toCustomDateTimeString(Instant.now()))
                .code(responseCode.getCode())
                .message(responseCode.getDetail())
                .build()
            );
    }
}
