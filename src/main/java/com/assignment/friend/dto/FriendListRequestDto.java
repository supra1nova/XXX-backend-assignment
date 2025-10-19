package com.assignment.friend.dto;

import com.assignment.common.model.PageRequestDto;
import com.assignment.common.validator.In;
import io.micrometer.common.util.StringUtils;
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
    @Positive
    private Long fromUserId;

    @Positive
    private Long toUserId;

    @In(anyOf = {"approvedAt"})
    private String sort;

    @Override
    public void init() {
        super.init();
        if (StringUtils.isBlank(sort)) sort = "approvedAt,desc";
    }
}
