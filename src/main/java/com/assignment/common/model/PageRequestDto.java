package com.assignment.common.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Range;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Slf4j
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class PageRequestDto {
    @Schema(description = "조회 대상 페이지", example = "1")
    @Min(value = 1, message = "페이지는 1 이상이어야 합니다.")
    private Integer page;

    @Schema(description = "최대 로드 수량 (min:10, max:50)", example = "10")
    @Range(min = 10, max = 50, message = "10 ~ 50 사이의 크기만 로드 가능합니다.")
    private Integer maxSize;

    public void init() {
        if (page == null) page = 1;
        if (maxSize == null) maxSize = 10;
    }

    public Pageable toPageable(String sort) {
        // todo: api spec 상에는 0으로 전달하고 있음
        //  ->spec 을 무시하고 1로 받을지, psec에 따라 0으로 받고 -1을 뺄지 고민 필요
        page = Math.max(page - 1, 0);

        String[] sortParts = sort.split(",");
        String sortField = sortParts[0];

        Sort.Direction direction = sortParts.length > 1 && sortParts[1].equalsIgnoreCase("asc")
            ? Sort.Direction.ASC
            : Sort.Direction.DESC;

        log.info("order direction: {}", direction);

        return PageRequest.of(this.page, this.maxSize, Sort.by(direction, sortField));
    }
}
