package com.assignment.friend.dto;

import com.assignment.common.model.CursorPageRequestDto;
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
public class FriendRequestListRequestDto extends CursorPageRequestDto {
    @Positive
    private Long requestUserId;

    @In(anyOf = {"requestedAt"})
    private String sort;

    @Override
    public void init() {
        super.init();
        this.sort = StringUtils.isBlank(sort) ? "requestedAt,desc" : sort;
    }
}
