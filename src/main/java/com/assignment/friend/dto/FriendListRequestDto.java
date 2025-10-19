package com.assignment.friend.dto;

import com.assignment.common.model.PageRequestDto;
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
public class FriendListRequestDto extends PageRequestDto {
    @Schema(description = "친구 신청 요청자", example = "6")
    @Positive
    private Long fromUserId;

    @Schema(description = "친구 신청 대상자", example = "2")
    @Positive
    private Long toUserId;

    @Schema(description = "정렬 방식 (approvedAt,desc)", example = "approvedAt,desc")
    @In(anyOf = {"approvedAt"})
    private String sort;

    @Override
    public void init() {
        super.init();
        if (StringUtils.isBlank(sort)) sort = "approvedAt,desc";
    }
}
