package com.assignment.friend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Schema(description = "응답용 친구 신청 목록 DTO")
@Getter
@Builder
@AllArgsConstructor
public class FriendRequestListResponseDto {
    @Schema(description = "친구 신청 요청 ID", example = "db0e5d3c-6d6a-44d9-b149-86e682c33771")
    @JsonProperty("request_id")
    private final String requestId;

    @Schema(description = "친구 신청 요청자 ID", example = "10")
    @JsonProperty("request_user_id")
    private final Long fromUserId;

    @Schema(description = "친구 신청 일시", example = "2025-01-02T11:22:33.456Z")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private final Instant requestedAt;
}
