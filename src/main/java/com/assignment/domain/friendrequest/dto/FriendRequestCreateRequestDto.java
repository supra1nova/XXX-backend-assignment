package com.assignment.domain.friendrequest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FriendRequestCreateRequestDto {
    @Schema(description = "친구 대상 유저 ID", example = "20")
    @Positive
    private Long toUserId;
}
