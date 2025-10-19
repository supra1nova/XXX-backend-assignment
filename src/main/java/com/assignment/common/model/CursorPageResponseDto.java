package com.assignment.common.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
public class CursorPageResponseDto<T> {
    @JsonProperty("data")
    private CursorData<T> cursorData;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CursorData<T> {
        private String window;
        private long totalCount;
        private List<T> items;
    }

    public static <T> CursorPageResponseDto<T> of(List<T> items, CursorPageRequestDto requestDto) {
        return CursorPageResponseDto.<T>builder()
            .cursorData(CursorData.<T>builder()
                .window(requestDto.getWindow())
                .totalCount(items.size())
                .items(items)
                .build())
            .build();
    }
}
