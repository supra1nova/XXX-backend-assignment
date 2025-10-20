package com.assignment.friend.dto;

import com.assignment.common.model.CursorPageRequestDto;
import com.assignment.common.validator.In;
import io.micrometer.common.util.StringUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class FriendRequestListRequestDto extends CursorPageRequestDto {
    @Schema(description = "친구 신청 요청자 ID (uuid 형식)", example = "10")
    @Positive
    private Long requestUserId;

    @Schema(description = "정렬 방식 (requestedAt,desc)", example = "requestedAt,desc")
    @In(anyOf = {"requestedAt"})
    private String sort;

    @Override
    public void init() {
        super.init();
        if (StringUtils.isBlank(sort)) sort = "requestedAt,desc";
    }
}
