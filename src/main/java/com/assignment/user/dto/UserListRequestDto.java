package com.assignment.user.dto;

import com.assignment.common.model.PageRequestDto;
import com.assignment.common.validator.In;
import io.micrometer.common.util.StringUtils;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.validator.constraints.Range;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class UserListRequestDto extends PageRequestDto {
    @Pattern(regexp = "^[A-Za-zㄱ-힣0-9]{0,20}$", message = "알파벳 대소문자, 한글 그리고 숫자만 가능합니다.")
    private String name;

    @Range(min = 1, max = 150, message = "1 ~ 150 사이의 숫자만 가능합니다.")
    private Integer age;

    @In(anyOf = {"userId", "userName", "createdAt", "updatedAt"})
    private String sort;

    @Override
    public void init() {
        super.init();
        if (StringUtils.isBlank(sort)) sort = "createdAt,desc";
    }
}
