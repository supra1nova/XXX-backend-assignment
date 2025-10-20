package com.assignment.common.model;

import com.querydsl.core.types.Order;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.Duration;

@Schema(description = "커서 객체 DTO")
@Getter
@Builder
public class Cursorable {
    @Schema(description = "정렬 기준", example = "over")
    private String order;
    @Schema(description = "정렬 방향", example = "over")
    private Order direction;
    @Schema(description = "친구 신청 ID", example = "1cb16868-e2a3-4175-b50f-81c4d909c80b")
    private String requestId;
    @Schema(description = "최대 로드 수량 (min:10, max:50)", example = "10")
    private Integer maxSize;
    @Schema(description = "조회 대상 기간", example = "over")
    private String window;

    public Duration getWindowDuration() {
        if (window == null) return null;
        return switch (window) {
            case "1d" -> Duration.ofDays(1);
            case "7d" -> Duration.ofDays(7);
            case "30d" -> Duration.ofDays(30);
            case "90d" -> Duration.ofDays(90);
            case "over" -> null;
            default -> throw new IllegalArgumentException("Invalid window: " + window);
        };
    }
}
