package com.assignment.common.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Schema(description = "응답용 페이지네이션 DTO")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageResponseDto<T> {
    @Schema(description = "실제 응답 객체")
    @JsonProperty("data")
    private PageData<T> pageData;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PageData<T> {
        @Schema(description = "전체 페이지 수량", example = "1")
        private Integer totalPages;
        @Schema(description = "전체 데이터 수량", example = "1")
        private Long totalCount;
        @Schema(description = "현재 페이지 내 포함된 데이터 목록")
        private List<T> items;
        @Schema(description = "현재 페이지 내 포함된 데이터 목록 수량", example = "1")
        private Integer itemCount;
    }

    public static <T> PageResponseDto<T> from(Page<T> page) {
        return PageResponseDto.<T>builder()
            .pageData(PageData.<T>builder()
                .totalPages(page.getTotalPages())
                .totalCount(page.getTotalElements())
                .items(page.getContent())
                .itemCount(page.getNumberOfElements())
                .build())
            .build();
    }
}
