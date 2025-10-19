package com.assignment.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
@AllArgsConstructor
public class UserListResponseDto {
    private final Long userId;
    private final String userName;
    private final Integer userAge;
    private final Instant createdAt;
    private final Instant updatedAt;
}
