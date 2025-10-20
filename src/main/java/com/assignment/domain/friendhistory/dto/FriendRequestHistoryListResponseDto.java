package com.assignment.domain.friendhistory.dto;

import com.assignment.domain.friendhistory.entity.FriendRequestStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Schema(description = "응답용 친구 신청 내역 목록 DTO")
@Getter
@Builder
@AllArgsConstructor
public class FriendRequestHistoryListResponseDto {
    @Schema(description = "친구 신청 내역 ID", example = "2bf6e409-6d1d-4b95-9dbf-21be48dbdd25")
    private final String historyId;

    @Schema(description = "친구 신청 요청 ID", example = "4664b811-1a6f-46cc-b958-490926122ab5")
    private final String requestId;

    @Schema(description = "친구 신청 요청자 ID", example = "2")
    private final Long fromUserId;

    @Schema(description = "친구 신청 수신자 ID", example = "10")
    private final Long toUserId;

    @Schema(description = "친구 신청 처리 상태", example = "PENDING")
    private FriendRequestStatus status;

    @Schema(description = "친구 신청 내역 생성 일시", example = "2025-01-02T11:22:33.456Z")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private final Instant createdAt;
}
