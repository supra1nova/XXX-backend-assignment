package com.assignment.common.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageResponseDto<T> {
    @JsonProperty("data")
    private PageData<T> pageData;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PageData<T> {
        private Integer totalPages;
        private Long totalCount;
        private List<T> items;
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
