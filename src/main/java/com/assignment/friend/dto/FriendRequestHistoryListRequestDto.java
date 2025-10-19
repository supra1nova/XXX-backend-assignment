package com.assignment.friend.dto;

import com.assignment.common.model.PageRequestDto;
import com.assignment.common.validator.In;
import io.micrometer.common.util.StringUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class FriendRequestHistoryListRequestDto extends PageRequestDto {
    @Schema(description = "정렬 방식 (createdAt,desc)", example = "createdAt,desc")
    @In(anyOf = {"createdAt"})
    private String sort;

    @Override
    public void init() {
        super.init();
        if (StringUtils.isBlank(sort)) sort = "createdAt,desc";
    }
}
