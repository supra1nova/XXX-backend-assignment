package com.assignment.friend.dto;

import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FriendRequestCreateRequestDto {
    @Positive
    private Long toUserId;
}
