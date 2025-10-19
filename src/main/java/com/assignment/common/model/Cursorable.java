package com.assignment.common.model;

import com.querydsl.core.types.Order;
import lombok.Builder;
import lombok.Getter;

import java.time.Duration;

@Getter
@Builder
public class Cursorable {
    private String order;
    private Order direction;
    private String requestId;
    private Integer maxSize;
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
