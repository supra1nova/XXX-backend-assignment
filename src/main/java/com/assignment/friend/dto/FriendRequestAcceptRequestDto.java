package com.assignment.friend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FriendRequestAcceptRequestDto {
    @Schema(description = "친구 신청 유저 ID", example = "10")
    @Positive
    private Long fromUserId;
}
