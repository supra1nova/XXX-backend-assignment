package com.assignment.friend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FriendListResponseDto {
    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("from_user_id")
    private Long fromUserId;

    @JsonProperty("to_user_id")
    private Long toUserId;

    @JsonProperty("approved_at")
    private Instant approvedAt;
}
