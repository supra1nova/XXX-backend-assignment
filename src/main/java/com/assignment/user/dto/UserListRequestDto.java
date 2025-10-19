package com.assignment.user.dto;

import com.assignment.common.model.PageRequestDto;
import com.assignment.common.validator.In;
import io.micrometer.common.util.StringUtils;
import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(description = "이름 (Like 검색)", example = "우")
    @Pattern(regexp = "^[A-Za-zㄱ-힣0-9]{0,20}$", message = "알파벳 대소문자, 한글 그리고 숫자만 가능합니다.")
    private String name;

    @Schema(description = "나이", example = "58")
    @Range(min = 1, max = 150, message = "1 ~ 150 사이의 숫자만 가능합니다.")
    private Integer age;

    @Schema(description = "정렬 방식 (createdAt,desc)", example = "createdAt,desc")
    @In(anyOf = {"userId", "userName", "createdAt", "updatedAt"})
    private String sort;

    @Override
    public void init() {
        super.init();
        if (StringUtils.isBlank(sort)) sort = "createdAt,desc";
    }
}
