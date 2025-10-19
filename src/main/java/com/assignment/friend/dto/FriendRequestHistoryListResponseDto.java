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
    private final String historyId;

    private final String requestId;

    private final Long fromUserId;

    private final Long toUserId;

    private FriendRequestStatus status;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private final Instant createdAt;
}
