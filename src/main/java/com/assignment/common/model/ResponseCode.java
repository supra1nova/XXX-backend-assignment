package com.assignment.common.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ResponseCode {
    /* 200 */
    OK(HttpStatus.OK, "S00001", "성공적으로 처리되었습니다."),

    /* 201 */
    CREATED(HttpStatus.CREATED, "S0A001", "성공적으로 저장되었습니다."),

    /* 202 */
    ACCEPTED(HttpStatus.ACCEPTED, "S0B001", "성공적으로 접수되었습니다.."),

    /* 204 */
    NO_CONTENT(HttpStatus.NO_CONTENT, "S0D001", null),

    /* 400 */
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "E00001", "잘못된 요청입니다."),
    INVALID_VALUES(HttpStatus.BAD_REQUEST, "E00002", "잘못된 입력값 입니다."),
    INVALID_HEADER_VALUE(HttpStatus.BAD_REQUEST, "E00003", "잘못된 헤더값 입니다."),
    INVALID_X_USER_ID(HttpStatus.BAD_REQUEST, "E00004", "잘못된 X-USER-ID 값 입니다."),
    CANNOT_SELF_FRIEND_REQUEST(HttpStatus.BAD_REQUEST, "E00005", "친구 신청은 스스로에게 할 수 없습니다."),
    ALREADY_FRIENDS(HttpStatus.BAD_REQUEST, "E00006", "이미 친구인 상대에게는 친구 신청을 할 수 없습니다."),
    FRIEND_REQUEST_ALREADY_SENT(HttpStatus.BAD_REQUEST, "E00007", "친구 신청이 이미 요청되었습니다."),
    FRIEND_REQUEST_ALREADY_RECEIVED(HttpStatus.BAD_REQUEST, "E00008", "상대로부터 받은 친구 신청이 이미 존재합니다."),

    /* 404 */
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "E0D001", "리소스가 존재하지 않습니다."),

    PATH_NOT_FOUND(HttpStatus.NOT_FOUND, "E0D002", "잘못된 요청 경로입니다"),
    REQUESTER_NOT_FOUND(HttpStatus.NOT_FOUND, "E0D003", "요청 사용자 정보가 존재하지 않습니다"),
    RECEIVER_NOT_FOUND(HttpStatus.NOT_FOUND, "E0D004", "대상 사용자 정보가 존재하지 않습니다"),
    X_USER_ID_NOT_FOUND(HttpStatus.NOT_FOUND, "E0D005", "X-user-Id 값이 헤더에 존재하지 않습니다"),
    FRIEND_REQUEST_NOT_FOUND(HttpStatus.BAD_REQUEST, "E00006", "친구 신청이 존재하지 않습니다."),

    /* 406 */
    INVALID_ACCEPT_HEADER(HttpStatus.NOT_ACCEPTABLE, "E0F001", "잘못된 Accept 입니다."),

    /* 409 */
    DUPLICATED_REQUEST(HttpStatus.CONFLICT, "E0I001", "지원하지 않는 Accept 헤더입니다."),

    /* 415 */
    INVALID_CONTENT_TYPE(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "EAE001", "지원하지 않는 Content-Type 입니다."),

    /* 422 */
    INVALID_REQUEST_STATE(HttpStatus.UNPROCESSABLE_ENTITY, "EBB001", "이미 처리된 요청입니다."),

    /* 429 */
    RATE_LIMIT_EXCEEDED(HttpStatus.UNPROCESSABLE_ENTITY, "EBI001", "요청이 너무 많습니다. 잠시 후 다시 시도해주세요."),
    ANOTHER_REQUEST_PROCESSING(HttpStatus.UNPROCESSABLE_ENTITY, "EBI002", "이미 처리중인 요청이 존재합니다. 잠시 후 다시 시도해주세요."),

    /* 500 */
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "U00001", "예기치 못한 서버 오류가 발생했습니다"),
    SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "U00002", "현재 서버가 일시적으로 응답할 수 없습니다"),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String detail;

    public static ResponseCode valueOf(HttpStatus statusCode) {
        ResponseCode[] responseCodes = values();
        for (ResponseCode responseCode : responseCodes) {
            if (responseCode.httpStatus == statusCode) {
                return responseCode;
            }
        }

        if (statusCode.is4xxClientError()) return INVALID_REQUEST;
        if (statusCode.is5xxServerError()) return INTERNAL_SERVER_ERROR;
        return SERVICE_UNAVAILABLE;
    }
}
