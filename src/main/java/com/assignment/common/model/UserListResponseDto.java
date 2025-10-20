package com.assignment.common.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Schema(description = "응답용 유저 목록 DTO")
@Getter
@Builder
@AllArgsConstructor
public class UserListResponseDto {
    @Schema(description = "유저 ID", example = "1")
    private final Long userId;
    @Schema(description = "유저 이름", example = "가나다")
    private final String userName;
    @Schema(description = "유저 나이", example = "22")
    private final Integer userAge;
    @Schema(description = "생성 일시", example = "2025-01-02T11:22:33.456Z")
    private final Instant createdAt;
    @Schema(description = "수정 일시", example = "2025-01-02T11:22:33.456Z")
    private final Instant updatedAt;
}
