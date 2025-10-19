package com.assignment.friend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
@AllArgsConstructor
public class FriendListResponseDto {
    @JsonProperty("user_id")
    private final Long userId;

    @JsonProperty("from_user_id")
    private final Long fromUserId;

    @JsonProperty("to_user_id")
    private final Long toUserId;

    @JsonProperty("approved_at")
    private final Instant approvedAt;
}
