package com.assignment.common.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Schema(description = "응답용 커서 페이지네이션 DTO")
@Getter
@Setter
@Builder
public class CursorPageResponseDto<T> {
    @Schema(description = "실제 응답 객체")
    @JsonProperty("data")
    private CursorData<T> cursorData;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CursorData<T> {
        @Schema(description = "조회 대상 기간", example = "over")
        private String window;
        @Schema(description = "전체 데이터 수량", example = "1")
        private long totalCount;
        @Schema(description = "현재 페이지 내 포함된 데이터 목록")
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
