package com.assignment.friend.dto;

import com.assignment.friend.entity.FriendRequestStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
@AllArgsConstructor
public class FriendRequestHistoryListResponseDto {
    @JsonProperty("history_id")
    private final String historyId;

    @JsonProperty("request_id")
    private final String requestId;

    @JsonProperty("from_user_id")
    private final Long fromUserId;

    @JsonProperty("to_user_id")
    private final Long toUserId;

    private FriendRequestStatus status;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private final Instant createdAt;
}
