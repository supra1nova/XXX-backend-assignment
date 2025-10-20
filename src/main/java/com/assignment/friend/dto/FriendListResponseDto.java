package com.assignment.friend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Schema(description = "응답용 친구 목록 DTO")
@Getter
@Builder
@AllArgsConstructor
public class FriendListResponseDto {
    @Schema(description = "기준 유저 ID", example = "2")
    @JsonProperty("user_id")
    private final Long userId;

    @Schema(description = "친구 신청 요청 ID", example = "2")
    @JsonProperty("from_user_id")
    private final Long fromUserId;

    @Schema(description = "친구 신청 수신 및 승인 ID", example = "2")
    @JsonProperty("to_user_id")
    private final Long toUserId;

    @Schema(description = "친구 신청 승인 일시", example = "2025-01-02T11:22:33.456Z")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private final Instant approvedAt;
}
