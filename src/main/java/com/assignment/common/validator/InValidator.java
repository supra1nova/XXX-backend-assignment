package com.assignment.common.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Slf4j
public class InValidator implements ConstraintValidator<In, String> {
    private final List<String> staticOrderList = List.of("desc", "asc");
    private Set<String> allowed;

    @Override
    public void initialize(In constraintAnnotation) {
        allowed = Set.of(constraintAnnotation.anyOf());
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (StringUtils.isBlank(value)) {
            return true;
        }

        String[] splitValueArr = value.split(",");
        String sort = splitValueArr[0];
        log.info("splitValueArr: {}", Arrays.toString(splitValueArr));

        if (StringUtils.isBlank(sort)) {
            return false;
        }

        if (!allowed.contains(sort)) {
            return false;
        }
        log.info("sort: {}", sort);

        if ((splitValueArr.length < 2)) {
            return true;
        }
        log.info("splitValueArr.length: {}", splitValueArr.length);

        String order = splitValueArr[1];
        if (StringUtils.isBlank(order)) {
            return false;
        }

        log.info("order: {}", order);

        return staticOrderList.contains(order);
    }
}
