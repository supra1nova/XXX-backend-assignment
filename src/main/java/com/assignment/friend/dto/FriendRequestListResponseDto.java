package com.assignment.friend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
@AllArgsConstructor
public class FriendRequestListResponseDto {
    @JsonProperty("request_id")
    private final String requestId;

    @JsonProperty("request_user_id")
    private final Long fromUserId;

    private final Instant requestedAt;
}
