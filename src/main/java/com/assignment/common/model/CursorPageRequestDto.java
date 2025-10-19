package com.assignment.common.model;

import com.assignment.common.validator.In;
import com.querydsl.core.types.Order;
import io.micrometer.common.util.StringUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Range;

import java.util.UUID;

@Slf4j
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class CursorPageRequestDto {
    private UUID requestId;

    @Range(min = 10, max = 50, message = "10 ~ 50 사이의 크기만 로드 가능합니다.")
    private Integer maxSize;

    @In(anyOf = {"1d", "7d", "30d", "90d", "over"}, message = "요청기간이 올바르지 않습니다.")
    private String window;

    public void init() {
        if (maxSize == null) maxSize = 20;
        if (StringUtils.isBlank(window)) window = "7d";
    }

    public Cursorable toCursorable(String sort) {
        String[] sortParts = sort.split(",");
        String order = sortParts[0];

        Order direction = sortParts.length > 1 && sortParts[1].equalsIgnoreCase("asc")
            ? Order.ASC
            : Order.DESC;

        String stringParsedRequestId = requestId == null ? null : requestId.toString();

        return Cursorable.builder()
            .order(order)
            .direction(direction)
            .requestId(stringParsedRequestId)
            .maxSize(maxSize)
            .window(window)
            .build();
    }
}
